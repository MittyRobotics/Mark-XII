//Last edited by Vadim Korolik
//on 03/01/2014
#include "TKOLEDArduino.h"

TKOLEDArduino* TKOLEDArduino::m_Instance = NULL;

TKOLEDArduino::TKOLEDArduino():
		data1(2, Relay::kBothDirections),
		data2(3, Relay::kBothDirections),
		data3(4, Relay::kBothDirections),
		reset(5, Relay::kBothDirections)
{	
	printf("Initializing drive\n");
	arduinoTask = new Task("TKOLEDArduino", (FUNCPTR) TaskRunner, 50);
	modeChanged = true;
	mode = 1;
	printf("Finished initializing drive\n");
	AddToSingletonList();
}
TKOLEDArduino* TKOLEDArduino::inst()
{
	if (!m_Instance)
	{
		printf("Drive instance is null\n");
		m_Instance = new TKOLEDArduino;
	}
	return m_Instance;
}
void TKOLEDArduino::TaskRunner()
{
	while (true)
	{
		m_Instance->processData();
	}
}

void TKOLEDArduino::setMode(short tmode)
{
	if (tmode == mode)
		modeChanged = false;
	else
		modeChanged = true;
	mode = tmode;
}

void TKOLEDArduino::processData()
{
	reset.Set(reset.kForward);
	data1.Set(data1.kForward);
	data2.Set(data2.kForward);
	data3.Set(data3.kForward);
	//^ default values
	Wait(0.1);
	if (modeChanged)
		sendData(mode);
}

void TKOLEDArduino::sendData(short mode)
{
	bitset<16> bitData = mode;
	
	printf("Seding data\n");
	if (bitData[0])
		data1.Set(data1.kForward);
	else
		data1.Set(data1.kOff);
	if (bitData[1])
		data2.Set(data2.kForward);
	else
		data2.Set(data2.kOff);
	if (bitData[2])
		data3.Set(data3.kForward);
	else
		data3.Set(data3.kOff);
	reset.Set(reset.kOff);//record data on arduino
	Wait(0.1);
	reset.Set(reset.kForward);
	Wait(2.);
	modeChanged = false;
}
void TKOLEDArduino::Start()
{
	if (not arduinoTask->Verify() or arduinoTask->IsSuspended())
		arduinoTask->Start();
}
void TKOLEDArduino::Stop()
{
	if (arduinoTask->Verify())
		arduinoTask->Stop();
}

TKOLEDArduino::~TKOLEDArduino()
{
	arduinoTask->Stop();
	m_Instance = NULL;
}

