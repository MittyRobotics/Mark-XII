package org.usfirst.team1351.robot.main;

import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOHardware;

public class Main
{

	public static void main(String[] args)
	{
		long startTime = System.currentTimeMillis();
		System.out.println("Enabling teleop!");
		TKOHardware.initObjects();
		TKOLogger.getInstance().start();
		TKODrive.getInstance().start();
		TKODataReporting.getInstance().start();
		while ((System.currentTimeMillis() - startTime) < 120000)
		{
			//do nothing
		}
		
		TKODataReporting.getInstance().stop();
		try
		{
			TKODataReporting.getInstance().dataReportThread.join();
		} catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		TKODrive.getInstance().stop();
		try
		{
			TKODrive.getInstance().driveThread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		TKOLogger.getInstance().stop();
		try
		{
			TKOLogger.getInstance().loggerThread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
