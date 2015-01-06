//Last edited by Vadim Korolik
//on 03/01/14
#include "TKOArm.h"

TKOArm* TKOArm::m_Instance = NULL;

/*
 * Initializes the 2 roller jaguars in percent Vbus mode
 *  \param int port 1 - roller 1 Jaguar ID
 *  \param int port 2- roller 2 Jaguar ID
 *  
 *  When match starts, our arm is looking up, tape in switch
 *  If not in tape, arm cannot work.
 *  We know forward is x units to floor, y units to back floor (y is negative, x positive)
 *  Range is from y to x, 0 is center (start position)
 */

TKOArm::TKOArm() :
	limitSwitchArm(ARM_OPTICAL_SWITCH), // Optical limit switch
	minArmPos(ARM_MINIMUM_POSITION), //TODO Critical, find out what these are...
	maxArmPos(ARM_MAXIMUM_POSITION),
	_arm(ARM_JAGUAR_ID, CANJaguar::kPosition), 
	usonic(ULTRASONIC_PORT),
	stick1(STICK_1_PORT),
	stick2(STICK_2_PORT),
	stick3(STICK_3_PORT),
	stick4(STICK_4_PORT)
{
	printf("Initializing intake\n");
	_arm.SetSafetyEnabled(false);
	_arm.ConfigNeutralMode(CANJaguar::kNeutralMode_Brake);  
	_arm.SetVoltageRampRate(0.0);
	_arm.ConfigFaultTime(0.1); 
	_arm.SetPositionReference(CANJaguar::kPosRef_QuadEncoder);
	_arm.ConfigEncoderCodesPerRev(250);
	//_arm.ConfigSoftPositionLimits(maxArmPos, minArmPos);
	//_arm.SetPID(-10000., -1., 0.);
	_arm.SetPID(-2500., -.5, 0.); //USED TO BE -5000
	_arm.EnableControl(0.);
	lastInc = GetTime();
	lastCalib = GetTime();
	lastLog = GetTime();
	//switchToPositionMode();
	armTask = new Task("TKOArm", (FUNCPTR) ArmRunner, 1);
	armEnabled = true;
	armTargetCurrent = _arm.Get();
	armTargetFinal = armTargetCurrent;
	if (limitSwitchArm.Get())
	{
		printf("ARM NOT IN SWITCH\n");
		//kill the arm? because init position not in center
		//TODO Maybe remove this for testing
		/*armEnabled = false;
		_arm.StopMotor();*/
	}
	AddToSingletonList();
}

TKOArm::~TKOArm() 
{
	m_Instance = NULL;
}

