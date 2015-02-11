package org.usfirst.team1351.robot.statemachine;

public class InstanceData
{
	public StateEnum curState;
	public boolean state[] = new boolean[StateEnum.NUM_STATES.getValue()];
}