//Last edited by Adam Filiz
//on 2/26/15

package org.usfirst.frc.team1351.robot.vision;

import org.usfirst.frc.team1351.robot.util.*;
import org.usfirst.frc.team1351.robot.logger.*;
import org.usfirst.frc.team1351.robot.main.*;
import org.usfirst.frc.team1351.robot.main.Definitions.*;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;
import com.ni.vision.NIVision.StructuringElement;

import edu.wpi.first.wpilibj.CameraServer; 
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.vision.AxisCamera;

public class TKOVision implements Runnable 
{
	public TKOThread visionThread = null;
	private static TKOVision m_Instance = null; 
	int session;
    Image frame, BinaryImage, MorphImage, FillImage;
    AxisCamera camera;
    int hexa = 5; //dunno man
	
	protected TKOVision()
	{
		System.out.println("Vision Activated!!!!!!!!!!");
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		camera = new AxisCamera("10.13.51.11");
	}	
	
	public static synchronized TKOVision getInstance()
	{
		if (TKOVision.m_Instance == null)
		{
			m_Instance = new TKOVision();
			m_Instance.visionThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}
	
	public void start()
	{
		System.out.println("Starting vision task");
		
		if (!visionThread.isAlive() && m_Instance != null)
			visionThread = new TKOThread(m_Instance);
		
		if (!visionThread.isThreadRunning())
			visionThread.setThreadRunning(true);

		System.out.println("Started vision task");
	}

	public void stop()
	{
		System.out.println("Stopping vision task");
		if (visionThread.isThreadRunning())
			visionThread.setThreadRunning(false);
		
		System.out.println("Stopped vision task");
	}
	
    public void autonomousControl() {
        NIVision.Rect rect = new NIVision.Rect(10, 10, 100, 100);

        while (isAutonomousControl() && isEnabled()) {
            camera.getImage(frame);
            CameraServer.getInstance().setImage(frame);
            //TODO:acquire image, get values from image, do equation
            Timer.delay(0.005);		// wait for a motor update time
        }
    }
    
    public double linearInterp(double pt1, double pt2, double t){
    	return (pt2 * (1-t)) + (pt1 *(t));
    	
    }
    
    public Boolean processImage(){ //VADIM I WILL FINISH THIS BY SATURDAY
    	
    	System.out.println("Actually starting processing now!!!"); //declare to start function
    
    	camera.writeBrightness(20); //20 for now - will edit later
    	camera.getImage(frame); //Get image from frame
    	
    	if(frame == null) {
    		System.out.println("THERE AIN'T AN IMAGE TO PROCESS");
    		return false;
    	}
    	
    	System.out.println("THERE IS AN IMAGE TO PROCESS. YAY");
    	
    	if(camera.getInstance().getImage(frame) == false) { //sort of at an impass here
    		System.out.println("RAW IMAGE DOESNT WORK BRO");
    		}
    	
    	System.out.println("RAW IMAGE WORKS");
    	
    	
    	NIVision.imaqColorThreshold(frame, BinaryImage, 0, HSV, NIVision.Range(), NIVision.Range(104, 255), NIVision.Range(92, 132)); //why u do dis
    	//imaqColorThreshold(frame,); change later =  (Image dest, Image source, int replaceValue, ColorMode mode, Range plane1Range, Range plane2Range, Range plane3Range)
    	System.out.println("Processed binary/color");
    	
    	NIVision.imaqMorphology(MorphImage, BinaryImage, NIVision.MorphologyMethod.ERODE, ); //Add structuringElement
    	//imaqMorphology (Image dest, Image source, , StructuringElement structuringElement)
    	System.out.println("Removed small objects");
    	
    	NIVision.imaqFillHoles(FillImage, MorphImage, 8); //wtfun is connectity8 broh
    	//imaqFillHoles (NIVision.Image dest,NIVision.Image source, int connectivity8) not sure what int connectivity8 is
    	System.out.println("Filled rest of objects");
    	
    	NIVision.ParticleReport(); //why aint it defined bruh
    	//imaqParticleReport or imaqGetROIBoundingBox - will find out later
    	
    	/*
    	 if (NIVision.ParticleReport() < 0){
    	  System.out.println("Did not get any particle values!!!!!");
    	}
    
     	System.out.println("Particle Report SUCCESSFUL!!!!!");
     	I am currently not sure how to get particle report values. Will inquire on Friday and Saturday (2/27, 2/28)
    	 */
    	//TODO: Figure out values for these functions
    	
    	
    	//if(stick4.GetRawButton(10)) for tomorrow
    }
    
    public double distance() {
    	double targetWidth_Px = 5.; //get n in px from something in WPILib later, and 5 is an example value
    	double dist = 7/(targetWidth_Px * 1.324);
    
    	return dist;
   }
    
   /* public double offAngle(){
    	double angle = 0;
    	
    	
    	return angle;
    }
    */
	@Override
	public void run() 
	{
		try
		{
			while (visionThread.isThreadRunning())
			{
				System.out.println("VISION THREAD RAN!");
				autonomousControl();
				synchronized (visionThread)
				{
					visionThread.wait(5);
				}
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}