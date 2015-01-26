#include "WPILib.h"
#include "Definitions.h"

/* 
 * 4.0 p, .2 i, ,125 increment, 3.001 distance 
 * 2.0, .01, .03, 3.001
 * Code for usage on the test bed. 
 * This code is for the testing of CANJaguars, Physical and Optical Limit Switches, and Encoders, as well as motors via the CANJaguars
 * Ports must be declared, otherwise, the values are mostly garbage, and the code shouldn't run properly
 * Have fun?
 * Last edited by Ishan Shah, Louis Coffin, and Shreyas Vaidya on 24 Jan 2015
 */
class RobotDemo: public SimpleRobot {
	//Initializations

	CANJaguar rightTest;
	CANJaguar leftTest;
	Encoder ncoder;
	DigitalInput opticalTest1;
	DigitalInput opticalTest2;
	DriverStation *driverStation;
	PIDController driveControl;
	Joystick joy1;

	//Ports for the parts
	int rightJaguarPort;//Controls motor current and voltage
	int leftJaguarPort;
	int opticalSwitchPort1;//Stops the robot if the flag interupts the light
	int opticalSwitchPort2;
	int digitalSideCar;//Moving metal piece on top of the slider
	int ncoderPort;
	int aChannel;
	int bChannel;

public:
	RobotDemo() :
				// as they are declared above.

				//Port Declarations
				rightTest(1, CANJaguar::kPercentVbus),
				leftTest(4, CANJaguar::kPercentVbus), opticalTest1(1, 7),
				opticalTest2(1, 6), ncoder(1, 2, true, CounterBase::k4X),
				driveControl(0, 0, 0, &ncoder, &rightTest), joy1(1)

	{
		//PLEASE SET VALUES FOR THESE!!!  
		//Like, make sure you set some before running, otherwise bad things will happen
		//BTW, these don't actually do anything, they are just very useful for reference. 
		rightJaguarPort = 1;
		leftJaguarPort = 4;
		opticalSwitchPort1 = 7;
		opticalSwitchPort2 = 6;
		digitalSideCar = 1;
		aChannel = 1;
		bChannel = 2;
		driverStation = DriverStation::GetInstance();
		ncoder.SetDistancePerPulse(0.027); //Change this later, if possible, based on requirements and later calculations 
		/*
		 *Recalculate this value every time you change the end hardware
		 *Utilize gear ratios to calculate the distance per pulse first, corroborate your work by actually measuring and stuff. 
		 *Place that value here, and have great fun
		 */
	}

	void RobotInit() {
	}

	void Disabled() {
		//Safety before the code runs for the driver's reference
		printf(
				"Initializing in autonomous mode with all default settings will make the robot perform 10 loops after an autohome. It will then travel to the second limit switch and hold its position there, all via PID. \n Use the DriverStation IO to change the number of iterations. \n");
		printf(
				"Having DS IO 1 set to true will have 10 loops, having it set to false will have 5 loops. \n");

		printf(
				"THIS AREA IS A PINCH ZONE. THE SLED WILL NOT STOP FOR FINGERS. CAN'T STOP, WON'T STOP! PLEASE REMOVE ALL ITEMS FROM THE BENCH. \n ");
	}

