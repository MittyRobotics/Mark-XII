// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.main;

import org.usfirst.team1351.robot.auton.Molecule;
import org.usfirst.team1351.robot.auton.atom.AutoCratePickupAtom;
import org.usfirst.team1351.robot.auton.atom.DriveAtom;
import org.usfirst.team1351.robot.auton.atom.GoUpAtom;
import org.usfirst.team1351.robot.auton.atom.GyroTurnAtom;
import org.usfirst.team1351.robot.auton.atom.TrashcanGrabAndUp;
import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.evom.TKOPneumatics;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOTalonSafety;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*-----------TODO-------------
 * Current Project: Autonomous testing (drive, turn, pickup crate, place crate)
 * 
 * Watch this video for tuning with Ziegler-Nichols: http://youtu.be/UOuRx9Ujsog
 * Do we care about StateMachine? Run everything manual?
 * Lift MUST honor limit switches!
 * Recreate documentation for java
 * Thread priorities
 * Don't forget to turn on all the subsystems
 * Destroy hardware pointers after operator control loop?
 * Check the TKOException writing to log file
 * Check that TKOHardware has initialized objects in all files?
 * Test TalonSafety!
 * StateMachine assumes that TKOLift is enabled and configured: check if ok
 * 
 * Calculate Kc by testing max p with i and d = 0 where stable oscillation 
 * Calculate max and min of oscillation and find the period between the max and min
 * Kp = 0.6 Kc
 * Ki = 2*Kp/Pc
 * Kd = 0.125*Kp*Pc
 * 
 * PUT STACK OF THREE TOTES ON MIDDLE STEP
 * GO DOWN A TAD WITH LIFT WITH JOYSTICK
 * 
 */

public class MarkXII extends SampleRobot
{
	SendableChooser autonChooser;
	
	public MarkXII()
	{
		//don't put stuff here, use robotInit();
	}

