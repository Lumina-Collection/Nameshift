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

package net.luminacollection.nameshift.paper.profiles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.luminacollection.nameshift.common.configuration.Settings;
import net.luminacollection.nameshift.common.hooks.LuckPermsHook;
import net.luminacollection.nameshift.paper.NameshiftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.intellij.lang.annotations.Subst;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileManager
{
	private final Map<String, Player> names = new HashMap<>();
	private final Map<Player, String> customNames = new HashMap<>();
	private MiniMessage miniMessage;
	private final NamespacedKey key;
	private static ProfileManager instance;
	public static ProfileManager instance()
	{
		if (instance == null) instance = new ProfileManager();
		return instance;
	}
	private ProfileManager() {
		key = new NamespacedKey(NameshiftPlugin.instance(), "name");
		reload();
	}
	
	public void reload()
	{
		var allowedTags = Settings.LIMITS_ALLOWED_TAGS.get();
		var tagBuilder = TagResolver.builder();
		tagBuilder.resolver(StandardTags.reset());
		if (allowedTags.contains("color")) tagBuilder.resolver(StandardTags.color());
		if (allowedTags.contains("gradient")) tagBuilder.resolver(StandardTags.gradient());
		if (allowedTags.contains("hover")) tagBuilder.resolver(StandardTags.hoverEvent());
		if (allowedTags.contains("insertion")) tagBuilder.resolver(StandardTags.insertion());
		Arrays.stream(TextDecoration.values()).toList().forEach(decoration -> {
			if (allowedTags.contains(decoration.toString().toLowerCase()))
				tagBuilder.resolver(StandardTags.decorations(decoration));
		});
		if (allowedTags.contains("font")) tagBuilder.resolver(StandardTags.font());
		if (allowedTags.contains("click")) tagBuilder.resolver(StandardTags.clickEvent());
		if (allowedTags.contains("key")) tagBuilder.resolver(StandardTags.keybind());
		if (allowedTags.contains("lang")) tagBuilder.resolver(StandardTags.translatable());
		if (allowedTags.contains("score")) tagBuilder.resolver(StandardTags.score());
		if (allowedTags.contains("selector")) tagBuilder.resolver(StandardTags.selector());
		if (allowedTags.contains("nbt")) tagBuilder.resolver(StandardTags.nbt());
		if (allowedTags.contains("newline")) tagBuilder.resolver(StandardTags.newline());
		if (allowedTags.contains("rainbow")) tagBuilder.resolver(StandardTags.rainbow());
		if (allowedTags.contains("transition")) tagBuilder.resolver(StandardTags.transition());
		var customColors = Settings.CUSTOM_COLORS.get();
		for (var color : customColors)
		{
			var split = color.toString().split(":");
			if (split.length <= 1) continue;
			@Subst("test_var") var name = split[0];
			var c1 = split[1];
			var placeholder = Placeholder.styling(name, TextColor.fromHexString(c1));
			if (split.length > 2)
			{
				var c2 = split[2];
				placeholder = Placeholder.parsed(name, "<gradient:" + c1 + ":" + c2 + ">");
			}
			tagBuilder.resolver(placeholder);
		}
		miniMessage = MiniMessage.builder().tags(tagBuilder.build()).build();
	}
	
	public Component updateComponent(Component component, Player player)
	{
		if (!hasContainerName(player)) return component;
		var name = miniMessage.serialize(player.displayName());
		var message = MiniMessage.miniMessage().serialize(component);
		message = message.replace(player.getName() + '"', name + '"');
		message = message.replace(player.getName() + "'", name + "'");
		return MiniMessage.miniMessage().deserialize(message);
	}
	
	public void removeFromList(Player player)
	{
		customNames.remove(player);
	}
	
	private void removeFromContainer(Player player)
	{
		var container = player.getPersistentDataContainer();
		container.remove(key);
	}
	
	public String[] customNames()
	{
		var tempMap = new HashMap<Player, String>();
		customNames.forEach(((player, name) ->
		{
			if (!isHidden(player)) tempMap.put(player, name);
		}));
		return tempMap.values().toArray(new String[0]);
	}
	
	public Player getPlayerFromList(String name)
	{
		for (var entry : customNames.entrySet())
			if (entry.getValue().equals(name)) return entry.getKey();
		return null;
	}
	
	public void refresh()
	{
		customNames.clear();
		Bukkit.getOnlinePlayers().forEach(this::refreshName);
	}
	
	public void refreshName(Player player)
	{
		NameshiftPlugin.instance().debug("Refreshing name for " + player.getName() + ".");
		//resetName(player, false);
		if (!hasContainerName(player))
		{
			NameshiftPlugin.instance().debug("No container name for " + player.getName() + " found.");
			return;
		}
		setName(player, getContainerName(player));
	}
	
	public void resetName(Player player, boolean clearContainer)
	{
		NameshiftPlugin.instance().debug("Resetting name for " + player.getName() + ".");
		player.displayName(player.name());
		if (Settings.PROXY_MODE.get())
		{
			NameshiftPlugin.instance().debug("[resetName] Updating LuckPerms for " + player.getName() + ".");
			LuckPermsHook.instance().update(player.getUniqueId(), player.getName());
		}
		player.playerListName(player.name());
		player.customName(player.name());
		removeFromList(player);
		TeamManager.instance().removePlayer(player);
		if (clearContainer) removeFromContainer(player);
	}
	
	public void setName(Player player, String name)
	{
		NameshiftPlugin.instance().debug("Setting name for " + player.getName() + " to " + name + ".");
		if (name.equals(player.getName()))
		{
			resetName(player, true);
			return;
		}
		player.displayName(miniMessage.deserialize(name));
		player.customName(miniMessage.deserialize(name));
		var tabName = (isHidden(player) ? "<grey><bold>⏼ <reset>" : " ") + name;
		if (Settings.PROXY_MODE.get())
		{
			NameshiftPlugin.instance().debug("[setName] Updating LuckPerms for " + player.getName() + ".");
			LuckPermsHook.instance().update(player.getUniqueId(), tabName);
		}
		player.playerListName(miniMessage.deserialize(tabName));
		customNames.put(player, miniMessage.stripTags(name));
		TeamManager.instance().addPlayer(player);
		setContainerName(player, name);
	}
	
	private void setContainerName(Player player, String name)
	{
		var container = player.getPersistentDataContainer();
		container.set(key, PersistentDataType.STRING, name);
	}
	
	private String getContainerName(Player player)
	{
		var container = player.getPersistentDataContainer();
		return container.get(key, PersistentDataType.STRING);
	}
	
	public boolean hasContainerName(Player player)
	{
		var container = player.getPersistentDataContainer();
		return container.has(key);
	}
	
	public String displayName(Player player)
	{
		return miniMessage.serialize(player.displayName()) + "<reset>";
	}
	
	public String containerName(Player player)
	{
		return getContainerName(player) + "<reset>";
	}
	
	public String name(Player player)
	{
		return hasContainerName(player) ? ContainerManager.instance().name(player) : player.getName();
	}
	
	public boolean isHidden(Player player)
	{
		for (MetadataValue meta : player.getMetadata("vanished")) {
			if (meta.asBoolean()) return true;
		}
		return false;
	}
}
