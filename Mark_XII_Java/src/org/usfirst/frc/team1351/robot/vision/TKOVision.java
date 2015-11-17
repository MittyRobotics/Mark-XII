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

//shit from sample

import java.lang.Math;
import java.util.Comparator;
import java.util.Vector;

import com.ni.vision.NIVision.ImageType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Example of finding yellow totes based on retroreflective target.
 * This example utilizes an image file, which you need to copy to the roboRIO
 * To use a camera you will have to integrate the appropriate camera details with this example.
 * To use a USB camera instead, see the SimpelVision and AdvancedVision examples for details
 * on using the USB camera. To use an Axis Camera, see the AxisCamera example for details on
 * using an Axis Camera.
 *
 * Sample images can found here: http://wp.wpi.edu/wpilib/2015/01/16/sample-images-for-vision-projects/ 
 */
public class TKOVision implements Runnable {
		//A structure to hold measurements of a particle
	
		public TKOThread visionThread = null; //declares our variables, camera, and things for threading
		private static TKOVision m_Instance = null; 
		int session;
	
		public class ParticleReport implements Comparator<ParticleReport>, Comparable<ParticleReport>{
			double PercentAreaToImageArea;
			double Area;
			double BoundingRectLeft;
			double BoundingRectTop;
			double BoundingRectRight;
			double BoundingRectBottom;
			
			public int compareTo(ParticleReport r)
			{
				return (int)(r.Area - this.Area);
			}
			
			public int compare(ParticleReport r1, ParticleReport r2)
			{
				return (int)(r1.Area - r2.Area);
			}
		};

		//Structure to represent the scores for the various tests used for target identification
		public class Scores {
			double Area;
			double Aspect;
		};

		//Images
		Image frame;
		Image binaryFrame;
		int imaqError;

		//Constants
		NIVision.Range TOTE_HUE_RANGE = new NIVision.Range(101, 64);	//Default hue range for yellow tote
		NIVision.Range TOTE_SAT_RANGE = new NIVision.Range(88, 255);	//Default saturation range for yellow tote
		NIVision.Range TOTE_VAL_RANGE = new NIVision.Range(134, 255);	//Default value range for yellow tote
		double AREA_MINIMUM = 0.5; //Default Area minimum for particle as a percentage of total image area
		double LONG_RATIO = 2.22; //Tote long side = 26.9 / Tote height = 12.1 = 2.22
		double SHORT_RATIO = 1.4; //Tote short side = 16.9 / Tote height = 12.1 = 1.4
		double SCORE_MIN = 75.0;  //Minimum score to be considered a tote
		double VIEW_ANGLE = 49.4; //View angle fo camera, set to Axis m1011 by default, 64 for m1013, 51.7 for 206, 52 for HD3000 square, 60 for HD3000 640x480
		NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
		NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
		Scores scores = new Scores();

		public void robotInit() {
		    // create images
			frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
			binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
			criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MINIMUM, 100.0, 0, 0);

			//Put default values to SmartDashboard so fields will appear
			SmartDashboard.putNumber("Tote hue min", TOTE_HUE_RANGE.minValue);
			SmartDashboard.putNumber("Tote hue max", TOTE_HUE_RANGE.maxValue);
			SmartDashboard.putNumber("Tote sat min", TOTE_SAT_RANGE.minValue);
			SmartDashboard.putNumber("Tote sat max", TOTE_SAT_RANGE.maxValue);
			SmartDashboard.putNumber("Tote val min", TOTE_VAL_RANGE.minValue);
			SmartDashboard.putNumber("Tote val max", TOTE_VAL_RANGE.maxValue);
			SmartDashboard.putNumber("Area min %", AREA_MINIMUM);
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

