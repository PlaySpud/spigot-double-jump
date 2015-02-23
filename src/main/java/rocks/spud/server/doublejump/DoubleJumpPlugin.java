/*
 * Copyright 2015 Johannes Donath <johannesd@torchmind.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rocks.spud.server.doublejump;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Provides a simple plugin for allowing players to double jump.
 * @author {@literal Johannes Donath <johannesd@torchmind.com>}
 */
public class DoubleJumpPlugin extends JavaPlugin implements Listener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEnable () {
		super.onEnable ();

		this.getServer ().getPluginManager ().registerEvents (this, this);
	}

	/**
	 * Performs an update on the player flight state.
	 * @param player The player.
	 */
	private void groundUpdate (Player player) {
		Location location = player.getPlayer ().getLocation ();
		location = location.subtract (0, 1, 0);

		Block block = location.getBlock ();
		if (!block.getType ().isSolid ()) return;

		player.setAllowFlight (true);
	}

	/**
	 * Handles player joins.
	 * @param event The event.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerJoin (PlayerJoinEvent event) {
		this.groundUpdate (event.getPlayer ());
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
		this.groundUpdate (event.getPlayer ());
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
		event.getPlayer ().setVelocity (event.getPlayer ().getLocation ().getDirection ().multiply (1.6d).setY (1.0d));
	}
}
