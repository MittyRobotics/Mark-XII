package org.usfirst.team1351.robot.evom;

public enum Action
{
	DONE(0),
	ASCENDING(1),
	DESCENDING(2),
	THINKING(3);
	
	private int value;
	
	Action (int val)
	{
		value = val;
	}
	
	public int getValue() {
		return value;
	}
}
