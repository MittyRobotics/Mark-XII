/*
 * TKODataReporting.cpp
 *
 *  Created on: Jan 4, 2015
 *      Author: Vadim
 */

#include <util/TKODataReporting.h>

TKODataReporting* TKODataReporting::_instance = 0;

TKODataReporting::TKODataReporting()
{
	_reportingTask = 0;
}

TKODataReporting::~TKODataReporting()
{
	if (_reportingTask)
		delete _reportingTask;
}

TKODataReporting* TKODataReporting::inst()
{
	if (!_instance)
		_instance = new TKODataReporting();
	return _instance;
}

void TKODataReporting::Start()
{
	if (!_instance)
		return;
	if (!_reportingTask)
	{
		_reportingTask = new Task("DataReporting", (FUNCPTR) reportRunner); // create a new task called Logging which runs LogRunner
		printf("Created reporting task\n");
		//		if (_logTask->SetPriority(254)) // use the constants first/wpilib provides?
		//			printf("logger priority set to 254\n");
		//		else
		printf("reoprt priority not set\n");
	}
	if (_reportingTask)
	{
		_reportingTask->Start();
	}
}

void TKODataReporting::Stop()
{
	if (!_instance)
		return;
	if (_reportingTask)
	{
		if (_reportingTask->Verify())
			_reportingTask->Stop();
	}
}

void TKODataReporting::reportRunner()
{
	while (true)
	{
		if (!_instance)
		{
			printf("Invalid reporter instance\n");
			break;
		}
		_instance->Report();
		Wait(0.25);
	}
}

void TKODataReporting::Report()
{

}
