package org.usfirst.team1351.robot.statemachine.states;

import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.statemachine.IStateFunction;
import org.usfirst.team1351.robot.statemachine.InstanceData;
import org.usfirst.team1351.robot.statemachine.StateEnum;
import org.usfirst.team1351.robot.statemachine.StateMachine;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

public class DropAll implements IStateFunction
{
	@Override
	public StateEnum doState(InstanceData data)
	{
		System.out.println("Entering DropAll state");
		
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

		data.curState = StateEnum.STATE_DROP_ALL;

		int sensors = StateMachine.getSensorData(data);

		TKOLift.getInstance().goToDropCrates(); // TODO Maybe goToDropCratesBasedOnLevel();
		while (TKOLift.getInstance().isMoving() &&
				(sensors == 42 || sensors == 43 || sensors == 20 || sensors == 21))
		{
			// double pos = TKOLift.getInstance().getEncoderPosition();
			// TKOLift.getInstance().updateCustomPositionTarget(); TODO THIS WHOLE WHILE LOOP WONT WORK
			// TKOLift.getInstance().goToPosition((int) pos);
			// TKOLift.getInstance().goToLevel((pos - TKOLift.bottomOffset) / TKOLift.oneLevel);
			Timer.delay(0.1);
		}
		
		StateMachine.getGripperSol().set(DoubleSolenoid.Value.kReverse);

		//TODO Error check, the one below isnt right
		//if (TKOLift.getInstance().getCurrentLevel() == 0)
		//	return StateEnum.STATE_ERR;

		System.out.println("Exiting DropAll state");
		return StateEnum.STATE_LOOK_FOR_CRATE;
	}
}