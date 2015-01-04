/*
 * Definitions.h
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */

#ifndef SRC_DEFINITIONS_H_
#define SRC_DEFINITIONS_H_

using namespace std;

#include "WPILib.h"
#include <exception>
#include <cstring>
#include <string>
#include <iostream>
#include "string.h"
#include "Base.h"
#include "SensorBase.h"
#include <fstream>
#include <Task.h>
#include <queue>
#include <sstream>
#include <iostream>
#include <ostream>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include "Vision/RGBImage.h"
#include "Vision/BinaryImage.h"
#include <iostream>
#include <stdarg.h>
#include "math.h"
#include "stdlib.h"
#include <cstring>

const int NUM_JOYSTICKS = 4;
const int NUM_DRIVE_JAGS = 4;

const int JOYSTICK_ID[4] = {1, 2, 3, 4};
const int DRIVE_JAG_ID[4] = {1, 2, 3, 4};

const int DRIVE_1_JAG_ID = 1;
const int DRIVE_2_JAG_ID = 2;
const int DRIVE_3_JAG_ID = 3;
const int DRIVE_4_JAG_ID = 4;

const int JOYSTICK_1_ID = 0;
const int JOYSTICK_2_ID = 1;
const int JOYSTICK_3_ID = 2;
const int JOYSTICK_4_ID = 3;

#endif /* SRC_DEFINITIONS_H_ */
