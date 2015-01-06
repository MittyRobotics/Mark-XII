//Last edited by Ishan Shah
#include "TurnAtom.h"

TurnAtom::TurnAtom(float ang, CANJaguar* drive1, CANJaguar* drive2, CANJaguar* drive3, CANJaguar* drive4) {
	//	 const float REVS_PER_FOOT = 0.7652439024;
	/* NOTE: Everything in this comment block, unless noted otherwise, is from Mark X! Data must be changed later to Mark XI or later revisions for pratical use. 
	 * Please keep original data for others to use until robot in question is destroyed, never to be rebuilt the same way. 
	 * average inches per rotation is about 12.925 inches on Mark X, measurements taken by Zach
	 * the distance between the wheels is ~26.51 inches
	 * circumferance of circle created with that distance is ~83.28362125 inches
	 * inches/degree = 0.231
	 * revolutions/inch = ~0.07736943907156673114119922630561
	 * revolutions/degrees = ~0.0178989085
	 * 
	 * Okay, we actually need feet/degree, which = 0.01925. Sorry!
	 */

	_angle = ang;
	_drive1 = drive1;
	_drive2 = drive2;
	_drive3 = drive3;
	_drive4 = drive4;
}

TurnAtom::~TurnAtom() {
}

void TurnAtom::resetEncoders() {
	_drive1->EnableControl(0);
	_drive3->EnableControl(0);
}

void TurnAtom::run() {

	//	TKOGyro::inst()->reset();
	// do really cool things with the motors here
	// like turn in circles here
	resetEncoders();
	reachedTarget = false;
	while (!reachedTarget) {
		turn(_angle);
		printf("drive 1: %f, drive 3: %f\n", _drive1->GetPosition(), _drive3->GetPosition());
	}
	printf("Sh!t Occured! Yay1");
}

void TurnAtom::turn(double target) {
	//Counterclockwise is positive, clockwise is negative
	//inches/tic = .333134485
	float _target = target * 0.01925;
	//_target += 10.0; 
	printf("target: %f\n", _target);
	_drive1->Set(_target);
	_drive2->Set(_drive1->GetOutputVoltage() / _drive1->GetBusVoltage()); //sets second jag to slave			
	_drive3->Set(_target);
	_drive4->Set(_drive3->GetOutputVoltage() / _drive3->GetBusVoltage()); //sets fourth jag to slave

	if ((fabs(_drive1->GetPosition()) - 0.05) <= fabs(_target) and (fabs(_drive1->GetPosition()) + 0.05) >= fabs(_target)) {
		if (!reachedTarget) {
			reachedTarget = true;
			resetEncoders();
			//printf("Reset position\n"); //All this resetting does not work properly, needs to be fixed
			//Cannot go without it because when the robot turns, it affects the position
		}
		printf("Reached target, Breaking out\n");
	}

}
