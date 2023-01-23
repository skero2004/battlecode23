package bot4;

import java.util.ArrayList;

import battlecode.common.*;

import bot4.util.*;

public class Paths {

	/*
	 * Calculates a single step using local gravity with provided weights.
	 * (defaults in bot4.util.Constants).
	 */
	static Direction findMove(RobotController rc, MapLocation target, int MWall, int MEnemyLauncher, int MFriend)
			throws GameActionException {
		MapLocation current = rc.getLocation();
		int dirX = target.x - current.x, dirY = target.y - current.y;

		// walls
		for (MapInfo m : rc.senseNearbyMapInfos())
			if (!m.isPassable()) {
				MapLocation wall = m.getMapLocation();
				int dx = wall.x - current.x, dy = wall.y - current.y;
				dirX += gravity(MWall, dx, dx, dy);
				dirX += gravity(MWall, dy, dx, dy);
			}

		// robots
		if (MEnemyLauncher != 0 || MFriend != 0) {
			for (RobotInfo r : rc.senseNearbyRobots()) {
				MapLocation robot = r.getLocation();
				int dx = current.x - robot.x, dy = current.y - robot.y;
				if (r.getTeam() == rc.getTeam() && r.getType() != RobotType.HEADQUARTERS) {
					dirX += gravity(MFriend, dx, dx, dy);
					dirY += gravity(MFriend, dy, dx, dy);
				} else if (r.getType() == RobotType.LAUNCHER) {
					dirX += gravity(MEnemyLauncher, dx, dx, dy);
					dirY += gravity(MEnemyLauncher, dy, dx, dy);
				}
			}
		}

		MapLocation to = rc.getLocation().translate(dirX, dirY);
		Direction move = rc.getLocation().directionTo(to);
		if (rc.canMove(move))
			return move;
		else
			return Randomize.move(rc);
	}

	static Direction findMove(RobotController rc, MapLocation target) throws GameActionException {
		return findMove(rc, target, Constants.MASS_WALL, Constants.MASS_ENEMY_LAUNCHER, Constants.MASS_FRIEND);
	}

	private static int gravity(int mass, int s, int dx, int dy) {
		int r = (int) Math.sqrt(dx * dx + dy * dy);
		return s * mass / (r * r * r);
	}
}
