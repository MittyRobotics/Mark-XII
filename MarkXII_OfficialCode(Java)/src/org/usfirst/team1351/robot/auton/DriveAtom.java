package org.usfirst.team1351.robot.auton;

//LINE 81 (TKOHardware.java) IS WHERE PID VALUE ARE SET TODO TUNE THOSE ASAP AFTER THIS WORKS 
//TODO TUNE PID - LINE 81 TKOHARDWARE.JAVA 
//Current values are 1, 0, 0 
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.DriverStation;

public class DriveAtom extends Atom
{

	float distance;
	int ncoder1, ncoder2;

	public DriveAtom(float f)
	{
		distance = f;
		// Talons 0 and 2 are the ones with Ncoders plugged in, keep that in
		// mind. 1 and 3 have already been declared as slaves.
	}

	public void init()
	{
		try
		{
			TKOHardware.getDriveTalon(0).changeControlMode(CANTalon.ControlMode.Position);
			TKOHardware.getDriveTalon(0).setFeedbackDevice(FeedbackDevice.QuadEncoder);
			TKOHardware.getDriveTalon(0).setPosition(0);
			TKOHardware.getDriveTalon(0).reverseSensor(true); // Look into this later. We may have a backwards encoder.
			TKOHardware.getDriveTalon(0).enableControl();

			TKOHardware.getDriveTalon(2).changeControlMode(CANTalon.ControlMode.Position);
			TKOHardware.getDriveTalon(2).setFeedbackDevice(FeedbackDevice.QuadEncoder);
			TKOHardware.getDriveTalon(2).setPosition(0);
			TKOHardware.getDriveTalon(2).reverseSensor(true); // Look into this later. We may have a backwards encoder.
			TKOHardware.getDriveTalon(2).enableControl();
		} catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("HOLY THIS ENCODER DUN GOOFED UP!!!");
		}
		System.out.println("Initialized");
		// try {
		// TKOHardware.getLeftEncoder().setDistancePerPulse(Definitions.DISTANCE_PER_PULSE);
		// TKOHardware.getRightEncoder().setDistancePerPulse(Definitions.DISTANCE_PER_PULSE);
		// } catch (TKOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
	}

	@Override
	public void execute()
	{
		System.out.println("Starting execution");
		try
		{
			while (DriverStation.getInstance().isEnabled())
			{
				TKOHardware.getDriveTalon(0).set(distance);
				TKOHardware.getDriveTalon(2).set(-distance);
				System.out.println("Ncoder Left: " + TKOHardware.getDriveTalon(0).getPosition() + "\t Ncoder Rgith: "
						+ TKOHardware.getDriveTalon(2).getPosition());
			}

		} catch (TKOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Error at another expected spot, I would assume....");
		}
		System.out.println("Done executing");

		// try {
		// TKOHardware.getDriveTalon(0).set(0.0);
		// TKOHardware.getDriveTalon(2).set(0.0);
		// } catch (TKOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
