package org.usfirst.team1351.robot.drive;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.DriverStation;

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
			driveThread = new TKOThread(m_Instance);
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

	public synchronized void PIDCurrentCalibration() //TODO Finish this
	{
		float p = 0, i = 0, d = 0;
		boolean calibrating = true;

		while (calibrating && DriverStation.getInstance().isEnabled())
		{
			TKOHardware.initObjects();
			try
			{
				for (int j = 0; j < Definitions.NUM_DRIVE_JAGS; j++)
				{
					TKOHardware.getDriveJaguar(j).disableControl();
					TKOHardware.getDriveJaguar(j).setCurrentMode(p, i, d);
					TKOHardware.getDriveJaguar(j).enableControl();
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TKOHardware.destroyObjects();
		}
		TKOHardware.destroyObjects(); //TODO make sure cant destroy if already destroyed
		TKOHardware.initObjects();
	}

	@Override
	public void run()
	{
		try
		{
			while (driveThread.isThreadRunning())
			{
				// System.out.println("DRIVE THREAD RAN!");
				tankDrive();
				synchronized (driveThread)
				{
					driveThread.wait(5);
				}
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
