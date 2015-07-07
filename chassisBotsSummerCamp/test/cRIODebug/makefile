################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include src/subdir.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(CC_DEPS)),)
-include $(CC_DEPS)
endif
ifneq ($(strip $(C++_DEPS)),)
-include $(C++_DEPS)
endif
ifneq ($(strip $(C_UPPER_DEPS)),)
-include $(C_UPPER_DEPS)
endif
ifneq ($(strip $(CXX_DEPS)),)
-include $(CXX_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(CPP_DEPS)),)
-include $(CPP_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: test.so

# Tool invocations
test.so: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: Thunder cRIO Tools Partial Linker'
	powerpc-wrs-vxworks-g++ -nostdlib -L"C:\Program Files (x86)\FRC_Toolchain\mingw\powerpc-wrs-vxworks\wind_base\/../lib" -mcpu=603 -mstrict-align -mlongcall -static -r -Xlinker -X -o "test.so" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '
	$(MAKE) --no-print-directory post-build

# Other Targets
clean:
	-$(RM) $(CC_DEPS)$(C++_DEPS)$(EXECUTABLES)$(OBJS)$(C_UPPER_DEPS)$(CXX_DEPS)$(C_DEPS)$(CPP_DEPS) test.so
	-@echo ' '

post-build:
	-powerpc-wrs-vxworks-nm "C:/repos/mark-xii/chassisBotsSummerCamp/test/cRIODebug/test.so" | tclsh "C:\Program Files (x86)\FRC_Toolchain\mingw\powerpc-wrs-vxworks\wind_base\/host/resource/hutils/tcl/munch.tcl" -c ppc | sed "s/extern void\(.*\);/extern void \1 __attribute__((externally_visible));/" > "C:/repos/mark-xii/chassisBotsSummerCamp/test/cRIODebug/ctdt.c"  && powerpc-wrs-vxworks-gcc -c "C:/repos/mark-xii/chassisBotsSummerCamp/test/cRIODebug/ctdt.c" -o ctdt.o -DCPU=PPC603 -DTOOL_FAMILY=gnu -DTOOL=gnu -D_WRS_KERNEL -fmessage-length=0  -mcpu=603 -mstrict-align -mlongcall &&  powerpc-wrs-vxworks-g++ "C:/repos/mark-xii/chassisBotsSummerCamp/test/cRIODebug/ctdt.o" "C:/repos/mark-xii/chassisBotsSummerCamp/test/cRIODebug/test.so" -o "C:/repos/mark-xii/chassisBotsSummerCamp/test/cRIODebug/test.so.out" -DCPU=PPC603 -DTOOL_FAMILY=gnu -DTOOL=gnu -D_WRS_KERNEL -fmessage-length=0  -mcpu=603 -mstrict-align -mlongcall -r -Wl,-X -static -T "C:\Program Files (x86)\FRC_Toolchain\mingw\powerpc-wrs-vxworks\wind_base\\..\share\ldscripts\dkm.ld" &&  powerpc-wrs-vxworks-stripsyms.bat "C:/repos/mark-xii/chassisBotsSummerCamp/test/cRIODebug/test.so.out" "C:\Program Files (x86)\FRC_Toolchain\mingw\powerpc-wrs-vxworks\wind_base\\..\lib\libstdc++.a" "C:\Program Files (x86)\FRC_Toolchain\mingw\powerpc-wrs-vxworks\wind_base\\..\lib\libsupc++.a" "C:\Program Files (x86)\FRC_Toolchain\mingw\powerpc-wrs-vxworks\wind_base\\..\..\lib\gcc\powerpc-wrs-vxworks\4.8.2\libgcc.a"
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY: post-build

-include ../makefile.targets
