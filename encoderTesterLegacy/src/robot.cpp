#include "WPILib.h"

/**
 * This starter template is for building a robot program from the
 * SimpleRobot base class.  This template does nothing - it merely
 * provides method stubs that you can use to begin your implementation.
 */
class SimpleRobotDemo: public SimpleRobot {
	Encoder encode;
	CANJaguar jaguar;
	int encoderPort = 4;
	int jaguarNumber = 7;
public:

	SimpleRobotDemo(void) :

			encode(1, encoderPort, false, Encoder::k4X), jaguar(jaguarNumber,
					CANJaguar::kPosition) {

	}

	void Disabled() {
		printf(
				"Use Autonomous for an encoder in a Jaguar, use TeleOp for a direct-feed encoder. Don't forget to properly set ports :) /n");
		cout
				<< "Use Autonomous for an encoder in a Jaguar, use TeleOp for a direct-feed encoder. Don't forget to properly set ports :)"
				<< "/n";
	}

	/**
	 * Your code for autonomous goes here.  You must be certain to exit the function when
	 * the Autonomous period is over!  Otherwise your program will never transition to
	 * OperatorControl.  You can use a loop similar to below, or otherwise frequently check
	 * IsAutonomous() and return when it returns false.
	 */
	void Autonomous() {
		encode.Reset();
		while (IsAutonomous() && IsEnabled()) {
			printf("Output: %i /n", encode.Get());
			cout << "Output: " << encode.Get() << "/n";
		}
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	void OperatorControl() {
		jaguar.SetPID(0.0, 0.0, 0.0);
		jaguar.EnableControl(0.0);
		while (IsOperatorControl() && IsEnabled()) {
			printf("Output: %f /n", jaguar.GetPosition());
			cout << "Output: " << jaguar.GetPosition() << "/n";
			Wait(0.005);
		}
	}

	/**
	 * Runs during test mode
	 */
	void Test() {

	}
};

/*
 * This macro invocation tells WPILib that the named class is your "main" robot class,
 * providing an entry point to your robot code.
 */
START_ROBOT_CLASS(SimpleRobotDemo);

