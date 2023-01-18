package bot2;

import battlecode.common.*;

import static bot2.Util.*;

public class Launcher {

    static final double ISLAND_PROB = 0.7;
    //static final double[] WELL_PROBS = { 0.5, 0.6 }; // WELL_AD, WELL_MN
    static LocationType wellTarget = LocationType.WELL_AD;

    static void runLauncher(RobotController rc) throws GameActionException {

        final int ACTION_RADIUS = rc.getType().actionRadiusSquared;
        final Team OPPONENT = rc.getTeam().opponent();

        // Assign role
        boolean isIsland = false;
        if (RobotPlayer.turnCount == 2) {

            if (RobotPlayer.rng.nextDouble() < ISLAND_PROB) {
                isIsland = true;
            } else {

                double rand = RobotPlayer.rng.nextDouble();
                /*
                if (rand < WELL_PROBS[0])
                    wellTarget = LocationType.WELL_AD;
                else if (rand < WELL_PROBS[1])
                    wellTarget = LocationType.WELL_MN;
                else
                    wellTarget = LocationType.WELL_EX;
                */
                if (rand < 0.5) wellTarget = LocationType.WELL_AD;
                else wellTarget = LocationType.WELL_MN;

            }

        }

        // Find target enemy
        RobotInfo[] enemies = rc.senseNearbyRobots(ACTION_RADIUS, OPPONENT);
        int bestScore = Integer.MIN_VALUE;
        RobotInfo target = null;
        for (RobotInfo enemy : enemies) {
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

        if (target != null){

            // If there is a target, attack
            if (rc.canAttack(target.getLocation()))
                rc.attack(target.getLocation());

        } else if (isIsland) {

            // Go to island if launcher role is to go to island
            Pathing.moveTowards(rc, LocationType.ISLAND);

        } else {

            // Go to resources if launcher role is to go to resources
            Pathing.moveTowards(rc, wellTarget);

        }

    }

}
