/*
 * TKOPointers.h
 *
 *  Created on: Jan 3, 2015
 *      Author: Vadim
 */

#ifndef SRC_TKOPOINTERS_H_
#define SRC_TKOPOINTERS_H_

class TKOPointers
{
	public:
		TKOPointers();
		TKOPointers* getInst();
		static void initPointers();
		virtual ~TKOPointers();

	private:
		static TKOPointers* _instance;
};

#endif /* SRC_TKOPOINTERS_H_ */
