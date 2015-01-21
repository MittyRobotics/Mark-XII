// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.main;

import java.util.ArrayList;
import java.util.HashMap;

public class Definitions
{
	public static final float DISTANCE_PER_PULSE = 0.f;

    public static final int ENCODER_LEFT_A = 0;
    public static final int ENCODER_LEFT_B = 1;
    public static final int ENCODER_RIGHT_A = 2;
    public static final int ENCODER_RIGHT_B = 3;
	
	public static final int NUM_DRIVE_TALONS = 4;
	public static final int NUM_JOYSTICKS = 4;

    public static final int PISTON_SOLENOID_A = 0;
    public static final int PISTON_SOLENOID_B = 1;
    public static final int COMP_ID = 0;

	public static final int[] JOYSTICK_ID =
	{ 0, 1, 2, 3 };
	public static final int[] DRIVE_TALON_ID =
	{ 0, 1, 2, 3 };
	// public static final double[] DRIVE_MULTIPLIER = {1., 1., -1., -1.};
	// public static final double[] DRIVE_MULTIPLIER =
	// { 7., 7., -7., -7. };
	public static final double[] DRIVE_MULTIPLIER =
	{ 1., 1., -1., -1. };
	public static final double[] TALON_CURRENT_TIMEOUT =
	{ 100, 100, 100, 100 };
	public static final long[] CURRENT_TIMEOUT_LENGTH =
	{ 1000L, 1000L, 1000L, 1000L };

	public static final int DEF_DATA_REPORTING_THREAD_WAIT = 250;
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

		default:
			return Thread.NORM_PRIORITY;
		}
	}

	public static void addThreadName(String name)
	{
		threadNames.add(name);
	}
}
