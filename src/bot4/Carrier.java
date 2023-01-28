package bot4;

import battlecode.common.*;

import bot4.util.*;

public class Carrier {

	static void run(RobotController rc) throws GameActionException {
		//if (true) { // TODO should get mission to check if
		//}

		// TODO Hardcoded locations for testing purposes
        MapLocation cur = rc.getLocation();
		int amt = rc.getWeight(); // should probably check for each resource separately
		if (rc.getID() % 2 == 0) {

			if (amt > 10) {
				MapLocation target;
				if (rc.getID() % 3 == 0) target = new MapLocation(4, 26);
				else if (rc.getID() % 3 == 1) target = new MapLocation(13, 23);
				else target = new MapLocation(25, 18);
				if (rc.canTransferResource(target, ResourceType.ADAMANTIUM, -1)) {
					rc.transferResource(target, ResourceType.ADAMANTIUM, amt);
				} else {
					Direction dir = Paths.findMove(rc, target);
					if (rc.canMove(dir)) rc.move(dir);
				}
			} else {
				MapLocation target = new MapLocation(22, 27);
				if (rc.canCollectResource(target, -1)) {
					rc.collectResource(target, -1);
				} else {
					Direction dir = Paths.findMove(rc, target);
					if (rc.canMove(dir)) rc.move(dir);
				}
			}

		} else {

			if (amt > 10) {
				MapLocation target;
				if (rc.getID() % 3 == 0) target = new MapLocation(4, 26);
				else if (rc.getID() % 3 == 1) target = new MapLocation(13, 23);
				else target = new MapLocation(25, 18);
				if (rc.canTransferResource(target, ResourceType.MANA, -1)) {
					rc.transferResource(target, ResourceType.MANA, amt);
				} else {
					Direction dir = Paths.findMove(rc, target);
					if (rc.canMove(dir)) rc.move(dir);
				}
			} else {
				MapLocation target = new MapLocation(20, 23);
				if (rc.canCollectResource(target, -1)) {
					rc.collectResource(target, -1);
				} else {
					Direction dir = Paths.findMove(rc, target);
					if (rc.canMove(dir)) rc.move(dir);
				}
			}

		}

        //int amount = rc.getResourceAmount(type);
        //if (amount > 0) {
        //    if (rc.canTransferResource(hqLoc, type, amount)) rc.transferResource(hqLoc, type, amount);
        //}
	}

}
