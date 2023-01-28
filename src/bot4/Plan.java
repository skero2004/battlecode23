package bot4;

import battlecode.common.*;

import bot4.util.*;

public class Plan {

	public class Mission {

		public int startTurn = 0;
		public int numLauncher = 0;
		public int numCarrier = 0;
		public int numDestabilizer = 0;
		public int numBooster = 0;
		public int numAmplifier = 0;
		public MissionName missionName = null;

		public Mission(MissionName m) {
			this(false, m);
		}

		public Mission(boolean isAdv, MissionName m) {
			missionName = m;

			// Setup for different missions
			switch (m) {

				case START_UP:
					numLauncher = 2;
					numCarrier = 2;
					break;

				case PROTECT_HQ:
					numLauncher = 4;
					numDestabilizer = isAdv ? 1 : 0;
					numBooster = isAdv ? 1 : 0;
					break;

				case PROTECT_ISLAND:
					numLauncher = 4;
					numDestabilizer = isAdv ? 1 : 0;
					numBooster = isAdv ? 1 : 0;
					break;

				case ATTACK_HQ:
					numLauncher = 5;
					numDestabilizer = isAdv ? 1 : 0;
					numBooster = isAdv ? 1 : 0;
					break;

				case CAPTURE_ISLAND:
					numLauncher = 5;
					numDestabilizer = isAdv ? 1 : 0;
					numBooster = isAdv ? 1 : 0;
					break;

				case AMBUSH:
					numLauncher = 3;
					numDestabilizer = isAdv ? 1 : 0;
					break;

				case CREATE_ELIXIR_WELL:
					numLauncher = 3;
					numCarrier = 3;
					numDestabilizer = isAdv ? 1 : 0;
					break;

				case SPEED_UP_HQ:
					numBooster = 1;
					break;

				case UPGRADE_ADAMANTIUM_WELL:
					numLauncher = 3;
					numCarrier = 3;
					numDestabilizer = isAdv ? 1 : 0;
					break;

				case UPGRADE_MANA_WELL:
					numLauncher = 3;
					numCarrier = 3;
					numDestabilizer = isAdv ? 1 : 0;
					break;

				case COLLECT_ADAMANTIUM:
					numLauncher = 2;
					numCarrier = 2;
					break;

				case COLLECT_MANA:
					numLauncher = 2;
					numCarrier = 2;
					break;

				case COLLECT_ELIXIR:
					numLauncher = 2;
					numCarrier = 2;
					break;

				case SCOUTING:
					numCarrier = 2;
					numAmplifier = 1;
					break;

			}

		}

	}

	public Mission chooseMission(RobotController rc) throws GameActionException {

		Team OPPONENT = rc.getTeam().opponent();

		if (rc.getType() != RobotType.HEADQUARTERS)
			throw new IllegalArgumentException("RobotType must be Headquarters to choose a mission!");

		// Variables to choose mission
		boolean isAdv = false;

		// Find number of enemy launchers near HQ
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, OPPONENT);
		int numEnemyLaunchers = 0;
		for (RobotInfo enemy : enemies)
			if (enemy.getType() == RobotType.LAUNCHER)
				numEnemyLaunchers++;

		// Logic to choose mission
		Mission chosenMission;
		if (RobotPlayer.turnCount < 50)
			chosenMission = new Mission(isAdv, MissionName.START_UP);
		else if (numEnemyLaunchers > 3)
			chosenMission = new Mission(isAdv, MissionName.PROTECT_HQ);
		// Something with protect island?
		// Something with attack HQ?
		// Something with capture island?
		// Something with ambush?
		else if (RobotPlayer.turnCount < 200)
			chosenMission = new Mission(isAdv, MissionName.SCOUTING);
		else
			chosenMission = new Mission(isAdv, MissionName.COLLECT_ADAMANTIUM);

		// Check to see if desired mission is able to run
		int amountMn = rc.getResourceAmount(ResourceType.MANA);
		int amountAd = rc.getResourceAmount(ResourceType.ADAMANTIUM);
		int amountEx = rc.getResourceAmount(ResourceType.ELIXIR);

		return chosenMission;

	}

}