TKOArm* TKOArm::inst() 
{
	if (!m_Instance) 
	{
		printf("TKOArm instance is null\n");
		m_Instance = new TKOArm;
		printf("TKOArm initialized\n");
	}
	return m_Instance;
}
void TKOArm::ArmRunner()
{
	while (true)
	{
		m_Instance->runManualArm();
		m_Instance->printDSMessages();
		m_Instance->currentTimeout();
		m_Instance->armTargetUpdate();
		m_Instance->logArmData();
		Wait(0.005);
	}
}
float TKOArm::getDistance()
{
	return (usonic.GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET);
}
AnalogChannel* TKOArm::getUsonic()
{
	return &usonic;
}
bool TKOArm::Start()
{
	if (not armTask->Verify())
		if (armTask->Start())
			return true;
	return false;
}
bool TKOArm::Stop()
{
	if (armTask->Verify())
		if (armTask->Stop())
			return true;
	return false;
}
float TKOArm::getSmoothValue() {
	float workingAverage = 0.;
	int count = 10;
	double sum = 0;
	for (int i = 0; i < count; i++)
	{
		float smoothingFactor = 0.01;
		float newValue = usonic.GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET;
		workingAverage = (newValue*smoothingFactor) + (workingAverage * ( 1.0 - smoothingFactor));
		sum += workingAverage;
	}
	return sum/count;
}
void TKOArm::logArmData()
{
	if (GetTime() - lastLog < 1.)
		return;
	
	TKOLogger::inst()->addMessage("-----ARM DATA------");

	TKOLogger::inst()->addMessage("Arm voltage output: %f", _arm.GetOutputVoltage());
	TKOLogger::inst()->addMessage("Arm current output: %f", _arm.GetOutputCurrent());
	TKOLogger::inst()->addMessage("Arm target: %f", _arm.Get());
	TKOLogger::inst()->addMessage("Arm position: %f", _arm.GetPosition());
	TKOLogger::inst()->addMessage("Arm jaguar temp: %f", _arm.GetTemperature());
	TKOLogger::inst()->addMessage("Arm battery bus voltage: %f\n", _arm.GetBusVoltage());
	
	lastLog = GetTime();
}
void TKOArm::printDSMessages()
{
	float tempVal = usonic.GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET;
	/*float avr = 0.;
	while (usonicVals.size() < 10)
	{
		usonicVals.push(usonic.GetVoltage() / ULTRASONIC_CONVERSION_TO_FEET);
		usonicAvr += tempVal;
	}
	
	usonicAvr -= usonicVals.back();
	usonicAvr += tempVal;
	usonicVals.push(tempVal);
	usonicVals.pop();
	avr = usonicAvr / usonicVals.size();*/
	DriverStation::GetInstance()->SetDigitalOut(8,limitSwitchArm.Get());
	DSClear();
	DSLog(1, "Arm Pos: %f", _arm.GetPosition());
	DSLog(3, "Arm Curr %f", _arm.GetOutputCurrent());
	DSLog(4, "Arm Tar %f", _arm.Get());
	//DSLog(5, "DistR %f", avr); //gets feet
	DSLog(6, "Dist %f", tempVal); //gets feet
	//printf("Running arm, ready to fire %d\n", armInFiringRange());
	
}
void TKOArm::forwardCalibration()
{
	if (GetTime() - lastCalib < 1.)
		return;
	printf("Running front calib\n");
	TKOLogger::inst()->addMessage("Running front calib");
	armEnabled = false;
	resetEncoder();
	printf("arm tar %f\n", _arm.Get());
	printf("arm pos %f\n", _arm.GetPosition());
	_arm.Set(0.);
	Timer _temp;
	_temp.Start();
	//float val = _arm.Get();
	Wait(.1);
	printf("arm tar %f\n", _arm.Get());
	printf("arm pos %f\n", _arm.GetPosition());
	_arm.Set(0.);
	while (_temp.Get() < 5. and limitSwitchArm.Get() and DriverStation::GetInstance()->IsEnabled())
	{
		//val += 0.00001;
		printf("arm tar %f\n", _arm.Get());
		printf("arm pos %f\n", _arm.GetPosition());
		_arm.Set(_arm.Get() - 0.0002);
		//_arm.Set(val);
	}
	printf("Out of while loop calib\n");
	_arm.Set(_arm.GetPosition());
	_temp.Stop();
	resetEncoder();
	lastCalib = GetTime();
	armEnabled = true;
	printf("Done with front calib!\n");
	TKOLogger::inst()->addMessage("Done with front calib");
}
void TKOArm::reverseCalibration()
{
	if (GetTime() - lastCalib < 1.)
		return;
	printf("Running rev calib\n");
	TKOLogger::inst()->addMessage("Running rev calib");
	armEnabled = false;
	resetEncoder();
	printf("arm tar %f\n", _arm.Get());
	printf("arm pos %f\n", _arm.GetPosition());
	_arm.Set(0.);
	Timer _temp;
	_temp.Start();
	//float val = _arm.Get();
	Wait(.1);
	printf("arm tar %f\n", _arm.Get());
	printf("arm pos %f\n", _arm.GetPosition());
	_arm.Set(0.);
	while (_temp.Get() < 5. and limitSwitchArm.Get() and DriverStation::GetInstance()->IsEnabled())
	{
		//val += 0.00001;
		printf("arm tar %f\n", _arm.Get());
		printf("arm pos %f\n", _arm.GetPosition());
		_arm.Set(_arm.Get() + 0.0002);
		//_arm.Set(val);
	}
	printf("Out of while loop calib\n");
	_arm.Set(_arm.GetPosition());
	_temp.Stop();
	resetEncoder();
	lastCalib = GetTime();
	armEnabled = true;
	printf("Done with rev calib!\n");
	TKOLogger::inst()->addMessage("Done with rev calib");
}
void TKOArm::setArmTarget(float target)
{
	armTargetFinal = target;
}
void TKOArm::armTargetUpdate()
{
	if (not armEnabled)
		return;
	if (armTargetFinal < armTargetCurrent)
	{
		armTargetCurrent -= ARM_TARGET_RAMP_INCREMENT; //TODO Arm increment, going forward
	}
	else if (armTargetFinal > armTargetCurrent)
	{
		armTargetCurrent += ARM_TARGET_RAMP_INCREMENT;
	}
	_arm.Set(armTargetCurrent);
}
void TKOArm::currentTimeout()
{
	if (_arm.GetOutputCurrent() >= ARM_CURRENT_THRESHOLD)
	{
		printf("Arm current timeout %f \n", _arm.GetOutputCurrent());
		TKOLogger::inst()->addMessage("CRITICAL: ARM CURRENT TIMEOUT %f", _arm.GetOutputCurrent());
		TKOLogger::inst()->addMessage("ARM TARGET: %f\t ARM POSITION: %f\t ARM VOLTAGE: %f", _arm.Get(), _arm.GetPosition(), _arm.GetOutputVoltage());
		Timer timeout;
		timeout.Start();
		while (timeout.Get() <= ARM_CURRENT_TIMEOUT)
		{
			_arm.Set(ARM_MID_POSITION);
			_arm.ConfigMaxOutputVoltage(0.);
			_arm.SetVoltageRampRate(0.1);
			//TODO Do something here
		}
		timeout.Stop();
		_arm.ConfigMaxOutputVoltage(ARM_MAX_OUTPUT_VOLTAGE);
		_arm.SetVoltageRampRate(ARM_VOLTAGE_RAMP_RATE);
		//_arm.EnableControl();
	}
}
void TKOArm::resetEncoder()
{
	_arm.DisableControl();
	_arm.EnableControl(0.);
	armTargetCurrent = 0.;
	armTargetFinal = 0.;
	printf("Reset encoder\n");
	TKOLogger::inst()->addMessage("Reset encoder");
}
void TKOArm::runManualArm()
{	
	//printf("running manual arm");
	if (DriverStation::GetInstance()->IsAutonomous())
		return;
	if (stick4.GetRawButton(8))
	{
		resetEncoder();
	}
	if (stick2.GetRawButton(10))
	{
		//reverseCalibration();
		return;
	}
	if (stick2.GetRawButton(11))
	{
		forwardCalibration();
		return;
	}
	
	TKORoller::inst()->rollerSimpleMove();
	if (/*not StateMachine::armCanMove or*/ StateMachine::getCockingSwitch()->Get() or not armEnabled)
	{
		printf("Arm can't move, \n");
		//setArmTarget(_arm.GetPosition());
		_arm.SetVoltageRampRate(0.001);
		return;
	}
	_arm.SetVoltageRampRate(0.0);
	if (stick4.GetRawButton(5))
		moveToFront();
	if (stick4.GetRawButton(2))
		moveToMid();
	if (stick4.GetRawButton(4))
		moveToBack();
	/*if (stick4.GetRawButton(3))
		moveToDSTarget();*/
	/*else
		moveToMid();*/
	if (GetTime() - lastInc <= 1.){}
	else
	{
		if (stick4.GetRawButton(6))
		{
			setArmTarget(_arm.Get() + ARM_MANUAL_DRIVE_INCREMENT);
			lastInc = GetTime();
		}
		if (stick4.GetRawButton(7))
		{
			setArmTarget(_arm.Get() - ARM_MANUAL_DRIVE_INCREMENT);
			lastInc = GetTime();
		}
	}
	
	//_arm.Set(_arm.Get()); //todo DO WE NEED THIS
	
}
void TKOArm::moveToFront()
{
	/*if (_arm.GetControlMode() == _arm.kPercentVbus)
		TKOArm::switchToPositionMode();*/
	setArmTarget(maxArmPos);
}
void TKOArm::moveToMid()
{
	/*if (_arm.GetControlMode() == _arm.kPercentVbus)
		TKOArm::switchToPositionMode();*/
	setArmTarget(ARM_MID_POSITION);
}
void TKOArm::moveToBack()
{
	/*if (_arm.GetControlMode() == _arm.kPercentVbus)
		TKOArm::switchToPositionMode();*/
	setArmTarget(minArmPos);
}
void TKOArm::moveToDSTarget()
{
	/*if (_arm.GetControlMode() == _arm.kPercentVbus)
		TKOArm::switchToPositionMode();*/
	setArmTarget(DriverStation::GetInstance()->GetAnalogIn(4));
}
bool TKOArm::armInFiringRange()
{
	if (limitSwitchArm.Get())
		return false;
	//return true;
	
	if (_arm.GetPosition() >= ARM_FIRING_LEFT_BOUND and _arm.GetPosition() <= ARM_FIRING_RIGHT_BOUND)
		return true;
	return false;
}
void TKOArm::switchToPositionMode()
{
	printf("switching to position\n");
	//_arm.ChangeControlMode(_arm.kPosition);
	_arm.SetSafetyEnabled(false);
	_arm.ConfigNeutralMode(CANJaguar::kNeutralMode_Brake);  
	_arm.SetVoltageRampRate(0.0);
	_arm.ConfigFaultTime(0.1); 
	_arm.SetPositionReference(CANJaguar::kPosRef_QuadEncoder);
	_arm.ConfigEncoderCodesPerRev(250);
	_arm.SetPID(-10000., -1., 0.);
	//_arm.EnableControl(0.);
	//_arm.SetVoltageRampRate(3.); //TODO maybe don't need ramping voltage with pid
}
