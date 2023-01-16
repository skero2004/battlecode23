package bot2;

import battlecode.common.*;

public class Headquarter {

    static void runHeadquarter(RobotController rc) throws GameActionException {

        final double[] ROBOT_PROBS = { 0.4, 0.8 }; // Carrier, Launcher
        final int MAX_ANCHORS = 10;
        final int MIN_RESOURCES = 400;

        // Pick a direction to build in.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);
        if (rc.canBuildAnchor(Anchor.STANDARD) &&
            rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR) > MIN_RESOURCES &&
            rc.getNumAnchors(Anchor.STANDARD) + rc.getNumAnchors(Anchor.ACCELERATING) < MAX_ANCHORS) {

            // Build an anchor if we can, if we have enough resources, and if we have less anchors than we want
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(Anchor.STANDARD));

        }

        double rand = RobotPlayer.rng.nextDouble();
        if (rand < ROBOT_PROBS[0]) {

            // Let's try to build a carrier.
            rc.setIndicatorString("Trying to build a carrier");
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }

        } else if (rand < ROBOT_PROBS[1]) {

            // Let's try to build a launcher.
            rc.setIndicatorString("Trying to build a launcher");
            if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }

        } else {

             // Let's try to build an amplifier
            rc.setIndicatorString("Trying to build an amplifier");
            if (rc.canBuildRobot(RobotType.AMPLIFIER, newLoc)) {
                rc.buildRobot(RobotType.AMPLIFIER, newLoc);
            }


        }

    }

}
