//Last edited by Vadim Korolik
//on 03/01/14
#include "DriveAtomUsonic.h"
#include <cstring>
#include "../drive/TKODrive.h"

DriveAtomUsonic::DriveAtomUsonic(float tarD, AnalogChannel* usonicPointer, CANJaguar* drive1, CANJaguar* drive2, CANJaguar* drive3, CANJaguar* drive4)
{
	tarDist = tarD;
	usonic = usonicPointer;
	_drive1 = drive1;
	_drive2 = drive2;
	_drive3 = drive3;
	_drive4 = drive4;
}

DriveAtomUsonic::~DriveAtomUsonic() 
{
	/*delete usonic;
	delete _drive1;
	delete _drive2;
	delete _drive3;
	delete _drive4;*/
}

void DriveAtomUsonic::run() {
	TKOLogger::inst()->addMessage("Starting driving usonic atom");
	//don't forget to divide number of rotations by REVS_PER_FOOT in order to get feet traveled
	float tarDistE = 18. - tarDist; //We need to drive until we pass x feet, so that we are tarDist away from wall (starting position 18 feet)
	//currently tarDistE is 11.5 ft
	float AUTON_DRIVE_MAXOUTPUT_VOLTAGE = 13.;
	_drive1->SetPID(-100., 0., DRIVE_kD);
	_drive3->SetPID(-100., 0., DRIVE_kD);
	_drive1->ConfigMaxOutputVoltage(AUTON_DRIVE_MAXOUTPUT_VOLTAGE);
	_drive2->ConfigMaxOutputVoltage(AUTON_DRIVE_MAXOUTPUT_VOLTAGE);
	_drive3->ConfigMaxOutputVoltage(AUTON_DRIVE_MAXOUTPUT_VOLTAGE);
	_drive4->ConfigMaxOutputVoltage(AUTON_DRIVE_MAXOUTPUT_VOLTAGE);
	_drive1->SetPositionReference(CANJaguar::kPosRef_QuadEncoder); 
	_drive3->SetPositionReference(CANJaguar::kPosRef_QuadEncoder);
	_drive1->ConfigEncoderCodesPerRev(250);
	_drive3->ConfigEncoderCodesPerRev(250);
	_drive1->EnableControl(0.);
	_drive3->EnableControl(0.);
	TKODrive::inst()->getShifterDoubleSolenoid()->Set(TKODrive::inst()->getShifterDoubleSolenoid()->kForward);	//shift to high gear
	TKOLogger::inst()->addMessage("Shifter to high gear, target for encoder is %f", tarDistE);
	TKOLogger::inst()->addMessage("Drive 1 Position: %f\n", _drive1->GetPosition());
	TKOLogger::inst()->addMessage("Drive 3 Position: %f\n", _drive3->GetPosition());
	TKOLogger::inst()->addMessage("Entering first while loop");
	Timer temp;
	temp.Start();
	while ((/*usonic->GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET > tarDist and */(_drive3->GetPosition() > -tarDistE + 0.1 and _drive1->GetPosition() < tarDistE - 0.1)) and DriverStation::GetInstance()->IsEnabled()) 
	{
		printf("In the outer while loop of drive atom!\n");
		//while current real distance is over target distance and ultrasonic confirms
		_drive1->Set(_drive1->Get() + 1.625);		
		_drive3->Set(_drive3->Get() - 1.625);
		_drive2->Set(_drive1->GetOutputVoltage() / _drive1->GetBusVoltage()); //sets second jag to slave	
		_drive4->Set(_drive3->GetOutputVoltage() / _drive3->GetBusVoltage()); //sets fourth jag to slave

		if (temp.Get() > 6.)///////CRITICAL FIGURE THIS OUT
		{
			TKOLogger::inst()->addMessage("TIMEOUT BREAKING FROM SMALL LOOP");
			break;
		}
		while (DriverStation::GetInstance()->IsEnabled())
		{
			printf("Autonomous driving inner while loop\n");
			printf("Drive 1 Position: %f\n", _drive1->GetPosition());
			printf("Drive 3 Position: %f\n", _drive3->GetPosition());
			printf("Drive 1 Target: %f\n", _drive1->Get());
			printf("Drive 3 Target: %f\n", _drive3->Get());
			
			TKOLogger::inst()->addMessage("Drive 1 Position: %f\n", _drive1->GetPosition());
			TKOLogger::inst()->addMessage("Drive 3 Position: %f\n", _drive3->GetPosition());
			TKOLogger::inst()->addMessage("Drive 1 Target: %f\n", _drive1->Get());
			TKOLogger::inst()->addMessage("Drive 3 Target: %f\n", _drive3->Get());
			TKOLogger::inst()->addMessage("Autonomous driving inner while loop\n");
			
			/*if (usonic->GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET < 3.2)
				break;*/
			if (_drive1->GetPosition() > (_drive1->Get() - 0.3) and _drive3->GetPosition() < (_drive3->Get() + 0.3))
			{
				TKOLogger::inst()->addMessage("BREAKING FROM SMALL LOOP");
				break;
			}
			if (temp.Get() > 6.)
			{
				TKOLogger::inst()->addMessage("TIMEOUT BREAKING FROM SMALL LOOP");
				break;
			}
			//while we havent reached our new small samped target, dont do anything. Otherwise ramp again
			Wait(0.005);
		}
		Wait(0.005);
		
	}
	temp.Stop();
	temp.Reset();
	_drive1->DisableControl();
	_drive2->Set(0);
	_drive3->DisableControl();
	_drive4->Set(0);
	Wait(1.0); 
	
}

/*void DriveAtomUsonic::run() {
	
	//don't forget to divide number of rotations by REVS_PER_FOOT in order to get feet traveled
	_drive1->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	_drive3->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	_drive1->EnableControl(0);
	_drive3->EnableControl(0);
	while (usonic->GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET > tarDist && DriverStation::GetInstance()->IsEnabled()) {
		//while current distance is greater than target distance

		_drive1->Set(_drive1->Get() - DRIVE_POSITION_INCREMENT);
		_drive2->Set(_drive1->GetOutputVoltage() / _drive1->GetBusVoltage()); //sets second jag to slave			
		_drive3->Set(_drive3->Get() + DRIVE_POSITION_INCREMENT);
		_drive4->Set(_drive3->GetOutputVoltage() / _drive3->GetBusVoltage()); //sets fourth jag to slave
		
	}
	_drive1->DisableControl();
	_drive2->Set(0);
	_drive3->DisableControl();
	_drive4->Set(0);
	Wait(1.0); 
	
}*/
