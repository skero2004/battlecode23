package bot5;

import battlecode.common.*;

public class Map {
	enum Symmetry {
		HORIZONTAL,
		VERTICAL,
		ROTATIONAL
	};

	static int WIDTH;
	static int HEIGHT;
	static Symmetry SYMMETRY = Symmetry.HORIZONTAL;

	static MapLocation reflect(MapLocation m, Symmetry s) {
		switch (s) {
			case HORIZONTAL:
				return new MapLocation(WIDTH - m.x, m.y);
			case VERTICAL:
				return new MapLocation(WIDTH, HEIGHT - m.y);
			case ROTATIONAL:
				return new MapLocation(WIDTH - m.x, HEIGHT - m.y);
			default:
				throw new IllegalStateException("Unknown symmetry type");
		}
	}
}
