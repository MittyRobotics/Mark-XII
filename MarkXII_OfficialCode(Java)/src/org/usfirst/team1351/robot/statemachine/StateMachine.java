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

	static DigitalInput m_gripper;
	static DigitalInput m_pistonRetract_L;
	static DigitalInput m_pistonExtend_L;
	static DigitalInput m_pistonRetract_R;
	static DigitalInput m_pistonExtend_R;

	static Joystick m_evomStick;
	static DoubleSolenoid m_gripperPiston;

	private InstanceData data = new InstanceData();

	static IStateFunction states[] = new IStateFunction[StateEnum.STATE_ERR.getValue() + 1];
	//static IStateFunction states[] = new IStateFunction[10];

	public static final float PISTON_RETRACT_TIMEOUT = 15.f;
	public static final float PISTON_EXTEND_TIMEOUT = 15.f;

	// 0b | GS | LR | LE | RR | RE | CP |
	// 0b | | | 8 | | 2 | | = 10
	// 0b | GS | LR | LE | RR | RE | CP |
	// 0b | | 16 | | 4 | | | = 20
	// 0b | GS | LR | LE | RR | RE | CP |
	// 0b | 32 | 16 | | 4 | | | = 52
	// 0b | GS | LR | LE | RR | RE | CP |
	// 0b | 32 | | 8 | | 2 | | = 42

	public static final int PISTON_EXTENDED = 10;
	public static final int PISTON_RETRACTED = 20;
	public static final int RC_FOUND = 52;
	public static final int READY_TO_LIFT = 42;

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

	protected void init()
	{
		// currentState = new DecideAction();
	}

	protected StateMachine()
	{
		m_timer = new Timer();

		try
		{
			m_gripper = TKOHardware.getSwitch(2);
			// m_pistonRetract_L = TKOHardware.getSwitch(3);
			// m_pistonExtend_L = TKOHardware.getSwitch(4);
			// m_pistonRetract_R = TKOHardware.getSwitch(5);
			// m_pistonExtend_R = TKOHardware.getSwitch(6);

			// TODO stick 4 for state machine actions, stick 3 for manual control?
			m_evomStick = TKOHardware.getJoystick(3);
			m_gripperPiston = TKOHardware.getPiston(1);

		}
		catch (TKOException e)
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
		try
		{
			id.state[0] = (m_gripper.get() == false);
			id.state[1] = (TKOHardware.getPiston(1).get() == DoubleSolenoid.Value.kReverse);
			id.state[2] = (TKOHardware.getPiston(1).get() == DoubleSolenoid.Value.kForward);
			id.state[3] = (TKOHardware.getPiston(1).get() == DoubleSolenoid.Value.kReverse);
			id.state[4] = (TKOHardware.getPiston(1).get() == DoubleSolenoid.Value.kForward);
			try
			{
				id.state[5] = (TKOHardware.cratePresent() == false);
			}
			catch (TKOException e)
			{
				e.printStackTrace();
			}
		}
		catch (TKOException e)
		{
			e.printStackTrace();
		}

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
			// stateThread.setPriority(Definitions.getPriority("gripper"));
		}
		if (!stateThread.isThreadRunning())
			stateThread.setThreadRunning(true);

		init();

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
				runState(data.curState, data);
				System.out.println("RUNNING STATE: " + data.curState);

				synchronized (stateThread)
				{
					stateThread.wait(20); // how long is this wait?
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}