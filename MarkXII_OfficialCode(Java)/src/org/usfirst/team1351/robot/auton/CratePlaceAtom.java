package org.usfirst.team1351.robot.auton;

//LINE 81 (TKOHardware.java) IS WHERE PID VALUE ARE SET TODO TUNE THOSE ASAP AFTER THIS WORKS 
//TODO TUNE PID - LINE 81 TKOHARDWARE.JAVA 
//Current values are 1, 0, 0 
import org.usfirst.team1351.robot.evom.TKOLift;

public class CratePlaceAtom extends Atom
{
	public CratePlaceAtom()
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

		// try {
		// TKOHardware.getDriveTalon(0).set(0.0);
		// TKOHardware.getDriveTalon(2).set(0.0);
		// } catch (TKOException e) {
		// } catch (TKOException e) {
		// } catch (TKOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
