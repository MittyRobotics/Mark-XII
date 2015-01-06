#include "Definitions.h"
#include <util/TKOPointers.h>
#include <drive/TKODrive.h>

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
class MarkXII: public SampleRobot
{

	public:
		MarkXII()
		{

		}

		/**
		 * Drive left & right motors for 2 seconds then stop
		 */
		void Autonomous()
		{
			CANJaguar *test = NULL;
			test->Disable();
		}

		/**
		 * Runs the motors with arcade steering.
		 */
		void OperatorControl()
		{
			TKOPointers::inst()->initPointers();
			TKODrive::inst()->Start();
			while (IsOperatorControl() && IsEnabled())
			{
				//myRobot.ArcadeDrive(stick); // drive with arcade style (use right stick)
				Wait(0.005);				// wait for a motor update time
			}
			TKODrive::inst()->Stop();
			TKOPointers::inst()->destroyPointers();
		}

		/**
		 * Runs during test mode
		 */
		void Test()
		{
		}
};

START_ROBOT_CLASS(MarkXII);
