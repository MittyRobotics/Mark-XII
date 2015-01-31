package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class OpenGripper implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		// TODO unclear what previous state will be...
		if (StateMachine.createIntFromBoolArray(data) != StateMachine.PISTON_EXTENDED)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_OPEN_GRIPPER;
	    StateMachine.getTimer().reset();
	    StateMachine.getTimer().start();
	    
	    // retracting piston = opening gripper
	    StateMachine.getGripperSol().set(DoubleSolenoid.Value.kReverse);
	    
	    int sensors = StateMachine.getSensorData(data);

	    int bs = 0;
	    while (sensors != StateMachine.READY_FOR_RC && (sensors == bs))
	    {
	    	if (StateMachine.getTimer().get() > StateMachine.PISTON_RETRACT_TIMEOUT)
	    	{
	            StateMachine.getTimer().stop();
	            StateMachine.getTimer().reset();
	            return StateEnum.STATE_ERR;
	        }
	    }
	    StateMachine.getTimer().stop();
	    StateMachine.getTimer().reset();

	    if (sensors != StateMachine.READY_FOR_RC)
	    {
	        return StateEnum.STATE_ERR;
	    }

	    return StateEnum.STATE_READY_FOR_RC;
	}
}