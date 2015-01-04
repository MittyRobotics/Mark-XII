/*
 * TKOPointers.h
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */
#include "../Definitions.h"

#ifndef SRC_TKOPOINTERS_H_
#define SRC_TKOPOINTERS_H_

class TKOPointers
{
	public:
		TKOPointers();
		static TKOPointers* getInst();
		void initPointers();
		void destroyPointers();
		virtual ~TKOPointers();
		CANJaguar* driveJagPointer(int numberOfJag);
		Joystick* joystickPointer(int numberOfStick);

	private:
		static TKOPointers* _instance;
		CANJaguar *drive1, *drive2, *drive3, *drive4;
		Joystick *stick1, *stick2, *stick3, *stick4;
};

#endif /* SRC_TKOPOINTERS_H_ */
