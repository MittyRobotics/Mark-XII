/*
 * TKODrive.cpp
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */

#include <drive/TKODrive.h>

TKODrive* TKODrive::_instance = 0;

TKODrive::TKODrive()
{
	_driveTask = NULL;
	//TODO make sure to use the TKOPointers and to catch the TKOError(s)
}

TKODrive::~TKODrive()
{
	if (_driveTask)
	{
		delete _driveTask;
	}
}

void TKODrive::Start()
{
	if (!_driveTask)
	{
		_driveTask = new Task("Driving", (FUNCPTR) driveRunner); // create a new task called Logging which runs LogRunner
		printf("Created drive task\n");
		//		if (_logTask->SetPriority(254)) // use the constants first/wpilib provides?
		//			printf("logger priority set to 254\n");
		//		else
		printf("drive priority not set\n");
	}
}

void TKODrive::driveRunner()
{

}
