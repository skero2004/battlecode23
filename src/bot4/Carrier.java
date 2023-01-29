package bot4;

import battlecode.common.*;

import bot4.util.*;
import bot4.Plan.Mission;

public class Carrier {

	static boolean init = true;
	static Team myTeam = Team.NEUTRAL;
	static RobotInfo myHq;
	static Mission myMission;

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

	static int INVENTORY_THRESHOLD = 30;

	static void run(RobotController rc) throws GameActionException {

		// Carrier gets mission
		init(rc);
		//rc.setIndicatorString("T: " + myMission.missionName);

		System.out.println("carrier mission: " + myMission.missionName + " " + myMission.target);
		if (myMission.missionName == MissionName.SCOUTING) {

			Scout.move(rc, myHq);
			Scout.updateInfos(rc);

		} else if (myMission.isValidCollectMission()) {
			executeCollectMission(rc, myMission);
		} else if (myMission.missionName == MissionName.CAPTURE_ISLAND) {
			executeCaptureMission(rc, myMission);
		}

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


	static void executeCaptureMission(RobotController rc, Mission mission) throws GameActionException {

		if (rc.getAnchor() == null) {
			
			// If no anchor held, then get anchor
			MapLocation target = myHq.location;
			if (rc.canTakeAnchor(target, Anchor.STANDARD)) {
				rc.takeAnchor(target, Anchor.STANDARD);
			} else {
				Direction dir = Paths.findMove(rc, target);
				if (rc.canMove(dir)) rc.move(dir);
			}
		} else {

			// If carrier has an anchor, then go to target
			MapLocation target = mission.target;
			rc.setIndicatorString("tgt: " + target);
			if (rc.canPlaceAnchor()) {
				rc.placeAnchor();
			} else {
				Direction dir = Paths.findMove(rc, target);
				if (rc.canMove(dir)) rc.move(dir);
			}

		}

	}

}
