package org.usfirst.team1351.robot.util;

import org.usfirst.team1351.robot.logger.TKOLogger;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Data collection for everything
 * 
 * @author Vadim
 */
public class TKODataReporting implements Runnable // implements Runnable is important to make this class support the Thread (run method)
{
	/*
	 * This creates an object of the TKOThread class, passing it the runnable of this class (ThreadExample) TKOThread is just a thread that
	 * makes it easy to make using the thread safe
	 */
	public TKOThread dataReportThread = null;
	private PowerDistributionPanel pdp = new PowerDistributionPanel();
	private static TKODataReporting m_Instance = null;
	private boolean collectingDriveData = false;
	private boolean collectingDefaultData = true;
	private int threadWaitTime = Definitions.DEF_DATA_REPORTING_THREAD_WAIT;
	private double currentPTested = -1;
	private double currentITested = -1;
	private double currentDTested = -1;

	// Typical constructor made protected so that this class is only accessed statically, though that doesnt matter
	protected TKODataReporting()
	{

	}

	public static synchronized TKODataReporting getInstance()
	{
		if (TKODataReporting.m_Instance == null)
		{
			m_Instance = new TKODataReporting();
			m_Instance.dataReportThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	/**
	 * The {@code start} method starts the thread, making it call the run method (only once) but can do this for threads in different
	 * classes in parallel. The {@code isThreadRunning} method checks with a boolean whether the thread is running. We only start the thread
	 * if it is not. The {@code setThreadRunning} method sets the boolean to true, and the {@code start} method starts the Thread. We use
	 * the {@code isThreadRunning} in the run function to verify whether our thread should be running or not, to make a safe way to stop the
	 * thread. This function is completely thread safe.
	 * 
	 * @category
	 
	 
	 */
	public void start()
	{
		if (!dataReportThread.isAlive() && m_Instance != null)
		{
			dataReportThread = new TKOThread(m_Instance);
			dataReportThread.setPriority(Definitions.getPriority("dataReporting"));
		}
		if (!dataReportThread.isThreadRunning())
		{
			dataReportThread.setThreadRunning(true);
		}
	}

	/**
	 * The {@code stop} method disables the thread, simply by setting the {@code isThreadRunning} to false via {@code setThreadRunning} and
	 * waits for the method to stop running (on the next iteration of run).
	 */
	// TODO Make sure that using join is a good idea
	public void stop()
	{
		if (dataReportThread.isThreadRunning())
		{
			dataReportThread.setThreadRunning(false);
		}
	}

	public synchronized void startCollectingDriveData(double p, double i, double d)
	{
		collectingDefaultData = false;
		collectingDriveData = true;
		threadWaitTime = 10;
		currentPTested = p;
		currentITested = i;
		currentDTested = d;
		record();
		dataReportThread.interrupt();
	}

	public synchronized void stopCollectingDriveData()
	{
		collectingDefaultData = true;
		collectingDriveData = false;
		threadWaitTime = Definitions.DEF_DATA_REPORTING_THREAD_WAIT;
		record();
		dataReportThread.interrupt();
	}

	public synchronized void stopAllDataCollection()
	{
		collectingDefaultData = false;
		collectingDriveData = false;
		threadWaitTime = Definitions.DEF_DATA_REPORTING_THREAD_WAIT;
		dataReportThread.interrupt();
	}

	public boolean isCollectingDefaultData()
	{
		return collectingDefaultData;
	}

	public boolean isCollectingDriveData()
	{
		return collectingDriveData;
	}

	/**
	 * The run method is what the thread actually calls once. The continual running of the thread loop is done by the while loop, controlled
	 * by a safe boolean inside the TKOThread object. The wait is synchronized to make sure the thread safely sleeps.
	 */
	// TODO Make sure that wait is what we want to use (and that this will work with multiple instances of TKOThread across code, not sleep
	@Override
	public void run()
	{
		try
		{
			while (dataReportThread.isThreadRunning())
			{
				// System.out.println("DATA REPORTING THREAD RAN!");
				record();
				synchronized (dataReportThread) // synchronized per the thread to make sure that we wait safely
				{
					dataReportThread.wait(threadWaitTime); // the wait time that the thread sleeps, in milliseconds
				}
			}
		} catch (InterruptedException e)
		{
			run();
			// e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void record()
	{
		if (collectingDefaultData)
		{
			TKOLogger inst = TKOLogger.getInstance();
			inst.addMessage("Total pdp current:" + pdp.getTotalCurrent());
			for (int i = 0; i < 16; i++)
			{
				inst.addMessage("PDP Current for " + i + ": " + pdp.getCurrent(i));
			}
			try
			{
				SmartDashboard.putNumber("Accelerometer X", TKOHardware.getAcc().getX());
				SmartDashboard.putNumber("Accelerometer Y", TKOHardware.getAcc().getY());
				SmartDashboard.putNumber("Accelerometer Z", TKOHardware.getAcc().getZ());
				inst.addMessage("accX: " + TKOHardware.getAcc().getX());
				inst.addMessage("accY: " + TKOHardware.getAcc().getY());
				inst.addMessage("accZ: " + TKOHardware.getAcc().getZ());
				
				SmartDashboard.putNumber("EncLeft", TKOHardware.getDriveTalon(0).getPosition());
				SmartDashboard.putNumber("EncRight", TKOHardware.getDriveTalon(2).getPosition());
				for (CANTalon motor : TKOHardware.getDriveTalons())
				{
					int id = motor.getDeviceID();
					// TODO Check if motors are null
					if (motor == null)
						continue;
					inst.addMessage("Temperature for jag " + motor.getDeviceID() + ": " + motor.getTemp());
					inst.addMessage("Current for jag " + motor.getDeviceID() + ": " + motor.getOutputCurrent());
					inst.addMessage("Output voltage for jag " + motor.getDeviceID() + ": " + motor.getOutputVoltage());
					inst.addMessage("Voltage for jag " + motor.getDeviceID() + ": " + motor.getBusVoltage());
					SmartDashboard.putNumber("Temperature Jag " + id, motor.getTemp());
					SmartDashboard.putNumber("Out_Current Jag " + id, motor.getOutputCurrent());
					SmartDashboard.putNumber("Out_Voltage Jag " + id, motor.getOutputVoltage());
					SmartDashboard.putNumber("In_Voltage Jag " + id, motor.getBusVoltage());
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (collectingDriveData)
		{
			collectDriveData();
		}
	}

	public void collectDriveData()
	{
		TKOLogger inst = TKOLogger.getInstance();
		SmartDashboard.putNumber("DataBufferSize", TKOLogger.getInstance().dataBufferSize);
		SmartDashboard.putNumber("PValTested", currentPTested);
		SmartDashboard.putNumber("IValTested", currentITested);
		SmartDashboard.putNumber("DValTested", currentDTested);
		try
		{			
			for (CANTalon motor : TKOHardware.getDriveTalons())
			{
				if (motor == null)
					continue;
				int id = motor.getDeviceID();

				if (currentPTested < 10)
				{
					inst.addData("Temperature", motor.getTemp(), "p: 0" + currentPTested + " i: 0" + currentITested + " d: 0" + currentDTested, id);
					inst.addData("Out_Current", motor.getOutputCurrent(), "p: 0" + currentPTested + " i: 0" + currentITested + " d: 0" + currentDTested, id);
					inst.addData("Out_Voltage", motor.getOutputVoltage(), "p: 0" + currentPTested + " i: 0" + currentITested + " d: 0" + currentDTested, id);
					inst.addData("In_Voltage", motor.getBusVoltage(), "p: 0" + currentPTested + " i: 0" + currentITested + " d: 0" + currentDTested, id);
				} else
				{
					inst.addData("Temperature", motor.getTemp(), "p: " + currentPTested + " i: " + currentITested + " d: " + currentDTested, id);
					inst.addData("Out_Current", motor.getOutputCurrent(), "p: " + currentPTested + " i: " + currentITested + " d: " + currentDTested, id);
					inst.addData("Out_Voltage", motor.getOutputVoltage(), "p: " + currentPTested + " i: " + currentITested + " d: " + currentDTested, id);
					inst.addData("In_Voltage", motor.getBusVoltage(), "p: " + currentPTested + " i: " + currentITested + " d: " + currentDTested, id);
				}
				SmartDashboard.putNumber("Temperature Jag " + id, motor.getTemp());
				SmartDashboard.putNumber("Out_Current Jag " + id, motor.getOutputCurrent());
				SmartDashboard.putNumber("Out_Voltage Jag " + id, motor.getOutputVoltage());
				SmartDashboard.putNumber("In_Voltage Jag " + id, motor.getBusVoltage());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}