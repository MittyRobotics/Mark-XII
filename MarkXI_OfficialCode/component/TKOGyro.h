//Last edited by Vadim Korolik
//on 10/15/2013
#ifndef __TKOGYRO_H
#define __TKOGYRO_H

#include "../Definitions.h"

class TKOGyro : public SensorBase
{
public:
	void reset();
	float GetAngle();
	static TKOGyro* inst();
private:
	DISALLOW_COPY_AND_ASSIGN(TKOGyro);
	static TKOGyro* m_Instance;
	//Gyro gyro;
	TKOGyro();
	~TKOGyro();
};
#endif
