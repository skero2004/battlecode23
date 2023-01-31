package bot5;

import battlecode.common.*;
import bot5.Map.Symmetry;
import bot5.util.*;

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

		rc.setIndicatorString(myMission.target.toString() + " " + Map.symmetriesPossible.toString());

		if (myMission.missionName == MissionName.FIND_SYMMETRY) {

			if (myMission.target.equals(new MapLocation(61, 61))) {

				Scout.move(rc);
				WellInfo[] wells = rc.senseNearbyWells();
			
				if (wells.length > 0) {

					memoryWell = wells[0];
					int x = wells[0].getMapLocation().x;
					int y = wells[0].getMapLocation().y;

					if (myMode == Symmetry.VERTICAL)
						myMission.target = new MapLocation(x, Map.HEIGHT - y - 1);
					else if (myMode == Symmetry.HORIZONTAL)
						myMission.target = new MapLocation(Map.WIDTH - x - 1, y);
					else
						myMission.target = new MapLocation(Map.WIDTH - x - 1, Map.HEIGHT - y - 1);

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

		} else Scout.move(rc);

	}
}
