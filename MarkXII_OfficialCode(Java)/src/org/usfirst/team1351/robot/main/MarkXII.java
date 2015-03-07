// Last edited by Ben Kim
// on 03/06/2015

package org.usfirst.team1351.robot.main;

import java.util.ArrayList;

import org.usfirst.team1351.robot.auton.Molecule;
import org.usfirst.team1351.robot.auton.atom.AutoCratePickupAtom;
import org.usfirst.team1351.robot.auton.atom.CratePlaceAtom;
import org.usfirst.team1351.robot.auton.atom.DriveAtom;
import org.usfirst.team1351.robot.auton.atom.GoUpAtom;
import org.usfirst.team1351.robot.auton.atom.GyroTurnAtom;
import org.usfirst.team1351.robot.auton.atom.TrashcanGrabAndUp;
import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.evom.TKOPneumatics;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOLEDArduino;
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
	ArrayList<SendableChooser> atoms = new ArrayList<>();
	SendableChooser atom1, atom2, atom3, atom4;

	public MarkXII()
	{
		// don't put stuff here, use robotInit();
	}

	public void robotInit()
	{
		System.out.println("-----WELCOME TO MARKXII 2015-----");
		System.out.println("-----SYSTEM BOOT: " + Timer.getFPGATimestamp() + "-----");
		TKOHardware.initObjects();

		atoms.add(atom1);
		atoms.add(atom2);
		atoms.add(atom3);
		atoms.add(atom4);

		for (SendableChooser a : atoms)
		{
			a = new SendableChooser();
			a.addDefault("N/A", "N/A");
			a.addObject("Drive", "Drive");
			a.addObject("Turn", "Turn");
			a.addObject("Trash can", "Trash can");
			a.addObject("Tote pickup", "Tote pickup");
			a.addObject("Tote drop", "Tote drop");
		}

		SmartDashboard.putData("Atom 1 chooser", atom1);
		SmartDashboard.putData("Atom 2 chooser", atom2);
		SmartDashboard.putData("Atom 3 chooser", atom3);
		SmartDashboard.putData("Atom 4 chooser", atom4);

		SmartDashboard.putNumber("Drive P: ", Definitions.AUTON_DRIVE_P);
		SmartDashboard.putNumber("Drive I: ", Definitions.AUTON_DRIVE_I);
		SmartDashboard.putNumber("Drive D: ", Definitions.AUTON_DRIVE_D);
		SmartDashboard.putNumber("Turn P: ", Definitions.AUTON_GYRO_TURN_P);
		SmartDashboard.putNumber("Turn I: ", Definitions.AUTON_GYRO_TURN_I * 1000.);
		SmartDashboard.putNumber("Turn D: ", Definitions.AUTON_GYRO_TURN_D);

		SmartDashboard.putNumber("Lift P: ", Definitions.LIFT_P);
		SmartDashboard.putNumber("Lift I: ", Definitions.LIFT_I);
		SmartDashboard.putNumber("Lift D: ", Definitions.LIFT_D);

		SmartDashboard.putNumber("Drive atom distance: ", -80.);
		SmartDashboard.putNumber("Turn atom angle: ", 85.);
		SmartDashboard.putNumber("Turn Incrementer: ", Definitions.TURN_ATOM_INCREMENTER);

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
		// TKODataReporting.getInstance().start();
		// TKOTalonSafety.getInstance().start();
		TKOLift.getInstance().start();
		TKOLEDArduino.getInstance().start();
		TKOPneumatics.getInstance().start();
		// TKOPneumatics.getInstance().reset(); //TODO This may be bad

		Molecule molecule = new Molecule();

		double dist = SmartDashboard.getNumber("Drive atom distance: ");
		double angle = SmartDashboard.getNumber("Turn atom angle: ");

		for (SendableChooser a : atoms)
		{
			String s = (String) a.getSelected();
			switch (s)
			{
			case "Drive":
				molecule.add(new DriveAtom(dist * Definitions.TICKS_PER_INCH));
				break;
			case "Turn":
				molecule.add(new GyroTurnAtom(angle));
				break;
			case "Trash can":
				molecule.add(new TrashcanGrabAndUp());
				break;
			case "Tote pickup":
				molecule.add(new AutoCratePickupAtom());
				break;
			case "Tote drop":
				molecule.add(new CratePlaceAtom());
				break;

			default: // case "N/A"
				break;
			}
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
			// TKODataReporting.getInstance().stop();
			// TKODataReporting.getInstance().dataReportThread.join();
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
		TKOLEDArduino.getInstance().start();

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
			Timer.delay(0.01); // wait for a motor update time
			// TODO This will make it so robot lags after disabling, need to make it sorta small
		}

		try
		{
			TKOLEDArduino.getInstance().stop();
			TKOLEDArduino.getInstance().ledArduinoThread.join();
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
		while (isTest() && isEnabled())
		{
			try
			{
				System.out.println("CRATE DISTANCE: " + TKOHardware.getCrateDistance());
			} catch (TKOException e)
			{
				e.printStackTrace();
			}
			Timer.delay(0.01); // wait for a motor update time
			// TODO This will make it so robot lags after disabling, need to make it sorta small
		}
	}
}
