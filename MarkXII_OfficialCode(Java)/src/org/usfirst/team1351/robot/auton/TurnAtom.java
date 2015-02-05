package org.usfirst.team1351.robot.auton;

import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;

public class TurnAtom extends Atom
{	
	float angle, direction;
	
	public TurnAtom(float f1, float f2)
	{
		angle = f1;
		direction = f2;
	}
	

	@Override
	public void init()
	{
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
			try {
				TKOHardware.getDriveTalon(i).changeControlMode(CANTalon.ControlMode.PercentVbus);
			} catch (TKOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		try {
			TKOHardware.getEncoder(1).setDistancePerPulse(Definitions.DISTANCE_PER_PULSE);
			TKOHardware.getEncoder(2).setDistancePerPulse(Definitions.DISTANCE_PER_PULSE);
		} catch (TKOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void execute()
	{
		/*try
		{
			while (TKOHardware.getGyro().getAngle() < angle)
			{
				TKOHardware.getDriveTalon(0).set(0.3 * direction);
				TKOHardware.getDriveTalon(1).set(0.3 * direction);
				TKOHardware.getDriveTalon(2).set(-0.3 * direction);
				TKOHardware.getDriveTalon(3).set(-0.3 * direction);
			}
			
			for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
				TKOHardware.getDriveTalon(i).set(0.);
		}
		catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
