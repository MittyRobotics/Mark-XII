package org.usfirst.team1351.robot.util;

import java.util.Random;

import org.usfirst.team1351.robot.evom.TKOLift;
import org.usfirst.team1351.robot.main.Definitions;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;
import edu.wpi.first.wpilibj.SerialPort.WriteBufferMode;


/**
 * This is an example of how to make a class that runs as a thread. The most important reason for making TKOThread was to make the thread
 * implementation thread-safe everywhere, meaning that if we happened to use two threads to do the same thing to an object, we would not
 * have memory corruption / other problems.
 * 
 * @author Vadim
 */
public class TKOLEDArduino implements Runnable // implements Runnable is important to make this class support the Thread (run method)
{
	/*
	 * This creates an object of the TKOThread class, passing it the runnable of this class (ThreadExample) TKOThread is just a thread that
	 * makes it easy to make using the thread safe
	 */
	public TKOThread ledArduinoThread = null;
	private static TKOLEDArduino m_Instance = null;
	private Random r = new Random();
	private SerialPort arduino = null;

	// Typical constructor made protected so that this class is only accessed statically, though that doesnt matter
	protected TKOLEDArduino()
	{

	}
	/**
	 * This function makes the class a singleton, so that there can only be one instance of the class even though the class is not static
	 * This is needed for the Thread to work properly. 
	 */
	public static synchronized TKOLEDArduino getInstance()
	{
		if (TKOLEDArduino.m_Instance == null)
		{
			m_Instance = new TKOLEDArduino();
			m_Instance.ledArduinoThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}
	
	public void ledStripGradientBasedOnLevel()
	{
		double lvl = TKOLift.getInstance().getCurrentLevel() - TKOLift.softLevelBot;
		lvl /= (TKOLift.softLevelTop - TKOLift.softLevelBot);
		double R = 0;
		double G = 255 * (lvl);
		double B = 255 * (1-lvl);
		setRGB((int) R, (int) G, (int) B);
	}
	
	public void ledStripUpdateColorTestPatterns()
	{
		double converted = TKOLift.getInstance().getCurrentLevel() - TKOLift.softLevelBot;
		converted /= (TKOLift.softLevelTop - TKOLift.softLevelBot);
		converted *= 14; //max pattern number
		short target = (short) converted;
		System.out.println("Setting pattern");
		setPattern(target);
		System.out.println("Set pattern");
	}
	
	public void ledStripRandomColor()
	{
		int randomR = r.nextInt(255);
		int randomG = r.nextInt(255);
		int randomB = r.nextInt(255);
		System.out.println("Setting pattern");
		setRGB(randomR, randomG, randomB);
		System.out.println("Set pattern");
	}
	
	public synchronized void setRGB (int R, int G, int B)
	{
		if (arduino == null)
			return;
		String patternString = new String("R 10 ");
		patternString = patternString + R + " " + G + " " + B;
		System.out.println("WRITING STRING: " + patternString);
		arduino.writeString(patternString);
		System.out.println("DONE WRITING STRING");
	}
	
	public synchronized void setPattern(int pattern)
	{
		if (arduino == null)
			return;
		String patternString = new String("R ");
		patternString += pattern;
		System.out.println("WRITING STRING");
		arduino.writeString(patternString);
		System.out.println("DONE WRITING STRING");
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
		if (!ledArduinoThread.isAlive() && m_Instance != null)
		{
			ledArduinoThread = new TKOThread(m_Instance);
			ledArduinoThread.setPriority(Definitions.getPriority("ledArduino"));
		}
		if (!ledArduinoThread.isThreadRunning())
		{
			ledArduinoThread.setThreadRunning(true);
		}
		arduino = new SerialPort(9600, Port.kUSB, 8, Parity.kNone, StopBits.kTwo);
		arduino.setWriteBufferMode(WriteBufferMode.kFlushOnAccess);
		arduino.reset();
	}

	/**
	 * The {@code stop} method disables the thread, simply by setting the {@code isThreadRunning} to false via {@code setThreadRunning} and
	 * waits for the method to stop running (on the next iteration of run).
	 */
	public void stop()
	{
		if (ledArduinoThread.isThreadRunning())
		{
			ledArduinoThread.setThreadRunning(false);
		}
		arduino.free();
		arduino = null;
	}

	/**
	 * The run method is what the thread actually calls once. The continual running of the thread loop is done by the while loop, controlled
	 * by a safe boolean inside the TKOThread object. The wait is synchronized to make sure the thread safely sleeps.
	 */
	@Override
	public void run()
	{
		try
		{
			while (ledArduinoThread.isThreadRunning())
			{
				if (arduino != null)
				{
					//ledStripUpdateColorTestPatterns();
					ledStripRandomColor();
					arduino.flush();
				}
				
				synchronized (ledArduinoThread) // synchronized per the thread to make sure that we wait safely
				{
					ledArduinoThread.wait(5000); // the wait time that the thread sleeps, in milliseconds
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

