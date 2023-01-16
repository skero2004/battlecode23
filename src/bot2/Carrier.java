package bot2;

import battlecode.common.*;

public class Carrier {

    static MapLocation hqLoc;
    static MapLocation wellLoc;
    static MapLocation islandLoc;

    static boolean anchorMode = false;

    static Pathing.LocationType wellTarget = Pathing.LocationType.WELL_AD;
    static final double[] WELL_PROBS = { 0.3, 0.3 }; // WELL_AD, WELL_MN

    static void runCarrier(RobotController rc) throws GameActionException {

        // Current location
        MapLocation me = rc.getLocation();

        // Set role initially
        if (RobotPlayer.turnCount == 2) {

            double rand = RobotPlayer.rng.nextDouble();
            if (rand < WELL_PROBS[0])
                wellTarget = Pathing.LocationType.WELL_AD;
            else if (rand < WELL_PROBS[1])
                wellTarget = Pathing.LocationType.WELL_MN;
            else
                wellTarget = Pathing.LocationType.WELL_EX;

        }

        // Scan for locations of nearby elements
        if (hqLoc == null) scanHQ(rc);
        if (wellLoc == null) scanWells(rc);
        scanIslands(rc);

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
            Pathing.moveTowards(rc, Pathing.LocationType.ISLAND);

            if (rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(me)) == Team.NEUTRAL) {
                rc.placeAnchor();
                anchorMode = false;
            }

        } else {

            int total = getTotalResources(rc);
            if (total == 0) {

                // Move towards well or search for well
                if (wellLoc == null) Pathing.moveTowards(rc, Pathing.LocationType.WELL_);
                else if (!me.isAdjacentTo(wellLoc)) RobotPlayer.moveTowards(rc, wellLoc);
                else RobotPlayer.moveTowards(rc, wellLoc);

            }
            if (total == GameConstants.CARRIER_CAPACITY) {

                Pathing.moveTowards(rc, Pathing.LocationType.HEADQUARTERS);

            }

        }
        Communication.tryWriteMessages(rc);

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
        WellInfo[] wells = rc.senseNearbyWells();
        if(wells.length > 0) wellLoc = wells[0].getMapLocation();
    }

    static void scanIslands(RobotController rc) throws GameActionException {
        int[] ids = rc.senseNearbyIslands();
        for(int id : ids) {
            if(rc.senseTeamOccupyingIsland(id) == Team.NEUTRAL) {
                MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                if(locs.length > 0) {
                    islandLoc = locs[0];
                    break;
                }
            }
            Communication.updateIslandInfo(rc, id);
        }
    }

    static void depositResource(RobotController rc, ResourceType type) throws GameActionException {

        int amount = rc.getResourceAmount(type);
        if(amount > 0) {
            if(rc.canTransferResource(Pathing.LocationType.WELL_AD, type, amount)) rc.transferResource(hqLoc, type, amount);
        }

    }

    static int getTotalResources(RobotController rc) {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM)
             + rc.getResourceAmount(ResourceType.MANA)
             + rc.getResourceAmount(ResourceType.ELIXIR);
    }

}
