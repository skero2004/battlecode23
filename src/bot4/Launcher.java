package bot4;

import battlecode.common.*;

import bot4.util.*;

public class Launcher {

	static int id = 0;
	static Direction dir = Constants.directions[0];
/*
	static void init(RobotController rc) throws GameActionException {

		id = rc.getID();
		dir = Constants.directions[id % 8];

	}
*/

	static void run(RobotController rc) throws GameActionException {

        final int ACTION_RADIUS = rc.getType().actionRadiusSquared;
        final Team OPPONENT = rc.getTeam().opponent();

		/*
		// Initialize
		if (RobotPlayer.turnCount == 1) init(rc);

		// Direction
		dir = Constants.directions[id % 8];
		if (rc.canMove(dir))
			rc.move(dir);
		else if (rc.getMovementCooldownTurns() - GameConstants.COOLDOWNS_PER_TURN < 0) 
			if (rc.getID() % 2 == 0) id++;
			else 					 id--;

        rc.setIndicatorString("" + rc.getMovementCooldownTurns());
		*/

		Scout.move(rc);

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

}
