//Last edited by Praks on 10/21/15

package org.usfirst.frc.team1351.robot.vision; //importing everything that makes our code work
												//these are given by First if you use the template, which you should use
import org.usfirst.frc.team1351.robot.util.*;
import org.usfirst.frc.team1351.robot.logger.*;
import org.usfirst.frc.team1351.robot.main.*;
import org.usfirst.frc.team1351.robot.main.Definitions.*;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ColorMode;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;
import com.ni.vision.NIVision.StructuringElement;
import com.ni.vision.NIVision.ParticleReport;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.MeasurementValue;

import edu.wpi.first.wpilibj.CameraServer; 
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.vision.AxisCamera;

public class TKOVision implements Runnable //vision function class beginning
{
	public TKOThread visionThread = null; //declares our variables, camera, and things for threading
	private static TKOVision m_Instance = null; 
	int session;
    Image frame, BinaryImage, MorphImage, CloseImage, FillImage;
    AxisCamera camera;
    double distanceToTote, boundingWidthRight, boundingWidthLeft, trueBoundingWidth, midBoundingPoint, finalAngleValue, boundingWidth;
 
	
	protected TKOVision() { //initializes camera and camera frame 
		System.out.println("Vision Activated!!!!!!!!!!");
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		camera = new AxisCamera("10.13.51.11");
	}	
	
	public static synchronized TKOVision getInstance() { //creates new vision thread in threading for the stuff
		if (TKOVision.m_Instance == null)
		{
			m_Instance = new TKOVision();
			m_Instance.visionThread = new TKOThread(m_Instance);
		}
		return m_Instance;
	}
	
	public void start() {//initializes vision thread if it is dead or is already running
		System.out.println("Starting vision task");
		
		if (!visionThread.isAlive() && m_Instance != null)
			visionThread = new TKOThread(m_Instance);
		
		if (!visionThread.isThreadRunning())
			visionThread.setThreadRunning(true);

		System.out.println("Started vision task");
	}

	public void stop() { //stops the vision thread if told to
		System.out.println("Stopping vision task");
		if (visionThread.isThreadRunning())
			visionThread.setThreadRunning(false);
		
		System.out.println("Stopped vision task");
	}
	
    public void autonomousControl() { //main function. creates frame on computer, puts an image in the frame, returns distance in inches to target
        NIVision.Rect rect /*um*/ = new NIVision.Rect(10, 10, 100, 100);

        while (DriverStation.getInstance().isAutonomous() && DriverStation.getInstance().isEnabled()) {
            camera.getImage(frame);
            CameraServer.getInstance().setImage(frame);
            
            //TODO:acquire image, get values from image, do equation
            
            Timer.delay(0.005);		// wait for a motor update time
        }
    }
    //sudo apt.get rekt
    
    public double linearInterp(double pt1, double pt2, double t){ //linear interpolation for data tables, not implemented but possible
    	return (pt2 * (1-t)) + (pt1 *(t)); 	
    }
    
    public Boolean processImage(){ 
    	
    System.out.println("Actually starting processing now!!!"); //declare to start function
    
    for(double j = 12.; j < distanceToTote; j++){
    	camera.writeBrightness(20); //20 for now - will test later with camera
    	camera.getImage(frame); //Get image from frame
    	
    	if(frame == null) { //checks if there is an image
    		System.out.println("There is not an image to process!");
    		return false;
    	}
    	
    	System.out.println("There is an image to process!");
    	
    	/*if(camera.getImage(frame) == false) { 
    		System.out.println("RAW IMAGE DOESNT WORK");
    		}
    	*/ //Not sure if necessary for code - must test to be sure
    	
    	//System.out.println("RAW IMAGE WORKS");
    	
    	
    	NIVision.imaqColorThreshold(frame, BinaryImage, 0, ColorMode.HSV, new NIVision.Range(100, 140), new NIVision.Range(104, 255), new NIVision.Range(92, 132)); 
    	//turns raw image into binary image using color threshold
    	
    	NIVision.imaqMorphology(MorphImage, BinaryImage, NIVision.MorphologyMethod.HITMISS, new StructuringElement()); //Add structuringElement
    	//removes noise and extra particles that might turn up
    	
    	NIVision.imaqMorphology(CloseImage, MorphImage, NIVision.MorphologyMethod.CLOSE, new StructuringElement());
    	//closes the object to make sure it is solid (might not be necessary, must test to be sure)
    	
    	NIVision.imaqFillHoles(FillImage, CloseImage, 4);
    	//fills any holes that might still exist
    	System.out.println("Processed image");
    	
    	MeasurementValue.WIDTH.getValue(); //This is what is supposed to get the bounding box width from FillImage
    	//Still not sure how to use it to get that value, since there is no argument for processing an image
    	
  
    	//TODO: Get widths of both bounding boxes
    	
    	
    	
    if(boundingWidth <= 0){
    		System.out.println("Particle analysis did not return values!");
    		return false;

    	}
    
    System.out.println("Particle analysis worked!");
    
    distanceToTote = distance(boundingWidth);
    
    System.out.println("The current distance to tote is: " + distanceToTote + "inches");
    
    	}
    return true;

    }
    
    public double distance(double width) {
    	double dist = 4480/(width * .8696); //TODO: fix distance equation later. Change values and use average bounding box distance 
    										//TODO: possibly need to update camera angle number thing for different cameras
    	return dist; //TODO: Make the calculated boundingBox width the average of the two bounding boxes' widths 
   }
    
   public double offAngle(){ //gets the offset angle needed for the robot to turn the right amount of degrees
    	double angle = 0;
    	trueBoundingWidth = (boundingWidthRight + boundingWidthLeft)/2 ;
    	finalAngleValue = (7 * (midBoundingPoint - 320))/(trueBoundingWidth * distanceToTote);
  
    	Math.asin(finalAngleValue); //until we figure out if this is fast enough or how to set up dynamic tables for getting inverse sin of answer, this is what we will use
    	
    	return angle;
    }
   //TODO: How the heck do you get the bounding box widths of each bounding box and the point between left and right bounding boxes
    
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
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}