package bot5a;

import battlecode.common.*;
import bot5a.util.*;
import bot5a.Plan.Mission;

public class Launcher extends Robot {

	int countEnemies(RobotController rc) {
		int c = 0;
		for (RobotInfo r : rc.senseNearbyRobots()) {
			if (r.getTeam() != rc.getTeam())
				++c;
		}
		return c;
	}

	void execute(RobotController rc) throws GameActionException {
		if (myMission.missionName != MissionName.ATTACK_ISLAND || turnCount % 200 == 0) {
			myMission = new Mission(MissionName.ATTACK_ISLAND);
			if (Communication.readIsland(rc, rc.getTeam().opponent()) != null)
				myMission.target = Communication.readIsland(rc, rc.getTeam().opponent());
			else if (Communication.readIsland(rc, Team.NEUTRAL) != null)
				myMission.target = Communication.readIsland(rc, Team.NEUTRAL);
			else
				myMission = new Mission(MissionName.SCOUTING);
		}

		attackEnemies(rc);

		if (!followLeader(rc)) {
			switch (myMission.missionName) {
				case SCOUTING:
					Scout.move(rc);
					Scout.updateInfos(rc);
					break;

				default:
					if (rc.getLocation().distanceSquaredTo(myMission.target) > 9)
						move(rc);
					else
						Scout.move(rc);
			}
		}
	}

	private boolean followLeader(RobotController rc) throws GameActionException {
		RobotInfo leader = null;
		for (RobotInfo r : rc.senseNearbyRobots()) {
			if (r.getTeam() == rc.getTeam() && r.getType() == RobotType.LAUNCHER
					&& (leader == null || leader.getID() > r.getID()))
				leader = r;
		}
		if (leader == null || leader.getID() > rc.getID())
			return false;
		if (rc.getLocation().distanceSquaredTo(leader.getLocation()) > 9)
			stepTowards(rc, leader.getLocation());
		else
			Scout.move(rc);
		return true;
	}

	private void attackEnemies(RobotController rc) throws GameActionException {
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
