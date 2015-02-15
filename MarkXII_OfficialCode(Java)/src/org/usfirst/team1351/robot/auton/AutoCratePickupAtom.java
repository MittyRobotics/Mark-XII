package org.usfirst.team1351.robot.auton;

//LINE 81 (TKOHardware.java) IS WHERE PID VALUE ARE SET TODO TUNE THOSE ASAP AFTER THIS WORKS 
//TODO TUNE PID - LINE 81 TKOHARDWARE.JAVA 
//Current values are 1, 0, 0 
import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANTalon;

public class AutoCratePickupAtom extends Atom
{
	double p, i, d;
	double driveMult;

	public AutoCratePickupAtom()
	{
		p = Definitions.AUTON_DRIVE_P;
		i = Definitions.AUTON_DRIVE_I;
		d = Definitions.AUTON_DRIVE_D;
		driveMult = Definitions.AUTON_DRIVE_VBUS_MULT;
	}

	public void init()
	{
		TKOLift.getInstance().start();
		try
		{
			TKOHardware.changeTalonMode(TKOHardware.getLeftDrive(), CANTalon.ControlMode.PercentVbus, p, i, d);
			TKOHardware.changeTalonMode(TKOHardware.getRightDrive(), CANTalon.ControlMode.PercentVbus, p, i, d);
			TKOHardware.getLeftDrive().setPosition(0);
			TKOHardware.getRightDrive().setPosition(0); // resets encoders
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Initialized");
	}

	@Override
	public void execute()
	{
		System.out.println("Starting execution");

		try
		{
			double crateDist = Double.MAX_VALUE;
			CANTalon left = TKOHardware.getLeftDrive();
			CANTalon right = TKOHardware.getRightDrive();

			while (crateDist > Definitions.CRATE_DISTANCE_THRESHOLD)
			{
				left.set(driveMult);
				right.set(driveMult);
				crateDist = TKOHardware.getCrateDistance();
			}

			while (TKOLift.getInstance().isMoving() || !TKOLift.getInstance().calibrated)
			{
				System.out.println("NOT READY TO GO UP");
			}

			TKOLift.getInstance().goUp();

			while (TKOLift.getInstance().isMoving())
			{
				System.out.println("MOVING");
			}

			TKOLift.getInstance().stop();

		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Done executing");
	}

}
