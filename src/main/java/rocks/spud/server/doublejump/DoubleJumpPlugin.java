package rocks.spud.server.doublejump;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a simple plugin for allowing players to double jump.
 * @author {@literal Johannes Donath <johannesd@torchmind.com>}
 */
public class DoubleJumpPlugin extends JavaPlugin implements Listener {

	/**
	 * Stores a list of players who used their double jump already.
	 */
	private List<Player> playerList = new ArrayList<> ();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEnable () {
		super.onEnable ();

		this.getServer ().getPluginManager ().registerEvents (this, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisable () {
		super.onDisable ();

		this.playerList.clear ();
	}

	/**
	 * Handles player joins.
	 * @param event The event.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerJoin (PlayerJoinEvent event) {
		event.getPlayer ().setAllowFlight (true);
	}

	/**
	 * Handles player disconnects.
	 * @param event The event.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerQuit (PlayerQuitEvent event) {
		this.playerList.remove (event.getPlayer ());
	}

	/**
	 * Handles player damage.
	 * @param event The event.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerDamage (EntityDamageEvent event) {
		if (event.getEntityType () != EntityType.PLAYER) return;
		if (event.getCause () != EntityDamageEvent.DamageCause.FALL) return;
		event.setCancelled (true);
	}

	/**
	 * Handles player movement.
	 * @param event The event.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerMove (PlayerMoveEvent event) {
		if (event.getPlayer ().getAllowFlight ()) return;

		if (this.playerList.contains (event.getPlayer ())) {
			Block block = event.getPlayer ().getLocation ().subtract (0, 1, 0).getBlock ();

			if (block.getType ().isSolid ()) {
				this.playerList.remove (event.getPlayer ());
				event.getPlayer ().setAllowFlight (true);
			}
		}
	}

	/**
	 * Handles player flight.
	 * @param event The event.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerToggleFlight (PlayerToggleFlightEvent event) {
		if (event.getPlayer ().getGameMode () == GameMode.CREATIVE || event.getPlayer ().getGameMode () == GameMode.SPECTATOR) return;

		event.setCancelled (true);
		event.getPlayer ().setAllowFlight (false);
		this.playerList.add (event.getPlayer ());

		event.getPlayer ().setVelocity (event.getPlayer ().getLocation ().getDirection ().multiply (1.6d).setY (1.0d));
	}
}
