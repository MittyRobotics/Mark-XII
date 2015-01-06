//Last edited by Vadim Korolik
//on 03/01/2014
#include "TKODrive.h"

TKODrive* TKODrive::m_Instance = NULL;

TKODrive::TKODrive() :
		//jags created later
		stick1(STICK_1_PORT), // initialize joystick 1 < first drive joystick
		stick2(STICK_2_PORT), // initialize joystick 2 < second drive joystick
		stick3(STICK_3_PORT), // initialize joystick 3 < first EVOM joystick
		stick4(STICK_4_PORT), // initialize joystick 4 < first EVOM joystick-m,	
		shifterDS(DRIVE_SHIFTER_SOLENOID_A,DRIVE_SHIFTER_SOLENOID_B),
		speedShiftRPM(165.)
{	
	printf("Initializing drive\n");
	driveTask = new Task("TKODrive", (FUNCPTR) DriveRunner, 1);
	shifterDS.Set(shifterDS.kForward);

	maxDrive1RPM = 0;
	maxDrive3RPM = 0;
	driveLogCounter = 0;
	lastShift = GetTime();
	lastDataLog = GetTime();
	lastFire = GetTime();

	printf("Finished initializing drive\n");
	AddToSingletonList();
}
TKODrive* TKODrive::inst()
{
	if (!m_Instance)
	{
		printf("Drive instance is null\n");
		m_Instance = new TKODrive;
	}
	return m_Instance;
}
void TKODrive::initJaguars()
{
	maxDrive1RPM = 0;
	maxDrive3RPM = 0;
	driveLogCounter = 0;
	lastShift = GetTime();
	lastDataLog = GetTime();
	lastFire = GetTime();
	
	drive1 = new CANJaguar(DRIVE_L1_ID, CANJaguar::kPercentVbus);
	drive2 = new CANJaguar(DRIVE_L2_ID, CANJaguar::kPercentVbus);
	drive3 = new CANJaguar(DRIVE_R1_ID, CANJaguar::kPercentVbus);
	drive4 = new CANJaguar(DRIVE_R2_ID, CANJaguar::kPercentVbus);
	
	drive1->SetSafetyEnabled(false);
	drive2->SetSafetyEnabled(false);
	drive3->SetSafetyEnabled(false);
	drive4->SetSafetyEnabled(false);
	drive1->ConfigNeutralMode(CANJaguar::kNeutralMode_Coast);  
	drive2->ConfigNeutralMode(CANJaguar::kNeutralMode_Coast);   
	drive3->ConfigNeutralMode(CANJaguar::kNeutralMode_Coast);
	drive4->ConfigNeutralMode(CANJaguar::kNeutralMode_Coast);
	drive1->SetVoltageRampRate(24.0);
	drive2->SetVoltageRampRate(24.0);
	drive3->SetVoltageRampRate(24.0);
	drive4->SetVoltageRampRate(24.0);
	drive1->ConfigFaultTime(0.1);
	drive2->ConfigFaultTime(0.1);
	drive3->ConfigFaultTime(0.1);
	drive4->ConfigFaultTime(0.1);

	drive1->SetSpeedReference(CANJaguar::kSpeedRef_Encoder); 
	drive3->SetSpeedReference(CANJaguar::kSpeedRef_Encoder);
	//drive1->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	//drive3->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	drive1->ConfigEncoderCodesPerRev(250);
	drive3->ConfigEncoderCodesPerRev(250);
	drive1->EnableControl();
	drive3->EnableControl();
	
	driving = new RobotDrive(drive1, drive2, drive3, drive4);
	driving->SetSafetyEnabled(false);
	driving->SetExpiration(1.);
	driving->SetInvertedMotor(RobotDrive::kFrontLeftMotor, false);
	driving->SetInvertedMotor(RobotDrive::kRearLeftMotor, false);
	driving->SetInvertedMotor(RobotDrive::kFrontRightMotor, false);
	driving->SetInvertedMotor(RobotDrive::kRearRightMotor, false);
}
void TKODrive::destroyJaguars()
{
	delete drive1;
	delete drive2;
	delete drive3;
	delete drive4;
	delete driving;
}
void TKODrive::DriveRunner()
{
	while (true)
	{
		//m_Instance->TankDrive();
		m_Instance->ArcadeDrive();
		m_Instance->LogData();
		m_Instance->ManualShift();
		m_Instance->AutoShift();
		//m_Instance->VerifyJags();
		Wait(0.005);
	}
}

