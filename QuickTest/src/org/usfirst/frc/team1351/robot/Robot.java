package org.usfirst.frc.team1351.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	CANTalon[] t = new CANTalon[7];

	public Robot()
	{
		t[1] = new CANTalon(1);
		t[2] = new CANTalon(2);
		t[3] = new CANTalon(3);
		t[4] = new CANTalon(4);
		t[5] = new CANTalon(5);
		t[6] = new CANTalon(6);
	}

	public void robotInit()
	{
		t[1].changeControlMode(CANTalon.ControlMode.PercentVbus);
		t[2].changeControlMode(CANTalon.ControlMode.PercentVbus);
		t[3].changeControlMode(CANTalon.ControlMode.PercentVbus);
		t[4].changeControlMode(CANTalon.ControlMode.PercentVbus);
		t[5].changeControlMode(CANTalon.ControlMode.PercentVbus);
		t[6].changeControlMode(CANTalon.ControlMode.PercentVbus);
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
		while (isOperatorControl() && isEnabled())
		{
			for (int i = 1; i <= 6; i++)
			{
				t[i].set(1.);
				SmartDashboard.putNumber("OutCurrent: " + i, t[i].getOutputCurrent());
				SmartDashboard.putNumber("OutVoltage: " + i, t[i].getOutputVoltage());
				//System.out.println(t[i].getOutputCurrent());
			}
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test()
	{
		while (isTest() && isEnabled())
		{
			for (int i = 1; i <= 6; i++)
			{
				t[i].set(1.);
				SmartDashboard.putNumber("OutCurrent: " + i, t[i].getOutputCurrent());
				SmartDashboard.putNumber("OutVoltage: " + i, t[i].getOutputVoltage());
				//System.out.println(t[i].getOutputCurrent());
			}
			System.out.println("output flipped 1");
			Timer.delay(1);
			for (int i = 1; i <= 6; i++)
			{
				t[i].set(-1.);
				SmartDashboard.putNumber("OutCurrent: " + i, t[i].getOutputCurrent());
				SmartDashboard.putNumber("OutVoltage: " + i, t[i].getOutputVoltage());
				//System.out.println(t[i].getOutputCurrent());
			}
			System.out.println("output flipped 2");
			Timer.delay(1);
		}
	}
}
