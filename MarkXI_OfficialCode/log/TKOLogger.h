//Last edited by Vadim Korolik
//on 07/27/2013
#ifndef __TKOLOGGER_H
#define __TKOLOGGER_H

#include "../Definitions.h"
#include "Base.h"
#include "SensorBase.h"
#include <fstream>
#include <Task.h>
#include <string>
#include <queue>
#include <sstream>
#include <iostream>
#include <ostream>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
using namespace std;
//#define IGNORE_LOGGING_SEMAPHORES "Comment out this line during competition"
/*
 * TO USE!!!
 * Create a regular object for the class in MarkMain.cpp;
	*  TKOLogger* log;
	*  log = TKOLogger::inst();
	*	log->printMessage("Blah blah, test message");
	*	log->addMessage("MOTOR SPEED: %f", 105214124.232);
 *
 * Implements tasks and filestream.
 * Creates an fstream object to write to the log file of choice.
 * The logger implements a queue buffer system in case the
 * writing thread does not keep up with messages, also prevents
 * excessive CPU load by spacing out the buffer writing with the task.
 * Initializes a task to manage the buffer writing to the log file
 * using the static LogRunner, calling the buffer writing.
 * Buffer writing, at any moment in time, takes the first value
 * of the buffer queue, writes it to the end of the log, and
 * deletes it from the buffer.
 */
class TKOLogger: public SensorBase
{
	public:
		/*
		 * Returns a pointer to the singleton TKOLogger instance.
		 */
		static TKOLogger* inst();
		/*
		 * Adds a message to the buffer, which is written to disk.
		 */
		void addMessage(const char *format, ...);
		/*
		 * Prints a message to the screen (not buffered).
		 */
		void printMessage(const char *format, ...);
		/*
		 * Starts the task that pops from the buffer into disk.
		 */
		void Start();
		/*
		 * Stops the task that pops from the buffer into disk.
		 */
		void Stop();
		
		int getBufferLength();

	private:
		DISALLOW_COPY_AND_ASSIGN(TKOLogger);
		SEM_ID _printSem;
		SEM_ID _bufSem;
		TKOLogger();
		~TKOLogger();
		const static int _MAX_BUF_LENGTH = 255;
		Task *_logTask;
		static void LogRunner();
		static TKOLogger* _instance;
		ofstream _logFile;
		queue<string> _messBuffer;
};

#endif
