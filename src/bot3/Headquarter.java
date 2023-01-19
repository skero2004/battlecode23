package bot3;

import battlecode.common.*;

enum BuildItem {
    ANCHOR,
    CARRIER,
    LAUNCHER,
    AMPLIFIER,
}

public class Headquarter {

    static int statNumAnchor = 0;
    static String statMsg = "";

    static int itemsBuilt = 0;

    static BuildItem nextItem() {
        BuildItem[] l = { BuildItem.CARRIER, BuildItem.CARRIER, BuildItem.ANCHOR, BuildItem.LAUNCHER, BuildItem.AMPLIFIER };
        return l[itemsBuilt % l.length];
    }

    static void refreshIndicator(RobotController rc) {
        rc.setIndicatorString(String.format("#T %d | #A %d | %s", itemsBuilt, statNumAnchor, statMsg));
    }

    /// mohit's old code

    static void runHeadquarter(RobotController rc) throws GameActionException {

        final int MAX_ANCHORS = 10;
        final int MIN_RESOURCES = 100;

        // Pick a direction to build in.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        boolean builtSomething = false;
        switch (nextItem()) {
            case ANCHOR:
                if (rc.canBuildAnchor(Anchor.STANDARD) &&
                    rc.getResourceAmount(ResourceType.ADAMANTIUM) > MIN_RESOURCES &&
                    rc.getNumAnchors(Anchor.STANDARD) + rc.getNumAnchors(Anchor.ACCELERATING) < MAX_ANCHORS) {

                    rc.buildAnchor(Anchor.STANDARD);
                    statNumAnchor += 1;
                    statMsg = "building anchor";
                    builtSomething = true;

                }
                break;
            case CARRIER:
                if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {

                    rc.buildRobot(RobotType.CARRIER, newLoc);
                    statMsg = "building carrier";
                    builtSomething = true;

                }
                break;
            case LAUNCHER:
                if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {

                    rc.buildRobot(RobotType.LAUNCHER, newLoc);
                    statMsg = "building launcher";
                    builtSomething = true;

                }
                break;
            case AMPLIFIER:
                if (rc.canBuildRobot(RobotType.AMPLIFIER, newLoc)) {

                    rc.buildRobot(RobotType.AMPLIFIER, newLoc);
                    statMsg = "building amplifier";
                    builtSomething = true;

                }
                break;
        }

        // Increment itemsBuilt if builtSomething is true
        itemsBuilt += builtSomething ? 1 : 0;

        refreshIndicator(rc);

    }

}
