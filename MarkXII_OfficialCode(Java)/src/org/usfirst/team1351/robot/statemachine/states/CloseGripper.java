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
		System.out.println("Entering CloseGripper state");
		
		if (StateMachine.createIntFromBoolArray(data) != StateMachine.RC_FOUND)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_CLOSE_GRIPPER;
	    StateMachine.getTimer().reset();
	    StateMachine.getTimer().start();

	    StateMachine.getGripperSol().set(DoubleSolenoid.Value.kForward);
	    
	    int sensors = StateMachine.getSensorData(data);
	    
	    // 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
	 	// 0b |     |     |  16 |     |   4 |     |   1 |	= 21
	    // 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
	 	// 0b |     |     |  16 |     |     |     |   1 |	= 17
	    // 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
	 	// 0b |     |     |  16 |     |   4 |     |     |	= 20
	    // 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
	 	// 0b |     |     |  16 |     |     |     |     |	= 16
	    // 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
	 	// 0b |     |     |  16 |   8 |     |     |     |	= 24
	    // 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
	 	// 0b |     |     |  16 |     |     |   2 |     |	= 18
	    // 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
	 	// 0b |     |     |  16 |   8 |     |   2 |     |	= 26
	    while (sensors != StateMachine.READY_TO_LIFT &&
	    		(sensors == 21 || sensors == 17 || sensors == 20 || sensors == 16 || sensors == 24 || sensors == 18))
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

	    System.out.println("Exiting CloseGripper state");
	    return StateEnum.STATE_LIFT_CRATE;
	}
}