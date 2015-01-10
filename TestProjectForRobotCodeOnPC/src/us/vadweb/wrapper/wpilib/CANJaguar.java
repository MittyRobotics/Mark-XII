package us.vadweb.wrapper.wpilib;

import java.util.Random;

public class CANJaguar
{
	private int id;
	private Random r = new Random();
	private long counterOfCurrentCalls = 0;
	
	public CANJaguar(int id)
	{
		//yolo
		this.id = id;
	}
	public int getDeviceID()
	{
		return id;
	}
	public void disableControl()
	{
		// TODO Auto-generated method stub
		
	}
	public void setCurrentMode(double p, double i, double d)
	{
		// TODO Auto-generated method stub
		
	}
	public void enableControl()
	{
		// TODO Auto-generated method stub
		
	}
	public void free()
	{
		// TODO Auto-generated method stub
	}
	public void set(double d)
	{
		counterOfCurrentCalls -= 20;
	}
	public double getTemperature()
	{
		return (r.nextDouble() * r.nextDouble()) + (r.nextInt(20));
	}
	public double getOutputCurrent()
	{
		counterOfCurrentCalls++;
		if (counterOfCurrentCalls < 100)
			return counterOfCurrentCalls;
		else
			return (r.nextDouble() * r.nextDouble()) + (r.nextInt(7));
	}
	public double getOutputVoltage()
	{
		return (r.nextDouble() * r.nextDouble()) + (r.nextInt(20));
	}
	public double getBusVoltage()
	{
		return (r.nextDouble() * r.nextDouble()) + (r.nextInt(20));
	}

}