void TKODrive::Start()
{
	if (not driveTask->Verify() or driveTask->IsSuspended())
	{
		initJaguars();
		driveTask->Start();
	}
}
void TKODrive::Stop()
{
	if (driveTask->Verify())
	{
		driveTask->Stop();
		destroyJaguars();
	}

	TKOLogger::inst()->addMessage("Drive 1 Max Speed: %f", maxDrive1RPM);
	TKOLogger::inst()->addMessage("Drive 3 Max Speed: %f", maxDrive3RPM);
	
	maxDrive1RPM = 0.;
	maxDrive3RPM = 0.;
}
DoubleSolenoid* TKODrive::getShifterDoubleSolenoid()
{
	return &shifterDS;
}
void TKODrive::LogData()
{
	if (drive1->GetSpeed() > maxDrive1RPM)
		maxDrive1RPM = drive1->GetSpeed();
	if (drive3->GetSpeed() > maxDrive3RPM)
		maxDrive3RPM = drive3->GetSpeed();

	if (!DriverStation::GetInstance()->IsEnabled()) return;
	if (GetTime() - lastDataLog <= 1.) return; //1.0 means logs every 1 second

	TKOLogger::inst()->addMessage("-----DRIVE DATA------\n");

	TKOLogger::inst()->addMessage("Drive 1 Vbus Percent Output: %f", drive1->Get());
	TKOLogger::inst()->addMessage("Drive 2 Vbus Percent Output: %f", drive2->Get());
	TKOLogger::inst()->addMessage("Drive 3 Vbus Percent Output: %f", drive3->Get());
	TKOLogger::inst()->addMessage("Drive 4 Vbus Percent Output: %f\n", drive4->Get());

	TKOLogger::inst()->addMessage("Drive 1 Voltage Output: %f", drive1->GetOutputVoltage());
	TKOLogger::inst()->addMessage("Drive 2 Voltage Output: %f", drive2->GetOutputVoltage());
	TKOLogger::inst()->addMessage("Drive 3 Voltage Output: %f", drive3->GetOutputVoltage());
	TKOLogger::inst()->addMessage("Drive 4 Voltage Output: %f\n", drive4->GetOutputVoltage());

	TKOLogger::inst()->addMessage("Drive 1 Current Output: %f", drive1->GetOutputCurrent());
	TKOLogger::inst()->addMessage("Drive 2 Current Output: %f", drive2->GetOutputCurrent());
	TKOLogger::inst()->addMessage("Drive 3 Current Output: %f", drive3->GetOutputCurrent());
	TKOLogger::inst()->addMessage("Drive 4 Current Output: %f\n", drive4->GetOutputCurrent());
	
	TKOLogger::inst()->addMessage("Automatic teleop fire joystick value: %d\n", stick3.GetTrigger());

	TKOLogger::inst()->addMessage("Drive 1 Speed: %f", drive1->GetSpeed());
	TKOLogger::inst()->addMessage("Drive 3 Speed: %f\n", drive3->GetSpeed());
	
	printf("-----DRIVE DATA------\n");

	printf("Drive 1 Vbus Percent Output: %f\n", drive1->Get());
	printf("Drive 2 Vbus Percent Output: %f\n", drive2->Get());
	printf("Drive 3 Vbus Percent Output: %f\n", drive3->Get());
	printf("Drive 4 Vbus Percent Output: %f\n", drive4->Get());

	printf("Drive 1 Voltage Output: %f\n", drive1->GetOutputVoltage());
	printf("Drive 2 Voltage Output: %f\n", drive2->GetOutputVoltage());
	printf("Drive 3 Voltage Output: %f\n", drive3->GetOutputVoltage());
	printf("Drive 4 Voltage Output: %f\n", drive4->GetOutputVoltage());

	printf("Drive 1 Current Output: %f\n", drive1->GetOutputCurrent());
	printf("Drive 2 Current Output: %f\n", drive2->GetOutputCurrent());
	printf("Drive 3 Current Output: %f\n", drive3->GetOutputCurrent());
	printf("Drive 4 Current Output: %f\n", drive4->GetOutputCurrent());
	
	printf("Automatic teleop fire joystick value: %d\n", stick3.GetTrigger());

	printf("Drive 1 Speed: %f\n", drive1->GetSpeed());
	printf("Drive 3 Speed: %f\n", drive3->GetSpeed());

	driveLogCounter++;
	lastDataLog = GetTime();
}

