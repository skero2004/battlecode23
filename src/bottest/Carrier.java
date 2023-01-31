package bottest;

import battlecode.common.*;

public class Carrier extends Robot {

	static final int INVENTORY_THRESHOLD = 30;

	void execute(RobotController rc) throws GameActionException {

		attackIfEnemiesAround(rc);	

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
			case ATTACK_ISLAND:
				executeCaptureMission(rc);
				break;

			default:
				init(rc);
				break;
		}

	}

	void attackIfEnemiesAround(RobotController rc) throws GameActionException {

		// Find number of enemy launchers near HQ
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		int numEnemyLaunchers = 0;
		
		RobotInfo target = null;
		for (RobotInfo enemy : enemies) {
			if (enemy.getType() == RobotType.LAUNCHER) {
				target = enemy;
				numEnemyLaunchers++;
			}
		}

		if (numEnemyLaunchers > 2 && rc.canAttack(target.getLocation()))
			rc.attack(target.getLocation());

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