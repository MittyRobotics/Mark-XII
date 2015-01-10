package us.vadweb.wrapper.wpilib;

import java.util.Random;

public class PowerDistributionPanel
{
	private Random r = new Random();

	public long getTotalCurrent()
	{
		return r.nextLong();
	}

	public double getCurrent(int i)
	{
		return (i * r.nextDouble());
	}
}
