package org.usfirst.team1351.robot.statemachineex.states;

import org.usfirst.team1351.robot.statemachineex.InstanceData;
import org.usfirst.team1351.robot.statemachineex.StateEnum;
import org.usfirst.team1351.robot.statemachineex.StateFunction;

public class ErrorState implements StateFunction {

	@Override
	public StateEnum doState(InstanceData data) {
//		TKOLEDArduino::inst()->setMode(6);
//		if (GetTime() - lastSensorStringPrint > 1.)
		{
//			printf("%s\n",state_to_string(data).c_str());
//			TKOLogger::inst()->addMessage("STATE ERROR: %s ERROR!!! SENSORS: %d", state_to_string(data).c_str(), createIntFromBoolArray(data));
		}
//		sensors_to_string(data);
	    return StateEnum.STATE_ERR;
	}

}
