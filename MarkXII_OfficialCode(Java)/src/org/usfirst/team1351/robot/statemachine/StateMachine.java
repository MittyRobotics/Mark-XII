// Last edited by Ben Kim
// on 2/4/2015

package org.usfirst.team1351.robot.statemachine;

import org.usfirst.team1351.robot.statemachine.states.CloseGripper;
import org.usfirst.team1351.robot.statemachine.states.DecideAction;
import org.usfirst.team1351.robot.statemachine.states.DropAll;
import org.usfirst.team1351.robot.statemachine.states.ErrorState;
import org.usfirst.team1351.robot.statemachine.states.LiftCrate;
import org.usfirst.team1351.robot.statemachine.states.LookForCrate;
import org.usfirst.team1351.robot.statemachine.states.OpenGripper;
import org.usfirst.team1351.robot.statemachine.states.ReadyForRC;
import org.usfirst.team1351.robot.statemachine.states.ResetLift;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class StateMachine implements Runnable
{	
	// TODO add analog inputs but make them act as digital inputs
	static Timer m_timer;
<<<<<<< HEAD
	
//	static DigitalInput m_crateLeft;
//	static DigitalInput m_crateRight;
	
	static DigitalInput m_gripper;
	static DigitalInput m_pistonRetract_L;
	static DigitalInput m_pistonExtend_L;
	static DigitalInput m_pistonRetract_R;
	static DigitalInput m_pistonExtend_R;
	static DigitalInput m_crateLeft;
	static DigitalInput m_crateRight;
	
=======
	static DigitalInput m_gripper, m_pistonRetract_L, m_pistonExtend_L, m_pistonRetract_R, m_pistonExtend_R;
>>>>>>> feature/javaDev
	static Joystick m_evomStick;
	static DoubleSolenoid m_gripperPiston;

	private InstanceData data = new InstanceData();

	static IStateFunction states[] = new IStateFunction[StateEnum.NUM_STATES.getValue() + 1];

	public static final float PISTON_RETRACT_TIMEOUT = 15.f;
	public static final float PISTON_EXTEND_TIMEOUT = 15.f;

	// refer to other states for detailed bit maps
	public static final int PISTON_EXTENDED = 10;		// goes to state: open gripper
	public static final int PISTON_RETRACTED = 5;		// goes to state: ready for rc
	public static final int RC_FOUND = 21;				// goes to state: close gripper
	public static final int READY_TO_LIFT = 26;			// goes to state: lift crate
	public static final int CRATE_FOUND = 122;			// goes to state: lift crate
	
	public TKOThread stateThread = null;
	private static StateMachine m_Instance = null;
	
	public static synchronized StateMachine getInstance()
	{
		if (m_Instance == null)
		{
			m_Instance = new StateMachine();
			m_Instance.stateThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}
	
	protected StateMachine()
	{
		m_timer = new Timer();

		try
		{
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
		id.state[0] = false; //(m_crateLeft.get() == false);
		id.state[1] = false; //(m_crateRight.get() == false);		
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
	
	public static synchronized boolean getGripperSwitch() throws TKOException
	{
		if (m_gripper == null)
			throw new TKOException("NULL GRIPPER SWITCH");
		return !m_gripper.get();
	}

	public synchronized void start()
	{
		System.out.println("Starting state machine task");
		if (!stateThread.isAlive() && m_Instance != null)
		{
			stateThread = new TKOThread(m_Instance);
//			stateThread.setPriority(Definitions.getPriority("gripper"));
		}
		if (!stateThread.isThreadRunning())
			stateThread.setThreadRunning(true);
		
		System.out.println("Started state machine task");
	}


	public synchronized void stop()
	{
		System.out.println("Stopping state machine task");
		if (stateThread.isThreadRunning())
			stateThread.setThreadRunning(false);
		System.out.println("Stopped state machine task");
	}
	
	public void run()
	{
		try
		{
			while (stateThread.isThreadRunning())
			{
				// what goes here?
				
				synchronized (stateThread)
				{
					stateThread.wait(10);	// how long is this wait?
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}