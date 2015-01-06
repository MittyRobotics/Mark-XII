//Last edited by Vadim Korolik
//on 03/01/14
#include "DriveAndShootUsonicAtom.h"
#include <cstring>

DriveAndShootUsonicAtom::DriveAndShootUsonicAtom(float tarD, AnalogChannel* usonicPointer, CANJaguar* drive1, CANJaguar* drive2, CANJaguar* drive3, CANJaguar* drive4, DoubleSolenoid* shifterDS)
{
	tarDist = tarD;
	usonic = usonicPointer;
	_drive1 = drive1;
	_drive2 = drive2;
	_drive3 = drive3;
	_drive4 = drive4;
	_shifterDoubleSolenoid = shifterDS;
}

DriveAndShootUsonicAtom::~DriveAndShootUsonicAtom() {}

void DriveAndShootUsonicAtom::run() 
{
	TKOLogger::inst()->addMessage("Starting run for drive and shoot atom, auton shot distance: %f", usonic->GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET);
	Timer test;
	//don't forget to divide number of rotations by REVS_PER_FOOT in order to get feet traveled
	_drive1->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	_drive3->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	_drive1->EnableControl(0);
	_drive3->EnableControl(0);
	test.Start();
	_shifterDoubleSolenoid->Set(_shifterDoubleSolenoid->kForward);	//shift to high gear
	while (/*test.Get() < 1.5*/ usonic->GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET > tarDist and DriverStation::GetInstance()->IsEnabled()) 
	{
		_drive1->Set(400.);
		_drive2->Set(_drive1->GetOutputVoltage() / _drive1->GetBusVoltage()); //sets second jag to slave			
		_drive3->Set(-400.);
		_drive4->Set(_drive3->GetOutputVoltage() / _drive3->GetBusVoltage()); //sets fourth jag to slave
		//printf("Drive 1: %f\t Drive 3: %f\n", _drive1->GetSpeed(), _drive3->GetSpeed());
		printf("Timer: %f\n", test.Get());
		printf("Drive1 speed %f\n", _drive1->GetSpeed());
		printf("Drive3 speed %f\n", _drive3->GetSpeed());
	}
	/*while (_drive1->GetPosition() < 8.9 and _drive3->GetPosition() > -8.9) //thresholds
	{
		while (_drive1->Get() < -9.1 and _drive3->Get() > 9.1) //target setpoint ramping
		{
			_drive1->Set(_drive1->Get() - 0.01);
			_drive2->Set(_drive1->GetOutputVoltage() / _drive1->GetBusVoltage()); //sets second jag to slave		
			_drive3->Set(_drive3->Get() + 0.01);
			_drive4->Set(_drive3->GetOutputVoltage() / _drive3->GetBusVoltage()); //sets fourth jag to slave
		}
		_drive1->Set(-9.1);
		_drive2->Set(_drive1->GetOutputVoltage() / _drive1->GetBusVoltage()); //sets second jag to slave		
		_drive3->Set(9.1);
		_drive4->Set(_drive3->GetOutputVoltage() / _drive3->GetBusVoltage()); //sets fourth jag to slave
	}*/
	TKOLogger::inst()->addMessage("Drive 1: %f\t Drive 3: %f\n", _drive1->GetPosition(), _drive3->GetPosition());
	//TKOLogger::inst()->addMessage("Drive 2: %f\t Drive 4: %f\n", _drive2->GetPosition(), _drive4->GetPosition());
	TKOLogger::inst()->addMessage("Drive 1: %f\t Drive 3: %f\n", _drive1->GetSpeed(), _drive3->GetSpeed());
	TKOLogger::inst()->addMessage("Auton shot distance %f", usonic->GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET);
	test.Stop();
	printf("Reached target, firing\n");
	StateMachine::manualFire();
	printf("Done firing\n");
	Wait(0.5);
	_drive1->Set(0);
	_drive3->Set(0);
	_drive1->DisableControl();
	_drive2->Set(0);
	_drive3->DisableControl();
	_drive4->Set(0);
	Wait(1.0); 
}
