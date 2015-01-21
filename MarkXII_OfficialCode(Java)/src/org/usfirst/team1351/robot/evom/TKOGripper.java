// Last edited by Ben Kim
// on 01/19/2015

package org.usfirst.team1351.robot.evom;

import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOThread;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class TKOGripper implements Runnable {
	public TKOThread gripperThread = null;
	private static TKOGripper m_Instance = null;

	protected TKOGripper() {

	}

	public static synchronized TKOGripper getInstance() {
		if (TKOGripper.m_Instance == null) {
			m_Instance = new TKOGripper();
			m_Instance.gripperThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	public void start() {
		System.out.println("Starting gripper task");
		if (!gripperThread.isAlive() && m_Instance != null) {
			gripperThread = new TKOThread(m_Instance);
			gripperThread.setPriority(Definitions.getPriority("gripper"));
		}
		if (!gripperThread.isThreadRunning())
			gripperThread.setThreadRunning(true);
		try {
			TKOHardware.getCompressor().start();
			TKOHardware.getPiston(0).set(DoubleSolenoid.Value.kForward);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Started gripper task");
	}

	public void stop() {
		System.out.println("Stopping gripper task");
		if (gripperThread.isThreadRunning())
			gripperThread.setThreadRunning(false);
		try {
			TKOHardware.getCompressor().stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Stopped gripper task");
	}

	public synchronized void pistonControl() {
		try {
			if (TKOHardware.getJoystick(0).getRawButton(2)) {
				TKOHardware.getPiston(0).set(DoubleSolenoid.Value.kForward); //TODO Fix the 0 to a constant
			}
			if (TKOHardware.getJoystick(0).getRawButton(3)) {
				TKOHardware.getPiston(0).set(DoubleSolenoid.Value.kReverse);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (gripperThread.isThreadRunning()) {

				pistonControl();

				synchronized (gripperThread) {
					gripperThread.wait(5);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}