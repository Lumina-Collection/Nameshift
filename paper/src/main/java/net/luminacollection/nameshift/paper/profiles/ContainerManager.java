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

import net.luminacollection.nameshift.paper.NameshiftPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ContainerManager
{
	private final NameshiftPlugin plugin = NameshiftPlugin.instance();
	
	private final NamespacedKey keyName;
	private final NamespacedKey keyPresets;
	
	private static ContainerManager instance;
	public static ContainerManager instance()
	{
		return instance == null ? instance = new ContainerManager() : instance;
	}
	private ContainerManager()
	{
		keyName = new NamespacedKey(plugin, "name");
		keyPresets = new NamespacedKey(plugin, "presets");
	}
	
	public String name(Player player)
	{
		return player.getPersistentDataContainer().get(keyName, PersistentDataType.STRING);
	}
	
	public void name(Player player, String name)
	{
		player.getPersistentDataContainer().set(keyName, PersistentDataType.STRING, name);
	}
	
	public String[] presets(Player player)
	{
		var container = player.getPersistentDataContainer();
		var hasPresets = container.has(keyPresets, PersistentDataType.STRING);
		var presets = container.get(keyPresets, PersistentDataType.STRING);
		hasPresets = hasPresets && presets != null && !presets.isEmpty();
		return  hasPresets ? presets.split("\u200B") : new String[0];
	}
	
	private void presets(Player player, String[] presets)
	{
		player.getPersistentDataContainer().set(keyPresets, PersistentDataType.STRING, String.join("\u200B", presets));
	}
	
	public void addPreset(Player player, String preset)
	{
		var presets = presets(player);
		var newPresets = Arrays.copyOf(presets, presets.length + 1);
		newPresets[newPresets.length - 1] = preset;
		presets(player, newPresets);
	}
	
	public void removePreset(Player player, String preset)
	{
		var presets = presets(player);
		var newPresets = Arrays.stream(presets).filter(p -> !p.equals(preset)).toArray(String[]::new);
		presets(player, newPresets);
	}
	
	public void resetPresets(Player player)
	{
		presets(player, new String[0]);
	}
}
