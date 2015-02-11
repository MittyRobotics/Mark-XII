package org.usfirst.team1351.robot.evom.states;

import org.usfirst.team1351.robot.evom.InstanceData;
import org.usfirst.team1351.robot.evom.StateEnum;
import org.usfirst.team1351.robot.evom.IStateFunction;

public class ErrorState implements IStateFunction
{
	public StateEnum doState(InstanceData data)
	{
		return StateEnum.STATE_ERR;
	}
}