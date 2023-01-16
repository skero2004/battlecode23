package bot1;

import battlecode.common.*;

public class Headquarter {

static void runHeadquarters(RobotController rc) throws GameActionException {

    final int MAX_ANCHORS = 10;

    // Pick a direction to build in.
    Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
    MapLocation newLoc = rc.getLocation().add(dir);
    if (RobotPlayer.turnCount == 1) {

        Communication.addHeadquarter(rc);

    } else if (RobotPlayer.turnCount == 2) {

        Communication.updateHeadquarterInfo(rc);

    }
    if (rc.canBuildAnchor(Anchor.STANDARD) &&
        rc.getResourceAmount(ResourceType.ADAMANTIUM) > 100 &&
        rc.getNumAnchors(Anchor.STANDARD) + rc.getNumAnchors(Anchor.ACCELERATING) < MAX_ANCHORS) {

        // Build an anchor if we can, and if we have less anchors than we want
        rc.buildAnchor(Anchor.STANDARD);
        rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(Anchor.STANDARD));

    }
    if (RobotPlayer.rng.nextBoolean()) {

        // Let's try to build a carrier.
        rc.setIndicatorString("Trying to build a carrier");
        if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
            rc.buildRobot(RobotType.CARRIER, newLoc);
        }

    } else {

        // Let's try to build a launcher.
        rc.setIndicatorString("Trying to build a launcher");
        if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
            rc.buildRobot(RobotType.LAUNCHER, newLoc);
        }

    }
    Communication.tryWriteMessages(rc);

    }

}
