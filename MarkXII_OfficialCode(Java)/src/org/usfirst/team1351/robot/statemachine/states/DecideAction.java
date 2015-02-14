package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.evom.TKOPneumatics;
import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

// This state is entered when the robot is enabled and after the lift is reset.

public class DecideAction implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering DecideAction state");
		
		TKOPneumatics.getInstance().notManual();
		
		int cur = StateMachine.createIntFromBoolArray(data);

		if (cur == StateMachine.PISTON_EXTENDED)	// gripper closed
		{
			System.out.println("Exiting DecideAction state");
			return StateEnum.STATE_OPEN_GRIPPER;
		}
		if (cur == StateMachine.PISTON_RETRACTED)		// gripper open
		{
			System.out.println("Exiting DecideAction state");
			return StateEnum.STATE_READY_FOR_RC;
		}
		if (cur == StateMachine.READY_TO_LIFT)		// gripper closed w/trash can
		{
			System.out.println("Exiting DecideAction state");
			return StateEnum.STATE_LIFT_CRATE;
		}
		
	    return StateEnum.STATE_ERR;
	}
}