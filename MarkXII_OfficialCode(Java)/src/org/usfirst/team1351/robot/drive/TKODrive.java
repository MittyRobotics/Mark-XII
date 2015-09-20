package org.usfirst.team1351.robot.drive;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKORuntimeException;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class TKODrive implements Runnable
{
	public static synchronized TKODrive getInstance()
	{
		if (TKODrive.m_Instance == null)
		{
			m_Instance = new TKODrive();
			m_Instance.driveThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	public TKOThread driveThread = null;

	private static TKODrive m_Instance = null;

	protected TKODrive()
	{

	}

	public void arcadeDrive()
	{
		boolean squaredInputs = true;
		try
		{
			double moveValue = TKOHardware.getJoystick(0).getY();
			if (TKOHardware.getJoystick(0).getTrigger())
				moveValue = TKOHardware.getJoystick(0).getY() * 0.6;

			double rotateValue = TKOHardware.getJoystick(1).getX() * 0.8;
			if (TKOHardware.getJoystick(1).getTrigger())
				rotateValue = TKOHardware.getJoystick(1).getX() * 0.6;

			double leftMotorSpeed;
			double rightMotorSpeed;

			if (squaredInputs)
			{
				// square the inputs (while preserving the sign) to increase fine control while permitting full power
				if (moveValue >= 0.0)
				{
					moveValue = (moveValue * moveValue);
				}
				else
				{
					moveValue = -(moveValue * moveValue);
				}
				if (rotateValue >= 0.0)
				{
					rotateValue = (rotateValue * rotateValue);
				}
				else
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
				}
				else
				{
					leftMotorSpeed = Math.max(moveValue, -rotateValue);
					rightMotorSpeed = moveValue + rotateValue;
				}
			}
			else
			{
				if (rotateValue > 0.0)
				{
					leftMotorSpeed = -Math.max(-moveValue, rotateValue);
					rightMotorSpeed = moveValue + rotateValue;
				}
				else
				{
					leftMotorSpeed = moveValue - rotateValue;
					rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
				}
			}
			TKOHardware.changeTalonMode(TKOHardware.getLeftDrive(), CANTalon.ControlMode.PercentVbus, Definitions.DRIVE_P,
					Definitions.DRIVE_I, Definitions.DRIVE_D);
			TKOHardware.changeTalonMode(TKOHardware.getRightDrive(), CANTalon.ControlMode.PercentVbus, Definitions.DRIVE_P,
					Definitions.DRIVE_I, Definitions.DRIVE_D);
			setLeftRightMotorOutputsPercentVBus(leftMotorSpeed, rightMotorSpeed);
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void currentModeTankDrive()
	{
		try
		{
			TKOHardware.changeTalonMode(TKOHardware.getLeftDrive(), CANTalon.ControlMode.Current, Definitions.DRIVE_P, Definitions.DRIVE_I,
					Definitions.DRIVE_D);
			TKOHardware.changeTalonMode(TKOHardware.getRightDrive(), CANTalon.ControlMode.Current, Definitions.DRIVE_P,
					Definitions.DRIVE_I, Definitions.DRIVE_D);
			setLeftRightMotorOutputsCurrent(TKOHardware.getJoystick(0).getY(), TKOHardware.getJoystick(1).getY());
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized void PIDCurrentCalibration()
	{
		double p = 0., i = 0., d = 0.;
		boolean calibrating = true;
		long bestTime = Long.MAX_VALUE;

		try
		{
			while (calibrating && DriverStation.getInstance().isEnabled())
			// TODO first run does not actually go until one iteration of loop (maybe fixed now)
			{
				System.out.println("Stopping all data collection");
				TKODataReporting.getInstance().stopAllDataCollection();
				System.out.println("Running PID Tuning! P: " + p + " I: " + i + " D: " + d);
				System.out.println("Destroying objects");
				TKOHardware.destroyObjects();
				System.out.println("Initialing objects");
				TKOHardware.initObjects();
				System.out.println("Configuring jaguars");
				TKOHardware.configDriveTalons(p, i, d, CANTalon.ControlMode.Current);
				TKOHardware.setAllDriveTalons(0.);
				System.out.println("Done with all, starting commands");
				// Thread.sleep(250);
				TKOLogger.getInstance().addData("Pval", p, null, -1);
				System.out.println("Starting collecting data");
				TKODataReporting.getInstance().startCollectingDriveData(p, i, d); // stops regular data collection
				System.out.println("Starting set commands");
				Thread.sleep(1500);
				for (int j = 0; j < Definitions.NUM_DRIVE_TALONS; j += 2)
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		TKOHardware.destroyObjects();
		TKOHardware.initObjects();
		TKODataReporting.getInstance().stopCollectingDriveData();
	}

	private void shimmy()
	{
		try
		{
			TKOHardware.getPiston(0).set(Definitions.SHIFTER_LOW);
			TKOHardware.getLeftDrive().enableBrakeMode(true);
			TKOHardware.getRightDrive().enableBrakeMode(true);
			boolean b = true;
			Timer t = new Timer();
			while (TKOHardware.getJoystick(1).getRawButton(2))
			{
				t.start();
				if (b)
				{
					while (t.get() < 0.25)
						setLeftRightMotorOutputsPercentVBus(-.25, .25);
				}
				else
				{
					while (t.get() < 0.25)
						setLeftRightMotorOutputsPercentVBus(.25, -.25);
				}
				b = !b;
				t.stop();
				t.reset();
			}

			TKOHardware.getLeftDrive().set(0);
			TKOHardware.getRightDrive().set(0);
			TKOHardware.getLeftDrive().enableBrakeMode(Definitions.DRIVE_BRAKE_MODE[0]);
			TKOHardware.getRightDrive().enableBrakeMode(Definitions.DRIVE_BRAKE_MODE[2]);
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void overTheLipPositioner()
	{
		try
		{
			TKOHardware.getPiston(0).set(Definitions.SHIFTER_LOW);
			TKOHardware.getLeftDrive().enableBrakeMode(true);
			TKOHardware.getRightDrive().enableBrakeMode(true);
			Timer t = new Timer();
			t.start();
			while ((TKOHardware.getCrateDistance() > Definitions.TRASHCAN_POSITIONING_MAX
					|| TKOHardware.getCrateDistance() < Definitions.TRASHCAN_POSITIONING_MIN) && t.get() < 10 && TKOHardware.getJoystick(1).getRawButton(3))
			{
				if (TKOHardware.getCrateDistance() > Definitions.TRASHCAN_POSITIONING_MAX)
				{
					System.out.println("TOO FAR");
					setLeftRightMotorOutputsPercentVBus(-.1, -.1);
				}
				else if (TKOHardware.getCrateDistance() < Definitions.TRASHCAN_POSITIONING_MIN)
				{
					System.out.println("TOO CLOSE");
					setLeftRightMotorOutputsPercentVBus(.1, .1);
				}
			}
			TKOHardware.getLeftDrive().set(0);
			TKOHardware.getRightDrive().set(0);
			t.stop();
			t.reset();
			Timer.delay(.25);
			TKOHardware.getLeftDrive().enableBrakeMode(Definitions.DRIVE_BRAKE_MODE[0]);
			TKOHardware.getRightDrive().enableBrakeMode(Definitions.DRIVE_BRAKE_MODE[2]);
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try
		{
			// boolean calibRan = false;
			// tankDrive();
			while (driveThread.isThreadRunning())
			{
				// System.out.println("DRIVE THREAD RAN!");
				// if (TKOHardware.getJoystick(0).getRawButton(2))
				// // if (!calibRan)
				// {
				// PIDCurrentCalibration();
				// // calibRan = true;
				// }
				// tankDrive();
				/*if (TKOHardware.getJoystick(3).getTrigger())
				{
					setLeftRightMotorOutputsPercentVBus(-0.3, -0.3);
				}*/
				
				if (TKOHardware.getJoystick(1).getRawButton(2))
				{
					shimmy();
					//overTheLipPositioner();
				}
				arcadeDrive();
				// currentModeTankDrive();
				synchronized (driveThread)
				{
					driveThread.wait(5);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void setLeftRightMotorOutputsCurrent(double leftMult, double rightMult)
	{
		try
		{
			if (TKOHardware.getLeftDrive().getControlMode() == CANTalon.ControlMode.Current)
			{
				TKOHardware.getLeftDrive().set(Definitions.DRIVE_MULTIPLIER_LEFT * Definitions.MAX_CURRENT_LEFT * leftMult);
			}
			else
				throw new TKORuntimeException("ERROR TRIED TO RUN TANK DRIVE ON NON-CURRENT TALON");

			if (TKOHardware.getRightDrive().getControlMode() == CANTalon.ControlMode.Current)
				TKOHardware.getRightDrive().set(Definitions.DRIVE_MULTIPLIER_RIGHT * Definitions.MAX_CURRENT_RIGHT * rightMult);
			else
				throw new TKORuntimeException("ERROR TRIED TO RUN TANK DRIVE ON NON-CURRENT TALON");

		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void setLeftRightMotorOutputsPercentVBus(double left, double right)
	{
		try
		{
			if (TKOHardware.getLeftDrive().getControlMode() == CANTalon.ControlMode.PercentVbus)
				TKOHardware.getLeftDrive().set(Definitions.DRIVE_MULTIPLIER[0] * left);
			else
				throw new TKORuntimeException("ERROR TRIED TO RUN TANK DRIVE ON NON-PERCENT VBUS TALON");

			if (TKOHardware.getRightDrive().getControlMode() == CANTalon.ControlMode.PercentVbus)
				TKOHardware.getRightDrive().set(Definitions.DRIVE_MULTIPLIER[2] * right);
			else
				throw new TKORuntimeException("ERROR TRIED TO RUN TANK DRIVE ON NON-PERCENT VBUS TALON");

		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
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

	public void tankDrive()
	{
		try
		{
			// the change talon mode should only do anything if the mode is not already that which it is trying to set
			TKOHardware.changeTalonMode(TKOHardware.getLeftDrive(), CANTalon.ControlMode.PercentVbus, Definitions.DRIVE_P,
					Definitions.DRIVE_I, Definitions.DRIVE_D); // TODO make sure this is efficient
			TKOHardware.changeTalonMode(TKOHardware.getRightDrive(), CANTalon.ControlMode.PercentVbus, Definitions.DRIVE_P,
					Definitions.DRIVE_I, Definitions.DRIVE_D);
			setLeftRightMotorOutputsPercentVBus(TKOHardware.getJoystick(0).getY(), TKOHardware.getJoystick(1).getY());
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}
}
