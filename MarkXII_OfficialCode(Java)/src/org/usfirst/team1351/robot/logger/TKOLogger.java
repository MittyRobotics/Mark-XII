package org.usfirst.team1351.robot.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.usfirst.team1351.robot.util.TKOThread;

public class TKOLogger implements Runnable
{
	private LinkedList<String> m_MessageBuffer = new LinkedList<String>();
	private PrintWriter m_LogFile;
	private LinkedList<String> m_DataBuffer = new LinkedList<String>();
	private PrintWriter m_DataLogFile;
	private static TKOLogger m_Instance = null;
	public TKOThread loggerThread = null;
	private String directory = "/home/lvuser/";
	private String logFileName = "log.txt";
	private String dataDumpFileName = "data.csv";
	public long startTime;

	protected TKOLogger()
	{
		System.out.println("Contstructing logger");
		addMessage("roboRIO LOGGER INITIALIZED AT: " + (System.nanoTime() / 1000000000));
		startTime = System.nanoTime();
		File f = new File(directory + logFileName + ".txt");
		if (f.exists() && !f.isDirectory()) // check if file exists
		{
			int i = 1;
			File test = null;
			do
			{
				test = new File(directory + logFileName + "_" + i + ".txt");
				i++;
			} while (test.exists() && !test.isDirectory());
			f.renameTo(test); // TODO Test this but it should rename log file if exists instead of rewriting
		}

		f = new File(directory + dataDumpFileName + ".csv");
		if (f.exists() && !f.isDirectory()) // check if file exists
		{
			int i = 1;
			File test = null;
			do
			{
				test = new File(directory + dataDumpFileName + "_" + i + ".csv");
				i++;
			} while (test.exists() && !test.isDirectory());
			f.renameTo(test); // TODO Test this but it should rename log file if exists instead of rewriting
		}
		System.out.println("Done constructing logger");
	}

	public static synchronized TKOLogger getInstance()
	{
		if (TKOLogger.m_Instance == null)
		{
			m_Instance = new TKOLogger();
			m_Instance.loggerThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}

	public void addMessage(String message)
	{
		// String str = "Time: " + DriverStation.getInstance().getMatchTime() + ";Message: " + message;
		String str = "Time(s): " + (System.nanoTime() / 1000000000) + " Message:" + message;
		m_MessageBuffer.push(str);
	}

	public void addData(String dataType, double value, String additionalComment)
	{
		if (additionalComment == null)
			additionalComment = new String();
		String sep = ",";
		String str = dataType + sep + (System.nanoTime() - startTime) + sep + value + sep + additionalComment;
		m_DataBuffer.push(str);
	}

	public void start()
	{
		System.out.println("Starting logger task");
		if (!loggerThread.isAlive() && m_Instance != null)
			loggerThread = new TKOThread(m_Instance);
		try
		{
			System.out.println("Home dir: " + System.getProperty("user.dir"));
			m_LogFile = new PrintWriter(new BufferedWriter(new FileWriter(directory + logFileName + ".txt", true)));
			m_DataLogFile = new PrintWriter(new BufferedWriter(new FileWriter(directory + dataDumpFileName + ".csv", true)));

			BufferedReader br = new BufferedReader(new FileReader(directory + dataDumpFileName + ".csv"));
			if (br.readLine() == null)
			{
				m_DataLogFile.println("DataDescriptor,TimeElap(ns),Value,AdditionalComment");
			}
			br.close();
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

	public void stop()
	{
		System.out.println("Stopping logger task");
		if (loggerThread.isThreadRunning())
		{
			loggerThread.setThreadRunning(false);
		}
		while (m_MessageBuffer.size() > 0 || m_DataBuffer.size() > 0)
		{
			writeFromQueue();
		}
		m_LogFile.flush();
		m_LogFile.close();
		m_DataLogFile.flush();
		m_DataLogFile.close();
		System.out.println("Stopped logger task");
	}

	public int getMessageBufferLength()
	{
		return m_MessageBuffer.size();
	}

	public void writeFromQueue()
	{
		try
		{
			if (m_LogFile == null)
				return;
			if (m_MessageBuffer.size() > 0)
			{
				synchronized (TKOLogger.class)
				{
					String s = m_MessageBuffer.removeLast();
					m_LogFile.println(s);
				}
			}
			if (m_DataBuffer.size() > 0)
			{
				synchronized (TKOLogger.class)
				{
					String s = m_DataBuffer.removeLast();
					m_DataLogFile.println(s);
				}
			}
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try
		{
			while (loggerThread.isThreadRunning())
			{
				writeFromQueue();
				// System.out.println("LOGGER THREAD RAN!");
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