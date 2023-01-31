package bot5a;

import battlecode.common.*;
import bot5a.Map.Symmetry;
import bot5a.util.*;

public class Amplifier extends Robot {

	void execute(RobotController rc) throws GameActionException {

		if (!followLeader(rc)) {
			if (rc.getLocation().distanceSquaredTo(myMission.target) > 8)
				move(rc);
			else
				Scout.move(rc);
		}

	}

	MapLocation[] last5;
	int cooldown = 0;
	private boolean followLeader(RobotController rc) throws GameActionException {
		RobotInfo leader = null;
		for (RobotInfo r : rc.senseNearbyRobots()) {
			if (r.getTeam() == rc.getTeam() && r.getType() == RobotType.LAUNCHER
					&& (leader == null || leader.getID() > r.getID()))
				leader = r;
		}
		if (leader == null || leader.getID() > rc.getID())
			return false;
		if (rc.getLocation().distanceSquaredTo(leader.getLocation()) > 12)
			stepTowards(rc, leader.getLocation());
		else
			Randomize.move(rc);
		return true;
	}

}
