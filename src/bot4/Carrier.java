package bot4;

import battlecode.common.*;

import bot4.util.*;
import bot4.Plan.Mission;

public class Carrier {

	static boolean init = true;
	static Team myTeam = Team.NEUTRAL;
	static RobotInfo myHq;
	static Mission myMission;

	static int INVENTORY_THRESHOLD = 30;

	static void init(RobotController rc) throws GameActionException {
		if (!init) return;
		myTeam = rc.getTeam();
		for (RobotInfo robot : rc.senseNearbyRobots()) {
			if (robot.team == myTeam && robot.type == RobotType.HEADQUARTERS) {
				myHq = robot;
			}
		}

		myMission = Communication.readMission(rc);

		init = false;
	}

	static void run(RobotController rc) throws GameActionException {
		init(rc);
		//rc.setIndicatorString("T: " + myMission.missionName);

		System.out.println("my mission: " + myMission.missionName + " " + myMission.target);
		if (myMission.missionName == MissionName.SCOUTING) {
			Scout.move(rc);
			Scout.updateInfos(rc);
		} else if (myMission.isValidCollectMission()) {
			// execute collect mission
			executeCollectMission(rc, myMission);
		}

        //int amount = rc.getResourceAmount(type);
        //if (amount > 0) {
        //    if (rc.canTransferResource(hqLoc, type, amount)) rc.transferResource(hqLoc, type, amount);
        //}
	}

	static void executeCollectMission(RobotController rc, Mission mission) throws GameActionException {
		int ad = rc.getResourceAmount(ResourceType.ADAMANTIUM);
		int mn = rc.getResourceAmount(ResourceType.MANA);
		if (ad + mn > INVENTORY_THRESHOLD) {
			MapLocation target = myHq.location;
			//ResourceType type = mission.getCollectResourceType();
			if (rc.canTransferResource(target, ResourceType.ADAMANTIUM, ad)) {
				rc.transferResource(target, ResourceType.ADAMANTIUM, ad);
			} else if (rc.canTransferResource(target, ResourceType.MANA, mn)) {
				rc.transferResource(target, ResourceType.MANA, mn);
			} else {
				Direction dir = Paths.findMove(rc, target);
				if (rc.canMove(dir)) rc.move(dir);
			}
		} else {
			MapLocation target = mission.target;
			rc.setIndicatorString("tgt: " + target);
			if (rc.canCollectResource(target, -1)) {
				rc.collectResource(target, -1);
			} else {
				Direction dir = Paths.findMove(rc, target);
				if (rc.canMove(dir)) rc.move(dir);
			}
		}
	}

}
