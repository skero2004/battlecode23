package bot3;

import battlecode.common.*;

import bot3.util.*;

public class Amplifier {

    static final double ISLAND_PROB = 0.4;

    static LocationType wellTarget = LocationType.WELL_ADAMANTIUM;

    static void runAmplifier(RobotController rc) throws GameActionException {

        MapLocation me = rc.getLocation();
        final Team OPPONENT = rc.getTeam().opponent();

        // Run away from enemies
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, OPPONENT);
        if (enemies.length > 0) {

            int goX = me.x - enemies[0].location.x;
            int goY = me.y - enemies[0].location.y;
            Direction move = me.directionTo(me.translate(goX, goY));
            if (rc.canMove(move)) {
                rc.move(move);
                return;
            }

        }

        Searching.moveTowards(rc, LocationType.ISLAND_FRIENDS, LocationType.ISLAND_ENEMIES, LocationType.ISLAND_NEUTRAL);
        /*
         * 
         * // Assign role
         * boolean isIsland = false;
         * if (RobotPlayer.turnCount == 2) {
         * if (rc.getID() % 3 == 0) {
         * isIsland = true;
         * } else {
         * if (rc.getID() % 2 == 0)
         * wellTarget = LocationType.WELL_ADAMANTIUM;
         * else
         * wellTarget = LocationType.WELL_MANA;
         * }
         * }
         * 
         * if (isIsland) {
         * // Go to island if launcher role is to go to island
         * Searching.moveTowards(rc, LocationType.ISLAND_FRIENDS,
         * LocationType.ISLAND_ENEMIES,
         * LocationType.ISLAND_NEUTRAL);
         * } else {
         * 
         * // Go to resources if launcher role is to go to resources
         * Searching.moveTowards(rc, wellTarget);
         * 
         * }
         */
    }

}
