package org.usfirst.team1351.robot.auton;

//LINE 81 (TKOHardware.java) IS WHERE PID VALUE ARE SET TODO TUNE THOSE ASAP AFTER THIS WORKS 
//TODO TUNE PID - LINE 81 TKOHARDWARE.JAVA 
//Current values are 1, 0, 0 
import org.usfirst.team1351.robot.auton.Atom;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDController;

public class GyroTurnAtom extends Atom
{
	PIDController pid;
	Gyro gyro;
	double angle, incrementer;
	int ncoder1, ncoder2;

	public GyroTurnAtom(double f)
	{
		angle = f;
		incrementer = 10; // TODO ADD INCREMENTER VALUE DEFINITIONS
	}

	public void init()
	{
		double p = 0.6;
		double i = 0.1;
		double d = 0.;
		try
		{
			TKOHardware.changeTalonMode(TKOHardware.getLeftDrive(), CANTalon.ControlMode.PercentVbus, p, i, d);
			TKOHardware.changeTalonMode(TKOHardware.getRightDrive(), CANTalon.ControlMode.PercentVbus, p, i, d);
			TKOHardware.getLeftDrive().setPosition(0);
			TKOHardware.getRightDrive().setPosition(0);
			TKOHardware.getRightDrive().reverseSensor(true);
			
			gyro = TKOHardware.getGyro();
			pid = new PIDController(p, i, d, gyro, TKOHardware.getLeftDrive());
		} catch (TKOException e)
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
			gyro.reset();
			pid.setOutputRange(-1, 1);
			pid.setAbsoluteTolerance(10);
			pid.enable();
			pid.setSetpoint(angle);
			while (DriverStation.getInstance().isEnabled() && pid.onTarget())
			{
				pid.setSetpoint(angle);
				TKOHardware.getRightDrive().set(-pid.get());
				//System.out.println("GYRO " + gyro.getAngle());
				System.out.println("Target Angle: " + pid.getSetpoint()+ " \t PID Error: " + pid.getError());
			}

			TKOHardware.getDriveTalon(0).set(0);
			TKOHardware.getDriveTalon(2).set(0);

		} catch (TKOException e1)
		{
			e1.printStackTrace();
		}
		System.out.println("Done executing");
	}

}
