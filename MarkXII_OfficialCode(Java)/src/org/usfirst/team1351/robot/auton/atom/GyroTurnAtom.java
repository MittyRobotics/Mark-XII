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
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDController;

public class GyroTurnAtom extends Atom
{
	PIDController pid;
	Gyro gyro;
	double angle, incrementer;
	double p, i, d;
	int ncoder1, ncoder2;

	public GyroTurnAtom(double f)
	{
		angle = f;
		incrementer = Definitions.AUTON_PID_INCREMENTER;
		p = Definitions.AUTON_GYRO_TURN_P;
		i = Definitions.AUTON_GYRO_TURN_I;
		d = Definitions.AUTON_GYRO_TURN_D;
	}

	public void init()
	{
		try
		{
			TKOHardware.changeTalonMode(TKOHardware.getLeftDrive(), CANTalon.ControlMode.PercentVbus, p, i, d);
			TKOHardware.changeTalonMode(TKOHardware.getRightDrive(), CANTalon.ControlMode.PercentVbus, p, i, d);
			TKOHardware.getLeftDrive().setPosition(0);
			TKOHardware.getRightDrive().setPosition(0);

			gyro = TKOHardware.getGyro();
			pid = new PIDController(p, i, d, gyro, TKOHardware.getLeftDrive());
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}

		gyro.reset();
		pid.reset();
		pid.setOutputRange(-1, 1);
		pid.setContinuous();
		pid.setAbsoluteTolerance(10);

		System.out.println("Initialized");
	}

	@Override
	public void execute()
	{
		System.out.println("Starting execution");
		try
		{
			pid.enable();
			pid.setSetpoint(angle);
			//pid.onTarget might not work if the setInput method isnt called
			while (DriverStation.getInstance().isEnabled() && pid.onTarget())
			{
				pid.setSetpoint(angle);
				TKOHardware.getRightDrive().set(-TKOHardware.getLeftDrive().get()); 
				//TKOHardware.getRightDrive().set(-pid.get()); //TODO what does pid.get() actually return?
				// System.out.println("GYRO " + gyro.getAngle());
				System.out.println("Target Angle: " + pid.getSetpoint() + " \t PID Error: " + pid.getError() + "\t PID GET: " + pid.get());
			}

			TKOHardware.getDriveTalon(0).set(0);
			TKOHardware.getDriveTalon(2).set(0);
		}
		catch (TKOException e1)
		{
			e1.printStackTrace();
		}
		pid.disable();
		System.out.println("Done executing");
	}

}
