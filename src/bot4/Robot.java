package bot4;

import battlecode.common.*;

import bot4.Plan.Mission;

public abstract class Robot {

	static int turnCount = 0;

	RobotInfo myHq;
	Mission myMission;

	void init(RobotController rc) throws GameActionException {
		for (RobotInfo robot : rc.senseNearbyRobots()) {
			if (robot.team == rc.getTeam() && robot.type == RobotType.HEADQUARTERS) {
				myHq = robot;
			}
		}

		myMission = Communication.readMission(rc);
	}

	void run(RobotController rc) throws GameActionException {
		if (rc.getType() != RobotType.HEADQUARTERS) {
			if (myHq == null)
				init(rc);
			rc.setIndicatorString("M: " + myMission.missionName + ", T: " + myMission.target);
			Scout.updateInfos(rc);
		}

		execute(rc);

		++turnCount;
	}

	abstract void execute(RobotController rc) throws GameActionException;
}
