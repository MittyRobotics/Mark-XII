// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.main;

import java.util.ArrayList;
import java.util.HashMap;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;

public class Definitions
{
	public static final double DISTANCE_PER_PULSE = 0.;
	
	public static final int NUM_DRIVE_TALONS = 4;
	public static final int NUM_LIFT_TALONS = 2;
	public static final int NUM_JOYSTICKS = 4;
	public static final int NUM_ENCODERS = 3;
	
	// TODO replace BS values
	public static final int NUM_PISTONS = 3; // number of piston, also is number of solenoids
	public static final int GRIPPER_A = 6; // gripper piston
	public static final int GRIPPER_B = 7;
	public static final int SHIFTER_A = 2; // drive train shifting piston
	public static final int SHIFTER_B = 3;
	public static final int WHEELIE_A = 0; // piston for left side of wheelie bar
	public static final int WHEELIE_B = 1;

	public static final int CRATE_SENSOR_ID = 3;
	
    public static final int NUM_SWITCHES = 7; // 5 for state machine + 2 for gripper top/bottom
	public static final int NUM_ANALOG = 2; // crate left and crate right

	public static final int[] JOYSTICK_ID =
	{ 0, 1, 2, 3 };
	public static final int[] DRIVE_TALON_ID =
	{ 0, 1, 2, 3 };
	public static final int[] LIFT_TALON_ID =
	{ 4, 5 };

	public static final boolean[] DRIVE_REVERSE_OUTPUT_MODE =
	{ true, false, false, false };
	public static final boolean[] DRIVE_BRAKE_MODE =
	{ false, false, false, false };

	public static final boolean[] LIFT_REVERSE_OUTPUT_MODE =
	{ true, false };
	public static final boolean[] LIFT_BRAKE_MODE =
	{ false, false };
	
	public static final int LIFT_CONTROL_STICK = 3;

	public static final int LIFT_GRIPPER_SWITCH = 2;
	public static final int LIFT_BOTTOM_OPTICAL_SWITCH = 0;
	public static final int LIFT_TOP_OPTICAL_SWITCH = 1;
	public static final double LIFT_PID_INCREMENTER = 100.;

	public static final double DRIVE_P = 4.;
	public static final double DRIVE_I = 0.01;
	public static final double DRIVE_D = 0;

	public static final double LIFT_P = 2.;
	public static final double LIFT_I = 0.01;
	public static final double LIFT_D = 0;

	public static final double LIFT_CALIBRATION_POWER = .4;

	public static final CANTalon.ControlMode DRIVE_TALONS_NORMAL_CONTROL_MODE = CANTalon.ControlMode.Current;
	public static final CANTalon.FeedbackDevice DRIVE_ENCODER_TYPE = CANTalon.FeedbackDevice.QuadEncoder;

	public static final CANTalon.ControlMode LIFT_TALONS_NORMAL_CONTROL_MODE = CANTalon.ControlMode.Position;
	public static final CANTalon.FeedbackDevice LIFT_ENCODER_TYPE = CANTalon.FeedbackDevice.QuadEncoder;

	public static final CANTalon.FeedbackDevice DEF_ENCODER_TYPE = CANTalon.FeedbackDevice.QuadEncoder;

	public static final double[] DRIVE_MULTIPLIER =
	{ -1., -1., 1., 1. };
	public static final double DRIVE_MULTIPLIER_LEFT = DRIVE_MULTIPLIER[0];
	public static final double DRIVE_MULTIPLIER_RIGHT = DRIVE_MULTIPLIER[2];

	public static final double MAX_CURRENT_LEFT = 10.; // used for current driving
	public static final double MAX_CURRENT_RIGHT = 10.;

	public static final double[] TALON_CURRENT_TIMEOUT =
	{ 100, 100, 100, 100, 50, 50 };
	public static final long[] CURRENT_TIMEOUT_LENGTH =
	{ 1000L, 1000L, 1000L, 1000L, 1000L, 1000L };

	public static final int DEF_DATA_REPORTING_THREAD_WAIT = 250;
	public static final int PCM_ID = 0;

	public static final double INCHES_PER_VOLT = 1.;
	public static final double CRATE_DISTANCE_THRESHOLD = 5.1;

	public static final int GYRO_ANALOG_CHANNEL = 1;

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
