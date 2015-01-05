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
	}

	public synchronized boolean isThreadRunning()
	{
		return isThreadRunning;
	}
}
