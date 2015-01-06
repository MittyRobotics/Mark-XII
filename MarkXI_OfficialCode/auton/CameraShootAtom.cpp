//Last edited by Vadim Korolik
//on 03/01/14
#include "CameraShootAtom.h"
#include <cstring>

CameraShootAtom::CameraShootAtom(AnalogChannel* usonicPointer)
{
	usonic = usonicPointer;
}

CameraShootAtom::~CameraShootAtom() {}

void CameraShootAtom::run() 
{
	TKOVision::inst()->StartProcessing();
	float _camTS = 0., _camDist = 0.;
	bool _camHot = false;
	Timer timeout;
	timeout.Start();
	while (DriverStation::GetInstance()->IsEnabled()) 
	{
		if (timeout.Get() >= 5.)	//if image not processed after 5 seconds, move on to driving/shooting anyway
			break;
		_camTS = TKOVision::inst()->getLastTimestamp();
		_camDist = TKOVision::inst()->getLastDistance();
		_camHot = TKOVision::inst()->getLastTargetReport().Hot;
		DSLog(1, "TS: %f", _camTS);
		DSLog(2, "Dist: %f", _camDist);
		DSLog(3, "HT: %d", _camHot);
		
		//make sure TS and dist are valid values before checking if target is hot
		if (_camTS > 0.)
		{
			if (_camDist > 0.)
			{
				if (_camHot)
				{
					break;
				}
			}
		}
	}
	timeout.Stop();

	TKOVision::inst()->StopProcessing();
}
