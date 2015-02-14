package org.usfirst.team1351.robot.auton;

import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANJaguar;

public class DriveAtom extends Atom
{
	float distance;

	public DriveAtom(float f)
	{
		distance = f;
	}

	public void init()
	{
		TKOHardware.configJags(TKOHardware.P, TKOHardware.I, TKOHardware.D);
	}

	public void execute()
	{
		
	}

}
