package org.usfirst.team1351.robot.main;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot
{
	public Robot()
	{
		
	}

	public void autonomous()
	{
		while (isAutonomous() && isEnabled())
		{
			Timer.delay(seconds);
		}
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl()
	{
		while (isOperatorControl() && isEnabled())
		{
			Timer.delay(seconds);
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test()
	{
		
	}
}
