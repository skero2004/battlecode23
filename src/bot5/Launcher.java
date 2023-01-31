package bot5;

import battlecode.common.*;

import bot5.util.*;
import bot5.Plan.Mission;;

public class Launcher extends Robot {

	void execute(RobotController rc) throws GameActionException {
		Mission listenMission = Communication.readMission(rc);
		if (listenMission != null && listenMission.missionName == MissionName.ATTACK_HQ)
			myMission = listenMission;

		attackEnemies(rc);

		switch (myMission.missionName) {
			case SCOUTING:
				Scout.move(rc);
				Scout.updateInfos(rc);
				break;

			default:
				if (rc.getLocation().distanceSquaredTo(myMission.target) > 18)
					move(rc);
				else if (rc.getLocation().distanceSquaredTo(myMission.target) <= 12) {
					Direction d = rc.getLocation().directionTo(myMission.target).opposite();
					if (rc.canMove(d))
						rc.move(d);
					else
						Scout.move(rc);
				}
		}
	}

	void attackEnemies(RobotController rc) throws GameActionException {
		int ACTION_RADIUS = rc.getType().actionRadiusSquared;
		Team OPPONENT = rc.getTeam().opponent();

		// Find target enemy
		RobotInfo[] enemies = rc.senseNearbyRobots(ACTION_RADIUS, OPPONENT);
		int bestScore = Integer.MIN_VALUE;
		RobotInfo target = null;
		for (RobotInfo enemy : enemies) {
			if (enemy.getType() != RobotType.HEADQUARTERS) {
				int score = 500 - enemy.getHealth();
				switch (enemy.getType()) {
					case BOOSTER:
					case DESTABILIZER:
						score *= 3;
						break;
					case LAUNCHER:
						score *= 3;
						break;
					case CARRIER:
						score *= 2;
						break;
					case AMPLIFIER:
						score *= 1;
						break;
					default:
						score *= 1;
						break;
				}
				if (score > bestScore) {
					bestScore = score;
					target = enemy;
				}
			}
		}

		if (target != null && rc.canAttack(target.getLocation()))
			rc.attack(target.getLocation());

		MapLocation clouds[] = rc.senseNearbyCloudLocations(ACTION_RADIUS);
		if (clouds != null && clouds.length > 0) {
			MapLocation targetLoc = clouds[Randomize.rng.nextInt(clouds.length)];
			if (rc.canAttack(targetLoc))
				rc.attack(targetLoc);
		}
	}
}
