#include "WPILib.h"
#include "Math.h"

//This code still includes some of the safeties we added for the children

/**
 * This starter template is for building a robot program from the
 * SimpleRobot base class.  This template does nothing - it merely
 * provides method stubs that you can use to begin your implementation.
 */
class SimpleRobotDemo: public SimpleRobot {
	CANJaguar driveR, driveL;
	Joystick stick1, stick2, stick3, stick4;
	Relay testRelay;
	float driveDeadzone = 0.1;
	float powerMult = 0.6;

public:
	SimpleRobotDemo(void) :
			driveR(1, CANJaguar::kPercentVbus), driveL(4,
					CANJaguar::kPercentVbus), stick1(1), stick2(2), stick3(3), stick4(
					4), testRelay(1, Relay::kOff) {

	}

	/**
	 * Your code for autonomous goes here.  You must be certain to exit the function when
	 * the Autonomous period is over!  Otherwise your program will never transition to
	 * OperatorControl.  You can use a loop similar to below, or otherwise frequently check
	 * IsAutonomous() and return when it returns false.
	 */
	void Autonomous() {
		printf("Hey look, it worked I guess?");
		while (IsAutonomous() && IsEnabled()) {
			// Update actuators based on sensors, elapsed time, etc here....

			/*
			 * Wait a short time before reiterating.  The wait period can be
			 * changed, but some time in a wait state is necessary to allow
			 * the other tasks, such as the Driver Station communication task,
			 * running on your cRIO to have some processor time. This also
			 * gives time for new sensor inputs, etc. to collect prior to
			 * further updating actuators on the subsequent iteration.
			 */
			driveR.Set(0.5);
			driveL.Set(-0.5);
			Wait(0.005);
		}
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	void OperatorControl() {
		while (IsOperatorControl() && IsEnabled()) {
			float s1Y = fabs(stick1.GetY()) / stick1.GetY();
			float s2Y = fabs(stick2.GetY()) / stick2.GetY();

			powerMult = (stick4.GetZ() + 1) / 2;

			if(stick2.GetTrigger()) {
				testRelay.Set(Relay::kOn);
			} else {
				testRelay.Set(Relay::kOff);
			}

			if (stick3.GetTrigger() || stick4.GetTrigger()) {
				driveR.Set(0);
				driveL.Set(0);
			} else if (stick1.GetTrigger()) {
				if (fabs(stick1.GetY()) >= driveDeadzone) {
					driveR.Set(s1Y * powerMult * sqrt(stick1.GetY()));
					driveL.Set(s1Y * -powerMult * sqrt(stick1.GetY()));
				} else {
					driveR.Set(0);
					driveL.Set(0);
				}

			} else {
				if (fabs(stick1.GetY()) >= driveDeadzone
						|| fabs(stick2.GetY()) >= driveDeadzone) {
					driveR.Set((s1Y * powerMult) * sqrt(fabs(stick1.GetY())));
					driveL.Set((s2Y * -powerMult) * sqrt(fabs(stick2.GetY())));
				} else {
					driveR.Set(0);
					driveL.Set(0);

				}
				Wait(0.005);
				printf("Y-Axis: %f \t getTrigger: ", stick1.GetY());
				printf((stick1.GetTrigger() ? "true \n" : "false \n"));
			}

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

