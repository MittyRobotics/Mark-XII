package org.usfirst.team1351.robot.drive;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOThread;

public class TKODrive implements Runnable
{
	public TKOThread driveThread = null;
	private static TKODrive m_Instance = null;

	protected TKODrive()
	{

	}

	public static synchronized TKODrive getInstance()
	{
		if (TKODrive.m_Instance == null)
		{
			m_Instance = new TKODrive();
			m_Instance.driveThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	public void start()
	{
		System.out.println("Starting drive task");
		if (!driveThread.isAlive() && m_Instance != null)
		{
			driveThread = new TKOThread(m_Instance);
			driveThread.setPriority(Definitions.getPriority("drive"));
		}
		if (!driveThread.isThreadRunning())
			driveThread.setThreadRunning(true);

		System.out.println("Started drive task");
	}

	public void stop()
	{
		System.out.println("Stopping drive task");
		if (driveThread.isThreadRunning())
			driveThread.setThreadRunning(false);
		System.out.println("Stopped drive task");
	}

	public synchronized void tankDrive()
	{
		try
		{
			TKOHardware.getDriveJaguar(0).set(TKOHardware.getJoystick(1).getY() * Definitions.DRIVE_MULTIPLIER[0]);
			TKOHardware.getDriveJaguar(1).set(TKOHardware.getJoystick(1).getY() * Definitions.DRIVE_MULTIPLIER[1]);
			TKOHardware.getDriveJaguar(2).set(TKOHardware.getJoystick(0).getY() * Definitions.DRIVE_MULTIPLIER[2]);
			TKOHardware.getDriveJaguar(3).set(TKOHardware.getJoystick(0).getY() * Definitions.DRIVE_MULTIPLIER[3]);
		} catch (Exception e)
		{
			e.printStackTrace();
			TKOLogger.getInstance().addMessage("ERROR IN TANK DRIVE CAUGHT! " + e.getMessage());
			TKODrive.getInstance().stop();
		}
	}

	public synchronized void PIDCurrentCalibration()
	{
		double p = 5., i = 0., d = 0.;
		boolean calibrating = true;
		long bestTime = Long.MAX_VALUE;

		try
		{
			while (calibrating)// TODO first run does not actually go until one iteration of loop
			{
				System.out.println("Stopping all data collection");
				TKODataReporting.getInstance().stopAllDataCollection();
				System.out.println("Running PID Tuning! P: " + p);
				System.out.println("Destroying objects");
				TKOHardware.destroyObjects();
				System.out.println("Initialing objects");
				TKOHardware.initObjects();
				System.out.println("Configuring jaguars");
				TKOHardware.configJags(p, i, d);
				System.out.println("Done with all, starting commands");
				Thread.sleep(250);
				TKOLogger.getInstance().addData("Pval", p, null);
				System.out.println("Starting collecting data");
				TKODataReporting.getInstance().startCollectingDriveData(p); // stops regular data collection
				System.out.println("Starting set commands");
				Thread.sleep(250);
				for (int j = 0; j < Definitions.NUM_DRIVE_JAGS; j++)
				{
					TKOHardware.getDriveJaguar(j).set(Definitions.DRIVE_MULTIPLIER[j]);
					if (p < 10)
						TKOLogger.getInstance().addData("MotorSetCommand", System.nanoTime(), j + "; p: 0" + p);
					else
						TKOLogger.getInstance().addData("MotorSetCommand", System.nanoTime(), j + "; p: " + p);
				}
				long start = System.currentTimeMillis();
				int runningTime = 5000;
				while ((System.currentTimeMillis() - start) < runningTime)
				{
					//record the point in time when feedback exceeds target, or is within x% of target
					if (TKOHardware.getDriveJaguar(0).getOutputCurrent() > Definitions.DRIVE_MULTIPLIER[0])
					{
						if (bestTime > System.nanoTime())
							bestTime = System.nanoTime();
					}
					//record final deviation from target at the end of 5 s
				}
				TKODataReporting.getInstance().stopAllDataCollection();
				System.out.println("Destroying objects");
				TKOHardware.destroyObjects();
				System.out.println("Reinitializing objects");
				TKOHardware.initObjects();
				System.out.println("Initialized objects, stopping collecting drive data");
				TKODataReporting.getInstance().stopCollectingDriveData(); // starts regular data collection
				p += 1.;
				if (p > 15.)
					calibrating = false;
				System.out.println("Next iteration");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		TKOHardware.destroyObjects();
		TKOHardware.initObjects();
		TKODataReporting.getInstance().stopCollectingDriveData();
	}

	@Override
	public void run()
	{
		try
		{
			boolean calibRan = false;
			while (driveThread.isThreadRunning())
			{
				// System.out.println("DRIVE THREAD RAN!");
				// if (TKOHardware.getJoystick(0).getRawButton(5))
				if (!calibRan)
				{
					PIDCurrentCalibration();
					calibRan = true;
				}

				tankDrive();
				synchronized (driveThread)
				{
					driveThread.wait(5);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
