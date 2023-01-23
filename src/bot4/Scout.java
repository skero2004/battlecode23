package bot4;

import battlecode.common.*;

import bot4.util.*;

public class Scout {
	static void move(RobotController rc) throws GameActionException {
	//	Direction move = RobotPlayer.directions[(int) Math.sqrt(RobotPlayer.turnCount) % 8];
	//	if (rc.canMove(move))
	//		rc.move(move);
	//	else
	//		RobotPlayer.moveRandom(rc);
	}

	static void getNewLocation(RobotController rc) throws GameActionException {
		MapInfo[] nearby = rc.senseNearbyMapInfos();
	}
}
