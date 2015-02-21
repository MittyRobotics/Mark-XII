package org.usfirst.team1351.robot.auton;

import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANTalon;

public class DriveAtom extends Atom {
	
	float distance;

	public DriveAtom(float f)
	{
		distance = f;
	}
	
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
	
	@Override
	public void execute()
	{	
		try {
			while (TKOHardware.getEncoder(1).get() < distance &&
					TKOHardware.getEncoder(2).get() < distance)
			{
				for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
					TKOHardware.getDriveTalon(i).set(0.3);
			}
		} catch (TKOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
			try {
				TKOHardware.getDriveTalon(i).set(0.0);
			} catch (TKOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}
