package bot4;

import battlecode.common.*;

public class Carrier extends Robot {

	static final int INVENTORY_THRESHOLD = 30;

	void execute(RobotController rc) throws GameActionException {
		switch (myMission.missionName) {
			case COLLECT_MANA:
			case COLLECT_ADAMANTIUM:
				if (turnCount % 100 == 0)
					myMission.target = Communication.readWell(rc, myMission.getCollectResourceType());
				executeCollectMission(rc);
				break;

			case CAPTURE_ISLAND:
			case ATTACK_ISLAND:
				executeCaptureMission(rc);
				break;

			default:
				init(rc);
				break;
		}
	}

	void executeCollectMission(RobotController rc) throws GameActionException {
		int ad = rc.getResourceAmount(ResourceType.ADAMANTIUM);
		int mn = rc.getResourceAmount(ResourceType.MANA);
		if (ad + mn > INVENTORY_THRESHOLD) {
			if (rc.canTransferResource(myHq, ResourceType.ADAMANTIUM, ad))
				rc.transferResource(myHq, ResourceType.ADAMANTIUM, ad);
			else if (rc.canTransferResource(myHq, ResourceType.MANA, mn))
				rc.transferResource(myHq, ResourceType.MANA, mn);
			else
				move(rc, myHq);
		} else {
			if (rc.canCollectResource(myMission.target, -1))
				rc.collectResource(myMission.target, -1);
			else
				move(rc);
		}
	}

	void executeCaptureMission(RobotController rc) throws GameActionException {
		if (rc.getAnchor() == null) {
			if (rc.canTakeAnchor(myHq, Anchor.STANDARD))
				rc.takeAnchor(myHq, Anchor.STANDARD);
			else
				move(rc, myHq);
		} else {
			if (rc.canPlaceAnchor())
				rc.placeAnchor();
			else
				move(rc);
		}
	}
}
