package bot5;

import battlecode.common.*;

import bot5.Plan.Mission;

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
		} else {
			Map.WIDTH = rc.getMapWidth();
			Map.HEIGHT = rc.getMapHeight();
		}

		execute(rc);

		++turnCount;
	}

	void move(RobotController rc) throws GameActionException {
		move(rc, myMission.target);
	}

	void move(RobotController rc, MapLocation target) throws GameActionException {
		if (target == null)
			Scout.move(rc);
		Direction dir = Paths.findMove(rc, target);
		if (rc.canMove(dir))
			rc.move(dir);
	}

	abstract void execute(RobotController rc) throws GameActionException;
}
