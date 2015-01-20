package org.usfirst.team1351.robot.auton;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;

public class DriveAtom extends Atom {

	CANTalon m_driveL1, m_driveL2, m_driveR1, m_driveR2;
	float distance;
	Encoder encoder_L;
	Encoder encoder_R;
	public DriveAtom()
	{
		m_driveL1 = new CANTalon(0);
		m_driveL2 = new CANTalon(1);
		m_driveR1 = new CANTalon(2);
		m_driveR2 = new CANTalon(3);
		encoder_L = new Encoder(1, 2);
		encoder_R = new Encoder(3, 4);
		distance = 10.f;
	}
	
	@Override
	public void execute()
	{
		m_driveL1.changeControlMode(CANTalon.ControlMode.PercentVbus);
		m_driveL2.changeControlMode(CANTalon.ControlMode.PercentVbus);
		m_driveR1.changeControlMode(CANTalon.ControlMode.PercentVbus);
		m_driveR2.changeControlMode(CANTalon.ControlMode.PercentVbus);
		
		encoder_L.setDistancePerPulse(0.112);
		encoder_R.setDistancePerPulse(0.112);
		while(encoder_L.get() < distance){
			m_driveL1.set(0.3);
			m_driveL2.set(0.3);
			m_driveR1.set(0.3);
			m_driveR2.set(0.3);
		}
		m_driveL1.set(0);
		m_driveL2.set(0);
		m_driveR1.set(0);
		m_driveR2.set(0);
	}
	
}
