package bot3;

import battlecode.common.*;

import bot3.util.*;

public class Launcher {

    static LocationType wellTarget = LocationType.WELL_ADAMANTIUM;
    static boolean goIsland = false;
    static boolean goHome = false;
    static boolean isExplorer = false;

    static void init(RobotController rc) throws GameActionException {

        // Initialize mode based on ID
        if (rc.getID() % 5 == 0) {
            goHome = false;
            goIsland = false;
            wellTarget = LocationType.WELL_ADAMANTIUM;
        } else if (rc.getID() % 5 == 1) {
            goHome = false;
            goIsland = false;
            wellTarget = LocationType.WELL_MANA;
        } else if (rc.getID() % 5 == 2) {
            goHome = false;
            goIsland = true;
            wellTarget = LocationType.WELL_ADAMANTIUM;
        } else if (rc.getID() % 5 == 3) {
            goHome = false;
            goIsland = true;
            wellTarget = LocationType.WELL_MANA;
        } else {
            goHome = true;
        }

    }

    static void refreshIndicator(RobotController rc) {
        rc.setIndicatorString("goIsland: " + goIsland + " | goHome: " + goHome);
    }

    static void setNextMove(RobotController rc) throws GameActionException {

        // Set next move randomly
        double rand = RobotPlayer.rng.nextDouble();
        if (rand < 0.25)
            goIsland = true;
        else if (rand < 0.5) {
            goIsland = false;
            wellTarget = LocationType.WELL_ADAMANTIUM;
        } else if (rand < 0.75) {
            goIsland = false;
            wellTarget = LocationType.WELL_MANA;
        } else {
            goHome = true;
        }

    }

    static void runLauncher(RobotController rc) throws GameActionException {

        final int ACTION_RADIUS = rc.getType().actionRadiusSquared;
        final Team OPPONENT = rc.getTeam().opponent();

        // Assign role initially
        if (RobotPlayer.turnCount == 2) init(rc);

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

        } else {

            if (isExplorer) {

                // Explore neutral islands if it is an explorer
                Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL);

            } else {

                // Otherwise, move towards set target
                if (goIsland) {

                    Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL, LocationType.ISLAND_FRIENDS, LocationType.ISLAND_ENEMIES);

                } else if (goHome) {

                    Searching.moveTowards(rc, LocationType.HEADQUARTERS);

                } else if (wellTarget == LocationType.WELL_ADAMANTIUM) {

                    Searching.moveTowards(rc, LocationType.WELL_ADAMANTIUM);

                } else {

                    Searching.moveTowards(rc, LocationType.WELL_MANA);

                }

            }

            // Change role every now and then
            if (RobotPlayer.turnCount % 200 == 0) {
                setNextMove(rc);
            }

        }

        refreshIndicator(rc);

    }

}
