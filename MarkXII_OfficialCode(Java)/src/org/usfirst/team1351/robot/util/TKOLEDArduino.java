package org.usfirst.team1351.robot.util;

import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.Joystick.RumbleType;

/**
 * This is an example of how to make a class that runs as a thread. The most important reason for making TKOThread was to make the thread
 * implementation thread-safe everywhere, meaning that if we happened to use two threads to do the same thing to an object, we would not
 * have memory corruption / other problems.
 * 
 * @author Vadim
 */
public class TKOLEDArduino implements Runnable // implements Runnable is important to make this class support the Thread (run method)
{
	/*
	 * This creates an object of the TKOThread class, passing it the runnable of this class (ThreadExample) TKOThread is just a thread that
	 * makes it easy to make using the thread safe
	 */
	public TKOThread ledArduinoThread = null;
	private static TKOLEDArduino m_Instance = null;

	// private Random r = new Random();

	// Typical constructor made protected so that this class is only accessed statically, though that doesnt matter
	protected TKOLEDArduino()
	{

	}

	/**
	 * This function makes the class a singleton, so that there can only be one instance of the class even though the class is not static
	 * This is needed for the Thread to work properly.
	 */
	public static synchronized TKOLEDArduino getInstance()
	{
		if (TKOLEDArduino.m_Instance == null)
		{
			m_Instance = new TKOLEDArduino();
			m_Instance.ledArduinoThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	/**
	 * The {@code start} method starts the thread, making it call the run method (only once) but can do this for threads in different
	 * classes in parallel. The {@code isThreadRunning} method checks with a boolean whether the thread is running. We only start the thread
	 * if it is not. The {@code setThreadRunning} method sets the boolean to true, and the {@code start} method starts the Thread. We use
	 * the {@code isThreadRunning} in the run function to verify whether our thread should be running or not, to make a safe way to stop the
	 * thread. This function is completely thread safe.
	 * 
	 * @category
	 
	 
	 
	 */
	public void start()
	{
		if (!ledArduinoThread.isAlive() && m_Instance != null)
		{
			ledArduinoThread = new TKOThread(m_Instance);
			ledArduinoThread.setPriority(Definitions.getPriority("ledArduino"));
		}
		if (!ledArduinoThread.isThreadRunning())
		{
			ledArduinoThread.setThreadRunning(true);
		}
	}

	/**
	 * The {@code stop} method disables the thread, simply by setting the {@code isThreadRunning} to false via {@code setThreadRunning} and
	 * waits for the method to stop running (on the next iteration of run).
	 */
	public void stop()
	{
		if (ledArduinoThread.isThreadRunning())
		{
			ledArduinoThread.setThreadRunning(false);
		}
	}

	public boolean color()
	{
		try
		{
			boolean inRange = false;
			if (TKOHardware.getCrateDistance() < Definitions.TRASHCAN_POSITIONING_MAX
					&& TKOHardware.getCrateDistance() > Definitions.TRASHCAN_POSITIONING_MIN)
				inRange = true;

			if (inRange)
			{
				//System.out.println("WITHIN RANGE");
				TKOHardware.arduinoWrite(4.99);
				//THIS IS THE WORST IMPLEMENTATION EVARRR BUT WHATEVS AM I RITE? 
				//THIS IS RUMBLE BRUHS!!!
				TKOHardware.getJoystick(0).setRumble(RumbleType.kLeftRumble, 50);
				TKOHardware.getJoystick(0).setRumble(RumbleType.kRightRumble, 50);
				return true;
			} 
			else if (TKOHardware.cratePresent())
			{
				TKOHardware.arduinoWrite(2.5);
				//THIS IS RUMBLE BRUHS!!!
				TKOHardware.getJoystick(0).setRumble(RumbleType.kLeftRumble, 0);
				TKOHardware.getJoystick(0).setRumble(RumbleType.kRightRumble, 0);
				return true;
			}
			else
			{
				//System.out.println("Outside range");
				TKOHardware.arduinoWrite(1.);
				//THIS IS RUMBLE BRUHS!!!
				TKOHardware.getJoystick(0).setRumble(RumbleType.kLeftRumble, 0);
				TKOHardware.getJoystick(0).setRumble(RumbleType.kRightRumble, 0);
			}
		} catch (TKOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void colorBasedOnLevel()
	{
		try
		{
			double range = TKOLift.softLevelTop - TKOLift.softLevelBot;
			TKOHardware.arduinoWrite(TKOLift.getInstance().getCurrentLevel() / range * 4. + 0.5);

		} catch (TKOException e)
		{
			e.printStackTrace();
		}
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
			while (ledArduinoThread.isThreadRunning())
			{
				synchronized (ledArduinoThread) // synchronized per the thread to make sure that we wait safely
				{
					// if (!colorForTrashcanOnLip())
					// colorBasedOnLevel();
					color();
					// if(TKOHardware.getJoystick(0).getRawButton(3))
					// TKOHardware.arduinoWrite(4.);
					ledArduinoThread.wait(20); // the wait time that the thread sleeps, in milliseconds
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
