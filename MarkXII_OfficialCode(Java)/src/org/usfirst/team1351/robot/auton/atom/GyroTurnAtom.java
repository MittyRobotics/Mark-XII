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
import edu.wpi.first.wpilibj.Timer;

public class GyroTurnAtom extends Atom
{
	PIDController pid;
	Gyro gyro;
	double angle, incrementer, threshold;
	double p, i, d;
	int ncoder1, ncoder2;

	public GyroTurnAtom(double f)
	{
		angle = f;
		threshold = 1;
		// incrementer = Definitions.AUTON_PID_INCREMENTER; //TODO CREATE NEW VARIABLE FOR THIS
		incrementer = .5;
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
	{ //TODO get incrementer tuned, ensure that the system is fully working. May need to adjust the timer, seems to be somewhat overshooting 
		System.out.println("Starting execution of GYRO TURN");
		try
		{
			pid.enable();
			// The following may or may not work TODO Test it
			// The following is designed as an incrementer for turn atom so that it will be smooth
			// pid.onTarget might not work if the setInput method isnt called
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
			}
			else if(angle < 0) {
				while(DriverStation.getInstance().isEnabled() && pid.getSetpoint() > angle) {
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
			while ((pid.getError() > threshold || t.get() < .75) && DriverStation.getInstance().isEnabled())
			{
				TKOHardware.getRightDrive().set(TKOHardware.getLeftDrive().get());
				System.out.println("Target Angle: " + pid.getSetpoint() + " \t PID Error: " + pid.getError() + "\t Gyro Get: "
						+ gyro.getAngle());
				Timer.delay(0.001);
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