void TKODrive::autoDriveShoot()
{
	/*
	 * code for shooting while driving:
	 * robot must be driving full speed and operator must be holding safety for shooter to fire once it reaches target distance
	 */
	//printf("Speed1: %f \tSpeed2: %f \tDist: %f\n", drive1->GetSpeed(), drive3->GetSpeed(), TKOArm::inst()->getDistance());
	if (drive1->GetSpeed() > 300 && drive3->GetSpeed() > 300 && stick3.GetTrigger() && TKOArm::inst()->getDistance() <= 6.) /////ORIGINALLY stick3.GetRawButton(8)
	{
		if (GetTime() - lastFire <= 1.) return;
		printf("Going to autofire\n");
		TKOLogger::inst()->addMessage("Automatic teleop firing! D1 Speed: %f \t D3 Speed: %f \t Dist: %f", drive1->GetSpeed(), drive3->GetSpeed(), TKOArm::inst()->getDistance());
		printf("Automatic teleop firing! D1 Speed: %f \t D3 Speed: %f \t Dist: %f\n", drive1->GetSpeed(), drive3->GetSpeed(), TKOArm::inst()->getDistance());
		//if (not DriverStation::GetInstance()->GetDigitalIn(8))
			StateMachine::manualFire();
		//else
		//	printf("NOT DOING TELEOP AUTOFIRE\n");
		drive1->Set(0);
		drive2->Set(0);
		drive3->Set(0);
		drive4->Set(0);
		lastFire = GetTime();
		Wait(1.);
	}
}

void TKODrive::ArcadeDrive()
{
	if (!DriverStation::GetInstance()->IsEnabled()) return;
	
	if (stick1.GetRawButton(4))
		driving->ArcadeDrive(stick1.GetY(), stick2.GetX() * 0.6);
	else
		driving->ArcadeDrive(stick1.GetY(), stick2.GetX());
}

