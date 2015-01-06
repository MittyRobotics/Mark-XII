//Last edited by Vadim Korolik
//on 03/01/14
#ifndef _DRIVEANDSHOOTUSONICATOM_H
#define _DRIVEANDSHOOTUSONICATOM_H

#include "Atom.h"
#include "../Definitions.h"
#include "../evom/StateMachine.h"

class DriveAndShootUsonicAtom: public Atom {

public:
	// For feet, positive is moving forward in relation to the robot
	DriveAndShootUsonicAtom(float tarD, AnalogChannel* usonicPointer, CANJaguar* drive1, CANJaguar* drive2, CANJaguar* drive3, CANJaguar* drive4, DoubleSolenoid* shifterDS);
	~DriveAndShootUsonicAtom();
	void run();
private:
	float tarDist;
	CANJaguar* _drive1;
	CANJaguar* _drive2;
	CANJaguar* _drive3;
	CANJaguar* _drive4;
	AnalogChannel* usonic;
	DoubleSolenoid* _shifterDoubleSolenoid;
};

#endif
