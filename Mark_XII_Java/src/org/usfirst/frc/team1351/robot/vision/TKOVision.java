//Last edited by Alex Parks
//on 1/17/15

package org.usfirst.frc.team1351.robot.vision;

import org.usfirst.frc.team1351.robot.util.*;
import org.usfirst.frc.team1351.robot.logger.*;
import org.usfirst.frc.team1351.robot.main.*;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.vision.AxisCamera;


public class TKOVision implements Runnable {

	private static TKOThread visionThread = new TKOThread(new TKOVision());
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
		double lastDist = 0.0;
		double lastProcessingTime = 0.0; //values from 2014
		double lastTimestamp = 0.0;
		//AxisCamera.WriteMaxFPS(30); check static
		//AxisCamera.WriteCompression(30);
		//AxisCamera.WriteBrightness(30); //TODO add setting writing
		
		boolean lastTarget = false; //.Hot later
		lastDist = 0.;
		
		//AddToSingletonList();
	}
	
	public void inst()
	{
		if(!m_Instance)
		{
			System.out.println("TKOVision instance is null\n");
			m_Instance = new TKOVision();		
		}
		return m_Instance;
	}
	
	
	public static void start()
	{
		System.out.println("Starting vision task");
		if (!visionThread.isThreadRunning())
			visionThread.setThreadRunning(true);

		System.out.println("Started vision task");
	}

	public static void stop()
	{
		System.out.println("Stopping vision task");
		if (visionThread.isThreadRunning())
			visionThread.setThreadRunning(false);
		
		System.out.println("Stopped vision task");
	}
	
	public static void draw()
	{
		NIVision.IMAQdxStartAcquisition(session);
	
		NIVision.Rect rect = new NIVision.Rect(10, 10, 100, 100);

		while (isOperatorControl() && isEnabled())
		{
			NIVision.IMAQdxGrab(session, frame, 1);
			NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f); // (Image dest, Image source, Rect rect, DrawMode mode, ShapeMode shape, float newPixelValue)
			CameraServer.getInstance().setImage(frame);

			//TODO Here:
			//
			Timer.delay(0.005); // wait for a motor update time
		}
		NIVision.IMAQdxStopAcquisition(session);
	}

	@Override
	public void run() {
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
