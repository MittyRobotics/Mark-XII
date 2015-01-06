#include "TKORelay.h"
///Constructor for the TKORelay class
/*!
	Initial state is off.
	\param int index - the channel number of the _relay constructor (WPILib)
*/
TKORelay::TKORelay(int index):
	_relay(1, index)
{
	SetOn(false);
	_pulsing = 0;
}
///Destructor for the TKORelay class
TKORelay::~TKORelay() {}
///Sets the spike relay
/*!
	\param int d - 1 for forward, 2 for reverse, 0 for off.
*/
void TKORelay::SetOn(int d) {
	if (d == 1)
		_relay.Set(Relay::kForward);
	else if (d == 2)
		_relay.Set(Relay::kReverse);
	else 
		_relay.Set(Relay::kOff);
}

///Pulses the spike relay
/*!
	Every time you call it, it turns on for 10 cycles, and then turns off for 10.
*/
void TKORelay::Pulse() {
	SetOn(_pulsing++ % 20 < 10);
}
