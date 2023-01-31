package bot5;

import battlecode.common.*;

public strictfp class RobotPlayer {
	public static void run(RobotController rc) throws GameActionException {
		Robot robot;
		switch (rc.getType()) {
			case HEADQUARTERS:
				robot = new Headquarters();
				break;
			case CARRIER:
				robot = new Carrier();
				break;
			case LAUNCHER:
				robot = new Launcher();
				break;
			case BOOSTER:
				robot = new Booster();
				break;
			case DESTABILIZER:
				robot = new Destabilizer();
				break;
			case AMPLIFIER:
				robot = new Amplifier();
				break;
			default:
				throw new IllegalStateException("Unknown robot type: " + rc.getType());
		}

		while (true) {
			try {
				robot.run(rc);
			} catch (Exception e) {
				System.out.println("\n" + rc.getType() + " Exception");
				e.printStackTrace();
			} finally {
				Clock.yield();
			}
		}
	}
}
