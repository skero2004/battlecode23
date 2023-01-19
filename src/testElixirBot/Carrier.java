package testElixirBot;

import battlecode.common.*;

import bot3.util.*;

public class Carrier {

    static MapLocation hqLoc;
    static MapLocation wellLoc;

    static boolean anchorMode = false;
    static boolean elixirMode = false;

    static LocationType wellTarget = null;

    static void runCarrier(RobotController rc) throws GameActionException {

        // Current location
        MapLocation me = rc.getLocation();

        // Set role initially
        if (wellTarget == null) {
            if (rc.getID() % 5 < 5)
                elixirMode = true;

            // 50% mana, 50% adamantium
            if (rc.getID() % 2 == 0)
                wellTarget = LocationType.WELL_ADAMANTIUM;
            else
                wellTarget = LocationType.WELL_MANA;

        }

        // Scan for locations of nearby elements
        if (hqLoc == null)
            scanHQ(rc);
        if (wellLoc == null)
            scanWells(rc);

        // Collect from well if close and inventory not full
        if (wellLoc != null && rc.canCollectResource(wellLoc, -1))
            rc.collectResource(wellLoc, -1);

        // Transfer resource to headquarters
        depositResource(rc, ResourceType.ADAMANTIUM);
        depositResource(rc, ResourceType.MANA);
        if (elixirMode)
            depositResource(rc, ResourceType.ELIXIR);

        // Take anchor if it can
        if (rc.canTakeAnchor(hqLoc, Anchor.STANDARD)) {
            rc.takeAnchor(hqLoc, Anchor.STANDARD);
            anchorMode = true;
        }

        if (anchorMode) {

            // If in anchor mode, move towards island
            Searching.moveTowards(rc, LocationType.ISLAND_NEUTRAL);

            if (rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(me)) == Team.NEUTRAL) {
                rc.placeAnchor();
                anchorMode = false;
            }

        } else {

            // Total number of resoures the robot is carrying
            int total = getTotalResources(rc);

            if (elixirMode) {

                if (total == GameConstants.CARRIER_CAPACITY) {

                    // Move towards different well if it has full capacity
                    if (wellTarget == LocationType.WELL_ADAMANTIUM)
                        Searching.moveTowards(rc, LocationType.WELL_MANA);
                    else
                        Searching.moveTowards(rc, LocationType.WELL_ADAMANTIUM);

                    // If it can transfer resources, create elixir well
                    int amountAd = rc.getResourceAmount(ResourceType.ADAMANTIUM);
                    int amountMn = rc.getResourceAmount(ResourceType.MANA);
                    if (amountAd > 0 && rc.canTransferResource(wellLoc, ResourceType.ADAMANTIUM, amountAd))
                        rc.transferResource(wellLoc, ResourceType.ADAMANTIUM, amountAd);
                    if (amountMn > 0 && rc.canTransferResource(wellLoc, ResourceType.MANA, amountMn))
                        rc.transferResource(wellLoc, ResourceType.MANA, amountMn);

                } else {

                    // If it doesn't have full capacity, move to original target well
                    if (wellLoc == null || !rc.canCollectResource(wellLoc, -1))
                        Searching.moveTowards(rc, wellTarget);

                }

            } else {

                // Move towards well or search for well if not in elixir mode
                if (wellLoc == null || !rc.canCollectResource(wellLoc, -1))
                    Searching.moveTowards(rc, wellTarget);

                // Move towards HQ if it has full capacity
                if (total == GameConstants.CARRIER_CAPACITY)
                    Searching.moveTowards(rc, LocationType.HEADQUARTERS);

            }

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
        if (amount > 0 && rc.canTransferResource(hqLoc, type, amount))
            rc.transferResource(hqLoc, type, amount);

    }

    static int getTotalResources(RobotController rc) {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM)
                + rc.getResourceAmount(ResourceType.MANA)
                + rc.getResourceAmount(ResourceType.ELIXIR);
    }

}
