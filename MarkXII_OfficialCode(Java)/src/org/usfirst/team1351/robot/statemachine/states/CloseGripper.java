package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

public class CloseGripper implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering CloseGripper state");
		
		if (StateMachine.createIntFromBoolArray(data) != StateMachine.RC_FOUND)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_CLOSE_GRIPPER;
	    StateMachine.getTimer().reset();
	    StateMachine.getTimer().start();

	    StateMachine.getGripperSol().set(DoubleSolenoid.Value.kForward);
	    
	    int sensors = StateMachine.getSensorData(data);
	    
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |  16 |     |   4 |     |     | = 52
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |  16 |     |     |     |     | = 48
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |     |   4 |     |     | = 36
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |     |     |     |     | = 32
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |   8 |     |     |     | = 40
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |     |     |   2 |     | = 34
	    
	    while (sensors != StateMachine.READY_TO_LIFT &&
	    		(sensors == 52 || sensors == 48 || sensors == 36 || sensors == 32 || sensors == 40 || sensors == 34))
	    {
	    	if (StateMachine.getTimer().get() > StateMachine.PISTON_EXTEND_TIMEOUT)
	    	{
	            StateMachine.getTimer().stop();
	            StateMachine.getTimer().reset();
	            return StateEnum.STATE_ERR;
	        }
	    	Timer.delay(0.1);
	    }
	    StateMachine.getTimer().stop();
	    StateMachine.getTimer().reset();

	    if (sensors != StateMachine.READY_TO_LIFT)
	    {
	        return StateEnum.STATE_ERR;
	    }

	    System.out.println("Exiting CloseGripper state");
	    return StateEnum.STATE_LIFT_CRATE;
	}
}