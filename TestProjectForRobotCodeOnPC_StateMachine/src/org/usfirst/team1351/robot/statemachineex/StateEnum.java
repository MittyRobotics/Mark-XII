package org.usfirst.team1351.robot.statemachineex;

public enum StateEnum {
	
	STATE_PISTON_RETRACT(0),
	STATE_LATCH_LOCK(1),
	STATE_PISTON_EXTEND(2),
	STATE_READY_TO_FIRE(3),
	STATE_LATCH_UNLOCK(4),
	NUM_STATES(5),
	STATE_ERR(6);
	
	private int value;
	
	StateEnum(int val)
	{
		value = val;
	}
	
	public int getValue() {
		return value;
	}
	
	
}
