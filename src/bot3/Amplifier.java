package bot3;

import battlecode.common.*;

import bot3.util.*;

public class Amplifier {

    static final double ISLAND_PROB = 0.4;

    static LocationType wellTarget = LocationType.WELL_ADAMANTIUM;

    static void runAmplifier(RobotController rc) throws GameActionException {
        Searching.moveTowards(rc, LocationType.ISLAND_FRIENDS, LocationType.ISLAND_ENEMIES,
                LocationType.ISLAND_NEUTRAL);
        return;
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
