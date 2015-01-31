package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKORuntimeException;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.CANTalon;

/**
 * @author Vadim
 * @version 01/31/15
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
	private int targetLevel;
	private int currentAction;
	private boolean calibrated;
	private double softBottom, softTop;
	private int currentPIDSetpoint;

	private static final int oneLevel = 100; // TODO tick increments or quantifiable units?
	private static final int minLevel = 0; // zero based
	private static final int maxLevel = 4; // 5th crate

	// Typical constructor made protected so that this class is only accessed statically, though that doesnt matter
	protected TKOLift()
	{
		level = -1;
		targetLevel = -1;
		currentAction = -1;
		currentPIDSetpoint = 0;
		calibrated = false;
		softBottom = 0.;
		softTop = 0.;
	}

	private int calculateLevel(int encPosition)
	{
		return Math.floorDiv(encPosition, oneLevel);
	}

	public boolean calibrate()
	{
		try
		{
			CANTalon lmotor = TKOHardware.getLiftTalon();
			currentPIDSetpoint = lmotor.getEncPosition();
			TKOHardware.changeTalonMode(lmotor, CANTalon.ControlMode.PercentVbus, Definitions.LIFT_P, Definitions.LIFT_I, Definitions.LIFT_D);

			while (!TKOHardware.getLiftTop())
			{
				lmotor.set(Definitions.LIFT_CALIBRATION_POWER);
			}
			lmotor.set(0);
			softTop = lmotor.getPosition();

			while (!TKOHardware.getLiftTop())
			{
				lmotor.set(-Definitions.LIFT_CALIBRATION_POWER);
			}
			lmotor.set(0);
			softBottom = lmotor.getPosition();
			
			TKOHardware.changeTalonMode(lmotor, CANTalon.ControlMode.Position, Definitions.LIFT_P, Definitions.LIFT_I, Definitions.LIFT_D);
			setStartPosition();
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
		// stores constants somewhere in this class about these values of top and bottom??
		return true;
	}

	private void completeManualJoystickControl()
	{
		try
		{
			if (TKOHardware.getLiftTalon().getControlMode() != CANTalon.ControlMode.PercentVbus)
				TKOHardware.changeTalonMode(TKOHardware.getLiftTalon(), CANTalon.ControlMode.PercentVbus, Definitions.LIFT_P, Definitions.LIFT_I, Definitions.LIFT_D);

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

	public double getPosition()
	{
		return 0.;
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

	public void goDown()
	{
		goDown(1);
	}

	public void goDown(int n)
	{

	}

	public void goUp()
	{
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
	}

	private void init()
	{
		if (!calibrated)
			calibrated = calibrate();
		if (level == -1) // TODO do we need to update level on enable/start?
		{
			calculateLevel(getEncoderPosition());
		}
		if (targetLevel == -1)
			targetLevel = 0;
	}

	public boolean isMoving()
	{
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
				// TODO maybe need to keep a separate targetLevel variable
				// do we need to update level int first?
				validate();
				// updateTarget();
				completeManualJoystickControl();

				synchronized (conveyorThread) // synchronized per the thread to make sure that we wait safely
				{
					conveyorThread.wait(10); // the wait time that the thread sleeps, in milliseconds TODO figure out what this should be
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
		// TODO reset to start position?
	}

	private void shutdown()
	{
		// sets talon target to current position? figure out what to do on restart
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

	public synchronized void updateTarget()
	{
		// TODO check the lift talon mode?
		// TODO increment setpoint
		try
		{
			TKOHardware.getLiftTalon().set(currentPIDSetpoint);
		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// since we are using an incrementer we need to keep increasing the current setpoint until we reach target setpoint
	}

	private void validate()
	{
		if (level < minLevel || level > maxLevel)
		{
			throw new TKORuntimeException("CRITICAL ERROR LEVEL OUT OF BOUNDS HOW IS THIS EVEN POSSIBLE?");
		}
		// check action out of bounds
	}

}
