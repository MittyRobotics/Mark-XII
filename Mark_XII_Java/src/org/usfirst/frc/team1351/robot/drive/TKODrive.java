package org.usfirst.frc.team1351.robot.drive;

import org.usfirst.frc.team1351.robot.util.*;
import org.usfirst.frc.team1351.robot.logger.*;
import org.usfirst.frc.team1351.robot.main.*;

public class TKODrive implements Runnable
{
	public TKOThread driveThread = null;
	private static TKODrive m_Instance = null;

	protected TKODrive()
	{
		
	}
	
	public static synchronized TKODrive getInstance()
	{
		if (TKODrive.m_Instance == null)
		{
			m_Instance = new TKODrive();
			m_Instance.driveThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	public void start()
	{
		System.out.println("Starting drive task");
		if (!driveThread.isAlive() && m_Instance != null)
			driveThread = new TKOThread(m_Instance);
		if (!driveThread.isThreadRunning())
			driveThread.setThreadRunning(true);

		System.out.println("Started drive task");
	}

	public void stop()
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
				TKOLogger.getInstance().addMessage("ERROR IN TANK DRIVE CAUGHT! " + e.getMessage());
				TKODrive.getInstance().stop();
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
