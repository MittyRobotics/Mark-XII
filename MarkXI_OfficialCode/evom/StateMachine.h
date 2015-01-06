//Last edited by Vadim Korolik
//on 02/06/2014
//@author Matthew Pleva

//USEFUL LINK: http://stackoverflow.com/questions/133214/is-there-a-typical-state-machine-implementation-pattern

#ifndef ____STATEMACHINE__H
#define ____STATEMACHINE__H

#include "../Definitions.h"
#include "../log/TKOLogger.h"
#include "TKOArm.h"
#include "TKOLEDArduino.h"

/*
 * 0b | IC | LL | PE | PR |
 * 0b |  8 |  4 |  2 |  1 |
 * 
 * 		Latch lock
 * 			Forward:2
 * 			Reverse:5
 * 		Catapult piston
 * 			Forward:3
 * 			Reverse:6
 * 		Drive shifter
 * 			Forward:7
 * 			Reverse:4
 *		Pressure Switch: 3
 *		Optical Limit Switch: 4
 *		Ball physical limit switch: 5
 *		Main magnetic sensor retract 6
 *		Main magnetic sensor extend 7
 *		Latch magnetic sensor 8
 */

#define DONE_FIRING 2	// PE
#define PISTON_RETRACTED 1	// PR
#define LATCH_LOCKED_PISTON_RETRACTED 5	// LL and PR
#define CONST_READY_TO_FIRE 14	// IC and LL and PE

#define PISTON_RETRACT_TIMEOUT 15.
#define LATCH_LOCK_FORWARD_TIMEOUT 10.
#define PISTON_EXTEND_TIMEOUT 15.
#define LATCH_UNLOCK_REVERSE_TIMEOUT 10.
#define POST_SHOOT_WAIT_TIME 1.
#define SHOOT_ROLLER_PRERUN_TIME .15

// Define the states
typedef enum {
    STATE_PISTON_RETRACT,
    STATE_LATCH_LOCK,
    STATE_PISTON_EXTEND,
    STATE_READY_TO_FIRE,
    STATE_LATCH_UNLOCK,
    NUM_STATES,
    STATE_ERR
} state_t;

// create the instance data type
typedef struct instance_data
{
    state_t curState;
    bool state[NUM_STATES-1];
}instance_data_t;

// function pointer stuff
typedef state_t state_func_t( instance_data_t *data );

class StateMachine {
public:
    StateMachine();
    ~StateMachine();

    static bool armCanMove;
    static bool hasSetPneumatics;
    static bool forceFire;
    static bool autonFired;
    static void initPneumatics();
    static void manualFire();
    static bool canArmMove();
    static void setArmMoveable(bool b);
    
    static float lastSensorStringPrint;
    
    state_t run_state(state_t, instance_data_t*);
    state_t init(instance_data_t *data, Joystick *stick);
    static string state_to_string(instance_data_t *data);
    static void sensors_to_string(instance_data_t *data);
    static void updateDriverStationSwitchDisplay();
    static DigitalInput* getCockingSwitch();
private:
    static state_t do_state_piston_retract(instance_data_t *data);
    static state_t do_state_piston_extend(instance_data_t *data);
    static state_t do_state_latch_lock(instance_data_t *data);
    static state_t do_state_latch_unlock(instance_data_t *data);
    static state_t do_state_ready_to_fire(instance_data_t *data);
    static state_t do_err_state(instance_data_t *data);

    static int getSensorData(instance_data_t *data);
    static int createIntFromBoolArray(instance_data_t *data);

    state_func_t*  _state_table[NUM_STATES + 1];
    
    static SEM_ID _armSem;

    static Timer* _timer;

    static Joystick* _triggerJoystick;

    static DoubleSolenoid* _piston_retract_extend;
    static DoubleSolenoid* _latch_lock_unlock;

    static DigitalInput* _piston_retract;
    static DigitalInput* _piston_extend;
    static DigitalInput* _latch_lock;
    static DigitalInput* _is_cocked;
};


#endif /* defined(____StateMachine__) */
