package gui;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import core.ActionType;
import core.Playable;
import core.Player;

/**
 * Game panel
 */
public class GamePanel extends AbsoluteJPanel implements ActionListener {
	/**
	 * Java UID
	 */
	private static final long serialVersionUID = -8860204251354754377L;

	/**
	 * Panel components
	 */
	private Vector<Card> cards = new Vector<Card>();
	private JButton nextRound = new JButton("Jouer !");

	/**
	 * Player field
	 */
	private Field selfField = null;

	/**
	 * Game information
	 */
	private JLabel actualSeason  = new JLabel("Saison actuelle : Printemps");
	private JLabel totalBigRocks = new JLabel("Score total : 0");

	/**
	 * Player reference
	 */
	private Player player;

	/**
	 * Action management
	 */
	public boolean lockingCards   = false;
	public boolean choosingTarget = false;
	public Field   targetField    = null;

	/**
	 * Parent window
	 */
	private MainWindow parentWindow;
	private Playable game;

	/**
	 * Create the panel
	 */
	public GamePanel(MainWindow parentWindow) {
		this.parentWindow = parentWindow;
		this.game         = parentWindow.getGame();

		// Check for victory
		if (!this.game.isRunning()) {
			Vector<Player> scores = new Vector<Player>(game.getPlayers());
			Collections.sort(scores);
			Collections.reverse(scores);

			String message = "Rankings:\n";

			System.out.println("Rankings:");
			for (int i = 0; i < game.getPlayerNumber(); i++) {
				core.Field field = scores.get(i).getField();

				message += "    Player " + (i+1) + ". Field: "+ field.getBigRockSum() +
						" big rocks; " + field.getSmallRockSum() + " small rocks;";
			}

			JOptionPane.showMessageDialog(this, message, "Partie terminée", JOptionPane.INFORMATION_MESSAGE);

			return;
		}

		// Clone, else removing the actual player would change the vector
		@SuppressWarnings("unchecked")
		Vector<Player> players = (Vector<Player>) this.game.getPlayers().clone();
		players.remove(this.game.getCurrentPlayer());

		this.setPreferredSize(this.parentWindow.getSize());

		System.out.println(this.game.getPlayers().size());
		this.player = this.game.getCurrentPlayer();

		for (int i = 0; i < this.player.getCards().size(); i++) {
			Card c = new Card(this, this.player.getCards().get(i).getType());
			this.cards.add(c);
		}

		this.selfField = new Field(this, this.player, "Votre terrain");

		int startX = 10;
		int stepX  = 100;
		for (int i = 0; i < players.size(); i++) {
			String playerN    = String.valueOf(players.get(i).getNumber() + 1);
			Field playerField = new Field(this, players.get(i), "Joueur " + playerN);
			this.addAbsolute(playerField, startX + i * stepX, 260);
		}

		startX = 10;
		stepX  = 110;
		for (int i = 0; i < this.cards.size(); i++) {
			this.addAbsolute(this.cards.get(i), startX + i * stepX, 30);
		}

		this.addAbsolute(this.actualSeason, 10, 10);
		this.addAbsolute(this.selfField, 10, 140);
		this.addAbsolute(this.totalBigRocks, 350, 10);
		this.addAbsolute(nextRound, 10, 370);

		this.nextRound.addActionListener(this);
	}

	/**
	 * Get the game reference
	 * @return The game
	 */
	public Playable getGame() {
		return this.game;
	}

	/**
	 * Disable cards and fields
	 */
	public void lockCards() {
		this.lockingCards = true;
		revalidate();
		repaint();
	}

	/**
	 * Disable cards and self field
	 */
	public void chooseTarget() {
		this.choosingTarget = true;
		this.lockCards();
	}

	/**
	 * Get all cards
	 * @return All four or less cards
	 */
	public Card[] getCards() {
		Card[] arr = new Card[this.cards.size()];
		for (int i = 0; i < this.cards.size(); i++) {
			arr[i] = (this.cards.get(i));
		}
		return arr;
	}

	/**
	 * Draw the panel
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
	    RenderingHints rh = new RenderingHints(
	             RenderingHints.KEY_TEXT_ANTIALIASING,
	             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2.setRenderingHints(rh);

		g2.setColor(Color.black);
		g2.drawLine(445, 25, 445, 120);

	    g2.drawString("Pas de cartes alliées en partie rapide", 460, 70);
	}


	/**
	 * Choose a card
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Give back focus to window (to re handle escapes)
		this.parentWindow.requestFocus();

		Card selectedCard          = null;

		core.Card selectedCoreCard = null;
		Player target              = null;
		ActionType action          = null;

		for (int i = 0; i < this.cards.size(); i++) {
			if (this.cards.get(i).isSelected()) {
				selectedCard = this.cards.get(i);
			}
		}

		if (selectedCard == null) {
			JOptionPane.showMessageDialog(this, "Veuillez choisir une carte", "Attention", JOptionPane.WARNING_MESSAGE);
			return;
		}

		selectedCoreCard = selectedCard.getCard();
		action           = selectedCard.getActionType();

		if (action == ActionType.HOBGOBLIN && this.targetField == null) {
			JOptionPane.showMessageDialog(this, "Veuillez choisir un adversaire", "Attention", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (action == ActionType.HOBGOBLIN) {
			target = this.targetField.getPlayer();
		}

		this.game.nextTurn(selectedCoreCard, action, target);

		parentWindow.switchPanel("GamePanel", 710, 450);
		parentWindow.revalidate();
		parentWindow.repaint();
	}
}
