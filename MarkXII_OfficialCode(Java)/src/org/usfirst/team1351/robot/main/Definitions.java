// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.main;

import java.util.ArrayList;
import java.util.HashMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.ControlMode;

public class Definitions
{
	public static final int NUM_DRIVE_TALONS = 4;
	public static final int NUM_JOYSTICKS = 4;

	// TODO replace BS values
	public static final int NUM_PISTONS = 4;
	public static final int SHIFTER_A = 2;			// drive train shifting piston
	public static final int SHIFTER_B = 3;
	public static final int GRIPPER_A = 4;			// gripper piston
	public static final int GRIPPER_B = 5;
	public static final int WHEELIE_L_A = 6;		// piston for left side of wheelie bar
	public static final int WHEELIE_L_B = 7;
	public static final int WHEELIE_R_A = 8;		// piston for right side of wheelie bar
	public static final int WHEELIE_R_B = 9;
	
	public static final int NUM_SWITCHES = 4;

	public static final int[] JOYSTICK_ID =
	{ 0, 1, 2, 3 };
	public static final int[] DRIVE_TALON_ID =
	{ 0, 1, 2, 3 };
	
	public static final int LIFT_TALON_ID = 5;
	public static final int LIFT_ENCODER_A = 0;
	public static final int LIFT_ENCODER_B = 1;
	
	// public static final double[] DRIVE_MULTIPLIER = {1., 1., -1., -1.};
	// public static final double[] DRIVE_MULTIPLIER =
	// { 7., 7., -7., -7. };
	public static final double DRIVE_P = 4.;
	public static final double DRIVE_I = 0.01;
	public static final double DRIVE_D = 0;
	public static final double[] DRIVE_MULTIPLIER =
	{ -1., -1., 1., 1. };
	public static final double DRIVE_MULTIPLIER_LEFT = DRIVE_MULTIPLIER[0];
	public static final double DRIVE_MULTIPLIER_RIGHT = DRIVE_MULTIPLIER[2];
	public static final double MAX_CURRENT_LEFT = 10.;
	public static final double MAX_CURRENT_RIGHT = 10.;

	public static final double[] TALON_CURRENT_TIMEOUT =
	{ 100, 100, 100, 100 };
	public static final long[] CURRENT_TIMEOUT_LENGTH =
	{ 1000L, 1000L, 1000L, 1000L };

	public static final int DEF_DATA_REPORTING_THREAD_WAIT = 250;
	public static final int PCM_ID = 0;
	public static final ControlMode DRIVE_TALONS_CONTROL_MODE = CANTalon.ControlMode.Current;
	public static ArrayList<String> threadNames = new ArrayList<String>();
	public static HashMap<String, Integer> threadPriorities;

	public static int getPriority(String name)
	{
		switch (name)
		{
		case "drive":
			return Thread.NORM_PRIORITY - 3;
		case "logger":
			return Thread.NORM_PRIORITY + 2;
		case "dataReporting":
			return Thread.NORM_PRIORITY + 3;
		case "ledArduino":
			return Thread.NORM_PRIORITY + 1;
		case "gripper":
			return Thread.NORM_PRIORITY - 1;
		case "conveyor":
			return Thread.NORM_PRIORITY - 2;

		default:
			return Thread.NORM_PRIORITY;
		}
	}

	public static void addThreadName(String name)
	{
		threadNames.add(name);
	}
}
