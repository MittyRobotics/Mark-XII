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
	for (int i = 1; i <= NUM_JOYSTICKS; i++)
	{
		stick[i] = 0;
	}
	for (int i = 1; i <= NUM_DRIVE_JAGS; i++)
	{
		drive[i] = 0;
	}
}

TKOPointers::~TKOPointers()
{

}

TKOPointers* TKOPointers::inst()
{
	if (!TKOPointers::_instance)
	{
		TKOPointers::_instance = new TKOPointers();
	}
	return (TKOPointers::_instance);
}

Joystick * TKOPointers::joystickPointer(int numberOfStick) //TODO remember to catch this
{
	if (numberOfStick > NUM_JOYSTICKS)
	{
		throw new TKOError("Joystick pointer requested out of bounds", -1);
		return 0;
	}
	if (stick[numberOfStick])
		return stick[numberOfStick];
	else
		throw new TKOError("Joystick pointer is null.", numberOfStick);
}

CANJaguar* TKOPointers::driveJagPointer(int numberOfJag) //TODO remember to catch this
{
	if (numberOfJag > NUM_DRIVE_JAGS)
	{
		throw new TKOError("Drive pointer requested out of bounds", -1);
		return 0;
	}
	if (drive[numberOfJag])
		return drive[numberOfJag];
	else
		throw new TKOError("Drive pointer is null.", numberOfJag);
}

void TKOPointers::initPointers()
{
	for (int i = 1; i <= NUM_JOYSTICKS; i++)
	{
		if (!stick[i])
			stick[i] = new Joystick(JOYSTICK_ID[i]);
	}
	for (int i = 1; i <= NUM_DRIVE_JAGS; i++)
	{
		if (!drive[i])
			drive[i] = new CANJaguar(DRIVE_JAG_ID[i]);
		drive[i]->SetPercentMode();
		drive[i]->EnableControl();
	}
}

void TKOPointers::destroyPointers()
{
	for (int i = 1; i <= NUM_JOYSTICKS; i++)
	{
		if (stick[i])
			delete stick[i];
	}
	for (int i = 1; i <= NUM_DRIVE_JAGS; i++)
	{
		if (drive[i])
			delete drive[i];
	}
}
