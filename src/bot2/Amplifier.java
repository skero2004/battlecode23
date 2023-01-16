package bot2;

import battlecode.common.*;

import static bot2.Util.*;

public class Amplifier {

    static final double ISLAND_PROB = 0.3;
    static final double[] WELL_PROBS = { 0.3, 0.6 }; // WELL_AD, WELL_MN
    static LocationType wellTarget = LocationType.WELL_AD;

    static void runAmplifier(RobotController rc) throws GameActionException {

        // Assign role
        boolean isIsland = false;
        if (RobotPlayer.turnCount == 2) {

            if (RobotPlayer.rng.nextDouble() < ISLAND_PROB) {
                isIsland = true;
            } else {

                double rand = RobotPlayer.rng.nextDouble();
                if (rand < WELL_PROBS[0])
                    wellTarget = LocationType.WELL_AD;
                else if (rand < WELL_PROBS[1])
                    wellTarget = LocationType.WELL_MN;
                else
                    wellTarget = LocationType.WELL_EX;

            }

        }

        if (isIsland) {

            // Go to island if launcher role is to go to island
            Pathing.moveTowards(rc, LocationType.ISLAND);

        } else {

            // Go to resources if launcher role is to go to resources
            Pathing.moveTowards(rc, wellTarget);

        }

    }

}
