// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.util;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.can.CANMessageNotFoundException;
import edu.wpi.first.wpilibj.util.AllocationException;

public class TKOHardware
{
	protected static CANTalon drive[] = new CANTalon[Definitions.NUM_DRIVE_TALONS];
	protected static Joystick stick[] = new Joystick[Definitions.NUM_JOYSTICKS];
	protected static DoubleSolenoid piston[] = new DoubleSolenoid[Definitions.NUM_PISTONS];
	protected static DigitalInput limitSwitch[] = new DigitalInput[Definitions.NUM_SWITCHES];
	protected static Compressor comp = new Compressor(Definitions.PCM_ID);

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
		
		if (comp == null)
			comp = new Compressor(Definitions.PCM_ID);

		configTalons(10., 0., 0.);
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
		/*drive[0].reverseOutput(true);
		drive[1].reverseOutput(false);
		drive[2].reverseOutput(false);
		drive[3].reverseOutput(false);*/
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
}
