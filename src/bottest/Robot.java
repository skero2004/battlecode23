package bottest;

import battlecode.common.*;

import bottest.Plan.Mission;

public abstract class Robot {

	static int turnCount = 0;

	MapLocation myHq;
	Mission myMission;

	void init(RobotController rc) throws GameActionException {
		for (RobotInfo robot : rc.senseNearbyRobots()) {
			if (robot.team == rc.getTeam() && robot.type == RobotType.HEADQUARTERS) {
				myHq = robot.getLocation();
			}
		}

		myMission = Communication.readMission(rc);
	}

	void run(RobotController rc) throws GameActionException {
		if (rc.getType() != RobotType.HEADQUARTERS) {
			if (myHq == null || myMission == null)
				init(rc);
			rc.setIndicatorString("M: " + myMission.missionName + ", T: " + myMission.target);
			Scout.updateInfos(rc);
		}

		execute(rc);

		++turnCount;
	}

	void move(RobotController rc) throws GameActionException {
		move(rc, myMission.target);
	}

	void move(RobotController rc, MapLocation target) throws GameActionException {
		Direction dir = Paths.findMove(rc, target);
		if (rc.canMove(dir))
			rc.move(dir);
	}

	abstract void execute(RobotController rc) throws GameActionException;
}
