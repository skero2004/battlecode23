package bot2;

import battlecode.common.*;

import static bot2.Util.*;

public class Carrier {

    static MapLocation hqLoc;
    static MapLocation wellLoc;

    static boolean anchorMode = false;

    static LocationType wellTarget = Util.LocationType.WELL_AD;
    //static final double[] WELL_PROBS = { 0.3, 0.6 }; // WELL_AD, WELL_MN

    static void runCarrier(RobotController rc) throws GameActionException {

        // Current location
        MapLocation me = rc.getLocation();

        // Set role initially
        if (RobotPlayer.turnCount == 2) {

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

        // Scan for locations of nearby elements
        if (hqLoc == null) scanHQ(rc);
        if (wellLoc == null) scanWells(rc);

        // Collect from well if close and inventory not full
        if (wellLoc != null && rc.canCollectResource(wellLoc, -1)) rc.collectResource(wellLoc, -1);

        // Transfer resource to headquarters
        depositResource(rc, ResourceType.ADAMANTIUM);
        depositResource(rc, ResourceType.MANA);

        // Take anchor if it can
        if (rc.canTakeAnchor(hqLoc, Anchor.STANDARD)) {
            rc.takeAnchor(hqLoc, Anchor.STANDARD);
            anchorMode = true;
        }

        if (anchorMode) {

            // If in anchor mode, move towards island
            Pathing.moveTowards(rc, LocationType.ISLAND);

            if (rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(me)) == Team.NEUTRAL) {
                rc.placeAnchor();
                anchorMode = false;
            }

        } else {

            int total = getTotalResources(rc);
            if (total == 0) {

                // Move towards well or search for well
                Pathing.moveTowards(rc, wellTarget);

            }
            if (total == GameConstants.CARRIER_CAPACITY) {

                // Move towards HQ if it has full capacity
                Pathing.moveTowards(rc, LocationType.HEADQUARTERS);

            }

        }

    }

    static void scanHQ(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots) {
            if(robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.HEADQUARTERS) {
                hqLoc = robot.getLocation();
                break;
            }
        }
    }

    static void scanWells(RobotController rc) throws GameActionException {
        WellInfo[] wells = rc.senseNearbyWells(2);
        if(wells.length > 0) wellLoc = wells[0].getMapLocation();
    }

    static void depositResource(RobotController rc, ResourceType type) throws GameActionException {

        int amount = rc.getResourceAmount(type);
        if (amount > 0) {
            if (rc.canTransferResource(hqLoc, type, amount)) rc.transferResource(hqLoc, type, amount);
        }

    }

    static int getTotalResources(RobotController rc) {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM)
             + rc.getResourceAmount(ResourceType.MANA)
             + rc.getResourceAmount(ResourceType.ELIXIR);
    }

}
