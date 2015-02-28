package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOException;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift implements Runnable
{
	Joystick stick;
	CANTalon liftTalon;
	boolean top, bottom;

	public TKOThread _thread = null;
	private static Lift m_Instance = null;
	
	protected Lift()
	{
		try
		{
			stick = TKOHardware.getJoystick(3);
			liftTalon = TKOHardware.getLiftTalon();
			top = TKOHardware.getLiftTop();
			bottom = TKOHardware.getLiftBottom();
		} catch (TKOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static synchronized Lift getInstance()
	{
		if (Lift.m_Instance == null)
		{
			m_Instance = new Lift();
			m_Instance._thread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	public synchronized void start()
	{
		System.out.println("Starting ghetto lift");
		if (!_thread.isAlive() && m_Instance != null)
		{
			_thread = new TKOThread(m_Instance);
			_thread.setPriority(9);
		}
		if (!_thread.isThreadRunning())
			_thread.setThreadRunning(true);
		
		liftTalon.changeControlMode(CANTalon.ControlMode.PercentVbus);
		
		System.out.println("Started ghetto lift task");
	}

	public synchronized void reset()
	{

	}


	public synchronized void stop()
	{
		System.out.println("Stopping ghetto lift task");
		if (_thread.isThreadRunning())
			_thread.setThreadRunning(false);
		
		liftTalon.ClearIaccum();

		System.out.println("Stopped ghetto lift task");
	}

	public synchronized void manualControl()
	{
		liftTalon.set(stick.getY() * .5);
		System.out.println("Stick output: " + stick.getY());
		System.out.println("Talon output: " + liftTalon.get() + "\n");
	}
	
	@Override
	public void run()
	{
		try
		{
			while (_thread.isThreadRunning())
			{
				manualControl();

				synchronized (_thread)
				{
					_thread.wait(20);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}