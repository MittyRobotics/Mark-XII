// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.util;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.can.CANMessageNotFoundException;
import edu.wpi.first.wpilibj.util.AllocationException;

public class TKOHardware
{
	// TODO Switch initialization
	/*
	 * For monitoring the control mode of the talons: Once a follower is created, it should never be accessed
	 */
	protected static Joystick joysticks[] = new Joystick[Definitions.NUM_JOYSTICKS];
	protected static CANTalon driveTalons[] = new CANTalon[Definitions.NUM_DRIVE_TALONS];
	protected static CANTalon liftTalons[] = new CANTalon[Definitions.NUM_LIFT_TALONS];
	protected static DoubleSolenoid pistonSolenoids[] = new DoubleSolenoid[Definitions.NUM_PISTONS];
	protected static DigitalInput limitSwitches[] = new DigitalInput[Definitions.NUM_SWITCHES];
	protected static Compressor compressor;
	protected static BuiltInAccelerometer acc;

	protected static CANTalon.ControlMode talonModes[] = new CANTalon.ControlMode[Definitions.NUM_DRIVE_TALONS
			+ Definitions.NUM_LIFT_TALONS]; // encompasses all talons

	public TKOHardware()
	{
		for (int i = 0; i < Definitions.NUM_JOYSTICKS; i++)
		{
			joysticks[i] = null;
		}
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			driveTalons[i] = null;
		}
		for (int i = 0; i < Definitions.NUM_LIFT_TALONS; i++)
		{
			liftTalons[i] = null;
		}
		for (int i = 0; i < Definitions.NUM_PISTONS; i++)
		{
			pistonSolenoids[i] = null;
		}
		for (int i = 0; i < Definitions.NUM_SWITCHES; i++)
		{
			limitSwitches[i] = null;
		}
		for (int i = 0; i < (Definitions.NUM_DRIVE_TALONS + Definitions.NUM_LIFT_TALONS); i++)
		{
			talonModes[i] = null;
		}
		compressor = null;
		acc = null;
	}

	public static synchronized void initObjects()
	{
		// TODO maybe destroy objects before initializing them?
		for (int i = 0; i < Definitions.NUM_JOYSTICKS; i++)
		{
			if (joysticks[i] == null)
				joysticks[i] = new Joystick(Definitions.JOYSTICK_ID[i]);
		}
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			if (driveTalons[i] == null)
			{
				try
				{
					driveTalons[i] = new CANTalon(Definitions.DRIVE_TALON_ID[i]);
					talonModes[i] = null; // null means not initialized
				}
				catch (AllocationException | CANMessageNotFoundException e)
				{
					e.printStackTrace();
					System.out.println("MOTOR CONTROLLER " + i + " NOT FOUND OR IN USE");
					TKOLogger.getInstance().addMessage("MOTOR CONTROLLER " + i + " CAN ERROR");
				}
			}
		}
		for (int i = 0; i < Definitions.NUM_LIFT_TALONS; i++)
		{
			if (liftTalons[i] == null)
			{
				try
				{
					liftTalons[i] = new CANTalon(Definitions.LIFT_TALON_ID[i]);
					talonModes[Definitions.NUM_DRIVE_TALONS + i] = null; // null means not initialized
				}
				catch (AllocationException | CANMessageNotFoundException e)
				{
					e.printStackTrace();
					System.out.println("MOTOR CONTROLLER " + i + " NOT FOUND OR IN USE");
					TKOLogger.getInstance().addMessage("MOTOR CONTROLLER " + i + " CAN ERROR");
				}
			}
		}

		if (pistonSolenoids[0] == null)
			pistonSolenoids[0] = new DoubleSolenoid(Definitions.SHIFTER_A, Definitions.SHIFTER_B);

		if (pistonSolenoids[1] == null)
			pistonSolenoids[1] = new DoubleSolenoid(Definitions.GRIPPER_A, Definitions.GRIPPER_B);

		if (pistonSolenoids[2] == null)
			pistonSolenoids[2] = new DoubleSolenoid(Definitions.WHEELIE_A, Definitions.WHEELIE_B);

		if (compressor == null)
			compressor = new Compressor(Definitions.PCM_ID);

		if (acc == null)
			acc = new BuiltInAccelerometer();

		configDriveTalons(Definitions.DRIVE_P, Definitions.DRIVE_I, Definitions.DRIVE_D, Definitions.DRIVE_TALONS_NORMAL_CONTROL_MODE);
		configLiftTalons(Definitions.LIFT_P, Definitions.LIFT_I, Definitions.LIFT_D, Definitions.LIFT_TALONS_NORMAL_CONTROL_MODE);
	}

	public static synchronized void configDriveTalons(double p, double I, double d, ControlMode mode)
	{
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			driveTalons[i].delete();
			driveTalons[i] = null;
			driveTalons[i] = new CANTalon(Definitions.DRIVE_TALON_ID[i]);
			talonModes[i] = null;
			if (driveTalons[i] != null)
			{
				if (i == 1 || i == 3) // if follower
				{
					driveTalons[i].changeControlMode(CANTalon.ControlMode.Follower);
					driveTalons[i].set(i - 1); // set to follow the CANTalon with id i - 1;
					talonModes[i] = CANTalon.ControlMode.Follower;
				}
				else
				// if not follower
				{
					if (!(mode instanceof CANTalon.ControlMode))
						throw new TKORuntimeException("CODE ERROR! Wrong control mode used (jag vs talon)");

					driveTalons[i].changeControlMode(mode);
					driveTalons[i].setFeedbackDevice(Definitions.DRIVE_ENCODER_TYPE);
					driveTalons[i].setPID(p, I, d);
					talonModes[i] = mode;
				}
				driveTalons[i].enableBrakeMode(Definitions.DRIVE_BRAKE_MODE[i]);
				driveTalons[i].reverseOutput(Definitions.DRIVE_REVERSE_OUTPUT_MODE[i]);
			}
		}

	}

	private static synchronized void configLiftTalons(double liftP, double liftI, double liftD, ControlMode liftTalonsNormalControlMode)
	{
		for (int i = 0; i < Definitions.NUM_LIFT_TALONS; i++)
		{
			liftTalons[i].delete();
			liftTalons[i] = null;
			liftTalons[i] = new CANTalon(Definitions.LIFT_TALON_ID[i]);
			talonModes[Definitions.NUM_DRIVE_TALONS + i] = null;
			if (liftTalons[i] != null)
			{
				if (i == 1 || i == 3) // if follower
				{
					liftTalons[i].changeControlMode(CANTalon.ControlMode.Follower);
					liftTalons[i].set(i - 1); // set to follow the CANTalon with id i - 1;
					talonModes[Definitions.NUM_DRIVE_TALONS + i] = CANTalon.ControlMode.Follower;
				}
				else
				// if not follower
				{
					if (!(liftTalonsNormalControlMode instanceof CANTalon.ControlMode))
						throw new TKORuntimeException("CODE ERROR! Wrong control mode used (jag vs talon)");
					// TODO not needed if specified in args

					liftTalons[i].changeControlMode(liftTalonsNormalControlMode);
					liftTalons[i].setFeedbackDevice(Definitions.LIFT_ENCODER_TYPE);
					liftTalons[i].setPID(liftP, liftI, liftD);
					talonModes[Definitions.NUM_DRIVE_TALONS + i] = liftTalonsNormalControlMode;
				}
				liftTalons[i].enableBrakeMode(Definitions.LIFT_BRAKE_MODE[i]);
				liftTalons[i].reverseOutput(Definitions.LIFT_REVERSE_OUTPUT_MODE[i]);
			}
		}
	}
	
	public static synchronized void changeTalonMode(CANTalon target, CANTalon.ControlMode newMode) throws TKOException
	{
		if (target == null)
			throw new TKOException("ERROR Attempted to change mode of null CANTalon");
		if (newMode == target.getControlMode())
			return;

		if (target.getControlMode() != CANTalon.ControlMode.Position && target.getControlMode() != CANTalon.ControlMode.Speed)
			liftTalons[target.getDeviceID()].setFeedbackDevice(Definitions.DEF_ENCODER_TYPE);

		target.changeControlMode(newMode);
		talonModes[target.getDeviceID()] = newMode;
	}

	public static synchronized void changeTalonMode(CANTalon target, CANTalon.ControlMode newMode, double newP, double newI, double newD)
			throws TKOException
	{
		if (target == null)
			throw new TKOException("ERROR Attempted to change mode of null CANTalon");
		if (newMode == target.getControlMode())
			return;

		if (target.getControlMode() != CANTalon.ControlMode.Position && target.getControlMode() != CANTalon.ControlMode.Speed)
			liftTalons[target.getDeviceID()].setFeedbackDevice(Definitions.DEF_ENCODER_TYPE);

		target.changeControlMode(newMode);
		target.setPID(newP, newI, newD);
		talonModes[target.getDeviceID()] = newMode;
	}

	/**
	 * Sets *ALL* drive Talons to given value. CAUTION WHEN USING THIS METHOD, DOES NOT CARE ABOUT FOLLOWER TALONS. Intended for PID Tuning
	 * loop ONLY.
	 * 
	 * @deprecated Try not to use this method. It is very prone to introducing errors. Use getLeftDrive() and getRightDrive() or
	 *             getDriveTalon(int n) instead, unless you know what you are doing.
	 * @param double setTarget - Value to set for all the talons
	 */
	public static synchronized void setAllDriveTalons(double setTarget)
	{
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			if (driveTalons[i] != null)
			{
				driveTalons[i].set(setTarget);
			}
		}
	}

	public static synchronized void destroyObjects()
	{
		for (int i = 0; i < Definitions.NUM_JOYSTICKS; i++)
		{
			if (joysticks[i] != null)
			{
				joysticks[i] = null;
			}
		}
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			if (driveTalons[i] != null)
			{
				driveTalons[i].delete();
				driveTalons[i] = null;
			}
		}
		for (int i = 0; i < Definitions.NUM_LIFT_TALONS; i++)
		{
			if (liftTalons[i] != null)
			{
				liftTalons[i].delete();
				liftTalons[i] = null;
			}
		}
		for (int i = 0; i < Definitions.NUM_PISTONS; i++)
		{
			if (pistonSolenoids[i] != null)
			{
				pistonSolenoids[i].free();
				pistonSolenoids[i] = null;
			}
		}
		for (int i = 0; i < (Definitions.NUM_DRIVE_TALONS + Definitions.NUM_LIFT_TALONS); i++)
		{
			talonModes[i] = null;
		}
		if (compressor != null)
		{
			compressor.free();
			compressor = null;
		}

		if (acc != null)
			acc = null;
	}

	public static synchronized Joystick getJoystick(int num) throws TKOException
	{
		if (num >= Definitions.NUM_JOYSTICKS)
		{
			throw new TKOException("Joystick requested out of bounds");
		}
		if (joysticks[num] != null)
			return joysticks[num];
		else
			throw new TKOException("Joystick " + (num) + "(array value) is null");
	}

	public static synchronized CANTalon getDriveTalon(int num) throws TKOException
	{
		if (num >= Definitions.NUM_DRIVE_TALONS)
		{
			throw new TKOException("Drive talon requested out of bounds");
		}
		if (driveTalons[num] != null)
		{
			if (driveTalons[num].getControlMode() == CANTalon.ControlMode.Follower)
				throw new TKOException("WARNING CANNOT ACCESS FOLLOWER TALON!");
			else if (talonModes[num] == null)
				throw new TKOException("ERROR TRYING TO ACCESS UNINITIALIZED TALON; MODE UNSET!");
			else
				return driveTalons[num];
		}
		else
			throw new TKOException("Drive talon " + (num) + "(array value) is null");
	}

	public static synchronized CANTalon getLiftTalon() throws TKOException
	{
		if (liftTalons[0] == null || liftTalons[1] == null)
			throw new TKOException("LIFT TALON IS NULL");

		if (talonModes[Definitions.NUM_DRIVE_TALONS + 0] == null)
			throw new TKOException("ERROR TRYING TO ACCESS UNINITIALIZED TALON; MODE IS UNSET!");
		if (talonModes[Definitions.NUM_DRIVE_TALONS + 1] != CANTalon.ControlMode.Follower)
			throw new TKOException("ERROR LIFT FOLLOWER TALON IS NOT UNITIALIZED; MODE IS UNSET!");

		return liftTalons[0];
	}

	public static synchronized DoubleSolenoid getPiston(int num) throws TKOException
	{
		if (num >= Definitions.NUM_PISTONS)
		{
			throw new TKOException("Piston requested out of bounds");
		}
		if (pistonSolenoids[num] != null)
			return pistonSolenoids[num];
		else
			throw new TKOException("Piston " + (num) + "(array value) is null");
	}

	public static synchronized Compressor getCompressor() throws TKOException
	{
		if (compressor == null)
			throw new TKOException("NULL COMPRESSOR");
		return compressor;
	}

	public static synchronized BuiltInAccelerometer getAcc() throws TKOException
	{
		if (acc == null)
			throw new TKOException("NULL ACCELEROMETER OBJECT");
		return acc;
	}

	public static synchronized CANTalon getLeftDrive() throws TKOException
	{
		if (driveTalons[0] == null || driveTalons[1] == null)
			throw new TKOException("NULL LEFT DRIVE TALON");
		if (talonModes[0] == null)
			throw new TKOException("ERROR TRYING TO ACCESS UNINITIALIZED TALON; MODE IS UNSET!");
		if (talonModes[1] != CANTalon.ControlMode.Follower)
			throw new TKOException("ERROR LEFT DRIVE FOLLOWER TALON IS NOT UNITIALIZED; MODE IS UNSET!");
		return driveTalons[0];
	}

	public static synchronized CANTalon getRightDrive() throws TKOException
	{
		if (driveTalons[2] == null || driveTalons[3] == null)
			throw new TKOException("NULL LEFT DRIVE TALON");
		if (talonModes[2] == null)
			throw new TKOException("ERROR TRYING TO ACCESS UNINITIALIZED TALON; MODE IS UNSET!");
		if (talonModes[3] != CANTalon.ControlMode.Follower)
			throw new TKOException("ERROR RIGHT DRIVE FOLLOWER TALON IS NOT UNITIALIZED; MODE IS UNSET!");
		return driveTalons[2];
	}

	/**
	 * Try not to use this function; use getJoystick(int n) instead.
	 * 
	 * @deprecated Not very safe function. Lacks Joystick checks.
	 * @return Joystick array
	 * @throws TKOException
	 */
	public static synchronized Joystick[] getJoysticks() throws TKOException
	{
		if (joysticks == null)
			throw new TKOException("NULL STICK ARRAY");
		return joysticks;
	}

	/**
	 * Try not to use this function; use getLeftDrive() and getRightDrive() instead.
	 * 
	 * @deprecated Not very safe function. Lacks Talon safety checks.
	 * @return CANTalon array
	 * @throws TKOException
	 */
	public static synchronized CANTalon[] getDriveTalons() throws TKOException
	{
		if (driveTalons == null)
			throw new TKOException("NULL DRIVE ARRAY");
		return driveTalons;
	}

	/**
	 * Try not to use this function; use getPiston(int n) instead.
	 * 
	 * @deprecated Not very safe function. Lacks DoubleSolenoid safety checks.
	 * @return DoubleSolenoid array
	 * @throws TKOException
	 */
	public static synchronized DoubleSolenoid[] getPistons() throws TKOException
	{
		if (pistonSolenoids == null)
			throw new TKOException("NULL PISTON ARRAY");
		return pistonSolenoids;
	}

}
