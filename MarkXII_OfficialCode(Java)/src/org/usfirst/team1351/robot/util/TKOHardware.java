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
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.can.CANMessageNotFoundException;
import edu.wpi.first.wpilibj.util.AllocationException;

public class TKOHardware
{
	protected static CANTalon drive[] = new CANTalon[Definitions.NUM_DRIVE_TALONS];
	protected static Joystick stick[] = new Joystick[Definitions.NUM_JOYSTICKS];
	protected static DoubleSolenoid piston[] = new DoubleSolenoid[Definitions.NUM_PISTONS];
	protected static DigitalInput limitSwitch[] = new DigitalInput[Definitions.NUM_SWITCHES];
	protected static Compressor comp = null;
	protected static BuiltInAccelerometer acc = null;

	protected static CANTalon lift = null;
	protected static Encoder liftEnc = null;

	public TKOHardware()
	{
		for (int i = 0; i < Definitions.NUM_JOYSTICKS; i++)
		{
			stick[i] = null;
		}
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			drive[i] = null;
		}
		for (int i = 0; i < Definitions.NUM_PISTONS; i++)
		{
			piston[i] = null;
		}
	}

	public static synchronized void initObjects()
	{
		for (int i = 0; i < Definitions.NUM_JOYSTICKS; i++)
		{
			if (stick[i] == null)
				stick[i] = new Joystick(Definitions.JOYSTICK_ID[i]);
		}
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			if (drive[i] == null)
			{
				try
				{
					drive[i] = new CANTalon(Definitions.DRIVE_TALON_ID[i]);
					drive[i].enableBrakeMode(false);
				} catch (AllocationException | CANMessageNotFoundException e)
				{
					e.printStackTrace();
					System.out.println("MOTOR CONTROLLER " + i + " NOT FOUND OR IN USE");
					TKOLogger.getInstance().addMessage("MOTOR CONTROLLER " + i + " CAN ERROR");
				}
			}
		}

		if (piston[0] == null)
			piston[0] = new DoubleSolenoid(Definitions.SHIFTER_A, Definitions.SHIFTER_B);

		if (piston[1] == null)
			piston[1] = new DoubleSolenoid(Definitions.GRIPPER_A, Definitions.GRIPPER_B);
		if (piston[2] == null)
			piston[2] = new DoubleSolenoid(Definitions.WHEELIE_L_A, Definitions.WHEELIE_L_B);
		if (piston[3] == null)
			piston[3] = new DoubleSolenoid(Definitions.WHEELIE_R_A, Definitions.WHEELIE_R_B);
		
		if (comp == null)
			comp = new Compressor(Definitions.PCM_ID);

		if (acc == null)
			acc = new BuiltInAccelerometer();

		if (lift == null)
			lift = new CANTalon(Definitions.LIFT_TALON_ID);

		if (liftEnc == null)
			liftEnc = new Encoder(Definitions.LIFT_ENCODER_A, Definitions.LIFT_ENCODER_B);

		configDriveTalons(Definitions.DRIVE_P, Definitions.DRIVE_I, Definitions.DRIVE_D, Definitions.DRIVE_TALONS_CONTROL_MODE);

		// TODO this is pretty ghetto
		lift.changeControlMode(CANTalon.ControlMode.PercentVbus);
	}

	public static synchronized void configDriveTalons(double p, double I, double d, ControlMode mode)
	{
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			drive[i].delete();
			drive[i] = null;
			drive[i] = new CANTalon(Definitions.DRIVE_TALON_ID[i]);
			if (drive[i] != null)
			{
				if (i == 1 || i == 3)
				{
					drive[i].changeControlMode(CANTalon.ControlMode.Follower);
					drive[i].set(i - 1);
				} else if (i == 0)
				{
					drive[i].setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
					drive[i].changeControlMode(CANTalon.ControlMode.Speed);
					drive[i].reverseSensor(true);
					drive[i].setPID(p, I, d);
					drive[i].enableControl();
				} else
				{
					if (!(mode instanceof CANTalon.ControlMode))
						throw new TKORuntimeException("CODE ERROR! Wrong control mode used (jag vs talon)");
					drive[i].changeControlMode(mode);
					drive[i].setPID(p, I, d);
					drive[i].enableControl();
				}
			}
		}
		/*
		 * drive[0].reverseOutput(true); drive[1].reverseOutput(false); drive[2].reverseOutput(false); drive[3].reverseOutput(false);
		 */
	}

	public static synchronized void setAllDriveTalons(double setTarget)
	{
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			if (drive[i] != null)
			{
				drive[i].set(setTarget);
			}
		}
	}

	public static synchronized void destroyObjects()
	{
		for (int i = 0; i < Definitions.NUM_JOYSTICKS; i++)
		{
			if (stick[i] != null)
			{
				stick[i] = null;
			}
		}
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			if (drive[i] != null)
			{
				drive[i].delete();
				drive[i] = null;
			}
		}
		for (int i = 0; i < Definitions.NUM_PISTONS; i++)
		{
			if (piston[i] != null)
			{
				piston[i].free();
				piston[i] = null;
			}
		}
		if (comp != null)
		{
			comp.free();
			comp = null;
		}

		if (acc != null)
			acc = null;

		if (lift != null)
			lift = null;

		if (liftEnc != null)
			liftEnc = null;
	}

	public static synchronized Encoder getLiftEncoder() throws TKOException
	{
		if (liftEnc == null)
			throw new TKOException("LIFT ENCODER IS NULL");
		return liftEnc;
	}

	public static synchronized CANTalon getLiftTalon() throws TKOException
	{
		if (lift == null)
			throw new TKOException("LIFT TALON IS NULL");
		return lift;
	}

	public static synchronized CANTalon getDriveTalon(int num) throws TKOException
	{
		if (num > Definitions.NUM_DRIVE_TALONS)
		{
			throw new TKOException("Drive talon requested out of bounds");
		}
		if (drive[num] != null)
			return drive[num];
		else
			throw new TKOException("Drive talon " + (num) + "(array value) is null");
	}

	public static synchronized Joystick getJoystick(int num) throws TKOException
	{
		if (num > Definitions.NUM_JOYSTICKS)
		{
			throw new TKOException("Joystick requested out of bounds");
		}
		if (stick[num] != null)
			return stick[num];
		else
			throw new TKOException("Joystick " + (num) + "(array value) is null");
	}

	public static synchronized DoubleSolenoid getPiston(int num) throws TKOException
	{
		if (num > Definitions.NUM_JOYSTICKS)
		{
			throw new TKOException("Piston requested out of bounds");
		}
		if (piston[num] != null)
			return piston[num];
		else
			throw new TKOException("Piston " + (num) + "(array value) is null");
	}

	public static synchronized Compressor getCompressor() throws TKOException
	{
		if (comp == null)
			throw new TKOException("NULL COMPRESSOR");
		return comp;
	}

	public static synchronized CANTalon[] getDriveTalons() throws TKOException
	{
		if (drive == null)
			throw new TKOException("NULL DRIVE ARRAY");
		return drive;
	}

	public static synchronized CANTalon getLeftDrive() throws TKOException
	{
		if (drive[0] == null)
			throw new TKOException("NULL LEFT DRIVE TALON");
		return drive[0];
	}

	public static synchronized CANTalon getRightDrive() throws TKOException
	{
		if (drive[2] == null)
			throw new TKOException("NULL LEFT DRIVE TALON");
		return drive[2];
	}

	public static synchronized Joystick[] getJoysticks() throws TKOException
	{
		if (stick == null)
			throw new TKOException("NULL STICK ARRAY");
		return stick;
	}

	public static synchronized DoubleSolenoid[] getPistons() throws TKOException
	{
		if (piston == null)
			throw new TKOException("NULL PISTON ARRAY");
		return piston;
	}

	public static synchronized BuiltInAccelerometer getAcc() throws TKOException
	{
		if (acc == null)
			throw new TKOException("NULL ACCELEROMETER OBJECT");
		return acc;
	}
}
