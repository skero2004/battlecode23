package bot3;

import java.util.Random;

import battlecode.common.*;

public strictfp class RobotPlayer {

    static int turnCount = 0;
    static final Random rng = new Random();

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

    public static void run(RobotController rc) throws GameActionException {
        while (true) {
            turnCount += 1;
            try {
                switch (rc.getType()) {
                    case HEADQUARTERS:
                        Headquarter.runHeadquarter(rc);
                        break;
                    case CARRIER:
                        Carrier.runCarrier(rc);
                        break;
                    case LAUNCHER:
                        Launcher.runLauncher(rc);
                        break;
                    case BOOSTER:
                        Booster.runBooster(rc);
                        break;
                    case DESTABILIZER:
                        Destabilizer.runDestabilizer(rc);
                        break;
                    case AMPLIFIER:
                        Amplifier.runAmplifier(rc);
                        break;
                }
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
        if (rc.canMove(dir))
            rc.move(dir);
    }
}
