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
		while ((System.currentTimeMillis() - startTime) < 80000)
		{
			// do nothing
		}

		try
		{
			TKODataReporting.getInstance().stop();
			TKODataReporting.getInstance().dataReportThread.join();
			TKODrive.getInstance().stop();
			TKODrive.getInstance().driveThread.join();
			TKOLogger.getInstance().stop();
			TKOLogger.getInstance().loggerThread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
