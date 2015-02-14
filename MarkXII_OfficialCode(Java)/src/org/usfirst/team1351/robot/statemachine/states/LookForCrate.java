package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

public class LookForCrate implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering LookForCrate state");
		
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |     |  16 |   8 |     |   2 |     |	= 26
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |     |     |     |   4 |     |   1 |	= 5
		
		if (StateMachine.createIntFromBoolArray(data) != 5 || StateMachine.createIntFromBoolArray(data) != 26)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_LOOK_FOR_CRATE;
		
		int sensors = StateMachine.getSensorData(data);

		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |     |  16 |   8 |     |   2 |     |	= 26
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |  64 |     |  16 |   8 |     |   2 |     |	= 90
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |  32 |  16 |   8 |     |   2 |     |	= 58
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |     |     |     |   4 |     |   1 |	= 5
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |  64 |     |     |     |   4 |     |   1 |	= 69
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |  32 |     |     |   4 |     |   1 |	= 37
		
		while ((sensors != StateMachine.CRATE_FOUND || sensors != 101) &&
				(sensors == 26 || sensors == 90 || sensors == 58) ||
				(sensors == 5 || sensors == 69 || sensors == 37))
		{
			if (StateMachine.getJoystick().getRawButton(11))
			{
				System.out.println("Exiting LookForCrate state");
			    return StateEnum.STATE_DROP_ALL;
			}
		}
			
		if (sensors != StateMachine.CRATE_FOUND)
			return StateEnum.STATE_ERR;
		
		System.out.println("Exiting LookForCrate state");
	    return StateEnum.STATE_LIFT_CRATE;
	}
}