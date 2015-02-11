// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.main;

import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.evom.TKOPneumatics;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOTalonSafety;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*-----------TODO-------------
 * Write TKOLEDArduino lol rekt
 * Test/fix StateMachine
 * Recreate documentation for java
 * TKOGyro/TKORelay?
 * Port/rewrite Autonomous Atoms/Molecules?
 * TODO global current checker/safety manager loop in TKOHardware for each jag?
 * TODO Thread priorities
 * TODO Drive - access only drives 0/2? - COMPLETE/NEEDS TESTING
 * TODO don't forget to turn on all the subsystems
 * TODO organize TKOHardware by the different things components will be used for, (array of arrays?) - COMPLETE/NEEDS TESTING
 * TODO figure out easy way to switch between drive modes - COMPLETE/NEEDS TESTING
 * 		TODO Maybe create a checker for what mode we are in, and have setVelocity, setPosition, etc. - COMPLETE/NEEDS TESTING
 * 		TODO 2 Lift motors - COMPLETE/NEEDS TESTING
 * 		TODO right now TKOHardware talon control mode changing and managing it turbo ghetto - COMPLETE/NEEDS TESTING
 * TODO Do we need to destroy hardware pointers when we are done with operator control loop
 * TODO Test the TKOException writing to log file - COMPLETE/NEEDS TESTING
 * TODO maybe its a bad idea to assume everywhere that TKOHardware has objects initialized?
 * 
 * TODO Figure out why the talon initialization is sometimes slow...
 * TODO Test TalonSafety
 * 
 * TODO SATURDAY REQUIREMENTS

 All manual

 lift running with .Set pid for later honoring limit switches
 Use the lift in manual mode (as in using the .set method on the can talon for moving the lift without pid). 
 Pid can happen after we can verify that the lift works
 Driving
 Grabber open and close
 wheelie bar up and down

 Get working
 drive up to a bin/trash can
 pickup
 and drive around

 Also need to ask Lead mentors for more time on Saturday

 */
public class MarkXII extends SampleRobot
{

	public MarkXII()
	{

	}

	public void robotInit()
	{
		System.out.println("-----WELCOME TO MARKXII 2015-----");
		System.out.println("-----SYSTEM BOOT: " + Timer.getFPGATimestamp() + "-----");
	}

	public void disabled()
	{
		System.out.println("ROBOT DISABLED!");
	}

	public void autonomous()
	{

	}

	public void operatorControl()
	{
		System.out.println("Enabling teleop!");
		TKOHardware.initObjects();
		TKOLogger.getInstance().start();
		TKODrive.getInstance().start();
		TKOPneumatics.getInstance().start();
		TKODataReporting.getInstance().start();
		//TKOTalonSafety.getInstance().start();
		TKOLift.getInstance().start();

		/*CANTalon motor = null;
		try
		{
			motor = TKOHardware.getLeftDrive();
		}
		catch (TKOException e1)
		{
			e1.printStackTrace();
		}*/

		while (isOperatorControl() && isEnabled())
		{
			//System.out.println("Distance: " + motor.getPosition());
			//System.out.println("Velocity: " + motor.getVelocity());
			/*try
			{
				System.out.println("Gripper: " + TKOHardware.getLiftGripper());
			}
			catch (TKOException e)
			{
				e.printStackTrace();
			}*/
			
			Timer.delay(0.1); // wait for a motor update time
		}

		try
		{
			//TKOTalonSafety.getInstance().stop();
			//TKOTalonSafety.getInstance().safetyCheckerThread.join();
			TKOLift.getInstance().stop();
			TKOLift.getInstance().conveyorThread.join();
			TKODataReporting.getInstance().stop();
			TKODataReporting.getInstance().dataReportThread.join();
			TKOPneumatics.getInstance().stop();
			TKOPneumatics.getInstance().pneuThread.join();
			TKODrive.getInstance().stop();
			TKODrive.getInstance().driveThread.join();
			TKOLogger.getInstance().stop();
			TKOLogger.getInstance().loggerThread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test()
	{
		System.out.println("Enabling test!");
		AnalogInput test = new AnalogInput(3);
		while (isTest() && isEnabled())
		{
			System.out.println("V: " + test.getVoltage());
			SmartDashboard.putNumber("A Voltage", test.getVoltage());
		}
		/*TKOHardware.initObjects();
		TKOLogger.getInstance().start();
		TKODataReporting.getInstance().start();

		try
		{
			TKOHardware.getLeftDrive().setPosition(0);
			while (isTest() && isEnabled())
			{
				TKOHardware.changeTalonMode(TKOHardware.getLeftDrive(), CANTalon.ControlMode.Position, 0.5, 0.01, 0.);
				TKOHardware.getLeftDrive().set(5000);
				System.out.println("Current Pos: " + TKOHardware.getLeftDrive().getEncPosition());
				System.out.println("Error: " + TKOHardware.getLeftDrive().getClosedLoopError());
			}

		}
		catch (TKOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try
		{
			TKODataReporting.getInstance().stop();
			TKODataReporting.getInstance().dataReportThread.join();
			TKOLogger.getInstance().stop();
			TKOLogger.getInstance().loggerThread.join();
			TKOHardware.destroyObjects();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}*/
	}
}
