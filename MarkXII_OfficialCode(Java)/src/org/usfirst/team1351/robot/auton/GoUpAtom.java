package org.usfirst.team1351.robot.auton;

import org.usfirst.team1351.robot.evom.TKOLift;

public class GoUpAtom extends Atom
{
	public GoUpAtom()
	{
		
	}

	public void init()
	{
		TKOLift.getInstance().start();
	}

	@Override
	public void execute()
	{
		System.out.println("Starting execution");
		
		while (TKOLift.getInstance().isMoving() || !TKOLift.getInstance().calibrated)
		{
			System.out.println("NOT READY TO GO UP");
		}
		
		TKOLift.getInstance().goUp();
		
		while (TKOLift.getInstance().isMoving())
		{
			System.out.println("MOVING");
		}
		
		TKOLift.getInstance().stop();
		
		System.out.println("Done executing");
	}

}
