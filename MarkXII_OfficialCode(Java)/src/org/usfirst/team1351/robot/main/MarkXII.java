// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.main;

import org.usfirst.team1351.robot.auton.Molecule;
import org.usfirst.team1351.robot.auton.atom.DriveAtom;
import org.usfirst.team1351.robot.auton.atom.GoUpAtom;
import org.usfirst.team1351.robot.auton.atom.GyroTurnAtom;
import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.evom.TKOPneumatics;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.statemachine.StateMachine;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOTalonSafety;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

/*-----------TODO-------------
 * Write TKOLEDArduino lol rekt
 * Test/fix StateMachine
 * Recreate documentation for java
 * TODO Thread priorities
 * TODO don't forget to turn on all the subsystems
 * TODO Do we need to destroy hardware pointers when we are done with operator control loop
 * TODO Test the TKOException writing to log file - COMPLETE/NEEDS TESTING
 * TODO maybe its a bad idea to assume everywhere that TKOHardware has objects initialized?
 * 
 * TODO Test TalonSafety !!!!!!!!!!!!!!!!!!!!
 * TODO Lift needs to honor limit switches
 * 
 * TODO Lift autotuned using the Zeigler-Nichols method;
 * Calculate Kc by testing max p with i and d = 0 where stable oscilation 
 * Calculate max and min of oscilation and find the period between the max and min
 * Kp = 0.6 Kc
 * Ki = 2*Kp/Pc
 * Kd = 0.125*Kp*Pc
 * 
 * TODO Important, StateMachine assumes that TKOLift is enabled and configured
 * 
 * TODO Auton
 * 	AutoCratePickupAtom - drives forward until crate engaged, automatically goes up immediately
	CratePlaceAtom - places the stack of 3 (hardcoded) or maybe place stack based on current lift level
 */

public class MarkXII extends SampleRobot
{

	public MarkXII()
	{
		//don't put stuff here, use robotInit();
	}

	public void robotInit()
	{
		System.out.println("-----WELCOME TO MARKXII 2015-----");
		System.out.println("-----SYSTEM BOOT: " + Timer.getFPGATimestamp() + "-----");
		TKOHardware.initObjects();
		try
		{
			TKOHardware.getGyro().initGyro();
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
		System.out.println("-----GYRO INITIALIZED: " + Timer.getFPGATimestamp() + "-----");
	}

	public void disabled()
	{
		System.out.println("ROBOT DISABLED!");
	}

	public void autonomous()
	{
		System.out.println("Enabling autonomous!");
		TKOLogger.getInstance().start();
		TKODataReporting.getInstance().start();
		//TKOTalonSafety.getInstance().start();
		TKOLift.getInstance().start();
		TKOPneumatics.getInstance().start();

		TKOPneumatics.getInstance().reset(); //TODO This may be bad
		Molecule molecule = new Molecule();
		DriveAtom drive = new DriveAtom(10000.);
		//GyroTurnAtom turnGyro = new GyroTurnAtom(45.f); 
		molecule.add(drive);
		molecule.add(new GoUpAtom());
		//molecule.add(turnGyro); 

		System.out.println("Running molecule");
		molecule.initAndRun();
		System.out.println("Finished running molecule");

		try
		{
			TKOPneumatics.getInstance().stop();
			TKOPneumatics.getInstance().pneuThread.join();
			TKOLift.getInstance().stop();
			TKOLift.getInstance().conveyorThread.join();
			TKODataReporting.getInstance().stop();
			TKODataReporting.getInstance().dataReportThread.join();
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
		TKOLogger.getInstance().start();
		TKODrive.getInstance().start();
		TKOPneumatics.getInstance().start();
		TKODataReporting.getInstance().start();
		TKOTalonSafety.getInstance().start();
		TKOLift.getInstance().start();


		while (isOperatorControl() && isEnabled())
		{			
			Timer.delay(0.01); // wait for a motor update time
			//TODO This will make it so robot lags after disabling, need to make it sorta small
		}

		try
		{
			TKOTalonSafety.getInstance().stop();
			TKOTalonSafety.getInstance().safetyCheckerThread.join();
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
		TKOHardware.initObjects();
		TKOLogger.getInstance().start();
		TKODrive.getInstance().start();
		TKOPneumatics.getInstance().start();
		TKODataReporting.getInstance().start();
		TKOLift.getInstance().start();
		System.out.println("STARTING STATE MACHINE");
		StateMachine.getInstance().start();

		while (isTest() && isEnabled())
		{
			Timer.delay(0.01); // wait for a motor update time
		}

		try
		{
			StateMachine.getInstance().stop();
			StateMachine.getInstance().stateThread.join();
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
}
