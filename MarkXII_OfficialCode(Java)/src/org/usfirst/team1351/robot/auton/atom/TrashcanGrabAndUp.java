package org.usfirst.team1351.robot.auton.atom;

import org.usfirst.team1351.robot.auton.Atom;
import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class TrashcanGrabAndUp extends Atom
{
	public TrashcanGrabAndUp()
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
		try
		{

			TKOHardware.getPiston(1).set(DoubleSolenoid.Value.kReverse);

			while ((TKOLift.getInstance().isMoving() || !TKOLift.getInstance().calibrated) && DriverStation.getInstance().isEnabled())
			{
				// System.out.println("NOT READY TO GO UP");
			}

			TKOLift.getInstance().goToTrashcanPickup();

			while ((TKOLift.getInstance().isMoving()) && DriverStation.getInstance().isEnabled())
			{
				// System.out.println("MOVING");
			}

			TKOHardware.getPiston(1).set(DoubleSolenoid.Value.kForward);
			Timer.delay(0.5);

			TKOLift.getInstance().goUp();

			while ((TKOLift.getInstance().isMoving()) && DriverStation.getInstance().isEnabled())
			{
				// System.out.println("MOVING");
			}
			
			TKOLift.getInstance().goUp();

			while ((TKOLift.getInstance().isMoving()) && DriverStation.getInstance().isEnabled())
			{
				// System.out.println("MOVING");
			}

			TKOLift.getInstance().stop();
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Done executing");
	}

}
