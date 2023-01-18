package bot3;

import battlecode.common.*;

import bot3.util.*;

public class Launcher {

    static final double ISLAND_PROB = 0.7;
    // static final double[] WELL_PROBS = { 0.5, 0.6 }; // WELL_AD, WELL_MN
    static LocationType wellTarget = LocationType.WELL_ADAMANTIUM;

    static void runLauncher(RobotController rc) throws GameActionException {

        final int ACTION_RADIUS = rc.getType().actionRadiusSquared;
        final Team OPPONENT = rc.getTeam().opponent();

        // Assign role
        boolean isIsland = false;
        if (RobotPlayer.turnCount == 2) {
            if (rc.getID() % 3 == 0) {
                isIsland = true;
            } else {
                if (rc.getID() % 2 == 0)
                    wellTarget = LocationType.WELL_ADAMANTIUM;
                else
                    wellTarget = LocationType.WELL_MANA;

            }

        }

        // Find target enemy
        RobotInfo[] enemies = rc.senseNearbyRobots(ACTION_RADIUS, OPPONENT);
        int bestScore = -1;
        RobotInfo target = null;
        if (enemies.length > 0) {
            for (RobotInfo enemy : enemies) {
                int score = 50 - enemy.getHealth();
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

        if (target != null) {

            // If there is a target, attack
            if (rc.canAttack(target.getLocation()))
                rc.attack(target.getLocation());

        } else if (isIsland) {

            // Go to island if launcher role is to go to island
            Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL, LocationType.ISLAND_FRIENDS,
                    LocationType.ISLAND_ENEMIES);

        } else {

            // Go to resources if launcher role is to go to resources
            Searching.moveTowards(rc, wellTarget);

        }

    }

}
