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
import net.luminacollection.nameshift.paper.NameshiftPlugin;
import net.luminacollection.nameshift.paper.profiles.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.sayandev.sayanvanish.bukkit.api.event.BukkitUserUnVanishEvent;
import org.sayandev.sayanvanish.bukkit.api.event.BukkitUserVanishEvent;

public class SayanVanishListeners implements Listener
{
	private final ProfileManager pm = ProfileManager.instance();
	private static SayanVanishListeners instance;
	public static SayanVanishListeners instance()
	{
		if (instance == null)
			instance = new SayanVanishListeners();
		return instance;
	}
	private SayanVanishListeners()
	{
		Bukkit.getPluginManager().registerEvents(this, NameshiftPlugin.instance());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPlayerShow(BukkitUserUnVanishEvent event)
	{
		var player = event.getUser().player();
		assert player != null;
		pm.refreshName(player);
		Bukkit.getOnlinePlayers().forEach(CommandAPI::updateRequirements);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPlayerHide(BukkitUserVanishEvent event)
	{
		var player = event.getUser().player();
		assert player != null;
		pm.refreshName(player);
		Bukkit.getOnlinePlayers().forEach(CommandAPI::updateRequirements);
	}

}
