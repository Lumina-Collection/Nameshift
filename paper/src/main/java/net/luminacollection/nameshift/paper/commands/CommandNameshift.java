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

package net.luminacollection.nameshift.paper.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;
import software.axios.api.Axios;
import software.axios.api.command.CommandsInterface;
import net.luminacollection.nameshift.common.i18n.Messages;
import net.luminacollection.nameshift.paper.NameshiftPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandNameshift implements CommandsInterface
{
	private static CommandNameshift instance;
	private final NameshiftPlugin plugin = NameshiftPlugin.instance();
	private final Axios axios = plugin.axios();
	private final CommandAPICommand command;
	private final List<CommandAPICommand> subCommands = new ArrayList<>();
	
	private CommandNameshift()
	{
		command = new CommandAPICommand(plugin.getName().toLowerCase());
		commandBody();
		subCommandReload();
		command.withSubcommands(subCommands.toArray(new CommandAPICommand[0]));
	}
	
	public static CommandNameshift instance()
	{
		if (instance == null) instance = new CommandNameshift();
		return instance;
	}
	
	@SuppressWarnings("all")
	private void commandBody()
	{
		command.withAliases("ns");
		command.withPermission(plugin.getName().toLowerCase() + ".command");
		command.withHelp(Messages.COMMAND_NAMESHIFT_META_SHORT_DESCRIPTION.toString(), "");
		command.executes((sender, args) ->
		{
			var locale = sender instanceof Player player ? player.locale() : axios.defaultLocale();
			var meta = plugin.getPluginMeta();
			var version = meta.getVersion();
			var author = meta.getAuthors().get(0);
			var description = meta.getDescription();
			var website = meta.getWebsite();
			if (plugin.isLocaleSupported(locale)) website = website.replace(plugin.getName().toLowerCase(), locale.getLanguage() + "/" + plugin.getName().toLowerCase());
			var name = meta.getName();
			var tags = axios.tagBuilder().add(Map.of(
				"version", version,
				"author", author,
				"description", description,
				"website", "<click:open_url:'" + website + "'>" + website + "</click>",
				"name", name
			), true).build();
			Messages.COMMAND_NAMESHIFT_MAIN.sendTo(sender, tags);
		});
	}
	
	private void subCommandReload()
	{
		var subCommand = new CommandAPICommand("reload");
		subCommand.withAliases("r");
		subCommand.withPermission(plugin.getName().toLowerCase() + ".command.reload");
		subCommand.executes((sender, args) ->
		{
			plugin.reload();
			Messages.COMMAND_NAMESHIFT_SUB_RELOAD.sendTo(sender);
		});
		subCommands.add(subCommand);
	}
	
	@Override
	public void register()
	{
		command.register();
	}
	
	@Override
	public void unregister()
	{
		CommandAPI.unregister(command.getName());
	}
}
