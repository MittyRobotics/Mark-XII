package org.usfirst.team1351.robot.drive;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
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
	
	public synchronized void setLeftRightMotorOutputs(double left, double right)
	{
		try
		{
			if (TKOHardware.getDriveTalon(0).getControlMode() == CANTalon.ControlMode.PercentVbus)
				TKOHardware.getDriveTalon(0).set(-left);
			else
				throw new TKOException("CANTALON NOT IN PERCENT VBUS: " + TKOHardware.getDriveTalon(0).getDeviceID());

			if (TKOHardware.getDriveTalon(2).getControlMode() == CANTalon.ControlMode.PercentVbus)
				TKOHardware.getDriveTalon(2).set(right);
			else
				throw new TKOException("CANTALON NOT IN PERCENT VBUS: " + TKOHardware.getDriveTalon(2).getDeviceID());

			System.out.println("Drive 0 Get " + TKOHardware.getDriveTalon(0).get());
			System.out.println("Drive 1 Get " + TKOHardware.getDriveTalon(1).get());
			System.out.println("Drive 2 Get " + TKOHardware.getDriveTalon(2).get());
			System.out.println("Drive 3 Get " + TKOHardware.getDriveTalon(3).get());
		} catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	public void tankDrive()
	{
		try
		{
			setLeftRightMotorOutputs(TKOHardware.getJoystick(0).getY(), TKOHardware.getJoystick(1).getY());
		} catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	public void arcadeDrive()
	{
		boolean squaredInputs = true;
		try
		{
			double moveValue = TKOHardware.getJoystick(0).getY();
			double rotateValue = TKOHardware.getJoystick(1).getY();
			double leftMotorSpeed;
			double rightMotorSpeed;

			if (squaredInputs)
			{
				// square the inputs (while preserving the sign) to increase fine control while permitting full power
				if (moveValue >= 0.0)
				{
					moveValue = (moveValue * moveValue);
				} else
				{
					moveValue = -(moveValue * moveValue);
				}
				if (rotateValue >= 0.0)
				{
					rotateValue = (rotateValue * rotateValue);
				} else
				{
					rotateValue = -(rotateValue * rotateValue);
				}
			}

			if (moveValue > 0.0)
			{
				if (rotateValue > 0.0)
				{
					leftMotorSpeed = moveValue - rotateValue;
					rightMotorSpeed = Math.max(moveValue, rotateValue);
				} else
				{
					leftMotorSpeed = Math.max(moveValue, -rotateValue);
					rightMotorSpeed = moveValue + rotateValue;
				}
			} else
			{
				if (rotateValue > 0.0)
				{
					leftMotorSpeed = -Math.max(-moveValue, rotateValue);
					rightMotorSpeed = moveValue + rotateValue;
				} else
				{
					leftMotorSpeed = moveValue - rotateValue;
					rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
				}
			}

			setLeftRightMotorOutputs(leftMotorSpeed, rightMotorSpeed);
		} catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void PIDCurrentCalibration()
	{
		double p = 0., i = 0., d = 0.;
		boolean calibrating = true;
		long bestTime = Long.MAX_VALUE;

		try
		{
			while (calibrating && DriverStation.getInstance().isEnabled())// TODO first run does not actually go until one iteration of loop
			{
				System.out.println("Stopping all data collection");
				TKODataReporting.getInstance().stopAllDataCollection();
				System.out.println("Running PID Tuning! P: " + p + " I: " + i + " D: " + d);
				System.out.println("Destroying objects");
				TKOHardware.destroyObjects();
				System.out.println("Initialing objects");
				TKOHardware.initObjects();
				System.out.println("Configuring jaguars");
				TKOHardware.configTalons(p, i, d);
				TKOHardware.setAll(0.);
				System.out.println("Done with all, starting commands");
				// Thread.sleep(250);
				TKOLogger.getInstance().addData("Pval", p, null, -1);
				System.out.println("Starting collecting data");
				TKODataReporting.getInstance().startCollectingDriveData(p, i, d); // stops regular data collection
				System.out.println("Starting set commands");
				Thread.sleep(1500);
				for (int j = 0; j < Definitions.NUM_DRIVE_TALONS; j++)
				{
					TKOHardware.getDriveTalon(j).set(Definitions.DRIVE_MULTIPLIER[j]);
					if (p < 10)
						TKOLogger.getInstance().addData("MotorSetCommand", System.nanoTime(), "p: 0" + p + " i: 0" + i + " d: 0" + d, j);
					else
						TKOLogger.getInstance().addData("MotorSetCommand", System.nanoTime(), "p: " + p + " i: " + i + " d: " + d, j);
				}
				long start = System.currentTimeMillis();
				int runningTime = 5000;
				while ((System.currentTimeMillis() - start) < runningTime)
				{
					// record the point in time when feedback exceeds target, or is within x% of target
					if (TKOHardware.getDriveTalon(0).getOutputCurrent() > Definitions.DRIVE_MULTIPLIER[0])
					{
						if (bestTime > System.nanoTime())
							bestTime = System.nanoTime();
					}
					// record final deviation from target at the end of 5 s
				}
				TKODataReporting.getInstance().stopAllDataCollection();
				System.out.println("Destroying objects");
				TKOHardware.destroyObjects();
				System.out.println("Reinitializing objects");
				TKOHardware.initObjects();
				System.out.println("Initialized objects, stopping collecting drive data");
				TKODataReporting.getInstance().stopCollectingDriveData(); // starts regular data collection
				// p += 1.;
				// if (p > 15.)
				i += 0.01;
				if (i > .1)
				{
					i = 0.;
					p += 1.;
					if (p > 15.)
						calibrating = false;
				}
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
			// boolean calibRan = false;
			while (driveThread.isThreadRunning())
			{
				// System.out.println("DRIVE THREAD RAN!");
				if (TKOHardware.getJoystick(0).getRawButton(2))
				// if (!calibRan)
				{
					PIDCurrentCalibration();
					// calibRan = true;
				}
				if (TKOHardware.getJoystick(0).getRawButton(4))
				{
					//TODO make this not ghetto
					TKOHardware.getPiston(0).set(DoubleSolenoid.Value.kForward);
				}
				if (TKOHardware.getJoystick(0).getRawButton(5))
				{
					TKOHardware.getPiston(0).set(DoubleSolenoid.Value.kReverse);
					//TODO make this not ghetto
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
