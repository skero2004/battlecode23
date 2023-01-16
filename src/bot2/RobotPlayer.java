package bot2;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        System.out.println("I'm a " + rc.getType() + " and I just got created! I have health " + rc.getHealth());

        while (true) {

            turnCount += 1;

            if (turnCount == 2) {
                // TODO: Update HQ information at the beginning of spawn
            }

            try {

                switch (rc.getType()) {
                    case HEADQUARTERS: Headquarter.runHeadquarter(rc); break;
                    case CARRIER: Carrier.runCarrier(rc); break;
                    case LAUNCHER: Launcher.runLauncher(rc); break;
                    case BOOSTER: break;
                    case DESTABILIZER: break;
                    case AMPLIFIER: break;
                }

            } catch (GameActionException e) {

                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } catch (Exception e) {

                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } finally {
                Clock.yield();
            }

        }

    }

    static void moveRandom(RobotController rc) throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if(rc.canMove(dir)) rc.move(dir);
    }

    static void moveTowards(RobotController rc, MapLocation loc) throws GameActionException{
        Direction dir = rc.getLocation().directionTo(loc);
        if(rc.canMove(dir)) rc.move(dir);
        else moveRandom(rc);
    }

}
