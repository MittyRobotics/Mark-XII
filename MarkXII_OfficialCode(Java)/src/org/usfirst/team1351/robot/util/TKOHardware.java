// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.util;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.CANTalon;
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
	protected static DoubleSolenoid piston;
	protected static Compressor comp;
	protected static Encoder encoder_L, encoder_R;
//	protected static DigitalInput limitSwitch[] = new DigitalInput[Definitions.NUM_SWITCHES];

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
		
		configTalons(1., 0., 0.); // THIS IS WHERE PID IS SET TODO FIX THIS SHIT SOMEONE REMIND ME!!! 
	}

	public static synchronized void configTalons(double p, double I, double d)
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
					drive[i].changeControlMode(CANTalon.ControlMode.PercentVbus);
				}
			}
		}
	}

	public static synchronized void setAll(double setTarget)
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

	public static synchronized CANTalon[] getDriveTalons() throws TKOException
	{
		return drive;
	}

	public static synchronized Joystick[] getJoysticks() throws TKOException
	{
		return stick;
	}
	
	public static synchronized DoubleSolenoid getPiston() throws TKOException
	{
		return piston;
	}
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
}
