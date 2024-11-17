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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class TeamManager
{
	private final BukkitTask task;
	private final Team team;
	private static TeamManager instance;
	public static TeamManager instance()
	{
		if (instance == null)
			instance = new TeamManager();
		return instance;
	}
	private TeamManager()
	{
		var mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		team = mainScoreboard.getTeam(NameshiftPlugin.instance().getName()) != null ? mainScoreboard.getTeam(NameshiftPlugin.instance().getName()) : mainScoreboard.registerNewTeam(NameshiftPlugin.instance().getName());
		assert team != null;
		team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		task = scheduler();
	}
	
	public void addPlayer(Player player)
	{
		team.addPlayer(player);
	}
	
	public void removePlayer(Player player)
	{
		team.removePlayer(player);
	}
	
	public void unregister()
	{
		task.cancel();
		team.unregister();
	}
	
	private BukkitTask scheduler()
	{
		return Bukkit.getScheduler().runTaskTimer(NameshiftPlugin.instance(), () -> {
			for (Player player : Bukkit.getOnlinePlayers())
			{
				if (ProfileManager.instance().hasContainerName(player))
					team.addPlayer(player);
			}
		}, 0, 20);
	}
}
