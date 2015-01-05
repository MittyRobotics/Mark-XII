

public class TKOThread extends Thread
{
	private boolean isThreadRunning = false;

	public TKOThread(Runnable r)
	{
		super(r);
	}

	public synchronized void setThreadRunning(boolean status)
	{
		isThreadRunning = status;
	}

	public synchronized boolean isThreadRunning()
	{
		return isThreadRunning;
	}
}
