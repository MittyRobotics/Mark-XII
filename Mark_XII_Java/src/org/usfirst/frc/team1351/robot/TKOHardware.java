package org.usfirst.frc.team1351.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;

public class TKOHardware
{
	protected static CANTalon drive[] = new CANTalon[Definitions.NUM_DRIVE_JAGS];
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
			if (stick[i] != null)
				stick[i] = new Joystick(Definitions.JOYSTICK_ID[i]);
		}
		for (int i = 1; i <= Definitions.NUM_DRIVE_JAGS; i++)
		{
			if (drive[i] != null)
				drive[i] = new CANTalon(Definitions.DRIVE_TALON_ID[i]);
			
			drive[i].changeControlMode(CANTalon.ControlMode.PercentVbus);
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
	
	public static CANTalon getDriveTalon(int num)
	{
		if (num > Definitions.NUM_JOYSTICKS)
		{
			return null;
		}
		if (stick[num])
		return null;
	}
	
	public static Joystick getJoystick(int num)
	{
		return null;
	}
}
