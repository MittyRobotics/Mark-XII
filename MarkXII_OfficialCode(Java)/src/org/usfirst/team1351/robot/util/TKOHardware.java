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
				/*
				driveTalons[i].changeControlMode(CANTalon.ControlMode.Position);
				driveTalons[i].setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
				driveTalons[i].reverseSensor(true);
				driveTalons[i].setPID(p, I, d);
				driveTalons[i].enableControl();
				
				motor = new CANTalon(1); // Initialize the CanTalonSRX on device 1.
				motor.changeControlMode(CANTalon.ControlMode.Position);
				motor.setFeedbackDevice(CANTalon.FeedbackDevice.AnalogPot);
				motor.setPID(1.0, 0.0, 0.0);
				*/
	
				if (i == 1 || i == 3) //if follower
				{
					driveTalons[i].changeControlMode(CANTalon.ControlMode.Follower);
					driveTalons[i].set(i - 1); //set to follow the CANTalon with id i - 1;
					talonModes[i] = CANTalon.ControlMode.Follower;
				}
				else //if not follower
				{
					if (!(mode instanceof CANTalon.ControlMode))
						throw new TKORuntimeException("CODE ERROR! Wrong control mode used (jag vs talon)");
					
					driveTalons[i].changeControlMode(mode);
					driveTalons[i].setFeedbackDevice(Definitions.DRIVE_ENCODER_TYPE);
					driveTalons[i].setPID(p, I, d);
					talonModes[i] = mode;
				}
				driveTalons[i].enableBrakeMode(Definitions.DRIVE_BRAKE_MODE[i]);
				driveTalons[i].reverseOutput(Definitions.DRIVE_REVERSE_MODE[i]);
			}
		}

	}

	/*public static synchronized void configDriveTalons(double p, double I, double d, ControlMode mode)
	{
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			driveTalons[i].delete();
			driveTalons[i] = null;
			driveTalons[i] = new CANTalon(Definitions.DRIVE_TALON_ID[i]);
			if (driveTalons[i] != null)
			{

				driveTalons[i].changeControlMode(CANTalon.ControlMode.Position);
				driveTalons[i].setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
				driveTalons[i].reverseSensor(true);
				driveTalons[i].setPID(p, I, d);
				driveTalons[i].enableControl();

				if (i == 1 || i == 3)
				{
					driveTalons[i].changeControlMode(CANTalon.ControlMode.Follower);
					driveTalons[i].set(i - 1);
				}
				else
				{
					if (!(mode instanceof CANTalon.ControlMode))
						throw new TKORuntimeException("CODE ERROR! Wrong control mode used (jag vs talon)");
					driveTalons[i].changeControlMode(mode);
					driveTalons[i].setPID(p, I, d);
					driveTalons[i].enableControl();
				}
				driveTalons[i].enableBrakeMode(Definitions.DRIVE_BRAKE_MODE[i]);
				driveTalons[i].reverseOutput(Definitions.DRIVE_REVERSE_MODE[i]);
			}
		}

	}*/

	private static void configLiftTalons(double liftP, double liftI, double liftD, ControlMode liftTalonsNormalControlMode)
	{
		// TODO Auto-generated method stub

	}

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
		if (driveTalons[0] == null)
			throw new TKOException("NULL LEFT DRIVE TALON");
		return driveTalons[0];
	}

	public static synchronized CANTalon getRightDrive() throws TKOException
	{
		if (driveTalons[2] == null)
			throw new TKOException("NULL LEFT DRIVE TALON");
		return driveTalons[2];
	}

	public static synchronized Joystick[] getJoysticks() throws TKOException
	{
		if (joysticks == null)
			throw new TKOException("NULL STICK ARRAY");
		return joysticks;
	}

	public static synchronized CANTalon[] getDriveTalons() throws TKOException
	{
		if (driveTalons == null)
			throw new TKOException("NULL DRIVE ARRAY");
		return driveTalons;
	}

	public static synchronized DoubleSolenoid[] getPistons() throws TKOException
	{
		if (pistonSolenoids == null)
			throw new TKOException("NULL PISTON ARRAY");
		return pistonSolenoids;
	}

}
