package bot4;

import battlecode.common.*;

import bot4.util.*;

public class Plan {

	private static final int LAUNCHER_MN = 45;
	private static final int CARRIER_AD = 50;
	private static final int AMPLIFIER_AD = 30;
	private static final int AMPLIFIER_MN = 15;

	// Store previous mission to prevent same mission from running all the time
	private static MissionName lastMission = MissionName.SCOUTING;

	public static class Mission {

		public int startTurn = 0;
		public int numLauncher = 0;
		public int numCarrier = 0;
		public int numAmplifier = 0;
		public MissionName missionName = null;
		public MapLocation target = null;

		public Mission(MissionName m) {

			startTurn = RobotPlayer.turnCount;
			missionName = m;

			// Setup for different missions
			switch (m) {

				case PROTECT_HQ:
					numLauncher = 4;
					break;

				case PROTECT_ISLAND:
					numLauncher = 4;
					break;

				case ATTACK_HQ:
					numLauncher = 5;
					break;

				case CAPTURE_ISLAND:
					numLauncher = 5;
					break;

				case AMBUSH:
					numLauncher = 3;
					break;

				case CREATE_ELIXIR_WELL:
					numLauncher = 3;
					numCarrier = 3;
					break;

				case UPGRADE_ADAMANTIUM_WELL:
					numLauncher = 3;
					numCarrier = 3;
					break;

				case UPGRADE_MANA_WELL:
					numLauncher = 3;
					numCarrier = 3;
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

				case CREATE_ANCHOR:
					break;

			}

		}

	}

	public static Mission chooseMission(RobotController rc) throws GameActionException {

		Team OPPONENT = rc.getTeam().opponent();

		if (rc.getType() != RobotType.HEADQUARTERS)
			throw new IllegalArgumentException("RobotType must be Headquarters to choose a mission!");

		// Find number of enemy launchers near HQ
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, OPPONENT);
		int numEnemyLaunchers = 0;
		for (RobotInfo enemy : enemies)
			if (enemy.getType() == RobotType.LAUNCHER)
				numEnemyLaunchers++;

		// Logic to choose mission
		Mission chosenMission;
		if (RobotPlayer.turnCount < 100 && lastMission != MissionName.COLLECT_ADAMANTIUM)
			chosenMission = new Mission(MissionName.COLLECT_ADAMANTIUM);
		else if (RobotPlayer.turnCount < 100 && lastMission != MissionName.COLLECT_MANA)
			chosenMission = new Mission(MissionName.COLLECT_MANA);

		else if (numEnemyLaunchers > 3)
			chosenMission = new Mission(MissionName.PROTECT_HQ);
		//else if (RobotPlayer.turnCount > 50 && lastMission != MissionName.PROTECT_ISLAND)
			//chosenMission = new Mission(MissionName.PROTECT_ISLAND);
		// Something with attack HQ?
		else if (RobotPlayer.turnCount > 50 && lastMission != MissionName.CAPTURE_ISLAND)
			chosenMission = new Mission(MissionName.CAPTURE_ISLAND);
		// Something with ambush?
		else if (RobotPlayer.turnCount < 200 && lastMission != MissionName.SCOUTING)
			chosenMission = new Mission(MissionName.SCOUTING);
		else
			chosenMission = new Mission(MissionName.COLLECT_ADAMANTIUM);

		// Variables for amount of mana/adamantium
		int amountMn = rc.getResourceAmount(ResourceType.MANA);
		int amountAd = rc.getResourceAmount(ResourceType.ADAMANTIUM);

		// If there is not enough resources, get resources
		if (amountAd < chosenMission.numCarrier * CARRIER_AD ||
				amountAd < chosenMission.numAmplifier * AMPLIFIER_AD)
			chosenMission = new Mission(MissionName.COLLECT_ADAMANTIUM);
		else if (amountMn < chosenMission.numLauncher * LAUNCHER_MN ||
				amountMn < chosenMission.numAmplifier * AMPLIFIER_MN)
			chosenMission = new Mission(MissionName.COLLECT_MANA);

		lastMission = chosenMission.missionName;
		return chosenMission;

	}

}
