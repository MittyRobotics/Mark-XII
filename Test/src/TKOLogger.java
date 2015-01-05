
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

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
			m_LogFile = new PrintWriter("log.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e)
		{
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
				System.out.println("THREAD RAN!");
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
