package org.usfirst.frc.team1351.robot.drive;

import org.usfirst.frc.team1351.robot.util.*;
import org.usfirst.frc.team1351.robot.logger.*;
import org.usfirst.frc.team1351.robot.main.*;

public class TKODrive implements Runnable
{
	public static TKOThread driveThread = new TKOThread(new TKODrive());

	protected TKODrive()
	{
		
	}

	public static void start()
	{
		System.out.println("Starting drive task");
		if (!driveThread.isThreadRunning())
			driveThread.setThreadRunning(true);

		System.out.println("Started drive task");
	}

	public static void stop()
	{
		System.out.println("Stopping drive task");
		if (driveThread.isThreadRunning())
			driveThread.setThreadRunning(false);
		System.out.println("Stopped drive task");
	}

	public static synchronized void tankDrive()
	{
		for (int i = 1; i <= Definitions.NUM_DRIVE_JAGS; i++)
		{
			try
			{
				TKOHardware.getDriveJaguar(i).set(TKOHardware.getJoystick((i/3) + 1).getY() * Definitions.DRIVE_MULTIPLIER[i]);
			} catch (Exception e)
			{
				e.printStackTrace();
				TKOLogger.addMessage("ERROR IN TANK DRIVE CAUGHT! " + e.getMessage());
				stop();
			}
		}
	}

	@Override
	public void run()
	{
		try
		{
			while (driveThread.isThreadRunning())
			{
				System.out.println("DRIVE THREAD RAN!");
				tankDrive();
				synchronized (driveThread)
				{
					driveThread.wait(5);
				}
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
