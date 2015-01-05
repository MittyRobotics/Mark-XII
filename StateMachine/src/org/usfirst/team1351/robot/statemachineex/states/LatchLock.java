package org.usfirst.team1351.robot.statemachineex.states;

import org.usfirst.team1351.robot.statemachineex.InstanceData;
import org.usfirst.team1351.robot.statemachineex.StateEnum;
import org.usfirst.team1351.robot.statemachineex.StateFunction;
import org.usfirst.team1351.robot.statemachineex.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class LatchLock implements StateFunction{

	@Override
	public StateEnum doState(InstanceData data) {
//		setArmMoveable(false);
//		TKOLEDArduino::inst()->setMode(1);
//		TKOLogger::inst()->addMessage("STATE ENTER Piston retract; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    // reason is that 0b0010 = 2 is piston extended
	    if (StateMachine.createIntFromBoolArray(data) != StateMachine.DONE_FIRING) {
//	    	TKOLogger::inst()->addMessage("STATE ERROR ENTER Piston retract; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	        return StateEnum.STATE_ERR;
	    }
	    data.curState = StateEnum.STATE_PISTON_RETRACT;
	    StateMachine.getTimer().reset();
	    StateMachine.getTimer().start();

	    StateMachine.getPistonSol().set(Value.kReverse);
//	    TKOLogger::inst()->addMessage("STATE ACTION Piston retract; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    
	    int sensors = StateMachine.getSensorData(data);
	    // reason for 8 is that piston is retracted then
	    while (sensors != StateMachine.PISTON_RETRACTED && (sensors == 0 || sensors == StateMachine.DONE_FIRING) ) {
	    	//printf("Piston Retract running: %d  Sensors: %d\n", sensors != PISTON_RETRACTED && (sensors == 0 || sensors == DONE_FIRING), sensors);
	        if (StateMachine.getTimer().get() > StateMachine.PISTON_RETRACT_TIMEOUT) {
	            StateMachine.getTimer().stop();
	            StateMachine.getTimer().reset();
//	            TKOLogger::inst()->addMessage("STATE ERROR TIMEOUT Piston retract; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	            return StateEnum.STATE_ERR;
	        }
	    }

	    StateMachine.getTimer().stop();
	    StateMachine.getTimer().reset();

	    if (sensors != StateMachine.PISTON_RETRACTED)
	    {
//	    	TKOLogger::inst()->addMessage("STATE ERROR EXIT Piston retract; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	        return StateEnum.STATE_ERR;
	    }
	    
//	    TKOLogger::inst()->addMessage("`STATE SUCCESS EXIT Piston retract; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    return StateEnum.STATE_LATCH_LOCK;
	}

}
