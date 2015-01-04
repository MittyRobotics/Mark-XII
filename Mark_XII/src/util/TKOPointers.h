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
		static TKOPointers* inst();
		void initPointers();
		void destroyPointers();
		virtual ~TKOPointers();
		CANJaguar* driveJagPointer(int numberOfJag);
		Joystick* joystickPointer(int numberOfStick);

	private:
		static TKOPointers* _instance;
		CANJaguar *drive[];
		Joystick *stick[];
};

#endif /* SRC_TKOPOINTERS_H_ */
