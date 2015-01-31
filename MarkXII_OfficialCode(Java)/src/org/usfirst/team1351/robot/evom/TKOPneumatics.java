// Last edited by Ben Kim
// on 01/20/2015

/**
 * In Java, we start each file by saying what package it is part of.
 */
package org.usfirst.team1351.robot.evom;

/**
 * These import statements are similar to how we would include header files in C++.
 * You'll notice that Eclipse will neatly collapse these lines. Expand it by clicking the plus on the left.
 */
import org.usfirst.team1351.robot.drive.TKODrive;
import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKODataReporting;
import org.usfirst.team1351.robot.util.TKOHardware;
import org.usfirst.team1351.robot.util.TKOThread;

/**
 * Although you could theoretically use wpilibj.*, it would be no bueno.
 * Eclipse makes it easy to import specific classes from WPILib.
 * For example, hovering over a CANTalon object without the import statement gives an error that Eclipse will correct.
 */
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * By now, it should be obvious that the naming convention is to use TKO[name].
 * Runnable is used by any class executed in a thread.
 */
public class TKOPneumatics implements Runnable
{
	/**
	 * Refer to ThreadExample.java in robot.util for a detailed explanation.
	 */
	public TKOThread pneuThread = null;
	private static TKOPneumatics m_Instance = null;

	protected TKOPneumatics() {

	}

	public static synchronized TKOPneumatics getInstance() {
		if (TKOPneumatics.m_Instance == null) {
			m_Instance = new TKOPneumatics();
			m_Instance.pneuThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	/**
	 * Inside the start method, we will set the gripper to the default configuration.
	 * We must first start the compressor, and then we open the "claw" by setting the piston to retracted.
	 */
	public void start() {
		System.out.println("Starting pneumatics task");
		if (!pneuThread.isAlive() && m_Instance != null) {
			pneuThread = new TKOThread(m_Instance);
			pneuThread.setPriority(Definitions.getPriority("gripper"));
		}
		if (!pneuThread.isThreadRunning())
			pneuThread.setThreadRunning(true);
		try {
			TKOHardware.getCompressor().start();
			// TODO check that this is kReverse in all branches
			TKOHardware.getPiston(1).set(DoubleSolenoid.Value.kReverse);
			TKOHardware.getPiston(2).set(DoubleSolenoid.Value.kReverse);
			TKOHardware.getPiston(3).set(DoubleSolenoid.Value.kReverse);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Started pneumatics task");
	}

	/**
	 * Inside the stop method, we must stop the compressor as well.
	 */
	public void stop() {
		System.out.println("Stopping pneumatics task");
		if (pneuThread.isThreadRunning())
			pneuThread.setThreadRunning(false);
		try {
			TKOHardware.getCompressor().stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Stopped pneumatics task");
	}

	/**
	 * The pistonControl() method runs continuously in the run() method.
	 * The try-catch loop exists since getPiston(i) can throw a TKOException.
	 * Joystick input is simple here: one button extends the piston, the other retracts it.
	 * 
	 */
	public synchronized void pistonControl() {
		try {
			if (TKOHardware.getJoystick(2).getRawButton(2)) {
				TKOHardware.getPiston(1).set(DoubleSolenoid.Value.kForward);
			}
			if (TKOHardware.getJoystick(2).getRawButton(3)) {
				TKOHardware.getPiston(1).set(DoubleSolenoid.Value.kReverse);
			}
			if (TKOHardware.getJoystick(3).getRawButton(2)) {
				TKOHardware.getPiston(2).set(DoubleSolenoid.Value.kForward);
				TKOHardware.getPiston(3).set(DoubleSolenoid.Value.kForward);
			}
			if (TKOHardware.getJoystick(3).getRawButton(3)) {
				TKOHardware.getPiston(2).set(DoubleSolenoid.Value.kReverse);
				TKOHardware.getPiston(3).set(DoubleSolenoid.Value.kReverse);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (pneuThread.isThreadRunning()) {

				pistonControl();

				synchronized (pneuThread) {
					pneuThread.wait(5);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}