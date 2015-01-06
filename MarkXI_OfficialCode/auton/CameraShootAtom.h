//Last edited by Vadim Korolik
//on 03/01/14
#ifndef _CAMERASHOOTATOM_H
#define _CAMERASHOOTATOM_H

#include "Atom.h"
#include "../Definitions.h"
#include "../evom/StateMachine.h"
#include "../vision/TKOVision.h"

class CameraShootAtom: public Atom {

public:
	// For feet, positive is moving forward in relation to the robot
	CameraShootAtom(AnalogChannel* usonicPointer);
	~CameraShootAtom();
	void run();
private:
	AnalogChannel* usonic;
};

#endif
