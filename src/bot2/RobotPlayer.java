package bot2;

import battlecode.common.*;
import bot2.Util.*;

import java.util.Random;

public strictfp class RobotPlayer {

    static int turnCount = 0;
    static final Random rng = new Random(6147);

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

        Communication.hq = new Vec2D(rc.getLocation());

        while (true) {

            turnCount += 1;

            try {

                switch (rc.getType()) {
                    case HEADQUARTERS: Headquarter.runHeadquarter(rc); break;
                    case CARRIER: Carrier.runCarrier(rc); break;
                    case LAUNCHER: Launcher.runLauncher(rc); break;
                    case BOOSTER: Booster.runBooster(rc); break;
                    case DESTABILIZER: Destabilizer.runDestabilizer(rc); break;
                    case AMPLIFIER: Amplifier.runAmplifier(rc); break;
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
