package bot4;

import java.util.Arrays;

import battlecode.common.*;

import bot4.util.*;

public class Plan {

	private static final int LAUNCHER_MN = 45;
	private static final int CARRIER_AD = 50;
	private static final int AMPLIFIER_AD = 30;
	private static final int AMPLIFIER_MN = 15;

	// These change to true in this file, but false in HQ (once mission is
	// completed)
	public static boolean[] isMissionActive = new boolean[MissionName.values().length];

	public static class Mission {

		public int startTurn = 0;
		public int numLauncher = 0;
		public int numCarrier = 0;
		public int numAmplifier = 0;
		public MissionName missionName = null;
		public MapLocation target = null;

		public Mission(MissionName m) {

			startTurn = Robot.turnCount;
			missionName = m;

			// Setup for different missions
			switch (m) {

				case PROTECT_HQ:
					numLauncher = 3;
					break;

				case PROTECT_ISLAND:
					numLauncher = 3;
					break;

				case ATTACK_HQ:
					numLauncher = 3;
					break;

				case CAPTURE_ISLAND:
					numLauncher = 3;
					numCarrier = 1;
					break;

				case AMBUSH:
					numLauncher = 3;
					break;

				case ATTACK_ISLAND:
					numLauncher = 3;

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
					numLauncher = 1;
					numCarrier = 2;
					break;

				case COLLECT_MANA:
					numLauncher = 1;
					numCarrier = 2;
					break;

				case COLLECT_ELIXIR:
					numLauncher = 2;
					numCarrier = 2;
					break;

				case SCOUTING:
					numLauncher = 2;
					// numAmplifier = 1;
					break;

				case SEND_AMPLIFIER:
					numAmplifier = 1;

				case CREATE_ANCHOR:
					break;

			}

		}

		boolean isValidCollectMission() {
			return this.missionName == MissionName.COLLECT_ADAMANTIUM
					|| this.missionName == MissionName.COLLECT_MANA;
		}

		ResourceType getCollectResourceType() {
			switch (this.missionName) {
				case COLLECT_ADAMANTIUM:
					return ResourceType.ADAMANTIUM;
				case COLLECT_MANA:
					return ResourceType.MANA;
				default:
					throw new IllegalArgumentException("Mission not implemented!");
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

		// Logic to choose mission (default is loop of scout -> adamantium -> mana)
		if (numEnemyLaunchers > 3)
			isMissionActive[MissionName.PROTECT_HQ.ordinal()] = true;
		// if (rc.getRoundNum() % 100 == 0) isAmplifier = true;
		// if (rc.getRoundNum() % 150 == 0) isAttackIsland = true;
		if (Headquarters.missionCount >= 200 && Headquarters.missionCount % 99 == 0
				&& rc.getNumAnchors(Anchor.STANDARD) < 4)
			isMissionActive[MissionName.CREATE_ANCHOR.ordinal()] = true;
		if (rc.getNumAnchors(Anchor.STANDARD) > 0)
			isMissionActive[MissionName.CAPTURE_ISLAND.ordinal()] = true;
		if (Headquarters.missionCount >= 50 && Headquarters.missionCount % 20 == 0)
			isMissionActive[MissionName.SEND_AMPLIFIER.ordinal()] = true;
		// TODO: Protect island???

		// Return the correct mission. Defaults to rotation between scouting, collect
		// adamantium, and collect mana.
		for (MissionName m : MissionName.values()) {

			if (isMissionActive[m.ordinal()])
				return new Mission(m);

		}
		switch (Headquarters.missionCount % 3) {
			case 0:
				return new Mission(MissionName.SCOUTING);
			case 1:
				return new Mission(MissionName.COLLECT_ADAMANTIUM);
			default:
				return new Mission(MissionName.COLLECT_MANA);
		}

	}

}
