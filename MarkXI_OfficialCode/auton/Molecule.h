#ifndef _MOLECULE_H
#define _MOLECULE_H

#include "Atom.h"
#include "../Definitions.h"
#include <queue>

class Molecule {

public:
	void addAtom(Atom*);
	void start();
	void MoleculeInit();
	void Test();
	CANJaguar drive1, drive2, drive3, drive4;
	Molecule();
	~Molecule();
private:
	std::queue<Atom*> _list;

};

#endif
