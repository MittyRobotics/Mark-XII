package org.usfirst.frc.team1351.drive;

import org.usfirst.frc.team1351.util.TKOThread;

public class TKODrive implements Runnable
{
	private static TKOThread driveThread = new TKOThread(new TKODrive());

	protected TKODrive()
	{

	}

	public static void start()
	{
		System.out.println("Starting drive task");
		if (!driveThread.isThreadRunning())
		{
			driveThread.setThreadRunning(true);
			driveThread.start();
		}
		System.out.println("Started drive task");
	}

	public static void stop()
	{
		System.out.println("Stopping drive task");
		if (driveThread.isThreadRunning())
		{
			driveThread.setThreadRunning(false);
			try
			{
				driveThread.join();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Stopped drive task");
	}

	public static void tankDrive()
	{

	}

	@Override
	public void run()
	{
		try
		{
			while (driveThread.isThreadRunning())
			{
				System.out.println("THREAD RAN!");
				synchronized (driveThread)
				{
					driveThread.wait(100);
				}
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
