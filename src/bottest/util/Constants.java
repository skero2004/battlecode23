package bottest.util;

import battlecode.common.*;

public final class Constants {
	// General constants
	public static final Direction[] directions = {
			Direction.NORTH,
			Direction.NORTHEAST,
			Direction.EAST,
			Direction.SOUTHEAST,
			Direction.SOUTH,
			Direction.SOUTHWEST,
			Direction.WEST,
			Direction.NORTHWEST,
	};

	// Default masses for local gravity
	public final static int MASS_WALL = -80000;
	public final static int MASS_ENEMY_LAUNCHER = -80000;
	public final static int MASS_FRIEND = 50000;
}
