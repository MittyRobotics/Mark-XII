package org.usfirst.team1351.robot.statemachine;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class StateMachine {
	
	// piston timeout constants
	
	// bit addition constants
	
	static Timer m_timer = new Timer();

	// switch channels

	static Joystick m_joystick = null;
	
	// piston doublesolenoids

	// misc booleans
	
	private InstanceData data = new InstanceData();
	
	static IStateFunction states[] = new IStateFunction[StateEnum.NUM_STATES.getValue()+1];
	
	public StateMachine(Joystick stick) {
		m_joystick = stick;
	
		/*switch (getSensorData(data)) {
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
	    }*/
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
