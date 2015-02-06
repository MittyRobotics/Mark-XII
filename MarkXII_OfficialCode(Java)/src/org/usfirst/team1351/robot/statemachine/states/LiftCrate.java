package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.statemachine.*;

public class LiftCrate implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		if (StateMachine.createIntFromBoolArray(data) != StateMachine.READY_TO_LIFT)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_LIFT_CRATE;

		// TODO recovery state??
	    if (TKOLift.getInstance().getCurrentLevel() == 5)
	    	return StateEnum.STATE_ERR;
	    
	    int lvl = TKOLift.getInstance().getCurrentLevel();
	    
	    TKOLift.getInstance().goUp();
	    
	    int sensors = StateMachine.getSensorData(data);
	    
	    if (TKOLift.getInstance().getCurrentLevel() != (lvl + 1) || sensors != StateMachine.READY_TO_LIFT)
	    {
	    	return StateEnum.STATE_ERR;
	    }

	    return StateEnum.STATE_LOOK_FOR_CRATE;
	}
}