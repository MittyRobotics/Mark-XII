package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class CloseGripper implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		if (StateMachine.createIntFromBoolArray(data) != StateMachine.RC_FOUND)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_CLOSE_GRIPPER;
	    StateMachine.getTimer().reset();
	    StateMachine.getTimer().start();
	    
	    // extending piston = closing gripper
	    StateMachine.getGripperSol().set(DoubleSolenoid.Value.kForward);
	    
	    int sensors = StateMachine.getSensorData(data);

	    int bs = 0;
	    while (sensors != StateMachine.READY_TO_LIFT && (sensors == bs))
	    {
	    	if (StateMachine.getTimer().get() > StateMachine.PISTON_EXTEND_TIMEOUT)
	    	{
	            StateMachine.getTimer().stop();
	            StateMachine.getTimer().reset();
	            return StateEnum.STATE_ERR;
	        }
	    }
	    StateMachine.getTimer().stop();
	    StateMachine.getTimer().reset();

	    if (sensors != StateMachine.READY_TO_LIFT)
	    {
	        return StateEnum.STATE_ERR;
	    }

	    return StateEnum.STATE_LIFT_CRATE;
	}
}