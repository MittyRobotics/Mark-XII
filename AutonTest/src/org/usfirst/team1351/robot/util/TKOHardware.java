package org.usfirst.team1351.robot.util;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.util.AllocationException;
import edu.wpi.first.wpilibj.can.CANMessageNotFoundException;

public class TKOHardware
{
	public static final double P = 0;
	public static final double I = 0;
	public static final double D = 0;
	
	public static final int NUM_JAGS = 4;
	public static final int NUM_STICKS = 4;
	protected static CANJaguar drive[] = new CANJaguar[NUM_JAGS];
	protected static Joystick stick[] = new Joystick[NUM_STICKS];

	public TKOHardware()
	{
		for (int i = 0; i < NUM_JAGS; i++)
		{
			drive[i] = null;
		}
		for (int i = 0; i < NUM_STICKS; i++)
		{
			stick[i] = null;
		}
	}

	public static synchronized void init()
	{
		for (int i = 0; i < NUM_STICKS; i++)
		{
			if (stick[i] == null)
				stick[i] = new Joystick(i);
		}
		for (int i = 0; i < NUM_JAGS; i++)
		{
			if (drive[i] == null)
			{
				try
				{
					drive[i] = new CANJaguar(i);
				} catch (AllocationException | CANMessageNotFoundException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		for (int i = 0; i < NUM_JAGS; i++)
		{
			drive[i].setVoltageMode();
			drive[i].enableControl();
		}
	}
	
	public static synchronized void configJags(double p, double i, double d)
	{	
		drive[0].setPositionMode(CANJaguar.kQuadEncoder, 256, 0, 0, 0);
		drive[0].enableControl();
		drive[0].setPID(p, i, d);
		
		drive[1].setVoltageMode();
		drive[1].enableControl();
		
		drive[2].setPositionMode(CANJaguar.kQuadEncoder, 256, 0, 0, 0);
		drive[2].enableControl();
		drive[2].setPID(p, i, d);
		
		drive[3].setVoltageMode();
		drive[3].enableControl();
	}
	
	public static synchronized void setZero
	
	public static synchronized void end()
	{
		for (int i = 0; i < NUM_JAGS; i++)
		{
			if (drive[i] != null)
			{
				drive[i].free();
				drive[i] = null;
			}
		}
		for (int i = 0; i < NUM_STICKS; i++)
		{
			if (stick[i] != null)
			{
				stick[i] = null;
			}
		}
	}

	public static synchronized CANJaguar getDriveJag(int num) throws Exception
	{
		if (num > NUM_JAGS)
		{
			throw new Exception("Jag requested out of bounds");
		}
		if (drive[num] != null)
			return drive[num];
		else
			throw new Exception("Drive jag " + (num) + "(array value) is null");
	}

	public static synchronized Joystick getStick(int num) throws Exception
	{
		if (num > NUM_STICKS)
		{
			throw new Exception("Joystick requested out of bounds");
		}
		if (stick[num] != null)
			return stick[num];
		else
			throw new Exception("Joystick " + (num) + "(array value) is null");
	}
}