	public void robotInit()
	{
		System.out.println("-----WELCOME TO MARKXII 2015-----");
		System.out.println("-----SYSTEM BOOT: " + Timer.getFPGATimestamp() + "-----");
		TKOHardware.initObjects();

		autonChooser = new SendableChooser();
		autonChooser.addDefault("RC, Drive, Turn", new Integer(7));
		autonChooser.addObject("RC, Drive", new Integer(6));
		autonChooser.addObject("Drive", new Integer(0));
		autonChooser.addObject("Drive, turn", new Integer(1));
		autonChooser.addObject("Turn", new Integer(2));
		autonChooser.addObject("Drive, pickup", new Integer(3));
		autonChooser.addObject("Box", new Integer(4));
		autonChooser.addObject("Auto Pickup", new Integer(5));
				
		SmartDashboard.putData("Auton mode chooser", autonChooser);
		SmartDashboard.putNumber("Drive P: ", Definitions.AUTON_DRIVE_P);
		SmartDashboard.putNumber("Drive I: ", Definitions.AUTON_DRIVE_I);
		SmartDashboard.putNumber("Drive D: ", Definitions.AUTON_DRIVE_D);
		SmartDashboard.putNumber("Turn P: ", Definitions.AUTON_GYRO_TURN_P);
		SmartDashboard.putNumber("Turn I: ", Definitions.AUTON_GYRO_TURN_I * 1000.);
		SmartDashboard.putNumber("Turn D: ", Definitions.AUTON_GYRO_TURN_D);
		
		SmartDashboard.putNumber("Lift P: ", Definitions.LIFT_P);
		SmartDashboard.putNumber("Lift I: ", Definitions.LIFT_I);
		SmartDashboard.putNumber("Lift D: ", Definitions.LIFT_D);
		
		SmartDashboard.putNumber("Drive atom distance: ", -90.);
		SmartDashboard.putNumber("Turn atom angle: ", 85);
		SmartDashboard.putNumber("Turn Incrementer: ", Definitions.TURN_ATOM_INCREMENTER);
		try
		{
			SmartDashboard.putNumber("CRATE DISTANCE: ", TKOHardware.getCrateDistance());
			SmartDashboard.putBoolean("Top switch", TKOHardware.getLiftTop());
			SmartDashboard.putBoolean("Bottom switch", TKOHardware.getLiftBottom());
		} catch (TKOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("robotInit() finished");
	}

	public void disabled()
	{
		System.out.println("ROBOT DISABLED!");
	}

	public void autonomous()
	{
		System.out.println("Enabling autonomous!");
		
		TKOLogger.getInstance().start();
//		TKODataReporting.getInstance().start();
//		TKOTalonSafety.getInstance().start();
		TKOLift.getInstance().start();
//		TKOLEDArduino.getInstance().start();
		TKOPneumatics.getInstance().start();
//		TKOPneumatics.getInstance().reset(); //TODO This may be bad
		
		Molecule molecule = new Molecule();
		molecule.clear();
		
		double dist = SmartDashboard.getNumber("Drive atom distance: ");
		double angle = SmartDashboard.getNumber("Turn atom angle: ");
		
		if (autonChooser.getSelected().equals(0))
		{
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH)); 
		}
		else if (autonChooser.getSelected().equals(1))
		{
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH)); 
			molecule.add(new GyroTurnAtom(angle));
		}
		else if (autonChooser.getSelected().equals(2))
		{
			molecule.add(new GyroTurnAtom(angle));
		}
		else if (autonChooser.getSelected().equals(3))
		{
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
			molecule.add(new GoUpAtom());
		}
		else if (autonChooser.getSelected().equals(4))
		{
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
			molecule.add(new GyroTurnAtom(angle));
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
			molecule.add(new GyroTurnAtom(angle));
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
			molecule.add(new GyroTurnAtom(angle));
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
			molecule.add(new GyroTurnAtom(angle));
		}
		else if (autonChooser.getSelected().equals(5))
			molecule.add(new AutoCratePickupAtom());
		else if (autonChooser.getSelected().equals(6))
		{
			molecule.add(new TrashcanGrabAndUp());
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
//			molecule.add(new GyroTurnAtom(-angle));
//			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
//			molecule.add(new GyroTurnAtom(angle));
//			molecule.add(new DriveAtom((dist - 2) * Definitions.TICKS_PER_INCH));
//			molecule.add(new AutoCratePickupAtom());
//			molecule.add(new DriveAtom((dist * 4) * Definitions.TICKS_PER_INCH));
		}
		else if (autonChooser.getSelected().equals(7))
		{
			molecule.add(new TrashcanGrabAndUp());
			molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
			molecule.add(new GyroTurnAtom(angle));
		}
		else
		{
			System.out.println("Molecule empty why this");
		}
		
		System.out.println("Running molecule");
		molecule.initAndRun();
		System.out.println("Finished running molecule");

		try
		{
			TKOPneumatics.getInstance().stop();
			TKOPneumatics.getInstance().pneuThread.join();
			TKOLift.getInstance().stop();
			TKOLift.getInstance().conveyorThread.join();
//			TKODataReporting.getInstance().stop();
//			TKODataReporting.getInstance().dataReportThread.join();
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
		//TKODataReporting.getInstance().start();
		TKOTalonSafety.getInstance().start();
		TKOLift.getInstance().start();
		//TKOLEDArduino.getInstance().start();
		
		while (isOperatorControl() && isEnabled())
		{
			try
			{
				SmartDashboard.putNumber("CRATE DISTANCE: ", TKOHardware.getCrateDistance());
				SmartDashboard.putBoolean("Top switch", TKOHardware.getLiftTop());
				SmartDashboard.putBoolean("Bottom switch", TKOHardware.getLiftBottom());
			} catch (TKOException e)
			{
				e.printStackTrace();
			}
			Timer.delay(0.1); // wait for a motor update time
			//TODO This will make it so robot lags after disabling, need to make it sorta small
		}

		try
		{
//			TKOLEDArduino.getInstance().stop();
//			TKOLEDArduino.getInstance().ledArduinoThread.join();
			TKOTalonSafety.getInstance().stop();
			TKOTalonSafety.getInstance().safetyCheckerThread.join();
			TKOLift.getInstance().stop();
			TKOLift.getInstance().conveyorThread.join();
//			TKODataReporting.getInstance().stop();
//			TKODataReporting.getInstance().dataReportThread.join();
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
		
	}
}
