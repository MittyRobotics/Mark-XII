package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

import edu.wpi.first.wpilibj.Timer;

public class LookForCrate implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering LookForCrate state");
		
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |   8 |     |   2 |     | = 42
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |     | = 20
		
		int cur = StateMachine.createIntFromBoolArray(data);
		
		if (cur != StateMachine.READY_TO_LIFT || cur != StateMachine.PISTON_RETRACTED)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_LOOK_FOR_CRATE;
		
		int sensors = StateMachine.getSensorData(data);
		
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |   8 |     |   2 |     | = 42
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |   8 |     |   2 |   1 | = 43
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |     | = 20
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |   1 | = 21
		
		while ((sensors != 43 || sensors != 21) && (sensors == 42 || sensors == 20))
		{
			if (StateMachine.getJoystick().getRawButton(11))
			{
				System.out.println("Exiting LookForCrate state");
			    return StateEnum.STATE_DROP_ALL;
			}
			Timer.delay(0.1);
		}
			
		if (sensors != 43 || sensors != 21)
			return StateEnum.STATE_ERR;
		
		System.out.println("Exiting LookForCrate state");
	    return StateEnum.STATE_LIFT_CRATE;
	}
}