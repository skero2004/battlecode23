package bot4.util;

public enum MissionName {

	PROTECT_HQ(0),
	ATTACK_HQ(1),
	PROTECT_ISLAND(2),
	CAPTURE_ISLAND(3),
	ATTACK_ISLAND(4),
	AMBUSH(5),
	CREATE_ELIXIR_WELL(6),
	UPGRADE_ADAMANTIUM_WELL(7),
	UPGRADE_MANA_WELL(8),
	COLLECT_ADAMANTIUM(9),
	COLLECT_MANA(10),
	COLLECT_ELIXIR(11),

	SCOUTING(12),
	SEND_AMPLIFIER(13),

	CREATE_ANCHOR(14);

	public final int id;

	private MissionName(int id) {
		this.id = id;
	}

}
