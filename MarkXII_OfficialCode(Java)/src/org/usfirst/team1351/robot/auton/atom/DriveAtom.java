package org.usfirst.team1351.robot.auton.atom;

//LINE 81 (TKOHardware.java) IS WHERE PID VALUE ARE SET TODO TUNE THOSE ASAP AFTER THIS WORKS 
//TODO TUNE PID - LINE 81 TKOHARDWARE.JAVA 
//Current values are 1, 0, 0 
import org.usfirst.team1351.robot.auton.Atom;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class DriveAtom extends Atom
{

	double distance, incrementer, threshold;
	int ncoder1, ncoder2;
	double p, i, d;

	public DriveAtom(double f)
	{
		distance = f;
		//incrementer = Definitions.AUTON_PID_INCREMENTER;
		incrementer = 25;
		threshold = 5;
		p = Definitions.AUTON_DRIVE_P;
		i = Definitions.AUTON_DRIVE_I;
		d = Definitions.AUTON_DRIVE_D;
		// Talons 0 and 2 are the ones with Ncoders plugged in, keep that in
		// mind. 1 and 3 have already been declared as slaves.
	}

	public void init()
	{
		try
		{
			TKOHardware.changeTalonMode(TKOHardware.getLeftDrive(), CANTalon.ControlMode.Position, p, i, d);
			TKOHardware.changeTalonMode(TKOHardware.getRightDrive(), CANTalon.ControlMode.Position, p, i, d);
			TKOHardware.getLeftDrive().reverseOutput(false);
			TKOHardware.getRightDrive().reverseOutput(true);
			TKOHardware.getLeftDrive().reverseSensor(true);
			TKOHardware.getRightDrive().reverseSensor(false);
			TKOHardware.getLeftDrive().setPosition(0);
			TKOHardware.getRightDrive().setPosition(0); // resets encoders
			Timer.delay(0.1);
			TKOHardware.getLeftDrive().set(TKOHardware.getLeftDrive().getPosition());
			TKOHardware.getRightDrive().set(TKOHardware.getRightDrive().getPosition());
		}
		catch (TKOException e)
		{
			e.printStackTrace();
			System.out.println("Err.... Talons kinda died ");
		}
		System.out.println("Initialized");
	}

	@Override
	public void execute()
	{
		System.out.println("Starting execution");
		try
		{
			while (DriverStation.getInstance().isEnabled() && TKOHardware.getDriveTalon(0).getSetpoint() < distance)
			{
				TKOHardware.getDriveTalon(0).set(TKOHardware.getDriveTalon(0).getSetpoint() + incrementer);
				TKOHardware.getDriveTalon(2).set(TKOHardware.getDriveTalon(2).getSetpoint() + incrementer);
				System.out.println("Ncoder Left: " + TKOHardware.getDriveTalon(0).getPosition() + "\t Ncoder Rgith: "
						+ TKOHardware.getDriveTalon(2).getPosition() + "\t Left Setpoint: " + TKOHardware.getDriveTalon(0).getSetpoint());
			}

			TKOHardware.getDriveTalon(0).set(distance);
			TKOHardware.getDriveTalon(2).set(distance);
			
			while (Math.abs(TKOHardware.getLeftDrive().getPosition() - distance) > threshold)
			{
				//not close enough doe
			}

		}
		catch (TKOException e1)
		{
			e1.printStackTrace();
			System.out.println("Error at another expected spot, I would assume....");
		}
		System.out.println("Done executing");
	}

}
