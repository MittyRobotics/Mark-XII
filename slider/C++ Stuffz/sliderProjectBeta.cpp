#include "WPILib.h"
#include "Definitions.h"

/* 
 * Code for usage on the test bed. 
 * This code is for the testing of CANJaguars, Physical and Optical Limit Switches, and Encoders, as well as motors via the CANJaguars
 * Ports must be declared, otherwise, the values a     re mostly garbage, and the code shouldn't run properly
 * Have fun?
 */
class RobotDemo : public SimpleRobot
{
	//Initializations

	CANJaguar rightTest; 
	//CANJaguar leftTest; 
	Encoder ncoder; 
	DigitalInput opticalTest1;
	DigitalInput opticalTest2; 
	DriverStation *driverStation; 
	PIDController driveControl; 
	
	//Ports for the parts
	int rightJaguarPort;
	int leftJaguarPort; 
	int opticalSwitchPort1; 
	int opticalSwitchPort2; 
	int digitalSideCar; 
	int ncoderPort; 
	int aChannel; 
	int bChannel; 

public:
	RobotDemo():
			// as they are declared above.
		
		//Moar Declarations
		rightTest(1, CANJaguar::kPercentVbus),
		//leftTest(4, CANJaguar::kPercentVbus),
		opticalTest1(1, 10), 
		opticalTest2(1, 5), 
		ncoder(1, 2, true, CounterBase::k4X), 
		driveControl(0, 0, 0, &ncoder, &rightTest ) 
		
	{
		//PLEASE SET VALUES FOR THESE!!!  
		//Like, make sure you set some before running, otherwise bad things will happen
		rightJaguarPort = 1;
		leftJaguarPort = 2; 
		opticalSwitchPort1 = 10; 
		opticalSwitchPort2 = 5; 
		digitalSideCar = 1;
		aChannel = 1; 
		bChannel = 2; 
		driverStation = DriverStation::GetInstance();
	}

	void autoHome() 
	{
		bool check = false; 
		while(!check) {
			rightTest.Set(.25); 
			//leftTest.Set(.25); 
			if(opticalTest1.Get() == 0 || opticalTest2.Get() == 0) {
				check = true; 
				ncoder.Reset(); 
			}
		}
	}
	
	void Autonomous()
	{
		/*
		 * Only runs while enabled in autonomous (Duh!) 
		 * First part is initialization of the Jaguars for the loop (made obvious later)
		 * Followed by the motor driving until it hits either type of limit, at which point it...
		 * Reverses and slowly (didn't want to work with PID) backs up some random 
		 */
		ncoder.Start(); 
		ncoder.Reset(); 
		driveControl.SetPID(0.2, 0.15, 0);
		driveControl.Enable(); 
		driveControl.SetSetpoint(ncoder.GetDistance()); 
		float distance = 150; 
		
		while (IsEnabled() && IsAutonomous()) {
			
			while(ncoder.GetDistance() <= distance && IsEnabled()) {
				driveControl.SetSetpoint(driveControl.GetSetpoint() + .002); 
				printf("%f\t", ncoder.GetDistance());
				printf("%f\n", driveControl.GetSetpoint());
				
			}
			if(opticalTest1.Get() == 0 || opticalTest2.Get() == 0) {
				break; 
			}
			
			//driveControl.SetSetpoint(distance); 
			//driveControl.Disable(); 
			
			
			
			//printf("%f \n", ncoder.GetDistance());  			
		}
	};

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl()
	{
		
	}
	
	void Test()
	{
		ncoder.Start(); 
		ncoder.Reset(); 
		driveControl.SetPID(0.005, 0., 0);
		driveControl.Enable(); 
		//driveControl.SetSetpoint(ncoder.Get());
		driveControl.SetSetpoint(ncoder.Get() + 400);
		driveControl.SetOutputRange(-1., 1.);
		driveControl.SetPercentTolerance(0.01); //Ask Vadman what is this, not in WPILib
		//For tomorrow: Work on having it autohome using the optical switches
		//After that, have it oscillate between two points about 10 times before asking it to go back to X value, and see how accurate it is. 
		while (IsEnabled())
		{
			printf("Encoder Dist: %f\t", ncoder.GetDistance());
			printf("Controller setpoint: %f\t", driveControl.GetSetpoint());
			printf("Controller E: %f\n", driveControl.GetError());
		}
	}
	
	/**
	 * Runs during test mode
	 */
//	void Test() {}
};

START_ROBOT_CLASS(RobotDemo);

/*
 * Requirements: 
 * 
 */

