package org.usfirst.frc.team1351.drive;

import org.usfirst.frc.team1351.logger.TKOLogger;
import org.usfirst.frc.team1351.robot.Definitions;
import org.usfirst.frc.team1351.util.TKOHardware;
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

	public static void tankDrive()
	{
		for (int i = 1; i <= Definitions.NUM_DRIVE_JAGS; i++)
		{
			try
			{
				//TODO multiplier negative positive for each motor
				TKOHardware.getDriveJaguar(i).set(TKOHardware.getJoystick(i).getY());
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
