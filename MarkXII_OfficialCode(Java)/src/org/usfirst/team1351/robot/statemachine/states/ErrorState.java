package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;

public class ErrorState implements IStateFunction
{
	public StateEnum doState(InstanceData data)
	{
		System.out.println("ERROR STATE");
		return StateEnum.STATE_ERR;
	}
}