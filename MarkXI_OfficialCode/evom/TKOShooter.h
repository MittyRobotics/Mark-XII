//Last edited by Vadim Korolik
//on 01/12/2014

//USEFULL LINK: http://stackoverflow.com/questions/133214/is-there-a-typical-state-machine-implementation-pattern
#ifndef __TKOSHOOTER_H
#define __TKOSHOOTER_H

#include "../Definitions.h"
#include "../log/TKOLogger.h"
#include "StateMachine.h"
#include "TKOLEDArduino.h"


class TKOShooter
{
	public:
		TKOShooter();
		~TKOShooter();
		static TKOShooter* inst();
		bool Start();
		bool Stop();
		bool stateMachineRunning();

	private:
		Task *shooterTask;
		Task *stateMachineTask;
		Joystick stick1, stick2, stick3, stick4;
		void initStateMachine();
		bool startStateMachine();
		bool stopStateMachine();
		int runStateMachine();
		bool startShooter();
		bool stopShooter();
		bool shooterDoAction(int action);

		static void shooterTaskRunner();
		static void stateMachineTaskRunner();

		static TKOShooter* _instance;
		
		instance_data_t data;
		state_t cur_state;
		StateMachine s;
};

#endif

/*
 * int main( void ) {
    state_t cur_state = STATE_INITIAL; // set our state
    instance_data_t data; // set up some data to use

    while ( 1 ) {
        cur_state = run_state( cur_state, &data ); // start the state machine

        // do other program logic, run other state machines, etc
		}
	}
 */
