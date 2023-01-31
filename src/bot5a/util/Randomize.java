package bot5a.util;

import java.util.Random;

import battlecode.common.*;

public class Randomize {

	public static final Random rng = new Random();

	public static Direction move(RobotController rc) throws GameActionException {
		for (int i = 0; i < 8; ++i) {
			Direction dir = Constants.directions[rng.nextInt(Constants.directions.length)];
			if (rc.canMove(dir))
				rc.move(dir);
		}
		return Direction.CENTER;
	}
}
