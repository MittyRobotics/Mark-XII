package org.usfirst.team1351.robot.auton.atom;

import org.usfirst.team1351.robot.auton.Atom;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;

/* TODO
 * tune incrementer
 * fix timer
 * set PID values in TKOHardware.java
 */

public class GyroTurnAtom extends Atom
{
	PIDController pid;
	Gyro gyro;
	double angle, incrementer, threshold;
	double p, i, d;
	int ncoder1, ncoder2;

	public GyroTurnAtom(double _angle)
	{
		angle = _angle;
		threshold = 2;
		incrementer = Definitions.TURN_ATOM_INCREMENTER;
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
			TKOHardware.getLeftDrive().reverseOutput(false);
			TKOHardware.getRightDrive().reverseOutput(true);
			TKOHardware.getLeftDrive().reverseSensor(true);
			TKOHardware.getRightDrive().reverseSensor(false);
			TKOHardware.getLeftDrive().setPosition(0);
			TKOHardware.getRightDrive().setPosition(0); // resets encoders
			Timer.delay(0.1);

			gyro = TKOHardware.getGyro();
			pid = new PIDController(p, i, d, gyro, TKOHardware.getLeftDrive());
		} catch (TKOException e)
		{
			e.printStackTrace();
		}

		gyro.reset();
		pid.reset();
		pid.setOutputRange(-0.5, 0.5);
		pid.setContinuous();
		pid.setAbsoluteTolerance(2);

		System.out.println("Initialized");
	}

	@Override
	public void execute()
	{
		System.out.println("Starting execution of GYRO TURN");
		try
		{
			pid.enable();
			// The following is designed as an incrementer for turn atom so that it will be smooth
			if (angle >= 0)
			{
				while (DriverStation.getInstance().isEnabled() && pid.getSetpoint() < angle)
				{
					pid.setSetpoint(pid.getSetpoint() + incrementer);
					TKOHardware.getRightDrive().set(TKOHardware.getLeftDrive().get());
					System.out.println("LEFT GET: " + TKOHardware.getLeftDrive().get() + "\t RIGHT GET: "
							+ TKOHardware.getRightDrive().get() + "\t Setpoint: " + pid.getSetpoint());
					Timer.delay(0.001);
				}
			} else if (angle < 0)
			{
				while (DriverStation.getInstance().isEnabled() && pid.getSetpoint() > angle)
				{
					pid.setSetpoint(pid.getSetpoint() - incrementer);
					TKOHardware.getRightDrive().set(TKOHardware.getLeftDrive().get());
					System.out.println("LEFT GET: " + TKOHardware.getLeftDrive().get() + "\t RIGHT GET: "
							+ TKOHardware.getRightDrive().get() + "\t Setpoint: " + pid.getSetpoint());
					Timer.delay(0.001);
				}
			}
			pid.setSetpoint(angle);
			Timer t = new Timer();
			t.reset();
			t.start();
			while (DriverStation.getInstance().isEnabled())
			{
				if (Math.abs(pid.getError()) > threshold)
				{
					t.reset();
					pid.setSetpoint(angle);
				}
				if (t.get() > .5)
				{
					break;
				}
				TKOHardware.getRightDrive().set(TKOHardware.getLeftDrive().get());
				System.out.println("Target Angle: " + pid.getSetpoint() + " \t PID Error: " + pid.getError() + "\t Gyro Get: "
						+ gyro.getAngle());
				// Timer.delay(0.001);
			}
			t.stop();
			TKOHardware.getDriveTalon(0).set(0);
			TKOHardware.getDriveTalon(2).set(0);
			Timer.delay(0.1);
		} catch (TKOException e1)
		{
			e1.printStackTrace();
		}
		pid.disable();
		System.out.println("GYRO Done executing");
	}

}
