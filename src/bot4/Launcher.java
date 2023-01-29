package bot4;

import battlecode.common.*;

import bot4.util.*;
import bot4.Plan.Mission;

public class Launcher {

	static boolean init = true;
	static Team myTeam = Team.NEUTRAL;
	static RobotInfo myHq;
	static Mission myMission;

	static void init(RobotController rc) throws GameActionException {
		if (!init) return;
	//	myTeam = rc.getTeam();
	//	for (RobotInfo robot : rc.senseNearbyRobots()) {
	//		if (robot.team == myTeam && robot.type == RobotType.HEADQUARTERS)
	//			myHq = robot;
	//	}

		myMission = Communication.readMission(rc);
		init = false;

	}

    static void run(RobotController rc) throws GameActionException {

        final int ACTION_RADIUS = rc.getType().actionRadiusSquared;
        final Team OPPONENT = rc.getTeam().opponent();

		// Move according to assigned mission
		System.out.println("launcher mission: " + myMission.missionName + " " + myMission.target);
	//	if (myMission.missionName == MissionName.SCOUTING) {

			Scout.move(rc);
			Scout.updateInfos(rc);

	//	} else if (myMission.missionName == MissionName.ATTACK_ISLAND) {
	//		executeAttackIslandMission(rc, myMission);
	//	}

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

        // If there is a target, attack
        if (target != null && rc.canAttack(target.getLocation()))
            rc.attack(target.getLocation());

    }

	static void executeAttackIslandMission(RobotController rc, Mission mission) throws GameActionException {

		MapLocation target = mission.target;
		rc.setIndicatorString("tgt: " + target);
		Direction dir = Paths.findMove(rc, target);
		if (rc.canMove(dir)) rc.move(dir);

		// TODO: (probably) change target once island is capured (or too many friend launchers?)

	}

}
