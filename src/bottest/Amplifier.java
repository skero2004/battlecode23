package bottest;

import battlecode.common.*;

import bottest.util.*;

public class Amplifier extends Robot {

	void execute(RobotController rc) throws GameActionException {
		Scout.move(rc);
	}
}
