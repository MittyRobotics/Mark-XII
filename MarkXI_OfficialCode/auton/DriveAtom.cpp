//Last edited by Ishan Shah
#include "DriveAtom.h"
#include <cstring>

DriveAtom::DriveAtom(float feet, CANJaguar* drive1, CANJaguar* drive2, CANJaguar* drive3, CANJaguar* drive4) {
	_distance = feet; //* REVS_PER_FOOT;
	_drive1 = drive1;
	_drive2 = drive2;
	_drive3 = drive3;
	_drive4 = drive4;
	ds = DriverStation::GetInstance();
}

DriveAtom::~DriveAtom() {
}

void DriveAtom::run() {
	
	//don't forget to divide number of rotations by REVS_PER_FOOT in order to get feet traveled
	_drive1->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	_drive3->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	_drive1->EnableControl(0);
	_drive3->EnableControl(0);
	
	while (not (_drive1->GetPosition() <= _distance + 0.5) and not (_drive1->GetPosition() >= _distance - 0.5)) { //TODO FFS FIX THIS

//		while(kP != ds->GetAnalogIn(1)*0.2) {
			//kP = ds->GetAnalogIn(1)*0.2;
//			_drive1->SetPID(kP, kI, kD);
//			_drive3->SetPID(kP, kI, kD);
//		}
//		while(kI != ds->GetAnalogIn(2)*0.0008) {
//			kI = ds->GetAnalogIn(2)*0.0008;
//			_drive1->SetPID(kP, kI, kD);
//			_drive3->SetPID(kP, kI, kD);
//		}
		
		_drive1->Set(_distance);
		_drive2->Set(_drive1->GetOutputVoltage() / _drive1->GetBusVoltage()); //sets second jag to slave			
		_drive3->Set(-_distance);
		_drive4->Set(_drive3->GetOutputVoltage() / _drive3->GetBusVoltage()); //sets fourth jag to slave
		
		printf("drive 1: %f, drive 3: %f, distance: %f\n", _drive1->GetPosition(), _drive3->GetPosition(), _distance);
		//TODO what is this for???
		//printf("breaking? %d\n", !((_drive1->GetPosition() <= _distance + 0.5) and (_drive1->GetPosition() >= _distance - 0.5)));
		
	}
	printf("drive 1: %f, drive 3: %f, distance: %f\n", _drive1->GetPosition(), _drive3->GetPosition(), _distance);
	//printf("broke %d\n", !((_drive1->GetPosition() <= _distance + 0.5) and (_drive1->GetPosition() >= _distance - 0.5)));
	
	_drive1->DisableControl();
	_drive2->Set(0);
	_drive3->DisableControl();
	_drive4->Set(0);
	Wait(1.0); 
	
}
