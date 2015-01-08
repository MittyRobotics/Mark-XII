package org.usfirst.frc.team1351.robot.util;

import org.usfirst.frc.team1351.robot.main.*;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;

public class TKOHardware
{
	protected static CANJaguar drive[] = new CANJaguar[Definitions.NUM_DRIVE_JAGS];
	protected static Joystick stick[] = new Joystick[Definitions.NUM_JOYSTICKS];
	
	public TKOHardware()
	{
		for (int i = 1; i <= Definitions.NUM_JOYSTICKS; i++)
		{
			stick[i] = null;
		}
		for (int i = 1; i <= Definitions.NUM_DRIVE_JAGS; i++)
		{
			drive[i] = null;
		}
	}
	public static void initObjects()
	{
		for (int i = 1; i <= Definitions.NUM_JOYSTICKS; i++)
		{
			if (stick[i - 1] != null)
				stick[i - 1] = new Joystick(Definitions.JOYSTICK_ID[i - 1]);
		}
		for (int i = 1; i <= Definitions.NUM_DRIVE_JAGS; i++)
		{
			if (drive[i - 1] != null)
				drive[i - 1] = new CANJaguar(Definitions.DRIVE_JAGUAR_ID[i - 1]);
			
			//drive[i].changeControlMode(CANTalon.ControlMode.PercentVbus);
			drive[i - 1].setPercentMode();
			drive[i - 1].enableControl();
		}
	}
	
	public static void destroyObjects()
	{
		for (int i = 1; i <= Definitions.NUM_JOYSTICKS; i++)
		{
			if (stick[i] != null)
				stick[i] = null;
		}
		for (int i = 1; i <= Definitions.NUM_DRIVE_JAGS; i++)
		{
			if (drive[i] != null)
				drive[i] = null;
		}
	}
	
	public static CANJaguar getDriveJaguar(int num) throws Exception
	{
		if (num > Definitions.NUM_DRIVE_JAGS)
		{
			throw new Exception("Drive jaguar requested out of bounds");
		}
		if (drive[num - 1] != null)
			return drive[num - 1];
		else
			throw new Exception("Drive jaguar " + (num - 1) + "(array value) is null");
	}
	
	public static Joystick getJoystick(int num) throws Exception
	{
		if (num > Definitions.NUM_JOYSTICKS)
		{
			throw new Exception("Joystick requested out of bounds");
		}
		if (stick[num - 1] != null)
			return stick[num - 1];
		else
			throw new Exception("Joystick " + (num - 1) + "(array value) is null");
	}
	
	public static CANJaguar[] getDriveJaguars() throws Exception
	{
		return drive;
	}
	
	public static Joystick[] getJoysticks() throws Exception
	{
		return stick;
	}
}
