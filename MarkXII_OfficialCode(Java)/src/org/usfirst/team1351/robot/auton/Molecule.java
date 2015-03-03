package org.usfirst.team1351.robot.auton;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;

public class Molecule {
	
	ArrayList<Atom> chain = new ArrayList<Atom>();
	
	public Molecule()
	{
		
	}
	
	public void add(Atom a)
	{
		chain.add(a);
	}
	
	public void clear()
	{
		chain.clear();
	}
	
	/**
	 * DO NOT CALL INIT
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void init()
	{
		for (Atom a : chain)
		{
			a.init();
		}
	}
	
	public void run()
	{
		for (Atom a : chain)
		{
			a.execute();
		}
	}
	
	public void initAndRun()
	{
		for (Atom a : chain)
		{
			a.init();
			a.execute();
			Timer.delay(0.5);
		}
	}
}
