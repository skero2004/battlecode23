package bot3;

import battlecode.common.*;

import bot3.util.*;

public class Launcher {

    static final double ISLAND_PROB = 0.7;
    static LocationType wellTarget = LocationType.WELL_ADAMANTIUM;
    static boolean isIsland = false;
    static boolean isExplorer = false;

    static void runLauncher(RobotController rc) throws GameActionException {

        final int ACTION_RADIUS = rc.getType().actionRadiusSquared;
        final Team OPPONENT = rc.getTeam().opponent();

        // Current location
        MapLocation me = rc.getLocation();

        // Assign role
        if (RobotPlayer.turnCount == 2) {
            if (rc.getID() % 3 != 0) {

                isIsland = true;
                if (rc.getID() % 4 == 0)
                    isExplorer = true;

            } else {

                // Adamantium by 50%, mana by 50%
                if (rc.getID() % 2 == 0)
                    wellTarget = LocationType.WELL_ADAMANTIUM;
                else
                    wellTarget = LocationType.WELL_MANA;

            }

        }

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

        if (target != null) {

            // If there is a target, attack
            if (rc.canAttack(target.getLocation()))
                rc.attack(target.getLocation());

        } else if (isIsland) {

            // Explore neutral islands if it is an explorer
            if (isExplorer)
                Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL);
            else
                Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL, LocationType.ISLAND_FRIENDS, LocationType.ISLAND_ENEMIES);

        } else {

            // Go to resources if launcher role is to go to resources
            Searching.moveTowards(rc, wellTarget);

        }

    }

}
