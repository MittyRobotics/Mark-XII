//Last edited by Vadim Korolik
#ifndef _SHOOTATOM_H
#define _SHOOTATOM_H

#include "Atom.h"
#include "../evom/StateMachine.h"
#include "../evom/TKOShooter.h"
#include "../Definitions.h"

class ShootAtom: public Atom
{
	public:
		ShootAtom();
		~ShootAtom();
		void run();
};

#endif
