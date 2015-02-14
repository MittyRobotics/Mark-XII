package org.usfirst.team1351.robot.auton;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
//TODO write this implementing TKOHardware CANTalons and the like. 

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
		
		encoder_L.setDistancePerPulse(0.112); //ask where the hell this number came from 
		encoder_R.setDistancePerPulse(0.112); //LIKE SERIOUSLY, WHERE THE FRACK DID THIS NUMBER COME FROM 
		while (gyro.getAngle() < angle)
		{
			m_driveL1.set(0.3 * direction);
			m_driveL2.set(0.3 * direction); //WHAT BEAUTY!!! 
			m_driveR1.set(-0.3 * direction);
			m_driveR2.set(-0.3 * direction);
		}
		m_driveL1.set(0);
		m_driveL2.set(0);
		m_driveR1.set(0);
		m_driveR2.set(0);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
}