	void autoHome() {
		ncoder.Start();
		ncoder.Reset();
		driveControl.SetPID(0.0100, 0.001, 0);
		driveControl.SetSetpoint(ncoder.GetDistance());
		driveControl.Disable();
		bool check = false;
		while (!check) {
			rightTest.Set(-.25);
			leftTest.Set(rightTest.Get());
			if (opticalTest1.Get() == 0) {
				check = true;
				ncoder.Reset();
				driveControl.SetSetpoint(ncoder.GetDistance());
				rightTest.Set(0);
				leftTest.Set(rightTest.Get());
			}
		}

		check = false;
		while (!check) {
			rightTest.Set(.25);
			leftTest.Set(rightTest.Get());
			if (opticalTest2.Get() == 0) {
				check = true;
				driveControl.SetSetpoint(ncoder.GetDistance());
				rightTest.Set(0);
				leftTest.Set(0);
			}
		}
		//driveControl.SetSetpoint(100); //100 increments above the original
	}
	//Code for going forward. Needs PID, incrementer, etc.
	void goForward(float incrementer) {
		double p = driveControl.GetP();
		while (ncoder.GetDistance() <= 24.5 && IsEnabled()) {
			driveControl.SetSetpoint(driveControl.GetSetpoint() + incrementer);
			printf("Encoder Dist: %f\t", ncoder.GetDistance());
			printf("Controller setpoint: %f\t", driveControl.GetSetpoint());
			printf("Controller E: %f\n", driveControl.GetError());
			printf("P Val: %f \n", p);
			leftTest.Set(rightTest.Get()); //ASDF
//			if (opticalTest1.Get() == 0 || opticalTest2.Get() == 0) {
//				driveControl.Disable();
//			}
		}
		driveControl.SetSetpoint(24.5);
		while (abs(driveControl.GetError()) > .5 && IsEnabled()) {
			printf("Encoder Dist: %f\t", ncoder.GetDistance());
			printf("Controller setpoint: %f\t", driveControl.GetSetpoint());
			printf("Controller E: %f\n", driveControl.GetError());
			printf("P Val: %f \n", p);
			leftTest.Set(rightTest.Get());
			if (opticalTest1.Get() == 0 || opticalTest2.Get() == 0) {
				driveControl.Disable();
			}
		}
	}
	//Code to move backwards. Includes incrementer, PID, etc.
	void goBackward(float incrementer) { //More horrid naming convention, very sorry
		double p = driveControl.GetP();
		while (ncoder.GetDistance() >= 1 && IsEnabled()) {
			driveControl.SetSetpoint(driveControl.GetSetpoint() - incrementer);
			printf("Encoder Dist: %f\t", ncoder.GetDistance());
			printf("Controller setpoint: %f\t", driveControl.GetSetpoint());
			printf("Controller E: %f\n", driveControl.GetError());
			printf("P Val: %f \n", p);
			leftTest.Set(rightTest.Get());

		}
		driveControl.SetSetpoint(1);
		while (abs(driveControl.GetError()) > .5 && IsEnabled()) {
			printf("Encoder Dist: %f\t", ncoder.GetDistance());
			printf("Controller setpoint: %f\t", driveControl.GetSetpoint());
			printf("Controller E: %f\n", driveControl.GetError());
			printf("P Val: %f \n", p);
			leftTest.Set(rightTest.Get());
			if (opticalTest1.Get() == 0 || opticalTest2.Get() == 0) {
				driveControl.Disable();
			}
		}
	}
	//test stuff
	void goForwardTest(float incrementer, double conversion) { //Please forgive the horrid naming convention. Please. 
		while (ncoder.GetDistance() <= 800 && IsEnabled()) {
			driveControl.SetSetpoint(driveControl.GetSetpoint() + incrementer);
			leftTest.Set(rightTest.Get());
			printf("Inches: %f \n", ncoder.GetDistance() * conversion);
			if (driveControl.GetSetpoint() > 800) {
				driveControl.SetSetpoint(800);
			}
		}
		driveControl.SetSetpoint(800);
		while (abs(driveControl.GetError()) > 2 && IsEnabled()) {
			leftTest.Set(rightTest.Get());
			printf("Inches: %f \n", ncoder.GetDistance() * conversion);

		}
	}
	void goBackwardTest(float incrementer, double conversion) { //More horrid naming convention, very sorry
		while (ncoder.GetDistance() >= 100 && IsEnabled()) {
			driveControl.SetSetpoint(driveControl.GetSetpoint() - incrementer);
			leftTest.Set(rightTest.Get());
			printf("Inches: %f \n", ncoder.GetDistance() * conversion);
			if (driveControl.GetSetpoint() < 100) {
				driveControl.SetSetpoint(100);
			}
		}
		driveControl.SetSetpoint(100);
		while (abs(driveControl.GetError()) > 2 && IsEnabled()) {
			leftTest.Set(rightTest.Get());
			printf("Inches: %f \n", ncoder.GetDistance() * conversion);

		}
	}
	//Set in autonomous for autohoming
	void Autonomous() {
		autoHome();
		while (IsAutonomous() && IsEnabled()) {
			//float point = driverStation->GetAnalogIn(4) * 100;
			driveControl.Enable();
			driveControl.SetOutputRange(-.5, .5);
			float incrementer = .3;
			int rotations = 10;
			if (!driverStation->GetDigitalIn(1)) {
				rotations = 5;
			}
			float p = 0.0050;
			float i = 0.001;
			driveControl.SetPID(p, i, 0.0);
			p = driverStation->GetAnalogIn(1) / 10;
			i = driverStation->GetAnalogIn(2) / 10;
			incrementer = driverStation->GetAnalogIn(3);
			driveControl.SetPID(p, i, 0.0);
			driveControl.SetSetpoint(ncoder.GetDistance());
			/*
			 driveControl.SetSetpoint(driveControl.GetSetpoint() - point);

			 while (driveControl.GetSetpoint() > point) {
			 driveControl.SetSetpoint(
			 driveControl.GetSetpoint() - incrementer);
			 }
			 */

			for (int i = 0; i < rotations; i++) {

				goBackward(incrementer);
				goForward(incrementer);
				if (opticalTest1.Get() == 0 || opticalTest2.Get() == 0) {
					driveControl.Disable();
				}
			}

			while (IsEnabled()) {
				printf("Encoder Dist: %f\t", ncoder.GetDistance());
				printf("Controller setpoint: %f\t", driveControl.GetSetpoint());
				printf("Controller E: %f\n", driveControl.GetError());
				printf("P: %f\t", driveControl.GetP());
				printf("I: %f \t", driveControl.GetI());
				printf("Increment: %f \t", incrementer);
				//printf("Distance: %f \n", point);
				leftTest.Set(rightTest.Get());
			}
		}

	}
	;

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl() {
		printf("Starting calibrating\n");
		autoHome();
		printf("Done calibrating!\n");
		//driveControl.SetOutputRange(-.5, .5);
		float incrementer = 3.0;
		float p = 0.0050;
		float i = 0.001;
		//		leftTest.SetVoltageRampRate(12.);
		//		rightTest.SetVoltageRampRate(12.);
		driveControl.SetPID(p, i, 0.0); //P is 0.2, I is 0, incrementer is 1.250, distance is 3.0 
		driveControl.Enable();
		rightTest.ConfigSoftPositionLimits(0, 940);
		incrementer = driverStation->GetAnalogIn(3);
		p = driverStation->GetAnalogIn(1) / 10;
		i = driverStation->GetAnalogIn(2) / 10;
		driveControl.SetPID(p, i, 0.0);
		double length = ncoder.GetDistance();
		bool check = false;
		while (IsEnabled()) {
			float addition = -joy1.GetY() * incrementer;
			printf("Setpoint: %f Addition: %f ncoderDist: %f\n",
					driveControl.GetSetpoint(), addition, ncoder.GetDistance());
			//			printf("Distance: %f\t Ticks?: %d\n P: %f\t I: %f\n",
			//					ncoder.GetDistance(), ncoder.Get(), driveControl.GetP(),
			//					driveControl.GetI());
			//			printf("Current: %f \t Voltage: %f\n",
			//					rightTest.GetOutputCurrent(), rightTest.GetOutputVoltage());
			//			printf("Switch 1: %d \t Switch 2: %d \n", opticalTest1.Get(),
			//					opticalTest2.Get());
			leftTest.Set(rightTest.Get());
			if ((addition > 0.1 && opticalTest2.Get() == 1) || (addition < -0.1
					&& opticalTest1.Get() == 1)) {
				double value = (10. * addition) + driveControl.GetSetpoint();
				if (value > length - .5) {
					value = length - .5;
				}
				if (value < 0.5) {
					value = 0.5;
				}
				driveControl.SetSetpoint(value);
				leftTest.Set(rightTest.Get());
			}
			while (joy1.GetTrigger() == 1) {
				driveControl.SetSetpoint(5);
				leftTest.Set(rightTest.Get());
				printf("Setpoint: %f Addition: %f ncoderDist: %f\n",
						driveControl.GetSetpoint(), addition,
						ncoder.GetDistance());
				printf("Current: %f \t Voltage: %f\n",
						rightTest.GetOutputCurrent(),
						rightTest.GetOutputVoltage());

			}
			while (joy1.GetRawButton(2) == 1) {
				driveControl.SetSetpoint(length / 2);
				leftTest.Set(rightTest.Get());
				printf("Setpoint: %f Addition: %f ncoderDist: %f\n",
						driveControl.GetSetpoint(), addition,
						ncoder.GetDistance());
				printf("Current: %f \t Voltage: %f\n",
						rightTest.GetOutputCurrent(),
						rightTest.GetOutputVoltage());

			}
			while (joy1.GetRawButton(3) == 1) {
				driveControl.SetSetpoint(6);
				leftTest.Set(rightTest.Get());
				printf("Setpoint: %f Addition: %f ncoderDist: %f\n",
						driveControl.GetSetpoint(), addition,
						ncoder.GetDistance());
				printf("Current: %f \t Voltage: %f\n",
						rightTest.GetOutputCurrent(),
						rightTest.GetOutputVoltage());

			}
			while (joy1.GetRawButton(4) == 1) {
				driveControl.SetSetpoint(0);
				leftTest.Set(rightTest.Get());
				printf("Setpoint: %f Addition: %f ncoderDist: %f\n",
						driveControl.GetSetpoint(), addition,
						ncoder.GetDistance());
				leftTest.Set(rightTest.Get());
				printf("Current: %f \t Voltage: %f\n",
						rightTest.GetOutputCurrent(),
						rightTest.GetOutputVoltage());
			}
			while (joy1.GetRawButton(5) == 1) {
				driveControl.SetSetpoint(length);
				leftTest.Set(rightTest.Get());
				printf("Setpoint: %f Addition: %f ncoderDist: %f\n",
						driveControl.GetSetpoint(), addition,
						ncoder.GetDistance());
				leftTest.Set(rightTest.Get());
				printf("Current: %f \t Voltage: %f\n",
						rightTest.GetOutputCurrent(),
						rightTest.GetOutputVoltage());
			}
			printf("Current: %f \t Voltage: %f\n",
					rightTest.GetOutputCurrent(), rightTest.GetOutputVoltage());
		}

	}

