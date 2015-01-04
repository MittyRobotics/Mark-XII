/*
 * TKOError.h
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */

#ifndef SRC_TKOERROR_H_
#define SRC_TKOERROR_H_

#include "Definitions.h"

class TKOError : public exception
{
	public:
		TKOError(string errorMessage);
		virtual ~TKOError();
		string getErrorMessage();
	private:
		string errorMessage;
};

#endif /* SRC_TKOERROR_H_ */
