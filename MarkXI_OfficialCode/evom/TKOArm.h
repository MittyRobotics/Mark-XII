#ifndef __TKOROLLER_H
#define __TKOROLLER_H

#include "../Definitions.h"

//Code for intake roller

class TKORoller 
{
	public: 
		~TKORoller();
		void rollerManualMove();
		void rollerSimpleMove();
		void rollerIn();
		static TKORoller* inst();
		CANJaguar _roller1, _roller2;
		bool override;
	private:
		TKORoller(int rollerPort1, int rollerPort2);
		DigitalInput limitSwitchBall;
		Joystick stick3;
		static TKORoller*  _instance;
};

#endif
//End of TKORoller .h^^^
#ifndef __TKOARM_H
#define __TKOARM_H

#include "StateMachine.h"
#include "../Definitions.h"

#define ARM_MAX_OUTPUT_VOLTAGE 12.
#define ARM_VOLTAGE_RAMP_RATE 24.

//Code for intake roller and arm movement

class TKOArm: public SensorBase
{
	public:
		~TKOArm();
		static TKOArm* inst();
		bool Start();
		bool Stop();
		void switchToPositionMode();
		float getSmoothValue();
		void printDSMessages();
		void switchToVBusMode();
		float getDistance();
		void moveToFront();
		void moveToMid();
		void moveToBack();
		void moveToDSTarget();
		bool armInFiringRange();
		void runManualArm();
		void currentTimeout();
		void armTargetUpdate();
		void setArmTarget(float target);
		void forwardCalibration();
		void reverseCalibration();
		void resetEncoder();
		void logArmData();
		AnalogChannel* getUsonic();
		DigitalInput limitSwitchArm;
	private:
		TKOArm();
		DISALLOW_COPY_AND_ASSIGN(TKOArm);
		static TKOArm* m_Instance;
		static void ArmRunner();
		const float minArmPos, maxArmPos;
		Task *armTask;
		float lastInc, lastCalib, lastLog;
		bool armEnabled;
		queue<float> usonicVals;
		float armTargetCurrent;
		float armTargetFinal;
		float usonicAvr;
	
		CANJaguar _arm;
		AnalogChannel usonic;
		Joystick stick1, stick2, stick3, stick4;
};
#endif
