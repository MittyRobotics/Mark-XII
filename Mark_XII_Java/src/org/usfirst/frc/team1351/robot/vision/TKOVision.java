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

public class TKOVision implements Runnable {

	private static TKOThread visionThread = new TKOThread(new TKOVision());
	private static int session;
	private static Image frame;
	private static boolean isEnabled; 
	
	protected TKOVision()
	{
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		// TODO change camera name
		session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
		NIVision.IMAQdxConfigureGrab(session);
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

		while (isOperatorControl() && isEnabled = true)
		{
			NIVision.IMAQdxGrab(session, frame, 1);
			NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);

			CameraServer.getInstance().setImage(frame);

			/** robot code here! **/
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