	void Test() {
		/*
		 double conversionFactor = 0.0320053120849933598937583001328;
		 while (IsEnabled()) {
		 autoHome();
		 float point = ncoder.GetDistance();
		 driveControl.Enable();
		 driveControl.SetOutputRange(-1., 1.);
		 float incrementer = 0.50;
		 int rotations = 10;
		 if (!driverStation->GetDigitalIn(1)) {
		 rotations = 5;
		 }

		 for (int i = 0; i < rotations; i++) {
		 goBackwardTest(incrementer, conversionFactor);
		 goForwardTest(incrementer, conversionFactor);
		 }
		 while (true && IsEnabled()) {
		 driveControl.SetSetpoint(point);
		 }
		 }
		 */
		autoHome();
		//driveControl.Enable(); 
		while (IsEnabled()) {

			printf("Distance: %f\t Ticks?: %d\n P: %f\t I: %f\n",
					ncoder.GetDistance(), ncoder.GetRaw(), driveControl.GetP(),
					driveControl.GetI());
			printf("Current: %f \t Voltage: %f\n",
					rightTest.GetOutputCurrent(), rightTest.GetOutputVoltage());
			printf("Switch 1: %d \t Switch 2: %d \n", opticalTest1.Get(),
					opticalTest2.Get());
		}

	}
};

START_ROBOT_CLASS(RobotDemo)
;

/*
 * Requirements: 
 * 
 */

