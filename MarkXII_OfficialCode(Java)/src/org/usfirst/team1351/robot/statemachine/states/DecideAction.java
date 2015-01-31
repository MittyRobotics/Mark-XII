package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class DecideAction implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		int cur = StateMachine.createIntFromBoolArray(data);

		if (cur == StateMachine.PISTON_EXTENDED)
			return StateEnum.STATE_OPEN_GRIPPER;
		if (cur == StateMachine.READY_FOR_RC)
			return StateEnum.STATE_READY_FOR_RC;
		if (cur == StateMachine.READY_TO_LIFT)
			return StateEnum.STATE_LIFT_CRATE;
		
	    return StateEnum.STATE_ERR;
	}
}