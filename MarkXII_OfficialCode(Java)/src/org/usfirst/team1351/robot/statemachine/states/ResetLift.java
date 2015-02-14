package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

import edu.wpi.first.wpilibj.Timer;

public class ResetLift implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering ResetLift state");
		
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |     |  16 |   8 |     |   2 |     |	= 26
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |  64 |     |  16 |   8 |     |   2 |     |	= 90
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |  32 |  16 |   8 |     |   2 |     |	= 58
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |  64 |  32 |  16 |   8 |     |   2 |     |	= 122
		
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |     |     |     |   4 |     |   1 |	= 5
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |  64 |     |     |     |   4 |     |   1 |	= 69
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |  32 |     |     |   4 |     |   1 |	= 37
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |  64 |  32 |     |     |   4 |     |   1 |	= 101
		
		int cur = StateMachine.createIntFromBoolArray(data);
		
		if (cur != 26 || cur != 90 || cur != 58 || cur != 122 || cur != 5  || cur != 69 || cur != 37 || cur != 101)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_RESET_LIFT;

		double lvl = TKOLift.getInstance().getCurrentLevel();
		int pos = TKOLift.getInstance().getEncoderPosition();
		
		int sensors = StateMachine.getSensorData(data);
		
		/*while (sensors == 26 || cur == 90 || cur == 58 || cur == 122 || cur == 5  || cur == 69 || cur == 37 || cur == 101)
		{
			while (TKOLift.getInstance().getCurrentLevel() != 0)
			{
				TKOLift.getInstance().updateCustomPositionTarget();
				TKOLift.getInstance().goToPosition(position);
				Timer.delay(1.);
				lvl--;
				//pos -= TKOLift.getInstance().oneLevel;
				if (lvl != TKOLift.getInstance().getCurrentLevel())
					break;
			}
			break;
		}*/
		
		if (TKOLift.getInstance().getCurrentLevel() != 0)
			return StateEnum.STATE_ERR;
		
		System.out.println("Exiting DropAll state");
	    return StateEnum.STATE_DECIDE_ACTION;
	}
}