/*
 * TKOPointers.cpp
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */

#include "TKOPointers.h"

TKOPointers* TKOPointers::_instance = 0;

TKOPointers::TKOPointers()
{
	drive1 = 0;
	drive2 = 0;
	drive3 = 0;
	drive4 = 0;
	stick1 = 0;
	stick2 = 0;
	stick3 = 0;
	stick4 = 0;
}

TKOPointers::~TKOPointers()
{

}

TKOPointers* TKOPointers::getInst()
{
	if (!TKOPointers::_instance)
	{
		TKOPointers::_instance = new TKOPointers();
	}
	return (TKOPointers::_instance);
}

Joystick * TKOPointers::joystickPointer(int numberOfStick) //TODO remember to catch this
{
	switch (numberOfStick)
	{
		case 1:
			if (!stick1)
				throw new TKOError("Joystick 1 pointer is null");
			return stick1;
			break;
		case 2:
			if (!stick1)
				throw new TKOError("Joystick 2 pointer is null");
			return stick2;
			break;
		case 3:
			if (!stick1)
				throw new TKOError("Joystick 3 pointer is null");
			return stick3;
			break;
		case 4:
			if (!stick1)
				throw new TKOError("Joystick 4 pointer is null");
			return stick4;
			break;
		default:
			return 0;
	}
}

CANJaguar* TKOPointers::driveJagPointer(int numberOfJag) //TODO remember to catch this
{
	switch (numberOfJag)
	{
		case 1:
			if (!stick1)
				throw new TKOError("Drive 1 pointer is null");
			return drive1;
			break;
		case 2:
			if (!stick1)
				throw new TKOError("Drive 2 pointer is null");
			return drive2;
			break;
		case 3:
			if (!stick1)
				throw new TKOError("Drive 3 pointer is null");
			return drive3;
			break;
		case 4:
			if (!stick1)
				throw new TKOError("Drive 4 pointer is null");
			return drive4;
			break;
		default:
			return 0;
	}
}

void TKOPointers::initPointers()
{
	if (!stick1)
		stick1 = new Joystick(JOYSTICK_1_ID);
	if (!stick2)
		stick2 = new Joystick(JOYSTICK_2_ID);
	if (!stick3)
		stick3 = new Joystick(JOYSTICK_3_ID);
	if (!stick4)
		stick4 = new Joystick(JOYSTICK_4_ID);

	if (!drive1)
		drive1 = new CANJaguar(DRIVE_1_JAG_ID);
	if (!drive2)
		drive2 = new CANJaguar(DRIVE_2_JAG_ID);
	if (!drive3)
		drive3 = new CANJaguar(DRIVE_3_JAG_ID);
	if (!drive4)
		drive4 = new CANJaguar(DRIVE_4_JAG_ID);

	drive1->SetPercentMode();
	drive2->SetPercentMode();
	drive3->SetPercentMode();
	drive4->SetPercentMode();

	drive1->EnableControl();
	drive2->EnableControl();
	drive3->EnableControl();
	drive4->EnableControl();
}

void TKOPointers::destroyPointers()
{
	if (drive1)
		delete drive1;
	if (drive2)
		delete drive2;
	if (drive3)
		delete drive3;
	if (drive4)
		delete drive4;
}
