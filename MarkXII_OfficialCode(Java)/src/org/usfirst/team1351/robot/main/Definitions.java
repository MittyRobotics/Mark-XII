package org.usfirst.team1351.robot.main;

import java.util.ArrayList;
import java.util.HashMap;

public class Definitions
{
	public static final int NUM_DRIVE_JAGS = 4;
	public static final int NUM_JOYSTICKS = 4;
	public static final int[] JOYSTICK_ID =
	{ 0, 1, 2, 3 };
	public static final int[] DRIVE_JAGUAR_ID =
	{ 1, 2, 3, 4 };
	// public static final double[] DRIVE_MULTIPLIER = {1., 1., -1., -1.};
	public static final double[] DRIVE_MULTIPLIER =
	{ 5., 5., -5., -5. };
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

		default:
			return Thread.NORM_PRIORITY;
		}
	}

	public static void addThreadName(String name)
	{
		threadNames.add(name);
	}
}
