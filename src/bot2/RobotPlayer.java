package bot2;

import battlecode.common.*;

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
