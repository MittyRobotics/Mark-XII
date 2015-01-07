package org.usfirst.frc.team1351.robot.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.usfirst.frc.team1351.robot.util.TKOThread;

public class TKOLogger implements Runnable
{
	private static LinkedList<String> m_MessageBuffer = new LinkedList<String>();
	private static PrintWriter m_LogFile;
	private static TKOThread loggerThread = new TKOThread(new TKOLogger());
	private static String directory = "/home/lvuser/";

	protected TKOLogger()
	{
		System.out.println("Contstructing logger");
		File f = new File(directory + "log.txt");
		if(f.exists() && !f.isDirectory()) //check if file exists
		{
			int i = 1;
			File test = null;
			do
			{
				test = new File(directory + "log_" + i + ".txt");
				i++;
			} while (test.exists() && !test.isDirectory());
			f.renameTo(test); //TODO Test this but it should rename log file if exists instead of rewriting
		}
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
			//m_LogFile = new PrintWriter(directory + "log.txt", "UTF-8");
			m_LogFile = new PrintWriter(new BufferedWriter(new FileWriter(directory + "log.txt", true)));
		} catch (IOException e)
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
		try
		{
			loggerThread.join();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			synchronized (TKOLogger.class)
			{
				String s = m_MessageBuffer.removeLast() + "\n";
				//m_LogFile.write(s, 0, s.length());
				m_LogFile.println(s);
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
			System.out.println("Leaving run method in TKOLogger...");
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
