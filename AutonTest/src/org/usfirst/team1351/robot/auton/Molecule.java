package org.usfirst.team1351.robot.auton;

import java.util.ArrayList;

public class Molecule
{
	ArrayList<Atom> chain = new ArrayList<Atom>();
	
	public Molecule() {}
	
	public void add(Atom a)
	{
		chain.add(a);
	}
	
	public void clear()
	{
		chain.clear();
	}
	
	public void init()
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
}
