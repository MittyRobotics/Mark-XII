//Last edited by Vadim Korolik
//on 03/01/2014
#ifndef __DEFINITIONS_H
#define __DEFINITIONS_H

/*! \file Definitions.h
 \brief Defines constants and macros used by the other files in the project.
 */
#include "WPILib.h"
#include "Vision/RGBImage.h"
#include "Vision/BinaryImage.h"
#include "math.h"
#include "stdlib.h"
#include <cstring>

/*!
 \def DSClear()
 Clears the Driver Station LCD of all text.
 */
/*!
 \def DSLog(line, msg, ...)
 Formatted printing of msg to the specified line on the Driver Station LCD.
 */
#define DSClear()				DriverStationLCD::GetInstance()->Clear(); \
								DriverStationLCD::GetInstance()->UpdateLCD();
#define DSLog(line, msg, ...)	DriverStationLCD::GetInstance()->Printf(DriverStationLCD::GetInstance()->kUser_Line##line, 1, msg, ##__VA_ARGS__); \
								DriverStationLCD::GetInstance()->UpdateLCD();

#define space printf("\n");
#define GO_AWAY NULL;

/*! \mainpage Welcome to TKO 1351's MarkXI's Code Documentation!
 *
 * 	This is the Robot Code for the 2014 Season of Aerial Assist
 *
 *  If you haven't already, please visit our <a href="http://www.amhsrobotics.com">website</a>.
 */

//Joystick ports
const int STICK_1_PORT = 1;
const int STICK_2_PORT = 2;
const int STICK_3_PORT = 3;
const int STICK_4_PORT = 4;

//Jaguar ports
const int DRIVE_L1_ID = 1;
const int DRIVE_L2_ID = 4;
const int DRIVE_R1_ID = 2;
const int DRIVE_R2_ID = 3;
const int ROLLER_1_JAGUAR_ID = 5;
const int ROLLER_2_JAGUAR_ID = 6;
const int ARM_JAGUAR_ID = 7;

//Arm constants
const float ARM_MINIMUM_POSITION = 0.12;
const float ARM_MAXIMUM_POSITION = -0.18;
const float ARM_SPEED_MULTIPLIER = -.9;
const float ARM_FIRING_LEFT_BOUND = -0.03;//-0.015; // -0.015991   
const float ARM_FIRING_RIGHT_BOUND = 0.;// -0.025;
const float ARM_CURRENT_THRESHOLD = 50.;
const float ARM_CURRENT_TIMEOUT = 5.;
const float ARM_TARGET_RAMP_INCREMENT = 0.002;
const float ARM_MANUAL_DRIVE_INCREMENT = 0.0025;
const float ARM_MID_POSITION = -0.012;//0.0025;//-0.014
// Spinner constants
const float RADIUS_WHEELS = 4;
const float TAN_SPINNER_THETA = tan(57.5);
const float SPINNER_DIV = 4250.0;
const float HALF_G = .5 * 9.8 * 100. / 2.54;
const float MID_FENDER = 0.425;
const float TOP_FENDER = 0.585;

//PID constants
const float SPINNER_kP = 0.2; //working P: 0.20, working I: .0034
const float SPINNER_kI = 0.004;
const float SPINNER_kD = 0.0;
const float DRIVE_kP = -65.0; //working P: 0.20, working I: .0034
const float DRIVE_kI = 0.046;
const float DRIVE_kD = 0.0;
const float WINCH_kP = .0075;
const float WINCH_kI = 0.000001;
const float WINCH_kD = 0.0;
const float REVS_PER_METER = 2.51;
const int ENCODER_REVS = 250;
const bool JAG_SAFETY = true;
const CANJaguar::SpeedReference JAG_SPEEDREF = CANJaguar::kSpeedRef_QuadEncoder;
const CANJaguar::PositionReference JAG_POSREF = CANJaguar::kPosRef_QuadEncoder;

//Drive constants
const float kMAX_DRIVE_RPM = 700;
const float deadzone = 0.1; 
const int kBURNOUT = 500;
const int kBURNOUT_CYCLES = 100;
const float DRIVE_POSITION_INCREMENT = 100.;

//Miscelaneous
const int GYRO_PORT = 1;
const float LOOPTIME = .01;
const double ULTRASONIC_CONVERSION_TO_FEET = (0.009765625 * 12.);

// Pneumatics constants
//PN stands for pneumatics, R stands for relay, S stands for solenoid
const int COMPRESSOR_ID = 1; //Compressor ID
const int PRESSURE_SWITCH_PORT = 3;

const int PISTON_RETRACT_SOLENOID_A = 6;
const int PISTON_RETRACT_SOLENOID_B = 3;
const int LATCH_RETRACT_SOLENOID_A = 2;
const int LATCH_RETRACT_SOLENOID_B = 5;
const int DRIVE_SHIFTER_SOLENOID_A = 7;
const int DRIVE_SHIFTER_SOLENOID_B = 4;

const int PISTON_SWITCH_RETRACT_CHANNEL = 7;
const int PISTON_SWITCH_EXTEND_CHANNEL = 6;
const int LATCH_PISTON_LOCK_SWITCH_CHANNEL = 1;
const int IS_COCKED_SWITCH_CHANNEL = 2;
const int BALL_LIMIT_SWITCH = 10;
const int ARM_OPTICAL_SWITCH = 11;
const int ULTRASONIC_PORT = 7;

// Camera Constants
 
	//Camera constants used for distance calculation
	#define Y_IMAGE_RES 480		//X Image resolution in pixels, should be 120, 240 or 480
	//#define VIEW_ANGLE 49		//Axis M1013
	//#define VIEW_ANGLE 41.7		//Axis 206 camera
	//#define VIEW_ANGLE 37.4  //Axis M1011 camera
	#define VIEW_ANGLE 35.15	// if thia angle doesn't work, try 55. or 39.60 or 33.24
	#define PI 3.141592653
	
	//Score limits used for target identification
	#define RECTANGULARITY_LIMIT 40
	#define ASPECT_RATIO_LIMIT 55
	
	//Score limits used for hot target determination
	#define TAPE_WIDTH_LIMIT 50
	#define VERTICAL_SCORE_LIMIT 50
	#define LR_SCORE_LIMIT 50
	
	//Minimum area of particles to be considered
	#define AREA_MINIMUM 150
	
	//Maximum number of particles to process
	#define MAX_PARTICLES 10

	//Structure to represent the scores for the various tests used for target identification
	struct Scores 
	{
		double rectangularity;
		double aspectRatioVertical;
		double aspectRatioHorizontal;
	};
	
	struct TargetReport 
	{
		int verticalIndex;
		int horizontalIndex;
		bool Hot;
		double totalScore;
		double leftScore;
		double rightScore;
		double tapeWidthScore;
		double verticalScore;
	};

//Autonomous Constants
const float DRIVE_CURRENT_CUTOFF = 20.0;
const float RAMP_RATE = 0.005;
const float RAMP_RATE_2 = 0.0005;

#endif
