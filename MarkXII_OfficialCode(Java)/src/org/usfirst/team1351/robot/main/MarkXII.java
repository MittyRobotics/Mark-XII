package org.usfirst.team1351.robot.main;

import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

/**
 * This is a demo program showing the use of the RobotDrive class. The SampleRobot class is the base of a robot application that will
 * automatically call your Autonomous and OperatorControl methods at the right time as controlled by the switches on the driver station or
 * the field controls.
 *
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, as described in the
 * SampleRobot documentation. If you change the name of this class or the package after creating this project, you must also update the
 * manifest file in the resource directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced, don't. Unless you know what you are doing,
 * complex code will be much more difficult under this system. Use IterativeRobot or Command-Based instead if you're new.
 */

/*-----------TODO-------------
 * Write TKOLEDArduino
 * Test/fix StateMachine
 * Recreate documentation for java
 * TKOGyro/TKORelay?
 * Port/rewrite Autonomous Atoms/Molecules?
 * TODO global current checker/safety manager in TKOHardware for each jag?
 */
public class MarkXII extends SampleRobot
{

	public MarkXII()
	{
		
	}
	
	public void robotInit()
	{
		
	}
	
	public void disabled()
	{
		
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	public void autonomous()
	{
		
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl()
	{
		System.out.println("Enabling teleop!");
		TKOHardware.initObjects();
		TKOLogger.getInstance().start();
		TKODrive.getInstance().start();
		TKODataReporting.getInstance().start();
		while (isOperatorControl() && isEnabled())
		{
			TKOLogger.getInstance().addMessage("Testing...");
			Timer.delay(0.25); // wait for a motor update time
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

	/**
	 * Runs during test mode
	 */
	public void test()
	{
	}
}
