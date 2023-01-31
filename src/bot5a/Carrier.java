package bot5a;

import battlecode.common.*;
import bot5a.Plan.Mission;
import bot5a.util.MissionName;

public class Carrier extends Robot {

	static final int INVENTORY_THRESHOLD = 30;

	void execute(RobotController rc) throws GameActionException {

		// TODO: IDK WTH IS GOING ON BUT CARRIERS ARE ASSIGNED ATTACK ISLAND BOOOOO
		if (myMission.missionName == MissionName.ATTACK_ISLAND || myMission.missionName == MissionName.PROTECT_HQ) {

			if (Headquarters.missionCount % 2 == 0) {
				myMission = new Mission(MissionName.COLLECT_ADAMANTIUM);
				myMission.target = Communication.readWell(rc, ResourceType.ADAMANTIUM);
			} else {
				myMission = new Mission(MissionName.COLLECT_MANA);
				myMission.target = Communication.readWell(rc, ResourceType.MANA);
			}

		}

		switch (myMission.missionName) {
			case SCOUTING:
				executeScout(rc);
				break;
			case COLLECT_MANA:
			case COLLECT_ADAMANTIUM:
				if (turnCount % 100 == 0)
					myMission.target = Communication.readWell(rc,
							myMission.getCollectResourceType());
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

	void executeScout(RobotController rc) throws GameActionException {

		Scout.move(rc);
		WellInfo[] wells = rc.senseNearbyWells();
		if (wells.length > 0) {
			if (wells[0].getResourceType() == ResourceType.ADAMANTIUM && rc.getID() % 2 == 0)
				myMission = new Mission(MissionName.COLLECT_ADAMANTIUM);
			else if (wells[0].getResourceType() == ResourceType.MANA && rc.getID() % 2 == 1)
				myMission = new Mission(MissionName.COLLECT_MANA);
			else
				return;

			myMission.target = wells[0].getMapLocation();
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
			if (rc.canPlaceAnchor() && rc
					.senseTeamOccupyingIsland(rc.senseIsland(rc.getLocation())) == Team.NEUTRAL)
				rc.placeAnchor();
			else if (rc.canPlaceAnchor())
				myMission.target = Communication.readIsland(rc, Team.NEUTRAL);
			move(rc);
		}
	}
}
