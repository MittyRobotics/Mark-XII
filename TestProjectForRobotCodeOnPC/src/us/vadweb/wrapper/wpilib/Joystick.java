package us.vadweb.wrapper.wpilib;

import java.util.Random;

public class Joystick
{
	private int id;
	private Random r = new Random();

	public Joystick(int id)
	{
		this.id = id;
	}

	public int getDeviceID()
	{
		return id;
	}

	public double getY()
	{
		return r.nextDouble();
	}
	public double getX()
	{
		return r.nextDouble();
	}

}
