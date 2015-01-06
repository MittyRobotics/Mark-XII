package org.usfirst.frc.team1351.util;

public class TKOThread extends Thread
{
	private boolean isThreadRunning = false;

	public TKOThread(Runnable r)
	{
		super(r);
		super.setName(r.toString());
	}

	public synchronized void setThreadRunning(boolean status)
	{
		isThreadRunning = status;
		if (isThreadRunning)
		{
			this.start();
		} else
		{
			try
			{//TODO Make sure join is good idea
				this.join(); //waits for it to finish running
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public synchronized boolean isThreadRunning()
	{
		return isThreadRunning;
	}
}
