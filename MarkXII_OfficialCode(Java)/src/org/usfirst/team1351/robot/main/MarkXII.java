// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.main;

import org.usfirst.team1351.robot.auton.DriveAtom;
import org.usfirst.team1351.robot.auton.Molecule;
import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.evom.TKOGripper;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

/*-----------TODO-------------
 * Write TKOLEDArduino
 * Test/fix StateMachine
 * Recreate documentation for java
 * TKOGyro/TKORelay?
 * Port/rewrite Autonomous Atoms/Molecules?
 * TODO global current checker/safety manager loop in TKOHardware for each jag?
 * TODO Thread priorities
 * 
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

	public void autonomous()
	{
		System.out.println("Enabling autonomous!");
		TKOHardware.initObjects();
		TKOLogger.getInstance().start();
		TKODrive.getInstance().start();
//		TKOGripper.getInstance().start();
		TKODataReporting.getInstance().start();
		
		Molecule molecule = new Molecule();
		DriveAtom drive = new DriveAtom(10.f);
		molecule.add(drive);
		molecule.init();
		
		while (isAutonomous() && isEnabled())
		{
			Timer.delay(0.25); // wait for a motor update time
		}

		try
		{
			TKODataReporting.getInstance().stop();
			TKODataReporting.getInstance().dataReportThread.join();
//			TKOGripper.getInstance().stop();
//			TKOGripper.getInstance().gripperThread.join();
			TKODrive.getInstance().stop();
			TKODrive.getInstance().driveThread.join();
			TKOLogger.getInstance().stop();
			TKOLogger.getInstance().loggerThread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void operatorControl()
	{
		System.out.println("Enabling teleop!");
		TKOHardware.initObjects();
		TKOLogger.getInstance().start();
		TKODrive.getInstance().start();
		TKOGripper.getInstance().start();
		TKODataReporting.getInstance().start();
		while (isOperatorControl() && isEnabled())
		{
			Timer.delay(0.25); // wait for a motor update time
		}

		try
		{
			TKODataReporting.getInstance().stop();
			TKODataReporting.getInstance().dataReportThread.join();
			TKOGripper.getInstance().stop();
			TKOGripper.getInstance().gripperThread.join();
			TKODrive.getInstance().stop();
			TKODrive.getInstance().driveThread.join();
			TKOLogger.getInstance().stop();
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
