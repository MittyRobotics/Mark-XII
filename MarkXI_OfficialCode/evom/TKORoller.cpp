//Last Edited by Zach Light
#include "TKOArm.h"
/*
 * Initializes the 2 roller jaguars in percent Vbus mode
 *  \parm int port 1 - roller 1 Jaguar ID
 *  \parm int port 2- roller 2 Jaguar ID
 */

TKORoller* TKORoller::_instance = NULL;

TKORoller::TKORoller(int rollerPort1,int rollerPort2):
	_roller1(rollerPort1, CANJaguar::kPercentVbus),
	_roller2(rollerPort2, CANJaguar::kPercentVbus),
	limitSwitchBall(BALL_LIMIT_SWITCH),
	stick3(3)
{
	override = false;
}

TKORoller::~TKORoller() {}

TKORoller* TKORoller::inst() 
{
	if (!_instance) 
	{
		printf("TKORoller instance is null\n");
		_instance = new TKORoller(ROLLER_1_JAGUAR_ID, ROLLER_2_JAGUAR_ID);
		printf("TKORoller initialized\n");
	}
	return _instance;
}

void TKORoller::rollerManualMove()
{
	/* not used
	 * 
	if (override)
		return;
	if(stick3.GetRawButton(2)) 
	{
		_roller1.Set(stick3.GetY());
		_roller2.Set(-stick3.GetY());
	}
	else
	{
		_roller1.Set(0);
		_roller2.Set(0);
	}
	*
	*/	
}
void TKORoller::rollerIn()
{
	if (!limitSwitchBall.Get()) return;
	_roller1.Set(-1.);
	_roller2.Set(1.);
}

void TKORoller::rollerSimpleMove() //TODO check negative signs, .Get sign, so that this works
{
	if (override)
		return;
	if (!limitSwitchBall.Get()) //if ball hitting switch, we dont want operator to make it go more
	{
		if(stick3.GetRawButton(5)) //only allow going out
		{
			_roller1.Set(1.);
			_roller2.Set(-1.);
		}
		/*else if (stick3.GetRawButton(3))
		{
			_roller1.Set(1.);
			_roller2.Set(1.);
		}*/
		else //if stick not pressed
		{
			_roller1.Set(0.);
			_roller2.Set(0.);
		}
	}
	else //if ball NOT hitting switch
	{
		if(stick3.GetRawButton(4)) //allow going in
		{
			_roller1.Set(-1.);
			_roller2.Set(1.);
		}
		else if(stick3.GetRawButton(5)) //allow going out
		{
			_roller1.Set(1.);
			_roller2.Set(-1.);
		}
		else //if stick not pressed
		{
			_roller1.Set(0.);
			_roller2.Set(0.);
		}
	}
}

