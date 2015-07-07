#include "WPILib.h"
#include "Math.h"

class testGamepad: public SimpleRobot {
	CANJaguar driveR, driveL;
	Joystick stick1;
	Relay testRelay;
	float driveDeadzone = 0.1;
	float powerMult = 0.6;

public:
	testGamepad(void) :
			driveR(1, CANJaguar::kPercentVbus), driveL(4,
					CANJaguar::kPercentVbus), stick1(1), testRelay(1,
					Relay::kOff) {

	}

	void Autonomous() {
		printf("Hey look, it worked I guess?");
		while (IsAutonomous() && IsEnabled()) {
			// Update actuators based on sensors, elapsed time, etc here....

			driveR.Set(0.5);
			driveL.Set(-0.5);
			Wait(0.005);
		}
	}

	void OperatorControl() {
		while (IsOperatorControl() && IsEnabled()) {
			float s1Y = fabs(stick1.GetY()) / stick1.GetY();
			float s2Y = fabs(stick1.GetTwist()) / stick1.GetTwist();

			if (stick1.GetRawButton(6)) { //Note: Not currently on robot, untested, use at own risk
				testRelay.Set(Relay::kOn);
			} else {
				testRelay.Set(Relay::kOff);
			}

			if (stick1.GetRawButton(8)) {
				powerMult = (stick1.GetY() + 1) / 2;
			}

			else if (fabs(stick1.GetY()) >= driveDeadzone
					|| fabs(stick1.GetTwist()) >= driveDeadzone) {
				driveR.Set((s1Y * powerMult) * sqrt(fabs(stick1.GetY())));
				driveL.Set((s2Y * -powerMult) * sqrt(fabs(stick1.GetTwist())));
			} else {
				driveR.Set(0);
				driveL.Set(0);

			}
			Wait(0.005);
			printf("Y-Axis: %f \t getTrigger: ", stick1.GetY());
			printf((stick1.GetTrigger() ? "true \n" : "false \n"));
		}

	}

	void Test() {
		//Testing the same controller in arcade drive, really don't like this one. //Nevermind, don't want to do this
	}
};

START_ROBOT_CLASS(testGamepad);

