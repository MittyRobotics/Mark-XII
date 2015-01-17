// Last edited by Ben Kim
// on 01/17/2015

package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.DriverStation;

public class TKOGripper implements Runnable
{
	public TKOThread gripperThread = null;
	private static TKOGripper m_Instance = null;

	protected TKOGripper()
	{

	}
	
	public static synchronized TKOGripper getInstance()
	{
		if (TKOGripper.m_Instance == null)
		{
			m_Instance = new TKOGripper();
			m_Instance.gripperThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	public void start()
	{
		System.out.println("Starting gripper task");
		if (!gripperThread.isAlive() && m_Instance != null)
		{
			gripperThread = new TKOThread(m_Instance);
			gripperThread.setPriority(Definitions.getPriority("gripper"));
		}
		if (!gripperThread.isThreadRunning())
			gripperThread.setThreadRunning(true);

		System.out.println("Started gripper task");
	}

	public void stop()
	{
		System.out.println("Stopping gripper task");
		if (gripperThread.isThreadRunning())
			gripperThread.setThreadRunning(false);
		System.out.println("Stopped gripper task");
	}
	
	@Override
	public void run()
	{
		
	}

}
