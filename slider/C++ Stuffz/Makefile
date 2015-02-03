#UCPP makefile

# Default target
all: force
	cd PPC603gnu && exec "$(MAKE)" -f Makefile

# Don't do anything for the "Makefile" target
Makefile: ;

# Recursively run all other targets
%: force
	cd PPC603gnu && exec "$(MAKE)" -f Makefile "$*"

force: ;

