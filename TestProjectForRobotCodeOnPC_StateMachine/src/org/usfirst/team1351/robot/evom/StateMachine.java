package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.evom.states.*;
import org.usfirst.team1351.robot.main.Definitions;

import us.vadweb.wrapper.wpilib.DigitalInput;
import us.vadweb.wrapper.wpilib.DoubleSolenoid;
import us.vadweb.wrapper.wpilib.Joystick;
import us.vadweb.wrapper.wpilib.Timer;

public class StateMachine
{
	// TODO move all of this to Definitions.java
	public static final int NUM_SWITCHES = 7;
	public static final int[] SWITCH_ID =
	{0, 1, 2, 3, 4, 5, 6};
	
	public static final int GRIPPER_A = 2; // gripper piston
	public static final int GRIPPER_B = 3;

	static Timer m_timer;
	
	static DigitalInput m_crateLeft;
	static DigitalInput m_crateRight;
	static DigitalInput m_gripper;
	static DigitalInput m_pistonRetract_L;
	static DigitalInput m_pistonExtend_L;
	static DigitalInput m_pistonRetract_R;
	static DigitalInput m_pistonExtend_R;
	
	static Joystick m_evomStick;

	static DoubleSolenoid m_gripperPiston;

	private InstanceData data = new InstanceData();

	static IStateFunction states[] = new IStateFunction[StateEnum.NUM_STATES.getValue() + 1];

	/*public static final int DONE_FIRING = 2;
	public static final int PISTON_RETRACTED = 1;
	public static final int LATCH_LOCKED_PISTON_RETRACTED = 5;
	public static final int CONST_READY_TO_FIRE = 14;

	public static final float PISTON_RETRACT_TIMEOUT = 15.f;
	public static final float LATCH_LOCK_FORWARD_TIMEOUT = 10.f;
	public static final float PISTON_EXTEND_TIMEOUT = 15.f;
	public static final float LATCH_UNLOCK_REVERSE_TIMEOUT = 10.f;
	public static final float POST_SHOOT_WAIT_TIME = 1.f;
	public static final float SHOOT_ROLLER_PRERUN_TIME = 15.f;
	
	static float m_lastSensorStringPrint = 0.0f;
	static boolean m_armCanMove = false;
	static boolean m_hasSetPneumatics = false;
	static boolean m_forceFire = false;
	static boolean m_autonFired = false;*/
	
	public StateMachine()	// used to take a Joystick as a parameter
	{
		m_timer = new Timer();
		
		m_crateLeft = new DigitalInput(SWITCH_ID[0]);
		m_crateRight = new DigitalInput(SWITCH_ID[1]);
		m_gripper = new DigitalInput(SWITCH_ID[2]);
		m_pistonRetract_L = new DigitalInput(SWITCH_ID[3]);
		m_pistonExtend_L = new DigitalInput(SWITCH_ID[4]);
		m_pistonRetract_R = new DigitalInput(SWITCH_ID[5]);
		m_pistonExtend_R = new DigitalInput(SWITCH_ID[6]);
		
		// TODO stick 4 for state machine actions, stick 3 for manual control?
		m_evomStick = new Joystick(Definitions.JOYSTICK_ID[3]);
		
		m_gripperPiston = new DoubleSolenoid(GRIPPER_A, GRIPPER_B);
		
		states[StateEnum.STATE_DECIDE_ACTION.getValue()] = new DecideAction();
		states[StateEnum.STATE_OPEN_GRIPPER.getValue()] = new OpenGripper();
		states[StateEnum.STATE_READY_FOR_RC.getValue()] = new ReadyForRC();
		states[StateEnum.STATE_CLOSE_GRIPPER.getValue()] = new CloseGripper();
		states[StateEnum.STATE_LIFT_CRATE.getValue()] = new LiftCrate();
		states[StateEnum.STATE_LOOK_FOR_CRATE.getValue()] = new LookForCrate();
		states[StateEnum.STATE_DROP_ALL.getValue()] = new DropAll();
		states[StateEnum.STATE_RESET_LIFT.getValue()] = new ResetLift();
		states[StateEnum.STATE_ERR.getValue()] = new ErrorState();
		
		switch (getSensorData(data))
		{
		case DONE_FIRING:
			data.curState = StateEnum.STATE_PISTON_RETRACT;
			break;
		case PISTON_RETRACTED:
			data.curState = StateEnum.STATE_LATCH_LOCK;
			break;
		case LATCH_LOCKED_PISTON_RETRACTED:
			data.curState = StateEnum.STATE_PISTON_EXTEND;
			break;
		case CONST_READY_TO_FIRE:
			data.curState = StateEnum.STATE_READY_TO_FIRE;
			break;
		default:
			data.curState = StateEnum.STATE_ERR;
			break;
		}
	}

	public static int getSensorData(InstanceData id)
	{
		id.state[0] = (m_pistonRetract.get() == false);
		id.state[1] = (m_pistonExtend.get() == false);
		id.state[2] = (m_latchLock.get() == false);
		id.state[3] = (m_isCocked.get() == false);
		return createIntFromBoolArray(id);
	}

	public static int createIntFromBoolArray(InstanceData id)
	{
		int num = 0;
		for (int i = 0; i < StateEnum.NUM_STATES.getValue() - 1; i++)
		{
			if (id.state[i])
			{
				num |= 1 << i;
			}
		}
		return num;
	}

	public static Timer getTimer()
	{
		return m_timer;
	}

	public static DoubleSolenoid getPistonSol()
	{
		return m_pistonRetractExtend;
	}

	public static DoubleSolenoid getLatchSol()
	{
		return m_latchLockUnlock;
	}

	public static Joystick getJoystick()
	{
		return m_triggerJoystick;
	}

	public static StateEnum runState(StateEnum curState, InstanceData data)
	{
		return states[curState.getValue()].doState(data);
	}

}