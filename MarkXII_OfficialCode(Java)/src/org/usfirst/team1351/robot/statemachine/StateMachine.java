/*
 * Last edited by Ben Kim
 * on 1/31/2015
 */

package org.usfirst.team1351.robot.statemachine;

import org.usfirst.team1351.robot.statemachine.states.*;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

// TODO replace all the BS values for everything

/*
 * 0b |  CL |  CR |  GS |  LE |	 LR	|  RE |	 RR	|
 * 0b |  64 |  32 |  16 |   8 |   4 |   2 |   1 |
 *
 */

public class StateMachine
{
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

	public static final float PISTON_RETRACT_TIMEOUT = 15.f;
	public static final float PISTON_EXTEND_TIMEOUT = 15.f;
	public static final float WAIT_FOR_RC_TIMEOUT = 15.f;

	// 0b |  CL |  CR |  GS |  LE |	 LR	|  RE |	 RR	|
	// 0b |     |     |     |   8 |     |   2 |     |
	public static final int PISTON_EXTENDED = 10;
	
	public static final int READY_FOR_RC = 99;	// piston retracted
	public static final int RC_FOUND = 99;	// piston still retracted AND gripper switch
	public static final int READY_TO_LIFT = 99;	// piston extended AND trash can is in
	public static final int CRATE_FOUND = 99;
	
	/*static float m_lastSensorStringPrint = 0.0f;
	static boolean m_armCanMove = false;
	static boolean m_hasSetPneumatics = false;
	static boolean m_forceFire = false;
	static boolean m_autonFired = false;*/
	
	public StateMachine()	// used to take a Joystick as a parameter
	{
		m_timer = new Timer();

		try
		{
			m_crateLeft = TKOHardware.getSwitch(0);
			m_crateRight = TKOHardware.getSwitch(1);
			m_gripper = TKOHardware.getSwitch(2);
			m_pistonRetract_L = TKOHardware.getSwitch(3);
			m_pistonExtend_L = TKOHardware.getSwitch(4);
			m_pistonRetract_R = TKOHardware.getSwitch(5);
			m_pistonExtend_R = TKOHardware.getSwitch(6);
			
			// TODO stick 4 for state machine actions, stick 3 for manual control?
			m_evomStick = TKOHardware.getJoystick(3);
			
			m_gripperPiston = TKOHardware.getPiston(1);
			
		} catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		states[StateEnum.STATE_DECIDE_ACTION.getValue()] = new DecideAction();
		states[StateEnum.STATE_OPEN_GRIPPER.getValue()] = new OpenGripper();
		states[StateEnum.STATE_READY_FOR_RC.getValue()] = new ReadyForRC();
		states[StateEnum.STATE_CLOSE_GRIPPER.getValue()] = new CloseGripper();
		states[StateEnum.STATE_LIFT_CRATE.getValue()] = new LiftCrate();
		states[StateEnum.STATE_LOOK_FOR_CRATE.getValue()] = new LookForCrate();
		states[StateEnum.STATE_DROP_ALL.getValue()] = new DropAll();
		states[StateEnum.STATE_RESET_LIFT.getValue()] = new ResetLift();
		states[StateEnum.STATE_ERR.getValue()] = new ErrorState();
		
		data.curState = StateEnum.STATE_DECIDE_ACTION;
	}

	public static int getSensorData(InstanceData id)
	{
		id.state[0] = (m_crateLeft.get() == false);
		id.state[1] = (m_crateRight.get() == false);
		id.state[2] = (m_gripper.get() == false);
		id.state[3] = (m_pistonRetract_L.get() == false);
		id.state[4] = (m_pistonExtend_L.get() == false);
		id.state[5] = (m_pistonRetract_R.get() == false);
		id.state[6] = (m_pistonExtend_R.get() == false);
		
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

	public static DoubleSolenoid getGripperSol()
	{
		return m_gripperPiston;
	}

	public static Joystick getJoystick()
	{
		return m_evomStick;
	}

	public static StateEnum runState(StateEnum curState, InstanceData data)
	{
		return states[curState.getValue()].doState(data);
	}

}