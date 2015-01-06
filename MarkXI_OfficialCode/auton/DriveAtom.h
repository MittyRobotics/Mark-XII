//Last edited by Ishan Shah
#ifndef _DRIVEATOM_H
#define _DRIVEATOM_H

#include "Atom.h"
#include "../Definitions.h"

class DriveAtom: public Atom {

public:
	// For feet, positive is moving forward in relation to the robot
	DriveAtom(float feet, CANJaguar* drive1, CANJaguar* drive2, CANJaguar* drive3, CANJaguar* drive4);
	~DriveAtom();
	DriverStation* ds;
	void run();
private:
	CANJaguar* _drive1;
	CANJaguar* _drive2;
	CANJaguar* _drive3;
	CANJaguar* _drive4;
	float _distance;
};

#endif
