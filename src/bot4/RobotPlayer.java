package bot4;

import battlecode.common.*;

public strictfp class RobotPlayer {

	static int turnCount = 0;

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
			try {
				switch (rc.getType()) {
					case HEADQUARTERS:
						Headquarters.run(rc);
						break;
					case CARRIER:
						Carrier.run(rc);
						break;
					case LAUNCHER:
						Launcher.run(rc);
						break;
					case BOOSTER:
						Booster.run(rc);
						break;
					case DESTABILIZER:
						Destabilizer.run(rc);
						break;
					case AMPLIFIER:
						Amplifier.run(rc);
						break;
				}
				++turnCount;
			} catch (Exception e) {
				System.out.println();
				System.out.println(rc.getType() + " Exception");
				e.printStackTrace();
			} finally {
				Clock.yield();
			}
		}
	}
}
