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
		
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |     |   8 |     |   2 |     | = 10
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |     |   8 |     |   2 |   1 | = 11
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |     | = 20
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |   1 | = 21

		int cur = StateMachine.createIntFromBoolArray(data);

		if (cur != 10 || cur != 11 || cur != 20 || cur != 21)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_RESET_LIFT;

		TKOLift.getInstance().goToLevel(0);
		
		while (TKOLift.getInstance().getCurrentLevel() != 0 || TKOLift.getInstance().isMoving())
		{
			Timer.delay(0.1);
		}
		
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