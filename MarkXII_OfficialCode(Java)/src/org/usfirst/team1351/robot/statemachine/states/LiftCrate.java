package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.statemachine.*;

import edu.wpi.first.wpilibj.Timer;

/**
 * This state is first entered after going through the gripper states.
 * However, it will be more commonly entered after a crate is found.
 * After driving the lift up, the state machine will either look for another crate or go to drop crate(s).
 *
 */

// TODO incorporate TKOLift into state machine

public class LiftCrate implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering LiftCrate state");
				
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |   8 |     |   2 |     | = 42
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |   8 |     |   2 |   1 | = 43
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |     | = 20
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |   1 | = 21
		
		int cur = StateMachine.createIntFromBoolArray(data);
		
		if (cur != 42 || cur != 43 || cur != 20 || cur != 21)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_LIFT_CRATE;

		double lvl = TKOLift.getInstance().getCurrentLevel();
		
		if (lvl > TKOLift.getInstance().getSoftTop() || lvl < TKOLift.getInstance().getSoftBottom())
		{
			return StateEnum.STATE_ERR;
		}
			
		if (lvl == TKOLift.trashcanPickupPosition)
		{
//			TKOLift.getInstance().goToLevel(newLevel);
		}
			
		TKOLift.getInstance().goUp();
		
		int sensors = StateMachine.getSensorData(data);
		
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |   8 |     |   2 |     | = 42
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |  32 |     |   8 |     |   2 |   1 | = 43
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |     | = 20
		// 0b |  GS |  LR |  LE |  RR |  RE |  CP |
		// 0b |     |  16 |     |   4 |     |   1 | = 21
		
		while ((TKOLift.getInstance().getCurrentLevel() != lvl + TKOLift.oneLevel) ||
				(TKOLift.getInstance().getCurrentLevel() != TKOLift.fullOfCratesPosition) &&
				(sensors == 42 || sensors == 43) || (sensors == 20 || sensors == 21))
		{
			Timer.delay(0.1);
		}
			
		if (TKOLift.getInstance().getCurrentLevel() == TKOLift.fullOfCratesPosition)
		{
			System.out.println("Lift full, waiting for joystick");
			while (StateMachine.getJoystick().getRawButton(8) == false)
			{
				Timer.delay(0.1);
			}
			System.out.println("Exiting LiftCrate state");
			return StateEnum.STATE_DROP_ALL;
		}
		
		if (TKOLift.getInstance().getCurrentLevel() != lvl + TKOLift.oneLevel)
			return StateEnum.STATE_ERR;
		
		System.out.println("Exiting LiftCrate state");
	    return StateEnum.STATE_LOOK_FOR_CRATE;
	}
}