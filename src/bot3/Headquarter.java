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

    static boolean makeAnchor = false;
    static boolean makeAmplifier = false;

    /*
    static BuildItem nextItem() {
        BuildItem[] l = {
                            BuildItem.CARRIER,
                            BuildItem.LAUNCHER,
                            BuildItem.CARRIER,
                            BuildItem.ANCHOR,
                            BuildItem.LAUNCHER,
                            BuildItem.LAUNCHER,
                            BuildItem.CARRIER,
                            BuildItem.LAUNCHER,
                            BuildItem.CARRIER,
                            BuildItem.LAUNCHER,
                            BuildItem.LAUNCHER,
                            BuildItem.AMPLIFIER
                        };
        return l[itemsBuilt % l.length];
    }
    */

    static void refreshIndicator(RobotController rc) {
        rc.setIndicatorString(String.format("#T %d | #A %d | %s", itemsBuilt, statNumAnchor, statMsg));
    }

    static int getTotalAnchors(RobotController rc) {
        return rc.getNumAnchors(Anchor.STANDARD) + rc.getNumAnchors(Anchor.ACCELERATING);
    }

    static void runHeadquarter(RobotController rc) throws GameActionException {

        /*
        final int MAX_ANCHORS = 10;
        final int MIN_RESOURCES = 100;
        */

        // Pick a direction to build in.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        /*
        int amountMn = rc.getResourceAmount(ResourceType.MANA);
        int amountAd = rc.getResourceAmount(ResourceType.ADAMANTIUM);

        boolean builtSomething = false;
        switch (nextItem()) {
            case ANCHOR:
                statMsg = "building anchor";
                if (getTotalAnchors(rc) > 0 || RobotPlayer.turnCount < 100 || Math.abs(amountMn - amountAd) > 200) {
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
                if (amountMn - amountAd > 150) {
                    itemsBuilt++;
                    break;
                }
                if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {

                    rc.buildRobot(RobotType.CARRIER, newLoc);
                    builtSomething = true;

                }
                break;
            case LAUNCHER:
                statMsg = "building launcher";
                if (amountAd - amountMn > 150 || RobotPlayer.turnCount < 100) {
                    itemsBuilt++;
                    break;
                }
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
        */

        if ((RobotPlayer.turnCount % 200 == 0 && RobotPlayer.turnCount > 100) || RobotPlayer.turnCount == 100)
            makeAnchor = true;
        if (RobotPlayer.turnCount % 50 == 0 && RobotPlayer.turnCount > 200)
            makeAmplifier = true;

        if (makeAnchor) {

            // Build anchor
            rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(Anchor.STANDARD));
            if (rc.canBuildAnchor(Anchor.STANDARD)) {
                rc.buildAnchor(Anchor.STANDARD);
                makeAnchor = false;
            }

        } else if (makeAmplifier) {

            // Build amplifier
            rc.setIndicatorString("Trying to build an amplifier");
            if (rc.canBuildRobot(RobotType.AMPLIFIER, newLoc)) {
                rc.buildRobot(RobotType.AMPLIFIER, newLoc);
                makeAmplifier = false;
            }

        } else {

            // Build carrier
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.setIndicatorString("Trying to build a carrier");
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }

            // Build launcher
            if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                rc.setIndicatorString("Trying to build a launcher");
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }

        }

    }

}
