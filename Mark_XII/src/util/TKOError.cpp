/*
 * TKOError.cpp
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */

#include "TKOError.h"

TKOError::TKOError(string errorMessage)
{
	this->errorMessage = errorMessage;
}

TKOError::~TKOError()
{

}

string TKOError::getErrorMessage()
{
	return this->errorMessage;
}
