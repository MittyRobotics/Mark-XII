package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKORuntimeException;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Vadim
 * @version 03/05/15
 * 
 *          TODO We may need to figure out how to shutdown properly
 * 
 *          BUGS
 * 
 *          TODO level variable needs to be reset when pressing goUp?
 * 
 *          TODO Test code with lift encoder unplugged; what happens?
 * 
 *          TODO Test validation, validate for above^?
 */

public class TKOLift implements Runnable // implements Runnable is important to make this class support the Thread (run method)
{
	/**
	 * This function makes the class a singleton, so that there can only be one instance of the class even though the class is not static
	 * This is needed for the Thread to work properly.
	 */
	public static synchronized TKOLift getInstance()
	{
		if (m_Instance == null)
		{
			m_Instance = new TKOLift();
			m_Instance.conveyorThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	public TKOThread conveyorThread = null;
	private static TKOLift m_Instance = null;

	private double level; // changes the level
	private Action currentAction; // figures out if you are ascending or descending
	public boolean calibrated; // calibrates
	private double softBottom; // Soft bottom and top set to ensure no damage is done to the robot through running ti
	private double currentPIDSetpoint;

	private Operation operation = Operation.PID_CRATES;

	public static final double oneLevel = 4960; // TODO 4750 before; 4900 before; 5000 before
	public static final byte minLevel = 0; // zero based
	public static final byte maxLevel = 4; // 5th crate
	public static final double bottomOffset = 2650;//2145; 2500; 2600; 2650
	public static final double dropoffPerLevel = 0.2; // TODO CALCULATE
	public static final double softBottomOffset = 0; // safety offset
	public static final double softTopOffset = 100; // safety offset
	public static final double encoderThreshold = 100;
	public static final double minorAdjustmentSize = 100;
	public static final long liftThreadSleep = 20; // used to be 20

	//public static final double softTop = 23400 - softTopOffset;
	public static final double softTop = 23400 - softTopOffset;

	public static final double softLevelTop = (-softTopOffset + softTop - bottomOffset) / oneLevel;
	public static final double softLevelBot = (softBottomOffset - bottomOffset) / oneLevel;

	public static final double trashcanPickupPosition = softLevelBot + 0.1;
	public static final double trashcanMushDownLevel = (2000 - bottomOffset) / oneLevel; //1800
	public static final double startLevel = trashcanPickupPosition;

	public static final double minorAdjustmentLevels = minorAdjustmentSize / oneLevel;
	public static final double fullOfCratesPosition = softLevelTop - 0.0001;
	public static final double threeCratesOnStepLevel = 0.5; // TODO Calculate this
	public static final double dropOffsetDistance = 0.75;
	public static final double liftManualIncrementer = 0.1; // TODO Calculate this

	public static double liftP = Definitions.LIFT_P;
	public static double liftI = Definitions.LIFT_I;
	public static double liftD = Definitions.LIFT_D;
	
	public long timeOfStartAutoPickup = 0;
	public boolean requestAutoPickup = false;

	private boolean manualEnabled = true;

	// Typical constructor made protected so that this class is only accessed statically (via getInstance), though that doesnt matter
	protected TKOLift()
	{
		level = -1;
		currentAction = Action.DONE;
		currentPIDSetpoint = 0;
		calibrated = false;
		softBottom = 0.;
		manualEnabled = true;
	}

	private double calculateLevel()
	{
		double calLevel = 0;
		try
		{
			calLevel = calculateLevel(TKOHardware.getLiftTalon().getEncPosition());
		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return calLevel;
	}

	private double calculateLevel(int encPosition)
	{
		double calculatedLevel = (encPosition - bottomOffset) / oneLevel;
		System.out.println("Calculated level: " + calculatedLevel + " pos: " + encPosition);
		return calculatedLevel;
	}

	private boolean calibrate()
	{
		try
		{
			// if (operation == Operation.MANUAL_VBUS)
			// return true;
			System.out.println("STARTING LIFT CALIBRATION");
			CANTalon lmotor = TKOHardware.getLiftTalon();
			TKOHardware.changeTalonMode(lmotor, CANTalon.ControlMode.PercentVbus, liftP, liftI, liftD);

			lmotor.reverseOutput(true);
			lmotor.setSafetyEnabled(false);

			while (!TKOHardware.getLiftBottom() && DriverStation.getInstance().isEnabled())
			{
				lmotor.set(Definitions.LIFT_CALIBRATION_POWER);
				System.out.println("Pos: " + lmotor.getPosition());
			}
			if (!DriverStation.getInstance().isEnabled() || !TKOHardware.getLiftBottom())
				return false;
			lmotor.set(0); // stop motor
			Timer.delay(0.1);
			System.out.println("POSITION: " + lmotor.getPosition());
			lmotor.setPosition(0); // reset encoder
			Timer.delay(0.25);
			System.out.println("POSITION AFTER RESET: " + lmotor.getPosition());
			softBottom = lmotor.getPosition() + softBottomOffset;
			/*
			 * Timer.delay(.1);
			 * 
			 * while (!TKOHardware.getLiftTop() && DriverStation.getInstance().isEnabled()) {
			 * lmotor.set(-Definitions.LIFT_CALIBRATION_POWER); } if (!DriverStation.getInstance().isEnabled()) return false; lmotor.set(0);
			 * // stop motor if (lmotor.getPosition() == 0) { System.out.println("CRITICAL ERROR ENCODER PROBABLY NOT PLUGGED IN"); throw
			 * new TKORuntimeException("CRITICAL ERROR ENCODER PROBABLY NOT PLUGGED IN"); } System.out.println("POSITION: " +
			 * lmotor.getPosition()); softTop = lmotor.getPosition() - softTopOffset;
			 */

			TKOHardware.changeTalonMode(lmotor, CANTalon.ControlMode.Position, liftP, liftI, liftD);
			setStartPosition(); // goto starting place
			currentPIDSetpoint = lmotor.getEncPosition();
			System.out.println("DONE CALIBRATING " + currentPIDSetpoint);
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
		return true;
	}

	private void completeManualJoystickControl()
	{
		try
		{
			if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.PercentVbus)
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.PercentVbus, liftP, liftI, liftD);
			TKOHardware.getLiftTalon().set(
					TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getY() * Definitions.LIFT_CALIBRATION_POWER);
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	public double getCurrentLevel()
	{
		return calculateLevel(getEncoderPosition());
	}

	public int getEncoderPosition()
	{
		try
		{
			return TKOHardware.getLiftTalon().getEncPosition();
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public double getSoftBottom()
	{
		return softBottom;
	}

	public double getSoftTop()
	{
		return softTop;
	}

	public double getTarget()
	{
		return 0.;
	}

	public double getTargetLevel()
	{
		return level;
	}

	public void goDown()
	{
		if (currentAction == Action.DONE)
		{
			// System.out.println("SHOULD BE GOING DOWN");
			if (level <= softLevelBot + 1)
			{
				goToTrashcanPickup();
				return;
			}
			else if (level > maxLevel)
			{
				goToLevel(maxLevel);
				return;
			}
			goDown(1);
		}
	}

	public void goDown(int n)
	{
		System.out.println("GOING DOWN");
		if (currentAction == Action.DONE)
		{
			if (level <= softLevelBot + 1)
			{
				goToTrashcanPickup();
				return;
			}
			goToLevel(level - n);
		}
	}

	public void goToFullPosition()
	{
		if (currentAction == Action.DONE)
		{
			System.out.println("FULL POSITION TIME");
			// level = fullOfCratesPosition;
			goToLevel(fullOfCratesPosition);
		}
	}

	public synchronized void goToStepPlaceLevel()
	{
		if (currentAction == Action.DONE)
		{
			goToLevel(threeCratesOnStepLevel);
		}
	}

	public synchronized void goToNearestRealToteLevel()
	{
		if (currentAction != Action.DONE)
			return;
		long intPartOfLevel;
		double floatPartOfLevel;
		double tarLevel;
		intPartOfLevel = (long) level;
		floatPartOfLevel = level - intPartOfLevel;

		if (floatPartOfLevel < 0.5)
			tarLevel = intPartOfLevel;
		else
			tarLevel = intPartOfLevel + 1;

		goToLevel(tarLevel);
	}

	public synchronized void goToLevel(double newLevel)
	{
		if (DriverStation.getInstance().isDisabled())
			return;

		System.out.println("GOING TO LEVEL " + newLevel);
		System.out.println("TARGET LEVEL POSITION: " + (newLevel * oneLevel + bottomOffset));

		level = newLevel;

		if (calculateLevel() > this.level)
			this.currentAction = Action.DESCENDING;
		else if (calculateLevel() < this.level)
			this.currentAction = Action.ASCENDING;
		else
		{
			System.out.println("WAT");
			if (getEncoderPosition() > (bottomOffset + oneLevel * level))
				currentAction = Action.DESCENDING;
			else if (getEncoderPosition() < (bottomOffset + oneLevel * level))
				currentAction = Action.ASCENDING;
		}
	}

	/**
	 * IMPORTANT, This method assumes that you already have set the currentAction variable
	 * 
	 * @param position
	 */
	public synchronized void goToPosition(double position)
	{
		// System.out.println("Tar Pos: " + position + " Setpoint: " + currentPIDSetpoint);
		try
		{
			if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.Position)
			{
				// TKOHardware.getLiftTalon().changeControlMode(CANTalon.ControlMode.Position);
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.Position, liftP, liftI, liftD);
				TKOHardware.getLiftTalon().enableControl();
			}

			TKOHardware.getLiftTalon().setPID(liftP, liftI, liftD);

			if (currentAction == Action.ASCENDING) // while ascending and not above the target
			{
				TKOHardware.getLiftTalon().ClearIaccum();
				if (currentPIDSetpoint >= position)
				{
					if (getEncoderPosition() >= (position - encoderThreshold) && getEncoderPosition() <= (position + encoderThreshold))
					{
						currentAction = Action.DONE;
						// done ascending
					}
					currentPIDSetpoint = position; // TODO DANGER
//					System.out.println("WAITING FOR MOTOR " + getEncoderPosition());
//					System.out.println("TarPos: " + position);
					// setpoint above target but we still havent reached the position with the motor
				}
				else if (currentPIDSetpoint <= getSoftTop())
				{
					currentPIDSetpoint += (Definitions.LIFT_PID_INCREMENTER);
				}
				else
				{
					System.out.println(currentAction);
					System.out.println("TRYING TO DRIVE LIFT BEYOND MAX: " + currentPIDSetpoint);
					currentPIDSetpoint = (int) getSoftTop();
					throw new TKORuntimeException("TRYING TO DRIVE LIFT BEYOND MAX: " + currentPIDSetpoint);
				}
			}
			else if (currentAction == Action.DESCENDING)
			{
				TKOHardware.getLiftTalon().ClearIaccum();
				if (currentPIDSetpoint <= position)
				{
					if (getEncoderPosition() <= (position + encoderThreshold) && getEncoderPosition() >= (position - encoderThreshold))
					{
						currentAction = Action.DONE;
						// done ascending
					}
					currentPIDSetpoint = position; // TODO DANGER
					System.out.println("WAITING FOR MOTOR " + getEncoderPosition());
					System.out.println("TarPos: " + position);
					// setpoint above target but we still havent reached the position with the motor
				}
				else if (currentPIDSetpoint >= getSoftBottom())
				{
					currentPIDSetpoint -= (Definitions.LIFT_PID_INCREMENTER);
				}
				else
				{
					currentPIDSetpoint = (int) getSoftBottom();
					System.out.println("TRYING TO DRIVE LIFT BEYOND MIN");
					// throw new TKORuntimeException("TRYING TO DRIVE LIFT BEYOND MIN");
				}
			}
			else
			{
				// System.out.println("Done");
			}
			// System.out.println("CURRENT PID SETPOINT : " + currentPIDSetpoint);
			TKOHardware.getLiftTalon().set(currentPIDSetpoint);
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
		// since we are using an incrementer we need to keep increasing the current setpoint until we reach target setpoint
	}

	public void goToTrashcanPickup()
	{
		if (currentAction == Action.DONE)
		{
			System.out.println("TRASHCAN TIME");
			// level = trashcanPickupPosition;
			goToLevel(trashcanPickupPosition);
		}
	}

	public void goToForceTrashcanDown()
	{
		if (currentAction == Action.DONE)
		{
			goToLevel(trashcanMushDownLevel);
		}
	}

	public void goToDropCrates()
	{
		if (currentAction == Action.DONE)
		{
			System.out.println("DROPPING STACK TIME");
			// level = calculateLevel() - dropOffsetDistance;
			goToLevel(calculateLevel() - dropOffsetDistance);
		}
	}

	public void goToDropCratesBasedOnLevel()
	{
		if (currentAction == Action.DONE)
		{
			System.out.println("DROPPING STACK TIME BASED ON LEVEL");
			// level = calculateLevel() - (level * dropoffPerLevel);
			goToLevel(calculateLevel() - (level * dropoffPerLevel));
		}
	}
	
	public void goUpSmallAmount()
	{
		if (currentAction == Action.DONE)
		{
			goToLevel(calculateLevel() + minorAdjustmentLevels);
		}
	}
	
	public void goDownSmallAmount()
	{
		if (currentAction == Action.DONE)
		{
			goToLevel(calculateLevel() - minorAdjustmentLevels);
		}
	}

	public void goUp()
	{
		if (currentAction == Action.DONE)
		{
			if (level >= softLevelTop - 1)
			{
				goToFullPosition();
				return;
			}
			else if (level < minLevel)
			{
				goToLevel(minLevel);
				return;
			}
			goUp(1);
		}
	}

	public void goUp(int n)
	{
		/*
		 * go up n levels. im thinking this function should return right away and everything actually runs in the thread run loop, not in
		 * this function eg this function only sets the target or something
		 * 
		 * figure out what has to be synchronized most important is that you cant spam this and that it has to take into account how much we
		 * can go and how much more we can go eg if we just told it to go down 2 and its on 3 that is ok but if we then also tell it to go
		 * down 2 more, it cant do that (figure out, if on level 4 and want to go down 5, dont go at all (exception) or go as much as
		 * possible (3 - to level 1) (will levels be 1 or 0 based... :( )
		 */
		if (currentAction == Action.DONE)
		{
			if (level >= softLevelTop - 1)
			{
				goToFullPosition(); // TODO THIS IS PROBABLY A REALLY BAD IDEA
				return;
			}
			goToLevel(this.level + n);
		}
	}

	private void init()
	{
		liftP = SmartDashboard.getNumber("Lift P: ");
		liftI = SmartDashboard.getNumber("Lift I: ");
		liftD = SmartDashboard.getNumber("Lift D: ");
		if (!calibrated)
			calibrated = calibrate();
		SmartDashboard.putBoolean("Calibrated: ", calibrated);

		// level = calculateLevel(getEncoderPosition());
		// goToLevel(level); TODO This is a problem because it just stays at start position
	}

	public boolean isMoving()
	{
		if (currentAction != Action.DONE)
			return true;
		return false;
	}

        public void PIDTune()
        {

        }

	/**
	 * TODO Lift autotuned using the Zeigler-Nichols method; Calculate Kc by testing max p with i and d = 0 where stable oscilation
	 * Calculate max and min of oscilation and find the period between the max and min Kp = 0.6 Kc Ki = 2*Kp/Pc Kd = 0.125*Kp*Pc
	 */
	public void PIDTuneZeiglerNichols()
	{
		try
		{
			double p = 1.0, i = 0., d = 0.;
			boolean tuning = true;

			while (tuning)
			{
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.Position, p, i, d);
				System.out.println(TKOHardware.getLiftTalon().getP());
				System.out.println(TKOHardware.getLiftTalon().getI());
				System.out.println(TKOHardware.getLiftTalon().getD());
				TKOHardware.getLiftTalon().enableControl();

				// int pos = getEncoderPosition();
				double target = softTop - 10000;
				/*
				 * if (pos > target) currentAction = Action.DESCENDING; if (pos < target) currentAction = Action.ASCENDING;
				 */
				goToLevel((target - bottomOffset) / oneLevel);

				while (isMoving() && DriverStation.getInstance().isEnabled())
				{
					goToPosition(target);
				}

				Timer t = new Timer();
				t.start();
				double max = Double.MIN_VALUE;
				double min = Double.MAX_VALUE;
				double timeMax = 0;
				double timeMin = 0;
				while (t.get() < 5)
				{
					if (TKOHardware.getLiftTalon().getPosition() > max)
					{
						max = TKOHardware.getLiftTalon().getPosition();
						timeMax = t.get();
					}
					if (TKOHardware.getLiftTalon().getPosition() < min)
					{
						min = TKOHardware.getLiftTalon().getPosition();
						timeMin = t.get();
					}
				}
				double period = Math.abs(timeMax - timeMin);

				System.out.println("Tested p: " + p);
				System.out.println("Period of oscilation with current p: " + Math.abs(timeMax - timeMin));
				System.out.println("MAX: " + max);
				System.out.println("MIN: " + min);
				System.out.println("Suggested i: " + (2 * p / period));
				System.out.println("Suggested d: " + (2 * p / period));

				// tuning = false;

				p++;
				if (p > 20)
					tuning = false;
			}

		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void PIDTune()
	{
		try
		{
			/*
			 * if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.Position) { //
			 * TKOHardware.getLiftTalon().changeControlMode(CANTalon.ControlMode.Position);
			 * TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.Position, liftP, liftI, liftD);
			 * TKOHardware.getLiftTalon().enableControl(); }
			 */

			double p = 0.1, i = 0., d = 0.;
			boolean tuning = true;

			while (tuning)
			{
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.Position, p, i, d);
				System.out.println(TKOHardware.getLiftTalon().getP());
				System.out.println(TKOHardware.getLiftTalon().getI());
				System.out.println(TKOHardware.getLiftTalon().getD());
				TKOHardware.getLiftTalon().enableControl();

				// int pos = getEncoderPosition();
				double target = softTop - 10000;
				/*
				 * if (pos > target) currentAction = Action.DESCENDING; if (pos < target) currentAction = Action.ASCENDING;
				 */
				goToLevel((target - bottomOffset) / oneLevel);

				while (isMoving() && DriverStation.getInstance().isEnabled())
				{
					goToPosition(target);
				}

				target = softBottom + 10000;
				/*
				 * if (pos < target) currentAction = Action.ASCENDING; if (pos > target) currentAction = Action.DESCENDING;
				 */

				goToLevel((target - bottomOffset) / oneLevel);

				while (isMoving() && DriverStation.getInstance().isEnabled())
				{
					goToPosition(target);
				}

				i += 0.01;
				if (i > .1)
				{
					i = 0.;
					p += 1.;
					if (p > 10.)
						tuning = false;
				}
			}
		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The run method is what the thread actually calls once. The continual running of the thread loop is done by the while loop, controlled
	 * by a safe boolean inside the TKOThread object. The wait is synchronized to make sure the thread safely sleeps.
	 */
	@Override
	/*
	 * TODO We need to make it so if we were doing an Operation.CUSTOM_POSITION action before we do goToLevel, we recalculate the level we
	 * are on. (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		try
		{
			SmartDashboard.putNumber("Current level: ", getCurrentLevel());
			SmartDashboard.putNumber("Target level: ", getTargetLevel());
			SmartDashboard.putNumber("Encoder pos: ", getEncoderPosition());

			boolean PIDTuning = false;
			while (conveyorThread.isThreadRunning())
			{
				if (PIDTuning)
				{
					PIDTune();
					return;
				}
				if (!calibrated)
				{
					calibrated = calibrate();
					continue;
				}
				if (manualEnabled)
				{
					/*
					 * If you are at the top then press goToTrashcanPickup, then press goDown, it does to level 2 (from theoretical level 3)
					 */
					if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(4))
					{
						goDown();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(5))
					{
						goUp();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(8))
					{
						goToTrashcanPickup();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(9))
					{
						goToFullPosition();
					}
					else if (!requestAutoPickup && TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getTrigger())
					{
						requestAutoPickup = true;
						timeOfStartAutoPickup = System.currentTimeMillis();
//						if (TKOHardware.cratePresent())
//							goUp();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(7))
					{
						goDownSmallAmount();
						// if (!TKOHardware.cratePresent())
						//goToDropCrates();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(6))
					{
						goUpSmallAmount();
						//operation = Operation.MANUAL_VBUS;
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(10))
					{
						goToDropCratesBasedOnLevel();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK - 1).getRawButton(10))
					{
						goToStepPlaceLevel();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK - 1).getRawButton(9))
					{
						goToForceTrashcanDown();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK - 1).getTrigger())
					{
						if (Math.abs(TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getY()) > 0.05)
						{
							double tarLevel = level + TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getY()
									* liftManualIncrementer;
							if (tarLevel > softLevelTop)
							{
								if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getY() > 0)
									tarLevel = softLevelTop;
								else
									tarLevel = softLevelTop + TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getY()
											* liftManualIncrementer;
							}
							if (tarLevel < softLevelBot)
							{
								if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getY() < 0)
									tarLevel = softLevelBot;
								else
									tarLevel = softLevelBot + TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getY()
											* liftManualIncrementer;
							}
							goToLevel(tarLevel);
						}
					}
				}
				
				if (requestAutoPickup)
				{
					if (!TKOHardware.cratePresent())
					{
						requestAutoPickup = false;
						timeOfStartAutoPickup = 0;
					}
					else
					{
						if (System.currentTimeMillis() - timeOfStartAutoPickup > 50)
							goUp();
					}
				}

//				if (!calibrated)
//					operation = Operation.MANUAL_VBUS;

				printMessages();
				validate(); // TODO have validate throw and if catch, continue

				if (operation == Operation.PID_CRATES)
					updateCrateLevelTarget();
				else if (operation == Operation.MANUAL_VBUS)
					completeManualJoystickControl();

				synchronized (conveyorThread) // synchronized per the thread to make sure that we wait safely
				{
					conveyorThread.wait(liftThreadSleep); // the wait time that the thread sleeps, in milliseconds
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof TKORuntimeException)
			{
				System.out.println("SHUTTING DOWN LIFT, RUNTIME EXCEPTION");
				stop();
			}
			e.printStackTrace();
		}
	}

