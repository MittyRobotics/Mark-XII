/*
 * TKOLogger.h
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */
#include <Definitions.h>

#ifndef SRC_UTIL_TKOLOGGER_H_
#define SRC_UTIL_TKOLOGGER_H_

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
		TKOLogger();
		~TKOLogger();
		const static int _MAX_BUF_LENGTH = 255;
		Task *_logTask;
		static void LogRunner();
		static TKOLogger* _instance;
		ofstream _logFile;
		queue<string> _messBuffer;
};


#endif /* SRC_UTIL_TKOLOGGER_H_ */
