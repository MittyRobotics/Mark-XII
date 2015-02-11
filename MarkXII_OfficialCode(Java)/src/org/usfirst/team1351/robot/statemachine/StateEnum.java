package org.usfirst.team1351.robot.statemachine;

// based on diagram "StateMachine 2015 v1.2"

public enum StateEnum
{
	STATE_DECIDE_ACTION(0),
	STATE_OPEN_GRIPPER(1),
	STATE_READY_FOR_RC(2),
	STATE_CLOSE_GRIPPER(3),
	STATE_LIFT_CRATE(4),
	STATE_LOOK_FOR_CRATE(5),
	STATE_DROP_ALL(6),
	STATE_RESET_LIFT(7),
	NUM_STATES(8), // excluding error state
	STATE_ERR(9);

	private int value;

	StateEnum(int val)
	{
		value = val;
	}

	public int getValue()
	{
		return value;
	}
}
