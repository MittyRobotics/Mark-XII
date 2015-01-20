package org.usfirst.team1351.robot.auton;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;

public class TurnAtom extends Atom{	
	CANTalon m_driveL1, m_driveL2, m_driveR1, m_driveR2;
	Encoder encoder_L;
	Encoder encoder_R;
	float angle;
	Gyro gyro;
	float direction;
	
	public TurnAtom(float f)
	{
		m_driveL1 = new CANTalon(0);
		m_driveL2 = new CANTalon(1);
		m_driveR1 = new CANTalon(2);
		m_driveR2 = new CANTalon(3);
		encoder_L = new Encoder(1, 2);
		encoder_R = new Encoder(3, 4);
		angle = 10.f;
		direction = f;
		gyro = new Gyro(5);
	}
	
	public void execute()
	{
		m_driveL1.changeControlMode(CANTalon.ControlMode.PercentVbus);
		m_driveL2.changeControlMode(CANTalon.ControlMode.PercentVbus);
		m_driveR1.changeControlMode(CANTalon.ControlMode.PercentVbus);
		m_driveR2.changeControlMode(CANTalon.ControlMode.PercentVbus);
		
		encoder_L.setDistancePerPulse(0.112);
		encoder_R.setDistancePerPulse(0.112);
		while (gyro.getAngle() < angle)
		{
			m_driveL1.set(0.3 * direction);
			m_driveL2.set(0.3 * direction);
			m_driveR1.set(-0.3 * direction);
			m_driveR2.set(-0.3 * direction);
		}
		m_driveL1.set(0);
		m_driveL2.set(0);
		m_driveR1.set(0);
		m_driveR2.set(0);
	}
}
