package org.usfirst.team1351.robot.evom;

public enum Operation
{
	MANUAL_VBUS(0),
	PID_CRATES(1),
	CUSTOM_POSITION(2);
	
	private int value;
	
	Operation (int val)
	{
		value = val;
	}
	
	public int getValue() {
		return value;
	}
}
