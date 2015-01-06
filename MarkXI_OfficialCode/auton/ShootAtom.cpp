//Last edited by Vadim Korolik
#include "ShootAtom.h"

ShootAtom::ShootAtom()
{
	
}

ShootAtom::~ShootAtom()
{
	
}

void ShootAtom::run() // TODO write this
{
	printf("Starting to shoot auton\n");
	TKOArm::inst()->setArmTarget(ARM_MID_POSITION);
	Wait(0.25);
	while (!StateMachine::armCanMove and DriverStation::GetInstance()->IsEnabled())
	{
		
	}
	printf("Done with manual fire\n");
	//if (!TKOArm::inst()->limitSwitchArm.Get())
		StateMachine::manualFire();
	Wait(1.);
}
