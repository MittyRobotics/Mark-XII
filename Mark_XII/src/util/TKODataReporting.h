/*
 * TKODataReporting.h
 *
 *  Created on: Jan 4, 2015
 *      Author: Vadim
 */

#include <Definitions.h>

#ifndef SRC_UTIL_TKODATAREPORTING_H_
#define SRC_UTIL_TKODATAREPORTING_H_

class TKODataReporting
{
	public:
		TKODataReporting();
		virtual ~TKODataReporting();
		static TKODataReporting* inst();
		void Start();
		void Stop();

	private:
		void Report();
		static TKODataReporting* _instance;
		Task *_reportingTask;
		static void reportRunner();
};

#endif /* SRC_UTIL_TKODATAREPORTING_H_ */