	private synchronized void printMessages()
	{
		// System.out.println("CurAct: " + currentAction);
		// System.out.println("CurrentOp: " + operation);
		// System.out.println("Level: " + level);
		// try
		// {
		// System.out.println("CRATE: " + TKOHardware.getCrateDistance());
		// System.out.println("CRATE TF: " + TKOHardware.cratePresent());
		// }
		// catch (TKOException e)
		// {
		// e.printStackTrace();
		// }
		// // System.out.println("Lift talon set to: " + currentPIDSetpoint);
		//
//		try
//		{
//			System.out.println("Lift Position: " + TKOHardware.getLiftTalon().getPosition());
//			// System.out.println("Current Level: " + getCurrentLevel());
//			System.out.println("PID ERROR?: " + TKOHardware.getLiftTalon().getClosedLoopError());
//			// System.out.println();
//		}
//		catch (TKOException e)
//		{
//			e.printStackTrace();
//		}
	}

	public void setStartPosition()
	{
		// currentPIDSetpoint = startLevel * oneLevel + bottomOffset;
		goToLevel(startLevel);
	}

	private void shutdown()
	{
		try
		{
			if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.Position)
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.Position, liftP, liftI, liftD);

			TKOHardware.getLiftTalon().set(TKOHardware.getLiftTalon().getPosition());
			// sets talon target to current position? figure out what to do on restart
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * The {@code start} method starts the thread, making it call the run method (only once) but can do this for threads in different
	 * classes in parallel. The {@code isThreadRunning} method checks with a boolean whether the thread is running. We only start the thread
	 * if it is not. The {@code setThreadRunning} method sets the boolean to true, and the {@code start} method starts the Thread. We use
	 * the {@code isThreadRunning} in the run function to verify whether our thread should be running or not, to make a safe way to stop the
	 * thread. This function is completely thread safe.
	 */
	public void start()
	{
		if (!conveyorThread.isAlive() && m_Instance != null)
		{
			conveyorThread = new TKOThread(m_Instance);
			conveyorThread.setPriority(Definitions.getPriority("conveyor"));
		}
		init();
		if (!conveyorThread.isThreadRunning())
		{
			conveyorThread.setThreadRunning(true);
		}
	}

