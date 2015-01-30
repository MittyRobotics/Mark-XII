package org.usfirst.team1351.robot.statemachineex;

import org.usfirst.team1351.robot.statemachineex.states.ErrorState;
import org.usfirst.team1351.robot.statemachineex.states.LatchLock;
import org.usfirst.team1351.robot.statemachineex.states.LatchUnlock;
import org.usfirst.team1351.robot.statemachineex.states.PistonExtend;
import org.usfirst.team1351.robot.statemachineex.states.PistonRetract;
import org.usfirst.team1351.robot.statemachineex.states.ReadyToFire;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import us.vadweb.wrapper.wpilib.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class StateMachine {
	
	/*
	 * #define DONE_FIRING 2	// PE
#define PISTON_RETRACTED 1	// PR
#define LATCH_LOCKED_PISTON_RETRACTED 5	// LL and PR
#define CONST_READY_TO_FIRE 14	// IC and LL and PE

#define PISTON_RETRACT_TIMEOUT 15.
#define LATCH_LOCK_FORWARD_TIMEOUT 10.
#define PISTON_EXTEND_TIMEOUT 15.
#define LATCH_UNLOCK_REVERSE_TIMEOUT 10.
#define POST_SHOOT_WAIT_TIME 1.
#define SHOOT_ROLLER_PRERUN_TIME .15
	 * */
	
	public static final int DONE_FIRING = 2;
	public static final int PISTON_RETRACTED = 1;
	public static final int LATCH_LOCKED_PISTON_RETRACTED = 5;
	public static final int CONST_READY_TO_FIRE = 14;
	
	public static final float PISTON_RETRACT_TIMEOUT = 15.f;
	public static final float LATCH_LOCK_FORWARD_TIMEOUT = 10.f;
	public static final float PISTON_EXTEND_TIMEOUT = 15.f;
	public static final float LATCH_UNLOCK_REVERSE_TIMEOUT = 10.f;
	public static final float POST_SHOOT_WAIT_TIME = 1.f;
	public static final float SHOOT_ROLLER_PRERUN_TIME = 15.f;
	
	static Timer m_timer = new Timer();

	/*
	 * const int PISTON_SWITCH_RETRACT_CHANNEL = 7;
const int PISTON_SWITCH_EXTEND_CHANNEL = 6;
const int LATCH_PISTON_LOCK_SWITCH_CHANNEL = 1;
const int IS_COCKED_SWITCH_CHANNEL = 2;
const int BALL_LIMIT_SWITCH = 10;
const int ARM_OPTICAL_SWITCH = 11;
	 * */
	
	static DigitalInput m_pistonRetract = new DigitalInput(7);
	static DigitalInput m_pistonExtend = new DigitalInput(6);
	static DigitalInput m_latchLock = new DigitalInput(1);
	static DigitalInput m_isCocked = new DigitalInput(2);
	static Joystick m_triggerJoystick = null;

	static DoubleSolenoid m_pistonRetractExtend = null;//new DoubleSolenoid(PISTON_RETRACT_SOLENOID_A, PISTON_RETRACT_SOLENOID_B);
	static DoubleSolenoid m_latchLockUnlock = null;//new DoubleSolenoid(LATCH_RETRACT_SOLENOID_A, LATCH_RETRACT_SOLENOID_B);
	//TODO CRITICAL If this still doesn't work, ^^  set them to null here, initialize in constructor?
	static float m_lastSensorStringPrint = 0.0f;
	static boolean m_armCanMove = false;
	static boolean m_hasSetPneumatics = false;
	static boolean m_forceFire = false;
	static boolean m_autonFired = false;
	
	private InstanceData data = new InstanceData();
	
	
	static IStateFunction states[] = new IStateFunction[StateEnum.NUM_STATES.getValue()+1];
	
	public StateMachine(Joystick trigger) {
		m_triggerJoystick = trigger;
		m_pistonRetractExtend = new DoubleSolenoid(6,3);
		m_latchLockUnlock = new DoubleSolenoid(2,5);
		states[StateEnum.STATE_PISTON_RETRACT.getValue()] = new PistonRetract();
		states[StateEnum.STATE_PISTON_EXTEND.getValue()] = new PistonExtend();
		states[StateEnum.STATE_LATCH_UNLOCK.getValue()] = new LatchUnlock();
		states[StateEnum.STATE_LATCH_LOCK.getValue()] = new LatchLock();
		states[StateEnum.STATE_READY_TO_FIRE.getValue()] = new ReadyToFire();
		states[StateEnum.STATE_ERR.getValue()] = new ErrorState();
		switch (getSensorData(data)) {
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
	
	public static int getSensorData(InstanceData id) {
		id.state[0] = (m_pistonRetract.get() == false);
		id.state[1] = (m_pistonExtend.get() == false);
		id.state[2] = (m_latchLock.get() == false);
		id.state[3] = (m_isCocked.get() == false);
		return createIntFromBoolArray(id);
	}
	
	public static int createIntFromBoolArray(InstanceData id) {
		int num = 0;
		for (int i = 0; i < StateEnum.NUM_STATES.getValue() - 1; i ++)
		{
			if (id.state[i]) {
				num |= 1 << i;
			}
		}
		return num;
	}
	
	public static Timer getTimer() {
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
	
	public static StateEnum runState(StateEnum curState, InstanceData data) {
		return states[curState.getValue()].doState(data);
	}
	
}
