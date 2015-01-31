package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

public class ReadyForRC implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		if (StateMachine.createIntFromBoolArray(data) != StateMachine.READY_FOR_RC)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_READY_FOR_RC;
	    
	    int sensors = StateMachine.getSensorData(data);

	    int bs = 0;
	    while (sensors != StateMachine.RC_FOUND && (sensors == bs))
	    {

	    }

	    if (sensors != StateMachine.RC_FOUND)
	    {
	        return StateEnum.STATE_ERR;
	    }

	    return StateEnum.STATE_CLOSE_GRIPPER;
	}
}