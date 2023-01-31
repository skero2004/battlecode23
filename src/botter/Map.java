package botter;

import java.util.HashMap;

import battlecode.common.*;

public class Map {
	enum Symmetry {
		HORIZONTAL,
		VERTICAL,
		ROTATIONAL
	};

	static int WIDTH;
	static int HEIGHT;
	static Symmetry SYMMETRY;

	static HashMap<Symmetry, Boolean> symmetriesPossible = new HashMap<>();
	static {
		symmetriesPossible.put(Symmetry.HORIZONTAL, true);
		symmetriesPossible.put(Symmetry.VERTICAL, true);
		symmetriesPossible.put(Symmetry.ROTATIONAL, true);
	}

	static MapLocation reflect(MapLocation m, Symmetry s) {
		switch (s) {
			case HORIZONTAL:
				return new MapLocation(WIDTH - 1 - m.x, m.y);
			case VERTICAL:
				return new MapLocation(m.x, HEIGHT - 1 - m.y);
			case ROTATIONAL:
				return new MapLocation(WIDTH - 1 - m.x, HEIGHT - 1 - m.y);
			default:
				throw new IllegalStateException("Unknown symmetry type");
		}
	}
}