		public void run() {
			while (isEnabled())
			{
				//read file in from disk. For this example to run you need to copy image.jpg from the SampleImages folder to the
				//directory shown below using FTP or SFTP: http://wpilib.screenstepslive.com/s/4485/m/24166/l/282299-roborio-ftp
				NIVision.imaqReadFile(frame, "/home/lvuser/SampleImages/image.jpg");

				//Update threshold values from SmartDashboard. For performance reasons it is recommended to remove this after calibration is finished.
				TOTE_HUE_RANGE.minValue = (int)SmartDashboard.getNumber("Tote hue min", TOTE_HUE_RANGE.minValue);
				TOTE_HUE_RANGE.maxValue = (int)SmartDashboard.getNumber("Tote hue max", TOTE_HUE_RANGE.maxValue);
				TOTE_SAT_RANGE.minValue = (int)SmartDashboard.getNumber("Tote sat min", TOTE_SAT_RANGE.minValue);
				TOTE_SAT_RANGE.maxValue = (int)SmartDashboard.getNumber("Tote sat max", TOTE_SAT_RANGE.maxValue);
				TOTE_VAL_RANGE.minValue = (int)SmartDashboard.getNumber("Tote val min", TOTE_VAL_RANGE.minValue);
				TOTE_VAL_RANGE.maxValue = (int)SmartDashboard.getNumber("Tote val max", TOTE_VAL_RANGE.maxValue);

				//Threshold the image looking for yellow (tote color)
				NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSV, TOTE_HUE_RANGE, TOTE_SAT_RANGE, TOTE_VAL_RANGE);

				//Send particle count to dashboard
				int numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
				SmartDashboard.putNumber("Masked particles", numParticles);

				//Send masked image to dashboard to assist in tweaking mask.
				CameraServer.getInstance().setImage(binaryFrame);

				//filter out small particles
				float areaMin = (float)SmartDashboard.getNumber("Area min %", AREA_MINIMUM);
				criteria[0].lower = areaMin;
				imaqError = NIVision.imaqParticleFilter4(binaryFrame, binaryFrame, criteria, filterOptions, null);

				//Send particle count after filtering to dashboard
				numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
				SmartDashboard.putNumber("Filtered particles", numParticles);

				if(numParticles > 0)
				{
					//Measure particles and sort by particle size
					Vector<ParticleReport> particles = new Vector<ParticleReport>();
					for(int particleIndex = 0; particleIndex < numParticles; particleIndex++)
					{
						ParticleReport par = new ParticleReport();
						par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
						par.Area = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA);
						par.BoundingRectTop = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
						par.BoundingRectLeft = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
						par.BoundingRectBottom = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
						par.BoundingRectRight = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
						particles.add(par);
					}
					particles.sort(null);

					//This example only scores the largest particle. Extending to score all particles and choosing the desired one is left as an exercise
					//for the reader. Note that this scores and reports information about a single particle (single L shaped target). To get accurate information 
					//about the location of the tote (not just the distance) you will need to correlate two adjacent targets in order to find the true center of the tote.
					scores.Aspect = AspectScore(particles.elementAt(0));
					SmartDashboard.putNumber("Aspect", scores.Aspect);
					scores.Area = AreaScore(particles.elementAt(0));
					SmartDashboard.putNumber("Area", scores.Area);
					boolean isTote = scores.Aspect > SCORE_MIN && scores.Area > SCORE_MIN;

					//Send distance and tote status to dashboard. The bounding rect, particularly the horizontal center (left - right) may be useful for rotating/driving towards a tote
					SmartDashboard.putBoolean("IsTote", isTote);
					SmartDashboard.putNumber("Distance", computeDistance(binaryFrame, particles.elementAt(0)));
				} else {
					SmartDashboard.putBoolean("IsTote", false);
				}

				Timer.delay(0.005);				// wait for a motor update time
			}
		}

		public void operatorControl() {
			while(isEnabled()) {
				Timer.delay(0.005);				// wait for a motor update time
			}
		}

		//Comparator function for sorting particles. Returns true if particle 1 is larger
		static boolean CompareParticleSizes(ParticleReport particle1, ParticleReport particle2)
		{
			//we want descending sort order
			return particle1.PercentAreaToImageArea > particle2.PercentAreaToImageArea;
		}

		/**
		 * Converts a ratio with ideal value of 1 to a score. The resulting function is piecewise
		 * linear going from (0,0) to (1,100) to (2,0) and is 0 for all inputs outside the range 0-2
		 */
		double ratioToScore(double ratio)
		{
			return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
		}

		double AreaScore(ParticleReport report)
		{
			double boundingArea = (report.BoundingRectBottom - report.BoundingRectTop) * (report.BoundingRectRight - report.BoundingRectLeft);
			//Tape is 7" edge so 49" bounding rect. With 2" wide tape it covers 24" of the rect.
			return ratioToScore((49/24)*report.Area/boundingArea);
		}

		/**
		 * Method to score if the aspect ratio of the particle appears to match the retro-reflective target. Target is 7"x7" so aspect should be 1
		 */
		double AspectScore(ParticleReport report)
		{
			return ratioToScore(((report.BoundingRectRight-report.BoundingRectLeft)/(report.BoundingRectBottom-report.BoundingRectTop)));
		}

		/**
		 * Computes the estimated distance to a target using the width of the particle in the image. For more information and graphics
		 * showing the math behind this approach see the Vision Processing section of the ScreenStepsLive documentation.
		 *
		 * @param image The image to use for measuring the particle estimated rectangle
		 * @param report The Particle Analysis Report for the particle
		 * @param isLong Boolean indicating if the target is believed to be the long side of a tote
		 * @return The estimated distance to the target in feet.
		 */
		double computeDistance (Image image, ParticleReport report) {
			double normalizedWidth, targetWidth;
			NIVision.GetImageSizeResult size;

			size = NIVision.imaqGetImageSize(image);
			normalizedWidth = 2*(report.BoundingRectRight - report.BoundingRectLeft)/size.width;
			targetWidth = 7;

			return  targetWidth/(normalizedWidth*12*Math.tan(VIEW_ANGLE*Math.PI/(180*2)));
		}
}

//old vision code
//used for reference

/*public class TKOVision implements Runnable //vision function class beginning
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
        NIVision.Rect rect um = new NIVision.Rect(10, 10, 100, 100);

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
    	
    	if(camera.getImage(frame) == false) { 
    		System.out.println("RAW IMAGE DOESNT WORK");
    		}
    	 //Not sure if necessary for code - must test to be sure
    	
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
}*/