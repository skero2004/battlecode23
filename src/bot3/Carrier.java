package bot3;

import battlecode.common.*;

import bot3.util.*;

public class Carrier {

    static MapLocation hqLoc;
    static MapLocation wellLoc;

    static boolean anchorMode = false;
    static LocationType wellTarget = null;
    static boolean goHome = false;

    static void refreshIndicator(RobotController rc) {
        rc.setIndicatorString(String.format("GH %s AM %s | %s", goHome, anchorMode, wellTarget));
    }

    static void init(RobotController rc) throws GameActionException {

        if (rc.getID() % 2 == 0) {
            wellTarget = LocationType.WELL_ADAMANTIUM;
            rc.setIndicatorString("rsrc:ad");
        } else {
            wellTarget = LocationType.WELL_MANA;
            rc.setIndicatorString("rsrc:mn");
        }

    }

    static void runCarrier(RobotController rc) throws GameActionException {

        // Current location
        MapLocation me = rc.getLocation();
        final Team OPPONENT = rc.getTeam().opponent();

        // Set role initially
        if (wellTarget == null) init(rc);

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

        if (goHome) {

            // Scan for HQ if not found
            if (hqLoc == null) scanHQ(rc);

            Searching.moveTowards(rc, LocationType.HEADQUARTERS);

            // Transfer resource to headquarters if possible
            depositResource(rc, ResourceType.ADAMANTIUM);
            depositResource(rc, ResourceType.MANA);

            // Take anchor if it can
            if (hqLoc != null && rc.canTakeAnchor(hqLoc, Anchor.STANDARD)) {
                rc.takeAnchor(hqLoc, Anchor.STANDARD);
                anchorMode = true;
            }

            // Once all items are deposited, set goHome to false
            if (getTotalResources(rc) == 0)
                goHome = false;

        } else {

            if (anchorMode) {

                // If in anchor mode, move towards neutral island
                Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL);

                // Place anchor if possible
                if (rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(me)) == Team.NEUTRAL) {
                    rc.placeAnchor();
                    anchorMode = false;
                }

            } else {

                // If neither goHome mode or anchorMode, then get resources

                // Scan well location
                if (wellLoc == null) scanWells(rc);

                // Collect from well if close and inventory not full
                if (wellLoc != null && rc.canCollectResource(wellLoc, -1))
                    rc.collectResource(wellLoc, -1);
                else {

                    // If not collecting, move

                    // At the beginning of the game, don't care about role
                    //if (RobotPlayer.turnCount < 1000)
                        //Searching.moveTowards(rc, LocationType.WELL_ADAMANTIUM, LocationType.WELL_MANA);
                    //else
                        Searching.moveTowards(rc, wellTarget);

                }

                // If robot carrying maximum capacity, then goHome
                if (getTotalResources(rc) == GameConstants.CARRIER_CAPACITY)
                    goHome = true;

            }

            refreshIndicator(rc);

        }

    }

    static void scanHQ(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots) {
            if (robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.HEADQUARTERS) {
                hqLoc = robot.getLocation();
                break;
            }
        }
    }

    static void scanWells(RobotController rc) throws GameActionException {
        WellInfo[] wells = rc.senseNearbyWells(2);
        if (wells.length > 0)
            wellLoc = wells[0].getMapLocation();
    }

    static void depositResource(RobotController rc, ResourceType type) throws GameActionException {
        int amount = rc.getResourceAmount(type);
        if (amount > 0) {
            if (rc.canTransferResource(hqLoc, type, amount))
                rc.transferResource(hqLoc, type, amount);
        }
    }

    static int getTotalResources(RobotController rc) {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM)
                + rc.getResourceAmount(ResourceType.MANA)
                + rc.getResourceAmount(ResourceType.ELIXIR);
    }

}
