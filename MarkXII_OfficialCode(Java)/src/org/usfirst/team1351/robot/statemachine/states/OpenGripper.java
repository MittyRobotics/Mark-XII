package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

public class OpenGripper implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering OpenGripper state");
		
		if (StateMachine.createIntFromBoolArray(data) != StateMachine.PISTON_EXTENDED)
		{
			return StateEnum.STATE_ERR;
		}
			
		data.curState = StateEnum.STATE_OPEN_GRIPPER;
	    StateMachine.getTimer().reset();
	    StateMachine.getTimer().start();

	    StateMachine.getGripperSol().set(DoubleSolenoid.Value.kReverse);
	    
	    int sensors = StateMachine.getSensorData(data);

		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |     |   8 |     |   2 |     | = 10
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |     |   8 |     |     |     | = 8
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |     |     |     |   2 |     | = 2
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |     |     |     |     |     | = 0
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |     |     |     | = 16
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |     |     |   4 |     |     | = 4
	    
	    while (sensors != StateMachine.PISTON_RETRACTED &&
	    		(sensors == 10 || sensors == 8 || sensors == 2 || sensors == 0 || sensors == 16 || sensors == 4))
	    {
	    	if (StateMachine.getTimer().get() > StateMachine.PISTON_RETRACT_TIMEOUT)
	    	{
	            StateMachine.getTimer().stop();
	            StateMachine.getTimer().reset();
	            return StateEnum.STATE_ERR;
	        }
	    	Timer.delay(0.1);
	    }
	    StateMachine.getTimer().stop();
	    StateMachine.getTimer().reset();

	    if (sensors != StateMachine.PISTON_RETRACTED)
	    {
	        return StateEnum.STATE_ERR;
	    }

	    System.out.println("Exiting OpenGripper state");
	    return StateEnum.STATE_READY_FOR_RC;
	}
}