package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

public class ReadyForRC implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering ReadyForRC state");
		
		if (StateMachine.createIntFromBoolArray(data) != StateMachine.PISTON_RETRACTED)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_READY_FOR_RC;
		
		if (TKOLift.getInstance().getCurrentLevel() != TKOLift.trashcanPickupPosition)
		{
			TKOLift.getInstance().goToTrashcanPickup();
		}
	    
	    int sensors = StateMachine.getSensorData(data);
	    
	    while (sensors != StateMachine.RC_FOUND && sensors == StateMachine.PISTON_RETRACTED)
	    {
	    	if (StateMachine.getJoystick().getRawButton(9))
	    	{
	    		// skip to LiftCrate w/o a trash can
	    		System.out.println("Exiting ReadyForRC state");
	    	    return StateEnum.STATE_LIFT_CRATE;
	    	}
	    }

	    if (sensors != StateMachine.RC_FOUND)
	    {
	    	return StateEnum.STATE_ERR;
	    }

		System.out.println("Exiting ReadyForRC state");
	    return StateEnum.STATE_CLOSE_GRIPPER;
	}
}