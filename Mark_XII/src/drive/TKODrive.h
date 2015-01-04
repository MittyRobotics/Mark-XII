/*
 * TKODrive.h
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */

#include <Definitions.h>
#include <util/TKOError.h>
#include <util/TKOPointers.h>
#include <util/TKOLogger.h>

#ifndef SRC_DRIVE_TKODRIVE_H_
#define SRC_DRIVE_TKODRIVE_H_

class TKODrive
{
	public:
		TKODrive();
		virtual ~TKODrive();
		static TKODrive* inst();
		void Start();
		void Stop();

	private:
		void Drive();
		void tankDrive();
		static TKODrive* _instance;
		Task *_driveTask;
		static void driveRunner();

};

#endif /* SRC_DRIVE_TKODRIVE_H_ */
