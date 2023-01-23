package bot1a;

import battlecode.common.*;

public class Headquarter {

    static void runHeadquarters(RobotController rc) throws GameActionException {

        final int MAX_ANCHORS = 10;
        final int MINIMAL_ROBOTS = 20;
        final int MIN_RESOURCES = 400;

        // Pick a direction to build in.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);
        if (RobotPlayer.turnCount == 1) {

            Communication.addHeadquarter(rc);

        } else if (RobotPlayer.turnCount == 2) {

            Communication.updateHeadquarterInfo(rc);

        }
        if (rc.canBuildAnchor(Anchor.STANDARD) &&
            rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR) > MIN_RESOURCES &&
            rc.getNumAnchors(Anchor.STANDARD) + rc.getNumAnchors(Anchor.ACCELERATING) < MAX_ANCHORS) {

            // Build an anchor if we can, if we have enough resources, and if we have less anchors than we want
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(Anchor.STANDARD));

        }

        double probLauncher = 0.5;
        if (rc.getRobotCount() < MINIMAL_ROBOTS) {
            probLauncher = 0.2;
        }
        if (RobotPlayer.rng.nextDouble() > probLauncher) {

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
