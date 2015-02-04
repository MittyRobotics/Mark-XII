// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.util;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CANTalon;
<<<<<<< HEAD
import edu.wpi.first.wpilibj.CANTalon.ControlMode;
=======
>>>>>>> 56074ee859d1aabfcb88f1e57997f1311ab909ee
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
<<<<<<< HEAD
	protected static DoubleSolenoid piston[] = new DoubleSolenoid[Definitions.NUM_PISTONS];
	protected static DigitalInput limitSwitch[] = new DigitalInput[Definitions.NUM_SWITCHES];
	protected static Compressor comp = null;
	protected static BuiltInAccelerometer acc = null;
=======
	protected static DoubleSolenoid piston;
	protected static Compressor comp;
	protected static Encoder encoder_L, encoder_R;
//	protected static DigitalInput limitSwitch[] = new DigitalInput[Definitions.NUM_SWITCHES];
>>>>>>> 56074ee859d1aabfcb88f1e57997f1311ab909ee

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
		piston = null;
		comp = null;
		encoder_L = null;
		encoder_R = null;
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
				} catch (AllocationException | CANMessageNotFoundException e)
				{
					e.printStackTrace();
					System.out.println("MOTOR CONTROLLER " + i + " NOT FOUND OR IN USE");
					TKOLogger.getInstance().addMessage("MOTOR CONTROLLER " + i + " CAN ERROR");
				}
			}
		}
<<<<<<< HEAD

		if (piston[0] == null)
			piston[0] = new DoubleSolenoid(Definitions.SHIFTER_A, Definitions.SHIFTER_B);
		
		if (comp == null)
			comp = new Compressor(Definitions.PCM_ID);
=======
		
        if (piston == null) {
            piston = new DoubleSolenoid(Definitions.PISTON_SOLENOID_A, Definitions.PISTON_SOLENOID_B);
        }
        if (comp == null) {
            comp = new Compressor(Definitions.COMP_ID);
        }
        if (encoder_L == null)
        {
        	encoder_L = new Encoder(Definitions.ENCODER_LEFT_A, Definitions.ENCODER_LEFT_B);
        }
        if (encoder_R == null)
        {
        	encoder_R = new Encoder(Definitions.ENCODER_RIGHT_A, Definitions.ENCODER_RIGHT_B);
        }
>>>>>>> 56074ee859d1aabfcb88f1e57997f1311ab909ee
		
		if (acc == null)
			acc = new BuiltInAccelerometer();

		configDriveTalons(Definitions.DRIVE_P, Definitions.DRIVE_I, Definitions.DRIVE_D, Definitions.DRIVE_TALONS_CONTROL_MODE);
	}

	public static synchronized void configDriveTalons(double p, double I, double d, ControlMode mode)
	{
		for (int i = 0; i < Definitions.NUM_DRIVE_TALONS; i++)
		{
			if (drive[i] != null)
			{
				if (i == 1 || i == 3)
				{
					drive[i].changeControlMode(CANTalon.ControlMode.Follower);
					drive[i].set(i - 1);
				} else
				{
					if (!(mode instanceof CANTalon.ControlMode))
						throw new TKORuntimeException("CODE ERROR! Wrong control mode used (jag vs talon)");
					drive[i].changeControlMode(mode);
				}
			}
		}
		/*drive[0].reverseOutput(true);
		drive[1].reverseOutput(false);
		drive[2].reverseOutput(false);
		drive[3].reverseOutput(false);*/
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
<<<<<<< HEAD
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
=======
        if (piston != null)
        {
            piston.free();
            piston = null;
        }
        if (comp != null)
            comp = null;
        if (encoder_L != null)
        	encoder_L = null;
        if (encoder_R != null)
        	encoder_R = null;
>>>>>>> 56074ee859d1aabfcb88f1e57997f1311ab909ee
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
<<<<<<< HEAD

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
=======
>>>>>>> 56074ee859d1aabfcb88f1e57997f1311ab909ee

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
<<<<<<< HEAD

	public static synchronized DoubleSolenoid[] getPistons() throws TKOException
=======
	
	public static synchronized DoubleSolenoid getPiston() throws TKOException
>>>>>>> 56074ee859d1aabfcb88f1e57997f1311ab909ee
	{
		if (piston == null)
			throw new TKOException("NULL PISTON ARRAY");
		return piston;
	}
<<<<<<< HEAD
	
	public static synchronized BuiltInAccelerometer getAcc() throws TKOException
	{
		if (acc == null)
			throw new TKOException("NULL ACCELEROMETER OBJECT");
		return acc;
	}
=======
    public static synchronized Compressor getCompressor() throws TKOException
    {
        return comp;
    }
    public static synchronized Encoder getLeftEncoder() throws TKOException
    {
        return encoder_L;
    }
    public static synchronized Encoder getRightEncoder() throws TKOException
    {
        return encoder_R;
    }
>>>>>>> 56074ee859d1aabfcb88f1e57997f1311ab909ee
}