	/**
	 * The {@code stop} method disables the thread, simply by setting the {@code isThreadRunning} to false via {@code setThreadRunning} and
	 * waits for the method to stop running (on the next iteration of run).
	 */
	public void stop()
	{
		if (conveyorThread.isThreadRunning())
		{
			conveyorThread.setThreadRunning(false);
		}
		shutdown();
	}

	/**
	 * So this function should use the level * oneLevel variable (which equals the encoder position of the level we SHOULD be at) and checks
	 * whether we have reached that setpoint or not. If we have not, keep incrementing; if we have, do not increment and wait for motor to
	 * actually reach the setpoint.
	 */

	public synchronized void updateCrateLevelTarget()
	{
		double target = oneLevel * level + bottomOffset;
		goToPosition(target); // TODO is it bad we don't check currentAction here?
	}

	private void validate() throws TKOException // TODO Test
	{
		TKOHardware.getLiftTalon().enableForwardSoftLimit(false);
		TKOHardware.getLiftTalon().enableReverseSoftLimit(false);
		//TKOHardware.getLiftTalon().setForwardSoftLimit(-500);
		//TKOHardware.getLiftTalon().setReverseSoftLimit(20000); // TODO use these
		//TKOHardware.getLiftTalon().pidWrite(0);
		if (TKOHardware.getJoystick(1).getRawButton(9))
		{
			TKOHardware.getLiftTalon().disableControl();
			Timer.delay(2.5);
			TKOHardware.getLiftTalon().enableControl();
		}
//		System.out.println("Lift Top: " + (TKOHardware.getLiftTop()));
//		System.out.println("Lift Bottom: " + (TKOHardware.getLiftBottom()));

//		if (level < minLevel || level > maxLevel)
//		{
//			// throw new TKORuntimeException("CRITICAL ERROR LEVEL OUT OF BOUNDS HOW IS THIS EVEN POSSIBLE?");
//		}
		int error = TKOHardware.getLiftTalon().getClosedLoopError();
		if (error > ((Definitions.LIFT_PID_INCREMENTER) + encoderThreshold))
		{
			//System.out.println("PROBLEM WHY IS OUR CLOSED LOOP ERROR LARGER THAN IT SHOULD BE: " + error);
			// throw new TKORuntimeException("PROBLEM WHY IS OUR CLOSED LOOP ERROR LARGER THAN IT SHOULD BE: " + error);
		}
		// check action out of bounds
		// check timeouts?
		// check current?
	}

}
