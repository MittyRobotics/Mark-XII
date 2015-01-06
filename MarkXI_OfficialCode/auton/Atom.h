#ifndef _ATOM_H
#define _ATOM_H

class Atom {
	
protected:
	Atom();
	
public:
	virtual void run() = 0;
	virtual ~Atom(){}
};
#endif
