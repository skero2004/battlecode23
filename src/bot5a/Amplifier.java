package bot5a;

import battlecode.common.*;
import bot5a.Map.Symmetry;
import bot5a.util.*;

public class Amplifier extends Robot {

	private static boolean[][] vis = new boolean[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
	private static WellInfo memoryWell;

	private boolean init = false;
	private Symmetry myMode;

	void execute(RobotController rc) throws GameActionException {

		if (!init) {

			// 0 = check vertical, 1 = check rotational, 2 = check horz
			if (rc.getID() % 3 == 0)
				myMode = Symmetry.VERTICAL;
			else if (rc.getID() % 3 == 1)
				myMode = Symmetry.HORIZONTAL;
			else
				myMode = Symmetry.ROTATIONAL;
			init = true;

		}

		if (memoryWell != null)
			rc.setIndicatorString(myMission.target + " " + memoryWell.getMapLocation() + " "
					+ Map.symmetriesPossible.toString());

		if (myMission.missionName == MissionName.FIND_SYMMETRY) {

			if (myMission.target.equals(new MapLocation(61, 61))) {

				move(rc, new MapLocation(Map.WIDTH / 2, Map.HEIGHT / 2));

				WellInfo[] wells = rc.senseNearbyWells();

				if (wells.length > 0) {
					memoryWell = wells[0];
					myMission.target = Map.reflect(wells[0].getMapLocation(), myMode);
				}

			} else {
				move(rc);
				if (rc.canSenseLocation(myMission.target)) {

					WellInfo targetWell = rc.senseWell(myMission.target);
					if (targetWell == null)
						Map.symmetriesPossible.put(myMode, false);
					else if (targetWell.getResourceType() != memoryWell.getResourceType())
						Map.symmetriesPossible.put(myMode, false);
				}
			}

			int m = rc.readSharedArray(63);
			m |= (Map.symmetriesPossible.get(Symmetry.VERTICAL) ? 0 : 1) << 0;
			m |= (Map.symmetriesPossible.get(Symmetry.HORIZONTAL) ? 0 : 1) << 1;
			m |= (Map.symmetriesPossible.get(Symmetry.ROTATIONAL) ? 0 : 1) << 2;
			rc.writeSharedArray(63, m);
		} else
			Scout.move(rc);

	}
}
