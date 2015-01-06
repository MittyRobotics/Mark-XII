//Last edited by Vadim Korolik
//on 10/29/2013
#include "TKOGyro.h"

TKOGyro* TKOGyro::m_Instance = NULL;
/*
 * Singleton Gyro Implementation
 * Can utilize Gyro in multiple classes
 * YAY ACTUALLY NO LONGER CAUSES KERNAL EXCEPTIONS
 */

TKOGyro::TKOGyro()/*:
	gyro(GYRO_PORT)*/ 
//CRITICAL GYRO PORT IS EITHER 1 OR 2 ON ANALOG SIDE CAR
//WILL NOT WORK IF IN PORT 3-8
{
	printf("Initializing gyro\n");
	/*
	if (!gyro.CheckAnalogModule(1))
		printf("Analog module 1 failed.\n");
	if (!gyro.CheckAnalogChannel(1))
		printf("Analog channel 1 failed.\n");
	
	gyro.SetSensitivity(0.007);
	
	if (gyro.StatusIsFatal())
		printf("Gyro status FATUL.......\n");*/
	printf("Initialized gyro\n");
	AddToSingletonList();
}

TKOGyro* TKOGyro::inst()
{
	if (m_Instance == NULL)
	{
		printf("Gyro instance null.\n");
		m_Instance = new TKOGyro();
		printf("Gyro instance created\n");
	}
	return m_Instance;
}

void TKOGyro::reset(){
	//gyro.Reset();
}
float TKOGyro::GetAngle()
{
	return 13.51;
	//CHANGE TO RETURN NO MORE THAN abs(360) 
	/*float angle = gyro.GetAngle();
	return angle;*/
}
TKOGyro::~TKOGyro(){
	m_Instance = NULL;
}

