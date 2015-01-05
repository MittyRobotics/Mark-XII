package org.usfirst.team1351.robot.statemachineex.states;

import org.usfirst.team1351.robot.statemachineex.InstanceData;
import org.usfirst.team1351.robot.statemachineex.StateEnum;
import org.usfirst.team1351.robot.statemachineex.StateFunction;
import org.usfirst.team1351.robot.statemachineex.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class PistonExtend implements StateFunction {

	@Override
	public StateEnum doState(InstanceData data) {
//		TKOLogger::inst()->addMessage("STATE ENTER Piston extend; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
//		setArmMoveable(false);
//		TKOLEDArduino::inst()->setMode(3);
		
	    // reason is that 0b0100 = 4 is piston extended
	    if (StateMachine.createIntFromBoolArray(data) != StateMachine.LATCH_LOCKED_PISTON_RETRACTED) {
//	    	TKOLogger::inst()->addMessage("STATE ERROR ENTER Piston extend; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	        return StateEnum.STATE_ERR;
	    }
	    data.curState = StateEnum.STATE_PISTON_EXTEND;
	    StateMachine.getTimer().reset();
	    StateMachine.getTimer().start();

	    StateMachine.getPistonSol().set(Value.kForward);
//	    TKOLogger::inst()->addMessage("STATE ACTION Piston extend; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));

	    int sensors = StateMachine.getSensorData(data);

	    // reason for 8 is that piston is retracted then
	    while (sensors != StateMachine.CONST_READY_TO_FIRE && (sensors == 12 || sensors == StateMachine.LATCH_LOCKED_PISTON_RETRACTED || sensors == 4 || sensors == 6)) {
	    	//printf("piston_extend running: %d\n", sensors != CONST_READY_TO_FIRE && (sensors == 12 || sensors == LATCH_LOCKED_PISTON_RETRACTED || sensors == 4 || sensors == 6));
	    	    	 
	    	if (StateMachine.getTimer().get() > StateMachine.PISTON_EXTEND_TIMEOUT) {
	            StateMachine.getTimer().stop();
	            StateMachine.getTimer().reset();
//	            TKOLogger::inst()->addMessage("STATE ERROR TIMEOUT Piston extend; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	            return StateEnum.STATE_ERR;
	        }
	    }
	    StateMachine.getTimer().stop();
	    StateMachine.getTimer().reset();

	    if (sensors != StateMachine.CONST_READY_TO_FIRE)
	    {
//	    	TKOLogger::inst()->addMessage("STATE ERROR EXIT Piston extend; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	        return StateEnum.STATE_ERR;
	    }

//	    TKOLogger::inst()->addMessage("STATE SUCCESS EXIT Piston extend; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    return StateEnum.STATE_READY_TO_FIRE;
	}

}
