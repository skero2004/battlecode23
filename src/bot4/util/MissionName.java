package bot4.util;

public enum MissionName {

	PROTECT_HQ(0),
	ATTACK_HQ(1),
	PROTECT_ISLAND(2),
	CAPTURE_ISLAND(3),
	AMBUSH(4),
	CREATE_ELIXIR_WELL(5),
	UPGRADE_ADAMANTIUM_WELL(6),
	UPGRADE_MANA_WELL(7),
	COLLECT_ADAMANTIUM(8),
	COLLECT_MANA(9),
	COLLECT_ELIXIR(10),

	SCOUTING(11),

	CREATE_ANCHOR(12);

	public final int id;

	private MissionName(int id) {
		this.id = id;
	}

}
