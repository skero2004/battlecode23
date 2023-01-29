package bot4;

import battlecode.common.*;

import bot4.util.*;
import bot4.Plan.Mission;

public class Carrier extends Robot {

	static final int INVENTORY_THRESHOLD = 30;

	void execute(RobotController rc) throws GameActionException {
		switch (myMission.missionName) {
			case SCOUTING:
				Scout.move(rc);
				Scout.updateInfos(rc);
				break;

			case COLLECT_MANA:
			case COLLECT_ADAMANTIUM:
				executeCollectMission(rc);
				break;

			case CAPTURE_ISLAND:
				executeCaptureMission(rc);
				break;

			default:
				executeCaptureMission(rc);
				break;
		}
	}

	void executeCollectMission(RobotController rc) throws GameActionException {
		int ad = rc.getResourceAmount(ResourceType.ADAMANTIUM);
		int mn = rc.getResourceAmount(ResourceType.MANA);
		if (ad + mn > INVENTORY_THRESHOLD) {
			MapLocation target = myHq.location;
			if (rc.canTransferResource(target, ResourceType.ADAMANTIUM, ad)) {
				rc.transferResource(target, ResourceType.ADAMANTIUM, ad);
			} else if (rc.canTransferResource(target, ResourceType.MANA, mn)) {
				rc.transferResource(target, ResourceType.MANA, mn);
			} else {
				Direction dir = Paths.findMove(rc, target);
				if (rc.canMove(dir))
					rc.move(dir);
			}
		} else {
			MapLocation target = myMission.target;
			rc.setIndicatorString("tgt: " + target);
			if (rc.canCollectResource(target, -1)) {
				rc.collectResource(target, -1);
			} else {
				Direction dir = Paths.findMove(rc, target);
				if (rc.canMove(dir))
					rc.move(dir);
			}
		}
	}

	void executeCaptureMission(RobotController rc) throws GameActionException {
		if (rc.getAnchor() == null) {
			// If no anchor held, then get anchor
			MapLocation target = myHq.location;
			if (rc.canTakeAnchor(target, Anchor.STANDARD)) {
				rc.takeAnchor(target, Anchor.STANDARD);
			} else {
				Direction dir = Paths.findMove(rc, target);
				if (rc.canMove(dir))
					rc.move(dir);
			}
		} else {
			// If carrier has an anchor, then go to target
			MapLocation target = myMission.target;
			rc.setIndicatorString("tgt: " + target);
			if (rc.canPlaceAnchor()) {
				rc.placeAnchor();
			} else {
				Direction dir = Paths.findMove(rc, target);
				if (rc.canMove(dir))
					rc.move(dir);
			}
		}
	}

}
