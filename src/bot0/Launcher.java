package bot0;

import battlecode.common.*;

public class Launcher {

    static void runLauncher(RobotController rc) throws GameActionException {

        Team opponent = rc.getTeam().opponent();
        MapLocation me = rc.getLocation();

        // List of enemies that are actionable
        int radius = rc.getType().actionRadiusSquared;
        RobotInfo[] actionEnemies = rc.senseNearbyRobots(radius, opponent);

        // Find target enemy
        int lowestHealth = Integer.MAX_VALUE;
        int smallestDistance = Integer.MAX_VALUE;
        RobotInfo target = null;
        if (actionEnemies.length > 0) {

            for (RobotInfo enemy : actionEnemies) {

                int enemyHealth = enemy.getHealth();
                int enemyDistance = enemy.getLocation().distanceSquaredTo(me);

                if (enemyHealth < lowestHealth) {

                    target = enemy;
                    lowestHealth = enemyHealth;
                    smallestDistance = enemyDistance;

                } else if (enemyHealth == lowestHealth) {

                    if (enemyDistance < smallestDistance) {

                        target = enemy;
                        smallestDistance = enemyDistance;

                    }

                }

            }

        }

        if (target != null) {

            // Attack target enemy
            if (rc.canAttack(target.getLocation()))
                rc.attack(target.getLocation());

        } else {

            // If no target, then move to well
            WellInfo[] wells = rc.senseNearbyWells();
            if (wells.length > 0) {

                MapLocation wellLoc = wells[0].getMapLocation();
                Direction dir = me.directionTo(wellLoc);
                if (rc.canMove(dir))
                    rc.move(dir);

            }

        }

        // List of enemies that are visible
        RobotInfo[] visibleEnemies = rc.senseNearbyRobots(-1, opponent);

        // Iterate through visible enemies
        for (RobotInfo enemy : visibleEnemies) {

            if (enemy.getType() != RobotType.HEADQUARTERS) {

                MapLocation enemyLocation = enemy.getLocation();
                Direction moveDir = me.directionTo(enemyLocation);
                if (rc.canMove(moveDir)) {
                    rc.move(moveDir);
                }

            }

        }
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

    }

}
