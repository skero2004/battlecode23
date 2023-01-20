package bot3;

import battlecode.common.*;

public class Headquarter {

<<<<<<< HEAD
    static int statNumAnchor = 0;
    static String statMsg = "";

    static int itemsBuilt = 0;

    static BuildItem nextItem() {
        BuildItem[] l = { BuildItem.CARRIER,
                          BuildItem.LAUNCHER,
                          BuildItem.ANCHOR,
                          BuildItem.CARRIER,
                          BuildItem.LAUNCHER,
                          BuildItem.AMPLIFIER };
        return l[itemsBuilt % l.length];
    }

    static void refreshIndicator(RobotController rc) {
        rc.setIndicatorString(String.format("#T %d | #A %d | %s", itemsBuilt, statNumAnchor, statMsg));
    }

    static int getTotalAnchors(RobotController rc) {
        return rc.getNumAnchors(Anchor.STANDARD) + rc.getNumAnchors(Anchor.ACCELERATING);
    }

=======
>>>>>>> 896d164c5ab555c39680cbfcc29cd7e05d866660
    static void runHeadquarter(RobotController rc) throws GameActionException {

        final int MAX_ANCHORS = 10;
        final int MIN_RESOURCES = 100;

        // Pick a direction to build in.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);
<<<<<<< Updated upstream

        boolean builtSomething = false;
        switch (nextItem()) {
            case ANCHOR:
                statMsg = "building anchor";
                if (getTotalAnchors(rc) > 0 || RobotPlayer.turnCount < 250) {
                    itemsBuilt++;
                    break;
                }
                if (rc.canBuildAnchor(Anchor.STANDARD) &&
                    rc.getResourceAmount(ResourceType.ADAMANTIUM) > MIN_RESOURCES &&
                    rc.getNumAnchors(Anchor.STANDARD) + rc.getNumAnchors(Anchor.ACCELERATING) < MAX_ANCHORS) {

                    rc.buildAnchor(Anchor.STANDARD);
                    statNumAnchor += 1;
                    builtSomething = true;

                }
                break;
            case CARRIER:
                statMsg = "building carrier";
                if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {

                    rc.buildRobot(RobotType.CARRIER, newLoc);
                    builtSomething = true;

                }
                break;
            case LAUNCHER:
                statMsg = "building launcher";
                if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {

                    rc.buildRobot(RobotType.LAUNCHER, newLoc);
                    builtSomething = true;

                }
                break;
            case AMPLIFIER:
                statMsg = "building amplifier";
                if (RobotPlayer.turnCount < 100) {
                    itemsBuilt++;
                    break;
                }
                if (rc.canBuildRobot(RobotType.AMPLIFIER, newLoc)) {

                    rc.buildRobot(RobotType.AMPLIFIER, newLoc);
                    builtSomething = true;

                }
                break;
        }

        // Increment itemsBuilt if builtSomething is true
        itemsBuilt += builtSomething ? 1 : 0;

        refreshIndicator(rc);

=======
        if (rc.canBuildAnchor(Anchor.STANDARD) &&
                rc.getResourceAmount(ResourceType.ADAMANTIUM) > MIN_RESOURCES &&
                rc.getNumAnchors(Anchor.STANDARD) + rc.getNumAnchors(Anchor.ACCELERATING) < MAX_ANCHORS) {

            // Build an anchor if we can, if we have enough resources, and if we have less
            // anchors than we want
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(Anchor.STANDARD));
            System.out.println("Built anchor!");

        }

        double rand = RobotPlayer.rng.nextDouble();
        if (rc.getResourceAmount(ResourceType.ADAMANTIUM) < MIN_RESOURCES || rand < ROBOT_PROBS[0]) {

            // Let's try to build a carrier.
            rc.setIndicatorString("Trying to build a carrier");
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }

        }

        if (rand < ROBOT_PROBS[1]) {

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

>>>>>>> Stashed changes
    }

}
