//Last edited by Alex Parks
//on 2/16/15

package org.usfirst.team1351.robot.vision;

import org.usfirst.team1351.robot.util.TKOThread;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.vision.AxisCamera;

public class TKOVision implements Runnable
{
	public TKOThread visionThread = null;
	private static TKOVision m_Instance = null;
	int session;
	Image frame;
	AxisCamera camera;

	protected TKOVision()

	{
		System.out.println("Vision Activated!!!!!!!!!");
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

	public void process()
	{
		if (DriverStation.getInstance().isAutonomous() && DriverStation.getInstance().isEnabled())
		{
			camera.writeBrightness(0);
			camera.getImage(frame);
			
			Image processedImage = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
			//NIVision.imaqColorThreshold(processedImage, frame, 0, NIVision.ColorMode.HSV, range1, range2, range3);
//			CameraServer.getInstance().setImage(frame);

			/** robot code here! **/

		}
	}

	@Override
	public void run()
	{
		try
		{
			while (visionThread.isThreadRunning())
			{
				process();
				synchronized (visionThread)
				{
					visionThread.wait(50);
				}
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}