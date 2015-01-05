package org.usfirst.frc.team1351.util;

import org.usfirst.frc.team1351.logger.TKOLogger;
import org.usfirst.frc.team1351.util.TKOThread;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Data collection for everything
 * 
 * @author Vadim
 */
public class TKODataReporting implements Runnable // implements Runnable is important to make this class support the Thread (run method)
{
	/*
	 * This creates an object of the TKOThread class, passing it the runnable of this class (ThreadExample) TKOThread is just a thread that
	 * makes it easy to make using the thread safe
	 */
	private static TKOThread exampleThread = new TKOThread(new TKODataReporting());
	private static PowerDistributionPanel pdp = new PowerDistributionPanel();

	// Typical constructor made protected so that this class is only accessed statically, though that doesnt matter
	protected TKODataReporting()
	{

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
	public static void start()
	{
		if (!exampleThread.isThreadRunning())
		{
			exampleThread.setThreadRunning(true);
			exampleThread.start();
		}
	}

	/**
	 * The {@code stop} method disables the thread, simply by setting the {@code isThreadRunning} to false via {@code setThreadRunning} and
	 * waits for the method to stop running (on the next iteration of run).
	 */
	// TODO Make sure that using join is a good idea
	public static void stop()
	{
		if (exampleThread.isThreadRunning())
		{
			exampleThread.setThreadRunning(false);
			try
			{
				exampleThread.join();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * The run method is what the thread actually calls once. The continual running of the thread loop is done by the while loop, controlled
	 * by a safe boolean inside the TKOThread object. The wait is synchronized to make sure the thread safely sleeps.
	 */
	// TODO Make sure that wait is what we want to use (and that this will work with multiple instances of TKOThread across code, not sleep
	@Override
	public void run()
	{
		try
		{
			while (exampleThread.isThreadRunning())
			{
				System.out.println("THREAD RAN!");
				record();
				synchronized (exampleThread) // synchronized per the thread to make sure that we wait safely
				{
					exampleThread.wait(500); // the wait time that the thread sleeps, in milliseconds
				}
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public static void record()
	{
		TKOLogger.addMessage("Current:" + pdp.getTotalCurrent());
	}
}
