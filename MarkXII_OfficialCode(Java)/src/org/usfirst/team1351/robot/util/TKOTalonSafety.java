package org.usfirst.team1351.robot.util;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;

/**
 * This is an example of how to make a class that runs as a thread. The most important reason for making TKOThread was to make the thread
 * implementation thread-safe everywhere, meaning that if we happened to use two threads to do the same thing to an object, we would not
 * have memory corruption / other problems.
 * 
 * @author Vadim
 * @version 01/17/15
 */
public class TKOTalonSafety implements Runnable // implements Runnable is important to make this class support the Thread (run method)
{
	/*
	 * This creates an object of the TKOThread class, passing it the runnable of this class (ThreadExample) TKOThread is just a thread that
	 * makes it easy to make using the thread safe
	 */
	public TKOThread safetyCheckerThread = null;
	private static TKOTalonSafety m_Instance = null;

	// Typical constructor made protected so that this class is only accessed statically, though that doesnt matter
	protected TKOTalonSafety()
	{

	}

	/**
	 * This function makes the class a singleton, so that there can only be one instance of the class even though the class is not static
	 * This is needed for the Thread to work properly.
	 */
	public static synchronized TKOTalonSafety getInstance()
	{
		if (m_Instance == null)
		{
			m_Instance = new TKOTalonSafety();
			m_Instance.safetyCheckerThread = new TKOThread(m_Instance);
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
		if (!safetyCheckerThread.isAlive() && m_Instance != null)
		{
			safetyCheckerThread = new TKOThread(m_Instance);
			safetyCheckerThread.setPriority(Definitions.getPriority("talonSafety"));
		}
		if (!safetyCheckerThread.isThreadRunning())
		{
			safetyCheckerThread.setThreadRunning(true);
		}
	}

	/**
	 * The {@code stop} method disables the thread, simply by setting the {@code isThreadRunning} to false via {@code setThreadRunning} and
	 * waits for the method to stop running (on the next iteration of run).
	 */
	public void stop()
	{
		if (safetyCheckerThread.isThreadRunning())
		{
			safetyCheckerThread.setThreadRunning(false);
		}
	}

	public void checkCurrent()
	{
		try
		{
			for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i+=2)
			{
				double current = TKOHardware.getDriveTalon(i).getOutputCurrent();
				if (current > Definitions.TALON_CURRENT_TIMEOUT[i])
				{
					TKOLogger.getInstance().addMessage("DRIVE TALON CURRENT EXCEPTION: " + current + " AMPS " + TKOHardware.getDriveTalon(i).getDeviceID() + " ID");
					System.out.println("DRIVE TALON CURRENT EXCEPTION: " + current);
					//TKOHardware.getDriveTalon(i).disableControl();
					Thread.sleep(Definitions.CURRENT_TIMEOUT_LENGTH[i]);
					//TKOHardware.getDriveTalon(i).enableControl();
				}
			}
			double current = TKOHardware.getLiftTalon().getOutputCurrent();
			if (current > Definitions.TALON_CURRENT_TIMEOUT[Definitions.NUM_DRIVE_TALONS])
			{
				TKOLogger.getInstance().addMessage("LIFT TALON CURRENT EXCEPTION: " + current + " AMPS " + TKOHardware.getLiftTalon().getDeviceID() + " ID");
				System.out.println("LIFT TALON CURRENT EXCEPTION: " + current);
				//TKOHardware.getLiftTalon().disableControl();
				Thread.sleep(Definitions.CURRENT_TIMEOUT_LENGTH[Definitions.NUM_DRIVE_TALONS]);
				//TKOHardware.getLiftTalon().enableControl();
			}
		} catch (Exception e)
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
			while (safetyCheckerThread.isThreadRunning())
			{
				checkCurrent();

				synchronized (safetyCheckerThread) // synchronized per the thread to make sure that we wait safely
				{
					safetyCheckerThread.wait(20); // the wait time that the thread sleeps, in milliseconds
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
