//Last edited by Ishan Shah
#ifndef _TURN_ATOM_H
#define _TURN_ATOM_H

#include "Atom.h"
#include "../Definitions.h"

class TurnAtom : public Atom {

public:
	// For angle, positive is (left/right) in relation to the front of the robot
	TurnAtom(float ang, CANJaguar* drive1, CANJaguar* drive2, CANJaguar* drive3, CANJaguar* drive4);
	~TurnAtom();
	void run();
private:
	CANJaguar* _drive1;
	CANJaguar* _drive2;
	CANJaguar* _drive3;
	CANJaguar* _drive4;
	float _angle;
	float _encoderValueLeft;
	float _encoderValueRight;
private:
	void turn(double target);
	bool reachedTarget;
	void resetEncoders();

};

#endif
