#include "WPILib.h"

/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
class Robot: public SampleRobot
{
	Joystick *stick1, *stick2; // only joystick
	CANJaguar *jag1, *jag2, *jag3, *jag4;

public:
	Robot()
	{
		stick1 = new Joystick(0);
		stick2 = new Joystick(1);
		jag1 = new CANJaguar(2);
		jag2 = new CANJaguar(3);
		jag3 = new CANJaguar(4);
		jag4 = new CANJaguar(5);
		//jag->SetControlMode(CANJaguar::kPercentVbus);
		jag1->SetPercentMode();
		jag1->EnableControl();
		jag2->SetPercentMode();
		jag2->EnableControl();
		jag3->SetPercentMode();
		jag3->EnableControl();
		jag4->SetPercentMode();
		jag4->EnableControl();
	}

	/**asajfdjasvd
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous()
	{

	}

	/**
	 * Runs the motors with arcade steering.
	 */
	void OperatorControl()
	{
		long counter = 0;
		while (IsOperatorControl() && IsEnabled())
		{
			if (counter % 100 == 0)
			{
				printf("Hello World! \n");
				//printf("Can Jag Bus V1: %f\n", jag1->GetBusVoltage());
				printf("Can Jag Bus V2: %f\n", jag2->GetBusVoltage());
				//printf("Can Jag Bus V3: %f\n", jag3->GetBusVoltage());
				printf("Can Jag Bus V4: %f\n", jag4->GetBusVoltage());
			}
			//jag1->Set(-stick1->GetY());
			jag2->Set(-stick1->GetY());

			//jag3->Set(stick2->GetY());
			jag4->Set(stick2->GetY());
			Wait(0.005);				// wait for a motor update time
			counter++;
		}
	}

	/**
	 * Runs during test mode
	 */
	void Test()
	{
	}
};

START_ROBOT_CLASS(Robot);
