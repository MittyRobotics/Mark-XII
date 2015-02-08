package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKORuntimeException;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

/**
 * @author Vadim
 * @version 01/31/15
 * 
 *          TODO We may need to figure out how to shutdown properly TODO What happens if disabled while calibrating
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
	private int currentPIDSetpoint;
	
	private int operation = 0; //0 is pid, 1 is manual

	private static final int oneLevel = 4750; // TODO tick increments or quantifiable units?
	private static final int minLevel = 0; // zero based
	private static final int maxLevel = 3; // 4th crate
	private static final int startLevel = 0;
	private static final int bottomOffset = 4515;
	private static final int softBottomOffset = 250; //safety offset
	private static final int softTopOffset = 250; //safety offset

	// Typical constructor made protected so that this class is only accessed statically (via getInstance), though that doesnt matter
	protected TKOLift()
	{
		level = -1;
		currentAction = Action.DONE;
		currentPIDSetpoint = 0;
		calibrated = false;
		softBottom = 0.;
		softTop = 0.;
	}

	private int calculateLevel(int encPosition)
	{
		return Math.floorDiv(encPosition - bottomOffset, oneLevel);
	}

	private int calculateLevel()
	{
		int level = minLevel;
		try
		{
			level = Math.floorDiv(TKOHardware.getLiftTalon().getEncPosition() - bottomOffset, oneLevel);
		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return level;
	}

	public boolean calibrate()
	{
		try
		{
			//if (operation == 1)
			//	return true;
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
			// System.out.println("Manual joystick lift drive");
			if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.PercentVbus)
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.PercentVbus, Definitions.LIFT_P,
						Definitions.LIFT_I, Definitions.LIFT_D);

			TKOHardware.getLiftTalon().enableLimitSwitch(true, true);
			// TKOHardware.getLiftTalon().setForwardSoftLimit(forwardLimit);
			// TKOHardware.getLiftTalon().setReverseSoftLimit(reverseLimit); TODO use these

			TKOHardware.getLiftTalon().set(TKOHardware.getJoystick(3).getY() * 0.4);
		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public int getCurrentLevel()
	{
		return calculateLevel(getEncoderPosition());
	}

	public int getTargetLevel()
	{
		return level;
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

	public void goToPosition(double position)
	{
		try
		{
			if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.Position)
			{
				// TKOHardware.getLiftTalon().changeControlMode(CANTalon.ControlMode.Position);
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.Position, Definitions.LIFT_P,
						Definitions.LIFT_I, Definitions.LIFT_D);
				TKOHardware.getLiftTalon().enableControl();
				System.out.println("!!!!CHANGED LIFT TALON MODE");
			}

			if (currentAction == Action.ASCENDING) // while ascending and not above the target
			{
				if (currentPIDSetpoint >= position)
				{
					if (getEncoderPosition() >= position)
					{
						currentAction = Action.DONE;
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
					throw new TKORuntimeException("TRYING TO DRIVE LIFT BEYOND MAX");
				}
			}
			else if (currentAction == Action.DESCENDING)
			{
				if (currentPIDSetpoint <= position)
				{
					if (getEncoderPosition() <= position)
					{
						currentAction = Action.DONE;
						// done ascending
					}
					// setpoint above target but we still havent reached the position with the motor
				}
				else if (currentPIDSetpoint >= getSoftBottom())
				{
					currentPIDSetpoint -= Definitions.LIFT_PID_INCREMENTER;
				}
				else
				{
					currentPIDSetpoint = (int) getSoftBottom();
					// throw new TKORuntimeException("TRYING TO DRIVE LIFT BEYOND MIN");
				}
			}
			TKOHardware.getLiftTalon().set(currentPIDSetpoint);
			System.out.println("CurAct: " + currentAction);
			System.out.println("Level: " + level);
			//System.out.println("Lift talon set to: " + currentPIDSetpoint);
			//System.out.println("PID ERROR?: " + TKOHardware.getLiftTalon().getClosedLoopError());
		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// since we are using an incrementer we need to keep increasing the current setpoint until we reach target setpoint
	}

	public synchronized void goToLevel(int level)
	{
		System.out.println("GOING TO LEVEL " + level);
		currentAction = Action.THINKING;
		if (level < minLevel)
		{
			TKOLogger.getInstance().addMessage("ERROR LIFT REQUESTED TO GO BELOW MINIMUM LEVEL");
			currentAction = Action.DONE;
			return;
		}
		if (level > maxLevel)
		{
			TKOLogger.getInstance().addMessage("ERROR LIFT REQUESTED TO GO BELOW MAXIMUM LEVEL");
			currentAction = Action.DONE;
			return;
		}
		if (level == this.level)
		{
			currentAction = Action.DONE;
			return;
		}

		this.level = level;
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

	public void goDown()
	{
		if (currentAction == Action.DONE) //TODO bad idea?
			goDown(1);
	}

	public void goDown(int n)
	{
		goToLevel(this.level - n);
	}

	public void goUp()
	{
		if (currentAction == Action.DONE) //TODO bad idea?
			goUp(1);
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
		goToLevel(this.level + n);
	}

	private void init()
	{
		if (!calibrated)
			calibrated = calibrate();
		if (level == -1) // TODO do we need to update level on enable/start?
		{
			level = calculateLevel(getEncoderPosition());
			goToLevel(minLevel);
		}
	}

	public boolean isMoving()
	{
		if (currentAction == Action.DONE)
			return true;
		return false;
	}

	/**
	 * The run method is what the thread actually calls once. The continual running of the thread loop is done by the while loop, controlled
	 * by a safe boolean inside the TKOThread object. The wait is synchronized to make sure the thread safely sleeps.
	 */
	@Override
	public void run()
	{
		try
		{
			// run calibrate once before the while loop?

			while (conveyorThread.isThreadRunning())
			{
				// TODO while we have to go up a level, don't let user go up again?
				//System.out.println("Lift Position: " + TKOHardware.getLiftTalon().getPosition());
				// System.out.println("Crate: " + TKOHardware.getCrateDistance());
				
				if (TKOHardware.getJoystick(3).getRawButton(4))
				{
					operation = 0;
					goDown();
				}
				else if (TKOHardware.getJoystick(3).getRawButton(5))
				{
					operation = 0;
					goUp();
				}
				else if (TKOHardware.getJoystick(3).getRawButton(8) || operation == 2)
				{
					System.out.println("TRASCAN: " + operation);
					operation = 2;
					if (getEncoderPosition() > softBottom + 10)
						currentAction = Action.DESCENDING;
					else
						currentAction = Action.DONE;
					
					goToPosition(softBottom);
				}
				
				
				validate();
				if (operation == 0)
					updateTarget();
				else if (operation == 1) //TODO wherever you switch the operation, when going back to PID mode, make sure to reset target 
					completeManualJoystickControl();

				synchronized (conveyorThread) // synchronized per the thread to make sure that we wait safely
				{
					conveyorThread.wait(20); // the wait time that the thread sleeps, in milliseconds TODO figure out what this should be
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setStartPosition()
	{
		try
		{
			currentPIDSetpoint = (int) TKOHardware.getLiftTalon().getPosition();
		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		goToLevel(startLevel);
		// TODO reset to start position?
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

	public synchronized void updateTarget()
	{
		double target = oneLevel * level + bottomOffset; // TODO Softcode this for any target position
		goToPosition(target);
	}

	private void validate()
	{
		if (level < minLevel || level > maxLevel)
		{
			this.stop();
			throw new TKORuntimeException("CRITICAL ERROR LEVEL OUT OF BOUNDS HOW IS THIS EVEN POSSIBLE?");
		}
		// check action out of bounds
	}

}
