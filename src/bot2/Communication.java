package bot2;

import java.util.List;
import java.util.ArrayList;

import bot2.Util.*;

import battlecode.common.*;

class Coordinates {
    final int x, y;

	Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	static int serialize(Coordinates coord) {
		return coord.x | (coord.y << 6);
	}

	static Coordinates deserialize(int bin) {
		return new Coordinates(bin & 63, (bin >> 6));
	}
}

abstract class Location { 
	final LocationType typ; 
	final Coordinates coordinates; 

	Location(LocationType typ, Coordinates coordinates) {
		this.typ = typ;
		this.coordinates = coordinates;
	}
}

class Island extends Location {
	final boolean isNotCaptured;
	final boolean needsLauncher; // launchers # < ??
	final boolean isAnchorDying; // HP below ?? %

	Island(LocationType typ, Coordinates coord, boolean isNotCaptured, boolean needsLauncher, boolean isAnchorDying) {
		super(typ, coord);
		this.isNotCaptured = isNotCaptured;
		this.needsLauncher = needsLauncher;
		this.isAnchorDying = isAnchorDying;
	}

	static Island deserialize(int bin) {
		int typ = bin & 1;
		if (typ != 1) throw new Exception();
		boolean isNotCaptured = ((bin >> 1) & 1) == 1;
		boolean needsLauncher = ((bin >> 2) & 1) == 1;
		boolean isAnchorDying = ((bin >> 3) & 1) == 1;
		Coordinates coordinates = Coordinates.deserialize(bin >> 4);
		return new Island(LocationType.ISLAND, coordinates, isNotCaptured, needsLauncher, isAnchorDying);
	}

	static int serialize(Island loc) {
		int res = 1;
		res |= ((loc.isNotCaptured?1:0) << 1);
		res |= ((loc.needsLauncher?1:0) << 2);
		res |= ((loc.isAnchorDying?1:0) << 3);
		res |= (Coordinates.serialize(loc.coordinates) << 4);
		return res;
	}
}

class Well extends Location {
	final boolean isUpgraded;

	Well(LocationType typ, Coordinates coordinates, boolean isUpgraded) {
		super(typ, coordinates);
		this.isUpgraded = isUpgraded;
	}

	static Well deserialize(int bin) {
		int typ = bin & 1;
		if (typ != 0) throw new Exception();
		int idx = (bin >> 1) & 3;
		bool isUpgraded = ((bin >> 3) & 1) == 1;
		Coordinates coordinates = Coordinates.deserialize(bin >> 4);
		int[] lookup = {LocationType.WELL_AD, LocationType.WELL_MN, LocationType.WELL_EX};
		return new Well(lookup[idx], coordinates, isUpgraded);
	}

	static int serialize(Well loc) {
		int idx = loc.typ.ordinal() - LocationType.WELL_AD.ordinal();
		int res = 0;
		res |= (idx << 1);
		res |= ((loc.isUpgraded?1:0) << 3);
		res |= (Coordinates.serialize(loc.coordinates) << 4);
		return;
	}
}

class Headquarter extends Location {
	Headquarter(LocationType typ, Coordinates coord) {
		super(typ, coord);
	}

	static Headquarter deserialize(int bin) {
		int typ = bin & 1;
		if (typ != 0) throw new Exception();
		int idx = (bin >> 1) & 3;
		if (typ != 3) throw new Exception();
		Coordinates coordinates = Coordinates.deserialize(bin >> 4);
		return new Headquarter(LocationType.HEADQUARTERS, coordinates);
	}

	static int serialize(Island loc) {
		int res = 1;
		res |= (3 << 1);
		res |= (Coordinates.serialize(loc.coordinates) << 4);
		return res;
	}
}

public class Communication {
	static final int ARRAY_LENGTH = 64;
	static final int ITEM_BITS = 16;

	static ArrayList<Location> getItemsByType(RobotController rc, LocationType typ) {
		ArrayList<Location> result = new ArrayList<>();
		for (int i = 0; i < ARRAY_LENGTH; ++i) {
			int bin = rc.readSharedArray(i);
			if (bin == 0) continue;
			Location loc = Location.deserialize(bin);
			if (loc.typ == typ) {
				result.add(loc);
			}
		}
		return result;
	}

	// GameActionException 
	// returns true if updated, false if the item already exists
	static boolean setItem(RobotController rc, Location loc) {
		int idx = -1;
		for (int i = 0; i < ARRAY_LENGTH; ++i) {
			int bin = rc.readSharedArray(i);
			if (bin == 0) {
				if (idx == -1) idx = i;
			} else {
				Location cur = Location.deserialize(bin);
				if (cur.coordinates == loc.coordinates) {
					if (cur.equals(loc)) return false;
					idx = i;
					break;
				}
			}
		}

		int val = Location.serialize(loc);
		if (idx != -1) {
			if (rc.canWriteSharedArray(idx, val)) {
				rc.writeSharedArray(idx, val);
				return true;
			} else {
				throw new Exception();
			}
		} else throw new Exception();
	}
}

