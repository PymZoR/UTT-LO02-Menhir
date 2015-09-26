package core;


import java.util.Vector;

/**
 * Base Strategy class
 */
abstract class Strategy {
	/**
	 * The actual player
	 */
	Player self;

	/**
	 * All the players
	 */
	Vector<Player> allPlayers;

	/**
	 * Chosen player to attack
	 */
	Player target;

	/**
	 * Strategy choices
	 */
	ActionType action;
	CardType card;
	CardType alliedCard;

	/**
	 * Create a new strategy for a given player
	 * @param self       The actual player
	 * @param allPlayers All the players
	 */
	public Strategy(Player self, Vector<Player> allPlayers) {
		this.self = self;
		this.allPlayers = allPlayers;

		this.allPlayers.removeElement(self);

		this.makeChoice();
	}

	/**
	 * Get the chosen action
	 * @return The action
	 */
	public ActionType getAction() {
		return this.action;
	}

	/**
	 * Get the optional played allied card
	 * @return The allied card
	 */
	public CardType getAlliedCard() {
		return this.alliedCard;
	}

	/**
	 * Get the chosen card
	 * @return The card id
	 */
	public CardType getCard() {
		return this.card;
	}

	/**
	 * Get the optional target
	 * @return The target
	 */
	public Player getTarget() {
		return this.target;
	}

	/**
	 * Sets the three variables to make the choice given the environment
	 */
	abstract public void makeChoice();
}
