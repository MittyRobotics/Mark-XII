#ifndef __TKORELAY_H
#define __TKORELAY_H
#include "../Definitions.h"

///Wrapper for WPI Relay Class

class TKORelay {
public:
	TKORelay(int port);
	~TKORelay();
	void SetOn(int d);
	void Pulse();
private:
	Relay _relay;
	int _pulsing;
};

#endif
