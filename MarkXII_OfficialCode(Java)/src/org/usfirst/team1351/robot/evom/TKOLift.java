package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKORuntimeException;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

/**
 * @author Vadim
 * @version 02/09/15
 * 
 *          TODO We may need to figure out how to shutdown properly 
 *           
 *          BUGS
 * 
 *          TODO Cannot go up with fourth crate on the ground, needs to go up about half a level from that point
 * 
 *          TODO Sometimes need to press go up to go down; sometimes pressing up or down does nothing
 * 
 *          TODO On lift start, calculateLevel sometimes doesnt work. Also fails after trashcan pickup, gets confused
 * 
 *          TODO From trashcan level - going to next level up, skips a level
 * 
 *          TODO Going down from level 3-2-0 skips a level
 *          
 *          TODO What happens if pressing goDown on trashcan level? Same with goToFullPosition() and goUp() 
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

	private int level;
	private Action currentAction;
	private boolean calibrated;
	private double softBottom, softTop;
	private int currentPIDSetpoint, customPositionTarget;

	private Operation operation = Operation.PID_CRATES;

	public static final short oneLevel = 4850; // TODO 4750 before
	public static final byte minLevel = 0; // zero based
	public static final byte maxLevel = 3; // 4th crate
	public static final byte startLevel = 0;
	public static final short bottomOffset = 4515;
	public static final short softBottomOffset = 0; // safety offset
	public static final short softTopOffset = 100; // safety offset
	public static final short encoderThreshold = 100;
	public static final short liftThreadSleep = 50; //used to be 20
	
	public static final short trashcanPickupPosition = softBottomOffset;
	public static final short fullOfCratesPosition = (short) (3.6 * oneLevel + bottomOffset);
	public static final short dropOffsetDistance = (short) (0.5 * oneLevel);

	private boolean manualEnabled = true;

	// Typical constructor made protected so that this class is only accessed statically (via getInstance), though that doesnt matter
	protected TKOLift()
	{
		level = -1;
		currentAction = Action.DONE;
		currentPIDSetpoint = 0;
		customPositionTarget = 0;
		calibrated = false;
		softBottom = 0.;
		softTop = 0.;
		manualEnabled = true;
	}

	private int calculateLevel()
	{
		int calculatedLevel = minLevel;
		try
		{
			calculatedLevel = Math.floorDiv(TKOHardware.getLiftTalon().getEncPosition() - bottomOffset, oneLevel);
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
		if (calculatedLevel < minLevel)
			return minLevel;
		if (calculatedLevel > maxLevel)
			return maxLevel;
		return calculatedLevel;
	}

	private int calculateLevel(int encPosition)
	{
		int calculatedLevel = Math.floorDiv(encPosition - bottomOffset, oneLevel);
		if (calculatedLevel < minLevel)
			return minLevel;
		if (calculatedLevel > maxLevel)
			return maxLevel;
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
			currentPIDSetpoint = lmotor.getEncPosition();
			TKOHardware.changeTalonMode(lmotor, CANTalon.ControlMode.PercentVbus, Definitions.LIFT_P, Definitions.LIFT_I,
					Definitions.LIFT_D);
			TKOHardware.getLiftTalon().reverseOutput(true);

			while (!TKOHardware.getLiftBottom() && DriverStation.getInstance().isEnabled())
			{
				lmotor.set(Definitions.LIFT_CALIBRATION_POWER);
			}
			if (!DriverStation.getInstance().isEnabled())
				return false;
			lmotor.set(0); // stop motor
			lmotor.setPosition(0); // reset encoder
			softBottom = lmotor.getPosition() + softBottomOffset;
			Timer.delay(.1);

			while (!TKOHardware.getLiftTop() && DriverStation.getInstance().isEnabled())
			{
				lmotor.set(-Definitions.LIFT_CALIBRATION_POWER);
			}
			if (!DriverStation.getInstance().isEnabled())
				return false;
			lmotor.set(0); // stop motor
			softTop = lmotor.getPosition() - softTopOffset;

			lmotor.setSafetyEnabled(false);
			TKOHardware.changeTalonMode(lmotor, CANTalon.ControlMode.Position, Definitions.LIFT_P, Definitions.LIFT_I, Definitions.LIFT_D);
			setStartPosition(); // goto starting place
			currentPIDSetpoint = lmotor.getEncPosition();
			customPositionTarget = lmotor.getEncPosition();
			System.out.println("DONE CALIBRATING");
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
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.PercentVbus, Definitions.LIFT_P,
						Definitions.LIFT_I, Definitions.LIFT_D);
			TKOHardware.getLiftTalon().set(
					TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getY() * Definitions.LIFT_CALIBRATION_POWER);
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
	}

	public int getCurrentLevel()
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

	public int getTargetLevel()
	{
		return level;
	}

	public void goDown()
	{
		if (currentAction == Action.DONE)
		{
			//System.out.println("SHOULD BE GOING DOWN");
			if (level == minLevel)
			{
				goToTrashcanPickup();
				return;
			}
			//System.out.println("GOING DOWN A LEVEL");
			goDown(1);
		}
	}

	public void goDown(int n)
	{
		if (currentAction == Action.DONE)
		{
			if (level == minLevel)
			{
				goToTrashcanPickup();
				return;
			}
			goToLevel(this.level - n);
		}
	}

	public void goToFullPosition()
	{
		if (currentAction == Action.DONE)
		{
			operation = Operation.CUSTOM_POSITION;
			customPositionTarget = fullOfCratesPosition;
		}
	}

	public synchronized void goToLevel(int newLevel)
	{
		System.out.println("GOING TO LEVEL " + newLevel);
		boolean usingDiff = false;
		int diff = 0;
		if (operation != Operation.PID_CRATES) // TODO TEST NEW CODE
		{
			diff = newLevel - this.level;
			usingDiff = true;
			this.level = calculateLevel();
		}
		operation = Operation.PID_CRATES;
		currentAction = Action.THINKING;
		if (newLevel < minLevel)
		{
			TKOLogger.getInstance().addMessage("ERROR LIFT REQUESTED TO GO BELOW MINIMUM LEVEL");
			currentAction = Action.DONE;
			return;
		}
		if (newLevel > maxLevel)
		{
			TKOLogger.getInstance().addMessage("ERROR LIFT REQUESTED TO GO BELOW MAXIMUM LEVEL");
			currentAction = Action.DONE;
			return;
		}
		if (newLevel == this.level)
		{
			currentAction = Action.DONE;
			return;
		}

		if (!usingDiff) // TODO TEST NEW CODE
			this.level = newLevel;
		else
			this.level -= diff;

		if (calculateLevel() > this.level)
			this.currentAction = Action.DESCENDING;
		else if (calculateLevel() < this.level)
			this.currentAction = Action.ASCENDING;
		else
		{
			System.out.println("WAT");
			currentAction = Action.DONE;
		}
	}

	/**
	 * IMPORTANT, This method assumes that you already have set the currentAction variable
	 * 
	 * @param position
	 */
	public synchronized void goToPosition(int position, Action _currentAction)
	{
		// System.out.println("Tar Pos: " + position + " CURRENT ACTION " + currentAction);
		try
		{
			if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.Position)
			{
				// TKOHardware.getLiftTalon().changeControlMode(CANTalon.ControlMode.Position);
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.Position, Definitions.LIFT_P,
						Definitions.LIFT_I, Definitions.LIFT_D);
				TKOHardware.getLiftTalon().enableControl();
			}

			if (_currentAction == Action.ASCENDING) // while ascending and not above the target
			{
				if (currentPIDSetpoint >= position)
				{
					if (getEncoderPosition() >= (position - encoderThreshold) && getEncoderPosition() <= (position + encoderThreshold))
					{
						_currentAction = Action.DONE;
						// done ascending
					}
					// setpoint above target but we still havent reached the position with the motor
				}
				else if (currentPIDSetpoint <= getSoftTop())
				{
					currentPIDSetpoint += Definitions.LIFT_PID_INCREMENTER;
				}
				else
				{
					currentPIDSetpoint = (int) getSoftTop();
					System.out.println("TRYING TO DRIVE LIFT BEYOND MAX");
					throw new TKORuntimeException("TRYING TO DRIVE LIFT BEYOND MAX");
				}
			}
			else if (_currentAction == Action.DESCENDING)
			{
				if (currentPIDSetpoint <= position)
				{
					if (getEncoderPosition() <= (position + encoderThreshold) && getEncoderPosition() >= (position - encoderThreshold))
					{
						_currentAction = Action.DONE;
						// done ascending
					}
					//System.out.println("WAITING FOR MOTOR " + getEncoderPosition());
					//System.out.println("TarPos: " + position);
					// setpoint above target but we still havent reached the position with the motor
				}
				else if (currentPIDSetpoint >= getSoftBottom())
				{
					currentPIDSetpoint -= Definitions.LIFT_PID_INCREMENTER;
				}
				else
				{
					currentPIDSetpoint = (int) getSoftBottom();
					System.out.println("TRYING TO DRIVE LIFT BEYOND MIN");
					// throw new TKORuntimeException("TRYING TO DRIVE LIFT BEYOND MIN");
				}
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
			operation = Operation.CUSTOM_POSITION;
			customPositionTarget = trashcanPickupPosition;
		}
	}

	public void goToDropCrates()
	{
		if (currentAction == Action.DONE)
		{
			System.out.println("DROPPING STACK TIME");
			operation = Operation.CUSTOM_POSITION;
			customPositionTarget = getEncoderPosition() - dropOffsetDistance;
		}
	}

	public void goUp()
	{
		if (currentAction == Action.DONE)
		{
			if (level == maxLevel)
			{
				goToFullPosition();
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
			if (level == maxLevel)
			{
				goToTrashcanPickup(); // TODO THIS IS PROBABLY A REALLY BAD IDEA
				return;
			}
			goToLevel(this.level + n);
		}
	}

	private void init()
	{
		if (!calibrated)
			calibrated = calibrate();

		// level = calculateLevel(getEncoderPosition());
		// goToLevel(level); TODO This is a problem because it just stays at start position
	}

	public boolean isMoving()
	{
		if (currentAction != Action.DONE)
			return true;
		return false;
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
			while (conveyorThread.isThreadRunning())
			{
				if (manualEnabled)
				{
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
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getTrigger())
					{
						if (TKOHardware.cratePresent())
							goUp();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(8))
					{
						if (!TKOHardware.cratePresent())
							goToDropCrates();
					}
					else if (TKOHardware.getJoystick(Definitions.LIFT_CONTROL_STICK).getRawButton(6))
					{
						operation = Operation.MANUAL_VBUS;
					}
				}

				if (!calibrated)
					operation = Operation.MANUAL_VBUS;

				// printMessages();
				validate(); // TODO have validate throw and if catch, continue

				if (operation == Operation.PID_CRATES)
					updateCrateLevelTarget();
				else if (operation == Operation.CUSTOM_POSITION)
					updateCustomPositionTarget();
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

	@SuppressWarnings("unused")
	private synchronized void printMessages()
	{
		System.out.println("CurAct: " + currentAction);
		System.out.println("CurrentOp: " + operation);
		System.out.println("Level: " + level);
		// System.out.println("Lift talon set to: " + currentPIDSetpoint);

		// try {
		// System.out.println("Lift Position: "
		// + TKOHardware.getLiftTalon().getPosition());
		// System.out.println("Crate: " + TKOHardware.getCrateDistance());
		// System.out.println("PID ERROR?: "
		// + TKOHardware.getLiftTalon().getClosedLoopError());
		// } catch (TKOException e) {
		// e.printStackTrace();
		// }
	}

	public void setStartPosition()
	{
		currentPIDSetpoint = startLevel * oneLevel + bottomOffset;
		customPositionTarget = startLevel * oneLevel + bottomOffset;
		goToLevel(startLevel);
	}

	private void shutdown()
	{
		try
		{
			if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.Position)
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.Position, Definitions.LIFT_P,
						Definitions.LIFT_I, Definitions.LIFT_D);

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
		int target = oneLevel * level + bottomOffset;
		goToPosition(target, currentAction); // TODO is it bad we don't check currentAction here?
	}

	public synchronized void updateCustomPositionTarget()
	{
		//System.out.println("UPDATE CUSTOM POSITION");
		currentAction = Action.THINKING;

		if (customPositionTarget < softBottom)
		{
			System.out.println("ERROR LIFT REQUESTED TO GO BELOW MINIMUM LEVEL");
			TKOLogger.getInstance().addMessage("ERROR LIFT REQUESTED TO GO BELOW MINIMUM LEVEL");
			currentAction = Action.DONE;
			return;
		}
		if (customPositionTarget > softTop)
		{
			System.out.println("ERROR LIFT REQUESTED TO GO BEYOND MAXIMUM LEVEL");
			TKOLogger.getInstance().addMessage("ERROR LIFT REQUESTED TO GO BEYOND MAXIMUM LEVEL");
			currentAction = Action.DONE;
			return;
		}
		if (customPositionTarget <= (getEncoderPosition() + encoderThreshold)
				&& customPositionTarget >= (getEncoderPosition() - encoderThreshold))
		{
			currentAction = Action.DONE;
			//System.out.println("CLOSE ENOUGH TO THE TARGET");
			return;
		}

		if (getEncoderPosition() > customPositionTarget)
			this.currentAction = Action.DESCENDING;
		else if (getEncoderPosition() < customPositionTarget)
			this.currentAction = Action.ASCENDING;
		else
		{
			System.out.println("Done? Though this should not really be the case?");
			currentAction = Action.DONE;
		}
//		System.out.println("CUSTOM POSITION: " + customPositionTarget);
//		System.out.println("ACTION: " + currentAction);
//		System.out.println("ENC: " + getEncoderPosition());
		goToPosition(customPositionTarget, currentAction);
	}

	private void validate() throws TKOException //TODO Test
	{
		// TKOHardware.getLiftTalon().enableLimitSwitch(true, true);
		// TKOHardware.getLiftTalon().setForwardSoftLimit(forwardLimit);
		// TKOHardware.getLiftTalon().setReverseSoftLimit(reverseLimit); TODO use these

		if (level < minLevel || level > maxLevel)
		{
			throw new TKORuntimeException("CRITICAL ERROR LEVEL OUT OF BOUNDS HOW IS THIS EVEN POSSIBLE?");
		}
		int error = TKOHardware.getLiftTalon().getClosedLoopError();
		if (error > (Definitions.LIFT_PID_INCREMENTER + encoderThreshold))
		{
			System.out.println("PROBLEM WHY IS OUR CLOSED LOOP ERROR LARGER THAN IT SHOULD BE: " + error);
			throw new TKORuntimeException("PROBLEM WHY IS OUR CLOSED LOOP ERROR LARGER THAN IT SHOULD BE: " + error);
		}
		// check action out of bounds
		// check timeouts?
		// check current?
	}

}