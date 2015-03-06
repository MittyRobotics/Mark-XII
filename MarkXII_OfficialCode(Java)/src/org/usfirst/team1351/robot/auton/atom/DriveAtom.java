package org.usfirst.team1351.robot.auton.atom;

/**
 * TODO check TKOHardware to see where PID is set
 */

import org.usfirst.team1351.robot.auton.Atom;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveAtom extends Atom
{
	double distance, incrementer, threshold;
	double p, i, d;

	public DriveAtom(double _dist)
	{

		p = SmartDashboard.getNumber("Drive P: ");
		i = SmartDashboard.getNumber("Drive I: ");
		d = SmartDashboard.getNumber("Drive D: ");

		distance = _dist;
		incrementer = Definitions.DRIVE_ATOM_INCREMENTER;
		threshold = 75; // we can be within approx. half an inch
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
			TKOHardware.getLeftDrive().enableBrakeMode(true);
			TKOHardware.getRightDrive().enableBrakeMode(true);
			TKOHardware.getLeftDrive().setPosition(0); // resets encoder
			TKOHardware.getRightDrive().setPosition(0);
			TKOHardware.getLeftDrive().ClearIaccum(); // stops bounce
			TKOHardware.getRightDrive().ClearIaccum();
			Timer.delay(0.1);
			TKOHardware.getLeftDrive().set(TKOHardware.getLeftDrive().getPosition());
			TKOHardware.getRightDrive().set(TKOHardware.getRightDrive().getPosition());
		} catch (TKOException e)
		{
			e.printStackTrace();
			System.out.println("Err.... Talons kinda died ");
		}
		System.out.println("Drive atom initialized");
	}

	@Override
	public void execute()
	{
		System.out.println("Starting execution");
		try
		{
			if (distance > 0)
			{
				while (DriverStation.getInstance().isEnabled() && TKOHardware.getDriveTalon(0).getSetpoint() < distance)
				{
					TKOHardware.getDriveTalon(0).set(TKOHardware.getDriveTalon(0).getSetpoint() + incrementer);
					TKOHardware.getDriveTalon(2).set(TKOHardware.getDriveTalon(2).getSetpoint() + incrementer);
					System.out.println("Ncoder Left: " + TKOHardware.getDriveTalon(0).getPosition() + "\t Ncoder Rgith: "
							+ TKOHardware.getDriveTalon(2).getPosition() + "\t Left Setpoint: "
							+ TKOHardware.getDriveTalon(0).getSetpoint());
				}
			} else
			{
				while (DriverStation.getInstance().isEnabled() && TKOHardware.getDriveTalon(0).getSetpoint() > distance)
				{
					TKOHardware.getDriveTalon(0).set(TKOHardware.getDriveTalon(0).getSetpoint() - incrementer);
					TKOHardware.getDriveTalon(2).set(TKOHardware.getDriveTalon(2).getSetpoint() - incrementer);
					System.out.println("Ncoder Left: " + TKOHardware.getDriveTalon(0).getPosition() + "\t Ncoder Rgith: "
							+ TKOHardware.getDriveTalon(2).getPosition() + "\t Left Setpoint: "
							+ TKOHardware.getDriveTalon(0).getSetpoint());
				}
			}

			TKOHardware.getDriveTalon(0).set(distance);
			TKOHardware.getDriveTalon(2).set(distance);

			while (Math.abs(TKOHardware.getLeftDrive().getPosition() - distance) > threshold && DriverStation.getInstance().isEnabled())
			{
				// not close enough doe; actually gets stuck here
			}

		} catch (TKOException e1)
		{
			e1.printStackTrace();
			System.out.println("Error at another expected spot, I would assume....");
		}
		System.out.println("Done executing");
	}

}
