/*
 * MIT License
 *
 * Copyright (c) 2023 Benjamin Selig
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.luminacollection.nameshift.paper.listeners;

import dev.jorel.commandapi.CommandAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luminacollection.nameshift.paper.NameshiftPlugin;
import net.luminacollection.nameshift.paper.profiles.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NameshiftListeners implements Listener
{
	private final ProfileManager pm = ProfileManager.instance();
	private static NameshiftListeners instance;
	public static NameshiftListeners instance()
	{
		if (instance == null)
			instance = new NameshiftListeners();
		return instance;
	}
	private NameshiftListeners() {
		Bukkit.getPluginManager().registerEvents(this, NameshiftPlugin.instance());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		var player = event.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(NameshiftPlugin.instance(), () -> pm.refreshName(player), 10L);
		Bukkit.getOnlinePlayers().forEach(CommandAPI::updateRequirements);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		var player = event.getPlayer();
		//event.quitMessage(pm.updateComponent(event.quitMessage(), player));
		pm.removeFromList(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(NameshiftPlugin.instance(), () -> Bukkit.getOnlinePlayers().forEach(CommandAPI::updateRequirements), 20L);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.deathMessage(pm.updateComponent(event.deathMessage(), event.getPlayer()));
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBookEdit(PlayerEditBookEvent event)
	{
		if (!event.isSigning()) return;
		var player = event.getPlayer();
		var meta = event.getNewBookMeta();
		var mm = MiniMessage.miniMessage();
		event.setNewBookMeta(meta.author(mm.deserialize(mm.stripTags(pm.displayName(player)))));
	}
}
