package org.usfirst.frc.team1351.robot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import edu.wpi.first.wpilibj.DriverStation;

public class TKOLogger implements Runnable
{
	private static TKOLogger m_Instance = null;
	private static LinkedList<String> m_MessageBuffer = new LinkedList<String>();;
	private static BufferedWriter m_LogFile;
	private static Path file = FileSystems.getDefault().getPath("logs", "log.txt");
	private static Charset charset = Charset.forName("US-ASCII");
	private static TKOThread loggerThread = new TKOThread(new TKOLogger());

	protected TKOLogger()
	{
		System.out.println("Contstructing logger");
		
		System.out.println("Done constructing logger");
	}

	public static TKOLogger getInstance()
	{
		if (m_Instance == null)
		{
			synchronized (TKOLogger.class)
			{
				m_Instance = new TKOLogger();
			}
		}
		return m_Instance;
	}

	public static void addMessage(String message)
	{
		String str = "Time: " + DriverStation.getInstance().getMatchTime() + ";Message: " + message;
		m_MessageBuffer.push(str);
	}

	public static void start()
	{
		System.out.println("Starting logger task");
		try
		{
			m_LogFile = Files.newBufferedWriter(file, charset);
		} catch (IOException e)
		{
			System.out.println("Error with creating logfile");
			e.printStackTrace();
		}
		loggerThread.setThreadRunning(true);
		loggerThread.start();
		System.out.println("Started logger task");
	}

	public static void stop()
	{
		System.out.println("Stopping logger task");
		loggerThread.setThreadRunning(false);
		try
		{
			loggerThread.join();
		} catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		while (m_MessageBuffer.size() > 0)
		{
			writeFromQueue();
		}
		try
		{
			m_LogFile.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Stopped logger task");
	}

	public static int getBufferLength()
	{
		return m_MessageBuffer.size();
	}

	public static void writeFromQueue()
	{
		if (m_MessageBuffer.size() > 0)
		{
			String s = m_MessageBuffer.pop() + "\n";
			try
			{
				synchronized (TKOLogger.class)
				{
					m_LogFile.write(s, 0, s.length());
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run()
	{
		try
		{
			while (TKOLogger.loggerThread.isThreadRunning())
			{
				writeFromQueue();
				System.out.println("THREAD RAN!");
				loggerThread.wait(250);
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
