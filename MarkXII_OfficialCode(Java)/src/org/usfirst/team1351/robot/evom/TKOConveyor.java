package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOThread;

/**
 * @author Vadim
 * @version 01/21/15
 */
public class TKOConveyor implements Runnable // implements Runnable is important to make this class support the Thread (run method)
{
	public TKOThread conveyorThread = null;
	private static TKOConveyor m_Instance = null;

	// Typical constructor made protected so that this class is only accessed statically, though that doesnt matter
	protected TKOConveyor()
	{
		
	}

	/**
	 * This function makes the class a singleton, so that there can only be one instance of the class even though the class is not static
	 * This is needed for the Thread to work properly.
	 */
	public static synchronized TKOConveyor getInstance()
	{
		if (m_Instance == null)
		{
			m_Instance = new TKOConveyor();
			m_Instance.conveyorThread = new TKOThread(m_Instance);
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
		if (!conveyorThread.isAlive() && m_Instance != null)
		{
			conveyorThread = new TKOThread(m_Instance);
			conveyorThread.setPriority(Definitions.getPriority("conveyor"));
		}
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
			while (conveyorThread.isThreadRunning())
			{
				System.out.println("THREAD RAN!");
				/*
				 * THIS IS WHERE YOU PUT ALL OF YOUR CODEZ
				 * Some way to manage our current position (both in encoder units and in levels) and maintain our target position
				 * with PID ramping incremented in this loop
				 * 
				 */
				synchronized (conveyorThread) // synchronized per the thread to make sure that we wait safely
				{
					conveyorThread.wait(100); // the wait time that the thread sleeps, in milliseconds
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void calibrate()
	{
		//stores constants somewhere in this class about these values?
	}
	
	public void goUp()
	{
		goUp(1);
	}
	
	public void goUp(int n)
	{
		/*
		 * go up n levels.
		 * im thinking this function should return right away and
		 * everything actually runs in the thread run loop, not in this function
		 * eg this function only sets the target or something
		 * 
		 * figure out what has to be synchronized
		 * most important is that you cant spam this and that it has to take into account
		 * how much we can go and how much more we can go
		 * eg if we just told it to go down 2 and its on 3 that is ok
		 * but if we then also tell it to go down 2 more, it cant do that 
		 * (figure out, if on level 4 and want to go down 5, dont go at all (exception) or go as much as possible (3 - to level 1)
		 * (will levels be 1 or 0 based... :(   )
		*/	
	}
	
	
	public void goDown()
	{
		goDown(1);
	}
	
	public void goDown(int n)
	{
		
	}
	
	public boolean isMoving()
	{
		return false;
	}
	
	public double getTarget()
	{
		return 0.;
	}
	
	public double getPosition()
	{
		return 0.;
	}
	
}
