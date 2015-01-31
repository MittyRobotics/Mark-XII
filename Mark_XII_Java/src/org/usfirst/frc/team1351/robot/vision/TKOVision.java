//Last edited by Alex Parks
//on 1/31/15

package org.usfirst.frc.team1351.robot.vision;

import org.usfirst.frc.team1351.robot.util.*;
import org.usfirst.frc.team1351.robot.logger.*;
import org.usfirst.frc.team1351.robot.main.*;
import org.usfirst.frc.team1351.robot.main.Definitions.*;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.vision.USBCamera;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

public class TKOVision implements Runnable {

	public TKOThread visionThread = null;
	private static TKOVision m_Instance = null;
	private static int session;
	private static Image frame;  
	
	protected TKOVision()
	
	{
		//stick4(STICK_4_PORT);
		System.out.println("Vision Activated!!!!!!!!!");
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		//TODO change camera name
		session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
		NIVision.IMAQdxConfigureGrab(session);
		
		//not sure if lastDist, lastProcessingTime, and lastTimestamp will be used
		//if so, will need to be implemented
		
		double lastDist = 0.0;
		double lastProcessingTime = 0.0; //values from 2014
		double lastTimestamp = 0.0;
		
		//TODO set up camera and its settings 
		
		//AxisCamera.WriteMaxFPS(30); //check static
		//AxisCamera.WriteCompression(30);
		//AxisCamera.WriteBrightness(30); //TODO add setting writing
		
		boolean lastTarget = false; //.Hot later
		
		//AddToSingletonList();
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
	
	public static void draw() //TODO figure this business out
	{
		NIVision.IMAQdxStartAcquisition(session);
	
		NIVision.Rect rect = new NIVision.Rect(10, 10, 100, 100);

		while (isOperatorControl() && isEnabled())
		{
			NIVision.IMAQdxGrab(session, frame, 1);
			NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f); // (Image dest, Image source, Rect rect, DrawMode mode, ShapeMode shape, float newPixelValue)
			CameraServer.getInstance().setImage(frame);

			//TODO Here: stuff
			//
			Timer.delay(0.005); // wait for a motor update time
		}
		NIVision.IMAQdxStopAcquisition(session);
	}

	public static void straightDistance()
	{
		int dist;
		int vertHeight;
		int targetHeight;
		int imageHeight;
		int straightAngle;
		
	
		
	}
	@Override
	public void run() 
	{
		try
		{
			while (visionThread.isThreadRunning())
			{
				System.out.println("VISION THREAD RAN!");
				draw();
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