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
		
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |     |  16 |   8 |     |   2 |     |	= 26
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |  64 |  32 |  16 |   8 |     |   2 |     |	= 122
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |     |     |     |     |   4 |     |   1 |	= 5
		// 0b |  CL |  CR |  GS |  LE |  LR |  RE |  RR |
		// 0b |  64 |  32 |     |     |   4 |     |   1 |	= 101
		
		int cur = StateMachine.createIntFromBoolArray(data);
		
		if (cur != 26 || cur != 122 || cur != 5 || cur != 101)
			return StateEnum.STATE_ERR;
		
		data.curState = StateEnum.STATE_LIFT_CRATE;

		double lvl = TKOLift.getInstance().getCurrentLevel();
		
		TKOLift.getInstance().goUp();
		
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
		
		while (TKOLift.getInstance().getCurrentLevel() != lvl + 1 &&
				(sensors == 26 || sensors == 90 || sensors == 58) ||
				(sensors == 5 || sensors == 69 || sensors == 37))
		{
			Timer.delay(0.1);
		}
			
		if (TKOLift.getInstance().getCurrentLevel() == TKOLift.fullOfCratesPosition)
		{
			System.out.println("Lift full, waiting for joystick");
			while (StateMachine.getJoystick().getRawButton(8) == false)
			{
				
			}
			System.out.println("Exiting LiftCrate state");
			return StateEnum.STATE_DROP_ALL;
		}
		
		if (TKOLift.getInstance().getCurrentLevel() != lvl + 1)
			return StateEnum.STATE_ERR;
		
		System.out.println("Exiting LiftCrate state");
	    return StateEnum.STATE_LOOK_FOR_CRATE;
	}
}