//Last edited by Vadim Korolik
//on 03/01/2014
#ifndef __TKOLEDARDUINO_H
#define __TKOLEDARDUINO_H

#include "../Definitions.h"
#include "../log/TKOLogger.h"
#include <bitset>

class TKOLEDArduino : public SensorBase
{
public:
	static TKOLEDArduino* inst();
	void Start();
	void Stop();
	void setMode(short mode);
private:
	Task *arduinoTask;
	Relay data1, data2, data3, reset;
	
	TKOLEDArduino();
	~TKOLEDArduino();
	static TKOLEDArduino* m_Instance;
	static void TaskRunner();
	short mode;
	bool modeChanged;
	void processData();
	void sendData(short);
};
#endif