void TKODrive::TankDrive()
{
	if (!DriverStation::GetInstance()->IsEnabled()) return;
	
	/*if (stick1.GetRawButton(10))
	{
		drive1->Set(DriverStation::GetInstance()->GetAnalogIn(4));
		drive2->Set(drive1->GetOutputVoltage() / drive1->GetBusVoltage());
		drive3->Set(-(DriverStation::GetInstance()->GetAnalogIn(4)));
		drive4->Set(drive3->GetOutputVoltage() / drive3->GetBusVoltage());
	}*/
	if (stick2.GetRawButton(4))
	{
		drive1->SetVoltageRampRate(0.0);
		drive2->SetVoltageRampRate(0.0);
		drive3->SetVoltageRampRate(0.0);
		drive4->SetVoltageRampRate(0.0);
	}
	else if (stick2.GetRawButton(5))
	{
		drive1->SetVoltageRampRate(24.0);
		drive2->SetVoltageRampRate(24.0);
		drive3->SetVoltageRampRate(24.0);
		drive4->SetVoltageRampRate(24.0);
	}
	else if (stick1.GetTrigger())
	{
		drive1->Set(stick1.GetY() * 0.4);
		drive2->Set(stick1.GetY() * 0.4);
		drive3->Set(-stick2.GetY() * 0.4);
		drive4->Set(-stick2.GetY() * 0.4);
	}
	else if (stick1.GetRawButton(2))
	{
		drive1->Set(stick1.GetY() * 0.2);
		drive2->Set(stick1.GetY() * 0.2);
		drive3->Set(-stick2.GetY() * 0.2);
		drive4->Set(-stick2.GetY() * 0.2);
	}
	else if (stick1.GetRawButton(4))
	{
		drive1->Set(stick1.GetY());
		drive2->Set(stick1.GetY());
		drive3->Set(-stick2.GetY());
		drive4->Set(-stick2.GetY());
	}
	else
	{
		drive1->Set(stick1.GetY() /* * 0.8*/);
		drive2->Set(stick1.GetY() /* * 0.8*/);
		drive3->Set(-stick2.GetY() /* * 0.8*/);
		drive4->Set(-stick2.GetY() /* * 0.8*/);
	}
	autoDriveShoot();
}
void TKODrive::ManualShift()
{
	if (GetTime() - lastShift < 1.) //1. is the constant for min delay between shifts
		return; 

	if (stick2.GetRawButton(2)) 
	{
		shifterDS.Set(shifterDS.kForward);
		lastShift = GetTime();
		printf("Manually shifted backwards (high gear)\n");
		TKOLogger::inst()->addMessage("Manually shifted backwards (high gear)\n");
	}
	if (stick2.GetRawButton(3)) 
	{
		shifterDS.Set(shifterDS.kReverse);
		lastShift = GetTime();
		printf("Manually shifted forward (low gear)\n");
		TKOLogger::inst()->addMessage("Manually shifted backwards (low gear)\n");
	}
}
void TKODrive::AutoShift()
{
	if (GetTime() - lastShift < 1.) //1. is the constant for min delay between shifts
		return; 
	if (drive1->GetSpeed() > speedShiftRPM and drive3->GetSpeed() > speedShiftRPM and shifterDS.Get() != shifterDS.kForward)
	{
		shifterDS.Set(shifterDS.kForward);
		lastShift = GetTime();
		printf("Auto shifted backwards (high gear)\n");
		TKOLogger::inst()->addMessage("Auto shifted backwards (high gear)\n");
	}
	else if (drive1->GetSpeed() < speedShiftRPM and drive3->GetSpeed() < speedShiftRPM and shifterDS.Get() != shifterDS.kReverse)
	{
		shifterDS.Set(shifterDS.kReverse);
		lastShift = GetTime();
		printf("Auto shifted forward (low gear)\n");
		TKOLogger::inst()->addMessage("Auto shifted backwards (low gear)\n");
	}
}


bool TKODrive::VerifyJags() //if returns false, jag problems
{
	if (drive1->IsAlive() and not drive1->StatusIsFatal())
	{
		if (drive2->IsAlive() and not drive2->StatusIsFatal())
		{
			if (drive3->IsAlive() and not drive3->StatusIsFatal())
			{
				if (drive4->IsAlive() and not drive4->StatusIsFatal())
				{
					return true;
				}
			}
		}
	}
	TKOLogger::inst()->addMessage("DRIVE JAGUAR FAILURE");
	printf("DRIVE JAGUARS DID NOT VERIFY\n");
	return false;
}
void TKODrive::switchToSpeed()
{
	drive1->ChangeControlMode(CANJaguar::kSpeed);
	drive3->ChangeControlMode(CANJaguar::kSpeed);
	drive1->SetSpeedReference(CANJaguar::kSpeedRef_Encoder);
	drive3->SetSpeedReference(CANJaguar::kSpeedRef_Encoder);
	drive1->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	drive3->SetPID(DRIVE_kP, DRIVE_kI, DRIVE_kD);
	drive1->ConfigNeutralMode(CANJaguar::kNeutralMode_Brake);  
	drive2->ConfigNeutralMode(CANJaguar::kNeutralMode_Brake);   
	drive3->ConfigNeutralMode(CANJaguar::kNeutralMode_Brake);
	drive4->ConfigNeutralMode(CANJaguar::kNeutralMode_Brake);
	drive1->EnableControl();
	drive3->EnableControl();
	//DONT FORGET TO USE SPEED MODE CORRECTLY
	//when driving use drive1->Set(SOME RPM); then drive2->Set(drive1->GetOutputVoltage());
}

TKODrive::~TKODrive()
{
	driveTask->Stop();
	m_Instance = NULL;
}

