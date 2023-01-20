package bot3;

import battlecode.common.*;

import bot3.util.*;

public class Carrier {

    static MapLocation hqLoc;
    static MapLocation wellLoc;

    static boolean anchorMode = false;
    static LocationType wellTarget = null;
<<<<<<< HEAD
=======

>>>>>>> 896d164c5ab555c39680cbfcc29cd7e05d866660
    static boolean goHome = false;

    static void refreshIndicator(RobotController rc) {
        rc.setIndicatorString(String.format("GH %s AM %s | %s", goHome, anchorMode, wellTarget));
    }

    static void init(RobotController rc) throws GameActionException {

        if (rc.getID() % 5 < 3) {
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

        // Set role initially
        if (wellTarget == null)
            init(rc);

<<<<<<< Updated upstream

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

            // TODO check if in range to set robot mode
            if (anchorMode) {

                // If in anchor mode, move towards neutral island
                Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL);

                // Place anchor if possible
                if (rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(me)) == Team.NEUTRAL) {
                    rc.placeAnchor();
                    anchorMode = false;
=======
        // Take anchorif it can
        if (hqLoc != null && rc.canTakeAnchor(hqLoc, Anchor.STANDARD)) {
            rc.takeAnchor(hqLoc, Anchor.STANDARD);
            anchorMode = true;
            goHome = false;
        }
        if (goHome) {
            int dx = me.x - hqLoc.x, dy = me.y - hqLoc.y;
            int squaredDist = dx * dx + dy * dy;

            if (squaredDist > 2) {
                // Move towards HQ if it has full capacity
                Searching.moveTowards(rc, LocationType.HEADQUARTERS);
            } else {

                // Transfer resource to headquarters
                depositResource(rc, ResourceType.ADAMANTIUM);
                depositResource(rc, ResourceType.MANA);

            }
        } else {
            // TODO check if in range to set robot mode
            if (anchorMode) {
                if (rc.canPlaceAnchor()
                        && rc.senseTeamOccupyingIsland(rc.senseIsland(me)) == Team.NEUTRAL) {
                    rc.placeAnchor();
                    anchorMode = false;
                    goHome = false;
                } else {
                    // If in anchor mode, move towards island
                    int i = rc.senseIsland(rc.getLocation());
                    if (i == -1)
                        Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL, LocationType.ISLAND_ENEMIES);
                    else if (rc.senseTeamOccupyingIsland(i) == rc.getTeam())
                        Searching.sync(rc);

>>>>>>> Stashed changes
                }

            } else {

<<<<<<< Updated upstream
                // If neither goHome mode or anchorMode, then get resources

                // At the beginning of the game, don't care about role
                if (RobotPlayer.turnCount < 100)
                    Searching.moveTowards(rc, LocationType.WELL_ADAMANTIUM, LocationType.WELL_MANA);
                else
                    Searching.moveTowards(rc, wellTarget);

                if (wellLoc == null) scanWells(rc);

                // Collect from well if close and inventory not full
                if (wellLoc != null && rc.canCollectResource(wellLoc, -1))
                    rc.collectResource(wellLoc, -1);

                // If robot carrying maximum capacity, then goHome
                if (getTotalResources(rc) == GameConstants.CARRIER_CAPACITY)
                    goHome = true;

            }

            refreshIndicator(rc);

        }

=======
                if (wellLoc == null)
                    scanWells(rc);
                // Collect from well if close and inventory not full
                if (wellLoc != null && rc.canCollectResource(wellLoc, -1))
                    rc.collectResource(wellLoc, -1);
                else
                    Searching.moveTowards(rc, wellTarget);

                int total = getTotalResources(rc);
                if (total == GameConstants.CARRIER_CAPACITY) {
                    goHome = true;
                }
            }
        }

        refreshIndicator(rc);
>>>>>>> Stashed changes
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
