package bot5a;

import battlecode.common.*;
import bot5a.util.*;
import bot5a.Map.Symmetry;
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

	boolean sameMission(int id1, int id2) {
		if (id1 % 5 != 0)
			return id2 % 5 != 0;
		return id2 % 5 == 0 && id1 % 2 == id2 % 2;
	}

	void execute(RobotController rc) throws GameActionException {
		// if (rc.getID() % 5 != 0) {
		// if (myMission.missionName != MissionName.ATTACK_ISLAND || turnCount % 200 ==
		// 0) {
		// myMission = new Mission(MissionName.ATTACK_ISLAND);
		// if (Communication.readIsland(rc, rc.getTeam().opponent()) != null)
		// myMission.target = Communication.readIsland(rc, rc.getTeam().opponent());
		// else if (Communication.readIsland(rc, Team.NEUTRAL) != null)
		// myMission.target = Communication.readIsland(rc, Team.NEUTRAL);
		// }
		// } else {
		// if (rc.getID() % 2 == 0) {
		// myMission = new Mission(MissionName.COLLECT_MANA);
		// myMission.target = Communication.readWell(rc, ResourceType.MANA);
		// } else {
		// myMission = new Mission(MissionName.COLLECT_ADAMANTIUM);
		// myMission.target = Communication.readWell(rc, ResourceType.ADAMANTIUM);
		// }
		// }
		//
		//

		attackEnemies(rc);

		retarget(rc);

		switch (myMission.missionName) {
			case SCOUTING:
				seekAndDestroy(rc);
				break;

			case ATTACK_HQ:
				if (!seekAndDestroy(rc))
					break;
				if (rc.getLocation().distanceSquaredTo(myMission.target) > 18)
					move(rc);
				else if (rc.getLocation().distanceSquaredTo(myMission.target) <= 12) {
					Direction d = rc.getLocation().directionTo(myMission.target).opposite();
					if (rc.canMove(d))
						rc.move(d);
					else
						Scout.move(rc);
				}

				break;

			case ATTACK_ISLAND:
			case CAPTURE_ISLAND:
			case PROTECT_ISLAND:
			case COLLECT_ADAMANTIUM:
			case COLLECT_MANA:
			case PROTECT_HQ:
				if (!followLeader(rc)) {
					if (rc.getLocation().distanceSquaredTo(myMission.target) > 8)
						move(rc);
					else
						Scout.move(rc);
				}
				break;

			default:
				seekAndDestroy(rc);
		}
	}

	int tryIndex = 2;

	private void retarget(RobotController rc) throws GameActionException {
		Mission mission = Communication.readMission(rc);
		if (mission.missionName == MissionName.PROTECT_HQ) {
			myMission = mission;
			return;
		}

		int f = 0, e = 0;
		for (RobotInfo r : rc.senseNearbyRobots()) {
			if (r.getTeam() == r.getTeam())
				++f;
			else
				++e;
		}

		if (f >= 2 * e)
			myMission = new Mission(MissionName.ATTACK_HQ);
	}

	private boolean seekAndDestroy(RobotController rc) throws GameActionException {
		if (myMission.missionName != MissionName.ATTACK_HQ)
			myMission = new Mission(MissionName.ATTACK_HQ);
		for (RobotInfo r : rc.senseNearbyRobots()) {
			if (r.getType() == RobotType.HEADQUARTERS && r.getTeam() == rc.getTeam().opponent()) {
				myMission.target = r.getLocation();
				return true;
			}
		}

		myMission.target = Map.reflect(myHq, Symmetry.values()[tryIndex % 3]);

		rc.setIndicatorString("i: " + tryIndex + " guess: " + myMission.target);
		move(rc);
		if (rc.getLocation().distanceSquaredTo(myMission.target) <= 8 || turnCount % 200 == 0) {
			tryIndex++;
		}
		return false;
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
		if (rc.getLocation().distanceSquaredTo(leader.getLocation()) > 12)
			stepTowards(rc, leader.getLocation());
		else
			Randomize.move(rc);
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
