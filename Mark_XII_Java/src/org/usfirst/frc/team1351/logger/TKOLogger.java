package org.usfirst.frc.team1351.logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.usfirst.frc.team1351.util.TKOThread;

public class TKOLogger implements Runnable
{
	private static LinkedList<String> m_MessageBuffer = new LinkedList<String>();
	private static PrintWriter m_LogFile;
	private static TKOThread loggerThread = new TKOThread(new TKOLogger());

	protected TKOLogger()
	{
		System.out.println("Contstructing logger");
		System.out.println("Done constructing logger");
	}

	public static void addMessage(String message)
	{
		// String str = "Time: " + DriverStation.getInstance().getMatchTime() + ";Message: " + message;
		String str = "Time:;Message:" + message;
		m_MessageBuffer.push(str);
	}

	public static void start()
	{
		System.out.println("Starting logger task");
		try
		{
			System.out.println(System.getProperty("user.dir"));
			m_LogFile = new PrintWriter("/home/lvuser/log.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		if (!loggerThread.isThreadRunning())
		{
			loggerThread.setThreadRunning(true);
		}
		System.out.println("Started logger task");
	}

	public static void stop()
	{
		System.out.println("Stopping logger task");
		if (loggerThread.isThreadRunning())
		{
			loggerThread.setThreadRunning(false);
		}
		while (m_MessageBuffer.size() > 0)
		{
			writeFromQueue();
		}
		m_LogFile.flush();
		m_LogFile.close();
		System.out.println("Stopped logger task");
	}

	public static int getBufferLength()
	{
		return m_MessageBuffer.size();
	}

	public static void writeFromQueue()
	{
		if (m_LogFile == null)
			return;
		if (m_MessageBuffer.size() > 0)
		{
			String s = m_MessageBuffer.removeLast() + "\n";
			synchronized (TKOLogger.class)
			{
				m_LogFile.write(s, 0, s.length());
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
				System.out.println("LOGGER THREAD RAN!");
				synchronized (loggerThread)
				{
					loggerThread.wait(100);
				}
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
