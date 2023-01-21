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
        int mod = rc.getID() % 10;
        if (mod == 0) {

            // Go to adamantium
            wellTarget = LocationType.WELL_ADAMANTIUM;

        } else if (mod == 1) {

            // Go to mana
            wellTarget = LocationType.WELL_MANA;

        } else if (mod == 2) {

            // Go to island
            goIsland = true;

        } else if (mod == 3 || mod == 7 || mod == 8 || mod == 9 || mod == 5 || mod == 6) {

            // Explorer
            isExplorer = true;

        } else {

            // Go home
            goHome = true;

        }

    }

    static void refreshIndicator(RobotController rc) {
        rc.setIndicatorString("goIsland: " + goIsland + " | goHome: " + goHome + " | isExplorer: " + isExplorer);
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

        RobotInfo[] visibleEnemies = rc.senseNearbyRobots(-1, OPPONENT);
        for (RobotInfo enemy : visibleEnemies) {
            if (enemy.getType() != RobotType.HEADQUARTERS) {
                MapLocation enemyLocation = enemy.getLocation();
                MapLocation robotLocation = rc.getLocation();
                Direction moveDir = robotLocation.directionTo(enemyLocation);
                if (rc.canMove(moveDir) && target == null) {
                    rc.move(moveDir);
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
                Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL, LocationType.ISLAND_ENEMIES);

            } else {

                // Otherwise, move towards set target
                if (goIsland) {

                    Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL, LocationType.ISLAND_FRIENDS);

                } else if (goHome) {

                    Searching.moveTowards(rc, LocationType.HEADQUARTERS);

                } else if (wellTarget == LocationType.WELL_ADAMANTIUM) {

                    Searching.moveTowards(rc, LocationType.WELL_ADAMANTIUM);

                } else {

                    Searching.moveTowards(rc, LocationType.WELL_MANA);

                }

            }

        }

        refreshIndicator(rc);

    }

}
