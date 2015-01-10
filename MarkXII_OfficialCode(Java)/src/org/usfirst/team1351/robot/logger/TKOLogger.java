package org.usfirst.team1351.robot.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.usfirst.team1351.robot.main.Definitions;
import org.usfirst.team1351.robot.util.TKOThread;

public class TKOLogger implements Runnable
{
	private ConcurrentLinkedQueue<String> m_MessageBuffer = new ConcurrentLinkedQueue<String>();
	private ConcurrentLinkedQueue<String> m_DataBuffer = new ConcurrentLinkedQueue<String>();
	private PrintWriter m_LogFile;
	private PrintWriter m_DataLogFile;
	private static TKOLogger m_Instance = null;
	public TKOThread loggerThread = null;
	private String directory = "/home/lvuser/logs/"; //TODO THIS IS ACTUAL
	//private String directory = "";
	private String logFileName = "log";
	private String dataDumpFileName = "data";
	public long startTime;
	public long dataBufferSize = 0;
	//private long writeCounter = 0;

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
		m_MessageBuffer.add(str);
	}

	public void addData(String dataType, double value, String additionalComment)
	{
		if (additionalComment == null)
			additionalComment = new String();
		String sep = ",";
		String str = dataType + sep + (System.nanoTime() - startTime) + sep + value + sep + additionalComment;
		m_DataBuffer.add(str);
		dataBufferSize++;
	}

	public void start()
	{
		System.out.println("Starting logger task");
		if (!loggerThread.isAlive() && m_Instance != null)
		{
			loggerThread = new TKOThread(m_Instance);
			loggerThread.setPriority(Definitions.getPriority("logger"));
		}
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

	public void stop() // TODO test abrupt program termination effects on log files
	{
		System.out.println("Stopping logger task");
		if (loggerThread.isThreadRunning())
		{
			loggerThread.setThreadRunning(false);
		}
		System.out.println("Logging flushing buffers");
		long totalOperations = 0;
		int messBuffSize = m_MessageBuffer.size();
		int dataBuffSize = m_DataBuffer.size();
		int largest = Math.max(messBuffSize, dataBuffSize);

		for (int i = 0; i < largest; i++)
		{
			long writeStartNS = System.nanoTime();
			writeFromQueue();
			totalOperations += (System.nanoTime() - writeStartNS);
		}
		System.out.println("Logging buffers flushed");
		System.out.println("Flushing took: " + (totalOperations / 10E9) + " (s).");
		System.out.println("Average log flush write duration: " + ((totalOperations / 10E6) / largest) + " (ms).");
		m_LogFile.close();
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
			if (!m_MessageBuffer.isEmpty())
			{
				String s = m_MessageBuffer.poll();
				synchronized (TKOLogger.class)
				{
					m_LogFile.println(s);
				}
			}
			if (!m_DataBuffer.isEmpty())
			{
				String s = m_DataBuffer.poll();
				dataBufferSize--;
				synchronized (TKOLogger.class)
				{
					m_DataLogFile.println(s);
				}
			}
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		//writeCounter++;
		/*
		 * if (writeCounter % 1000 == 0 && loggerThread.isThreadRunning()) { m_LogFile.flush(); m_DataLogFile.flush(); }
		 */
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

				if (m_MessageBuffer.isEmpty() && m_DataBuffer.isEmpty()) //TODO make sure this doesnt slow down other threads
				{
					synchronized (loggerThread)
					{
						loggerThread.wait(50);
					}
				}
			}
			System.out.println("Leaving run method in TKOLogger...");
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}