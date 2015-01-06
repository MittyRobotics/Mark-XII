package org.usfirst.frc.team1351.robot;

import org.usfirst.frc.team1351.drive.TKODrive;
import org.usfirst.frc.team1351.logger.TKOLogger;
import org.usfirst.frc.team1351.util.TKODataReporting;

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
public class Robot extends SampleRobot
{

	public Robot()
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
		TKOLogger.start();
		TKODrive.start();
		TKODataReporting.start();
		while (isOperatorControl() && isEnabled())
		{
			TKOLogger.addMessage("Testing...");
			Timer.delay(1); // wait for a motor update time
		}
		TKODataReporting.stop();
		TKODrive.stop();
		TKOLogger.stop();
	}

	/**
	 * Runs during test mode
	 */
	public void test()
	{
	}
}
