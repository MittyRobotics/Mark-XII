package org.usfirst.team1351.robot.statemachineex.states;

import org.usfirst.team1351.robot.statemachineex.InstanceData;
import org.usfirst.team1351.robot.statemachineex.StateEnum;
import org.usfirst.team1351.robot.statemachineex.IStateFunction;
import org.usfirst.team1351.robot.statemachineex.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class LatchUnlock implements IStateFunction {

	@Override
	public StateEnum doState(InstanceData data) {
//		setArmMoveable(false);
		// TKOLEDArduino::inst()->setMode(5);
		// TKOLogger::inst()->addMessage("STATE ENTER Latch Unlock; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    // reason is that 0b0111 = 7 is piston extended, is cocked, and latch locked
	    if (StateMachine.createIntFromBoolArray(data) != StateMachine.CONST_READY_TO_FIRE) // or ! TKOArm::inst()->armInFiringRange()) {
	    {
	    	// TKOLogger::inst()->addMessage("STATE ERROR ENTER Latch Unlock; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	        // TKOLogger::inst()->addMessage("Arm status: %d", TKOArm::inst()->armInFiringRange());
	    	return StateEnum.STATE_ERR;
	    }
	    
	    data.curState = StateEnum.STATE_LATCH_UNLOCK;
	    StateMachine.getTimer().reset();
	    StateMachine.getTimer().start();
	    
	    // TKORoller::inst()->override = true;
	    // TKORoller::inst()->_roller1.Set(1.);
	    // TKORoller::inst()->_roller2.Set(-1.);
//	    Wait(StateMachine.SHOOT_ROLLER_PRERUN_TIME); //timing for roller prerun
	    
	    StateMachine.getLatchSol().set(Value.kReverse);
	    // TKOLogger::inst()->addMessage("STATE ACTION Latch Unlock; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    int sensors = StateMachine.getSensorData(data);

	    // reason for 4 is that piston is extended after this step 
	    while (sensors != StateMachine.DONE_FIRING && (sensors == StateMachine.CONST_READY_TO_FIRE || sensors == 10))
	    {
	        if (StateMachine.getTimer().get() > StateMachine.LATCH_UNLOCK_REVERSE_TIMEOUT) {
	        	StateMachine.getTimer().stop();
	        	StateMachine.getTimer().reset();
	            // TKOLogger::inst()->addMessage("STATE ERROR TIMEOUT Latch Unlock; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	            
	            return StateEnum.STATE_ERR;
	        }
	    }
	    StateMachine.getTimer().stop();
	    StateMachine.getTimer().reset();

//	    Wait(POST_SHOOT_WAIT_TIME); //TODO figure this out
	    
		// TKORoller::inst()->_roller1.Set(0.);
		// TKORoller::inst()->_roller2.Set(0.);
	    // TKORoller::inst()->override = false;
	    
	    if (sensors != StateMachine.DONE_FIRING)
	    {
	    	// TKOLogger::inst()->addMessage("STATE ERROR EXIT Latch Unlock; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	        return StateEnum.STATE_ERR;
	    }

	    // TKOLogger::inst()->addMessage("STATE SUCCESS EXIT Latch Unlock; state: %s; sensors: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
	    // TKOLogger::inst()->addMessage("!!!SUCCESSFUL SHOT!!!");
	    // TKOLogger::inst()->addMessage("!!!Shot Distance: %f\n", (TKOArm::inst()->getUsonic()->GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET));
//	    StateMachine::autonFired = true;
	    return StateEnum.STATE_PISTON_RETRACT;
	}

}
