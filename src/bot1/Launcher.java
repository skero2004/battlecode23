package bot1;

import battlecode.common.*;

public class Launcher {

    static void runLauncher(RobotController rc) throws GameActionException {

        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        int bestScore = -1;
        RobotInfo target = null;
        if (RobotPlayer.turnCount == 2) {
            Communication.updateHeadquarterInfo(rc);
        }
        Communication.clearObsoleteEnemies(rc);
        if (enemies.length > 0) {
            for (RobotInfo enemy: enemies) {
                Communication.reportEnemy(rc, enemy.location);
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
        Communication.tryWriteMessages(rc);
        if (target != null){
            if (rc.canAttack(target.getLocation()))
            rc.attack(target.getLocation());
        }
        else {
            WellInfo[] wells = rc.senseNearbyWells();
            if (wells.length > 0){
                MapLocation wellLoc = wells[0].getMapLocation();
                Direction dir = rc.getLocation().directionTo(wellLoc);
                if (rc.canMove(dir))
                rc.move(dir);
            }
        }

        RobotInfo[] visibleEnemies = rc.senseNearbyRobots(-1, opponent);
        for (RobotInfo enemy : visibleEnemies) {
            if (enemy.getType() != RobotType.HEADQUARTERS) {
                MapLocation enemyLocation = enemy.getLocation();
                MapLocation robotLocation = rc.getLocation();
                Direction moveDir = robotLocation.directionTo(enemyLocation);
                if (rc.canMove(moveDir)) {
                    rc.move(moveDir);
                }
            }
        }

        // Also try to move randomly.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

    }

}
