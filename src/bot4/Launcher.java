package bot4;

import battlecode.common.*;
import bot4.util.Randomize;

public class Launcher extends Robot {

	void execute(RobotController rc) throws GameActionException {
		attackEnemies(rc);

		switch (myMission.missionName) {
			default:
				Scout.move(rc);
				Scout.updateInfos(rc);
				break;

			/*
			 * case SCOUTING:
			 * if (rc.getLocation().distanceSquaredTo(myMission.target) > 1)
			 * move(rc);
			 * else
			 * Scout.move(rc);
			 * 
			 * break;
			 */
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
