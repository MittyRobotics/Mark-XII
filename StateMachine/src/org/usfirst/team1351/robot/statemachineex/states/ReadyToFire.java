package org.usfirst.team1351.robot.statemachineex.states;

import org.usfirst.team1351.robot.statemachineex.InstanceData;
import org.usfirst.team1351.robot.statemachineex.StateEnum;
import org.usfirst.team1351.robot.statemachineex.IStateFunction;
import org.usfirst.team1351.robot.statemachineex.StateMachine;

public class ReadyToFire implements IStateFunction {

	@Override
	public StateEnum doState(InstanceData data) {
//		setArmMoveable(false);
//		TKOLEDArduino::inst()->setMode(4);
//		TKOLogger::inst()->addMessage("STATE ENTER Ready to Fire; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    // reason is that 0b0111 = 7 is piston extended, is cocked, and latch locked
	    if (StateMachine.createIntFromBoolArray(data) != StateMachine.CONST_READY_TO_FIRE) {
//	    	TKOLogger::inst()->addMessage("STATE ERROR ENTER Ready to Fire; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	        return StateEnum.STATE_ERR;
	    }
	    
	    data.curState = StateEnum.STATE_READY_TO_FIRE;

//		setArmMoveable(true);
	    
	    // wait for the trigger then fire!
		
	    // The while loop needs the arm class which has not been ported yet
	    
		while (!StateMachine.getJoystick().getTrigger())// /* || !_triggerJoystick->GetRawButton(3) */|| !TKOArm::inst()->armInFiringRange()) 
	    {
	    	/*DSLog(4, "READY TO FIRE");
	    	DSLog(5, "Arm status: %d", TKOArm::inst()->armInFiringRange());*/
//	    	if (StateMachine::forceFire and TKOArm::inst()->armInFiringRange())
//	    	{
//	    		StateMachine::forceFire = false;
//	    		break;
//	    	}
	    }
	    // go to next state
//	    TKOLogger::inst()->addMessage("STATE SUCCESS EXIT Ready to Fire; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    return StateEnum.STATE_LATCH_UNLOCK;
	}

}
