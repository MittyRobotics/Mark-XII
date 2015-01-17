package org.usfirst.team1351.robot.util;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.can.CANMessageNotFoundException;
import edu.wpi.first.wpilibj.util.AllocationException;

public class TKOHardware
{
	protected static CANTalon drive[] = new CANTalon[Definitions.NUM_DRIVE_JAGS];
	protected static Joystick stick[] = new Joystick[Definitions.NUM_JOYSTICKS];

	public TKOHardware()
	{
		for (int i = 0; i < Definitions.NUM_JOYSTICKS; i++)
		{
			stick[i] = null;
		}
		for (int i = 0; i < Definitions.NUM_DRIVE_JAGS; i++)
		{
			drive[i] = null;
		}
	}

	public static synchronized void initObjects()
	{
		for (int i = 0; i < Definitions.NUM_JOYSTICKS; i++)
		{
			if (stick[i] == null)
				stick[i] = new Joystick(Definitions.JOYSTICK_ID[i]);
		}
		for (int i = 0; i < Definitions.NUM_DRIVE_JAGS; i++)
		{
			if (drive[i] == null)
			{
				try
				{
					drive[i] = new CANTalon(Definitions.DRIVE_JAGUAR_ID[i]);
				} catch (AllocationException | CANMessageNotFoundException e)
				{
					e.printStackTrace();
					System.out.println("MOTOR CONTROLLER " + i + " NOT FOUND OR IN USE");
					TKOLogger.getInstance().addMessage("MOTOR CONTROLLER " + i + " CAN ERROR");
				}
			}
		}
		configJags(10., 0., 0.);
	}

	public static synchronized void configJags(double p, double I, double d)
	{
		drive[0].changeControlMode(CANTalon.ControlMode.PercentVbus);
		drive[0].enableControl();
		
		drive[1].changeControlMode(CANTalon.ControlMode.Follower);
		drive[1].set(0);
		
		drive[2].changeControlMode(CANTalon.ControlMode.PercentVbus);
		//drive[2].reverseOutput(true);
		drive[2].enableControl();
		
		drive[3].changeControlMode(CANTalon.ControlMode.Follower);
		drive[3].set(0);
	}

	public static synchronized void setZero()
	{
		for (int i = 0; i < Definitions.NUM_DRIVE_JAGS; i++)
		{
			if (drive[i] != null)
			{
				drive[i].set(0);
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
		for (int i = 0; i < Definitions.NUM_DRIVE_JAGS; i++)
		{
			if (drive[i] != null)
			{
				drive[i].delete();
				drive[i] = null;
			}
		}
	}

	public static synchronized CANTalon getDriveJaguar(int num) throws Exception
	{
		if (num > Definitions.NUM_DRIVE_JAGS)
		{
			throw new Exception("Drive jaguar requested out of bounds");
		}
		if (drive[num] != null)
			return drive[num];
		else
			throw new Exception("Drive jaguar " + (num) + "(array value) is null");
	}

	public static synchronized Joystick getJoystick(int num) throws Exception
	{
		if (num > Definitions.NUM_JOYSTICKS)
		{
			throw new Exception("Joystick requested out of bounds");
		}
		if (stick[num] != null)
			return stick[num];
		else
			throw new Exception("Joystick " + (num) + "(array value) is null");
	}

	public static synchronized CANTalon[] getDriveJaguars() throws Exception
	{
		return drive;
	}

	public static synchronized Joystick[] getJoysticks() throws Exception
	{
		return stick;
	}
}
