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
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luminacollection.nameshift.common.i18n.Messages;
import net.luminacollection.nameshift.paper.NameshiftPlugin;
import net.luminacollection.nameshift.paper.profiles.ProfileManager;
import org.apache.logging.log4j.message.Message;
import org.bukkit.entity.Player;
import software.axios.api.Axios;
import software.axios.api.command.CommandsInterface;

public class CommandWhoIs implements CommandsInterface
{
	private static CommandWhoIs instance;
	private final CommandAPICommand command;
	private final Axios axios = NameshiftPlugin.instance().axios();
	
	private final String ARG_NAME = Messages.COMMAND_WHOIS_META_ARG_NAME.toString();
	
	private CommandWhoIs()
	{
		command = new CommandAPICommand(Messages.COMMAND_WHOIS_META_NAME.toString());
		commandBody();
		subCommandAll();
	}
	
	public static CommandWhoIs instance()
	{
		if (instance == null) instance = new CommandWhoIs();
		return instance;
	}
	
	private void commandBody()
	{
		command.withPermission(NameshiftPlugin.instance().getName() + ".command.whois");
		command.withHelp(Messages.COMMAND_WHOIS_META_SHORT_DESCRIPTION.toString(), "");
		command.withArguments(new GreedyStringArgument(ARG_NAME).replaceSuggestions(ArgumentSuggestions.strings(info -> ProfileManager.instance().customNames())));
		command.executes((sender, args) ->
		{
			var locale = (sender instanceof Player player) ? player.locale() : axios.defaultLocale();
			var name = (String) args.get(ARG_NAME);
			var player = ProfileManager.instance().getPlayerFromList(name);
			if (player == null || ProfileManager.instance().isHidden(player))
				throw CommandAPIBukkit.failWithAdventureComponent(MiniMessage.miniMessage().deserialize(Messages.COMMAND_WHOIS_MAIN_NOT_FOUND.toString(locale), axios.tagBuilder().add("name", name).build()));
			else
				Messages.COMMAND_WHOIS_MAIN.sendTo(sender, axios.tagBuilder()
															   .add("name", ProfileManager.instance().displayName(player), true)
															   .add("player", player.getName()).build());
		});
	}
	
	private void subCommandAll()
	{
		var subCommand = new CommandAPICommand(Messages.COMMAND_WHOIS_SUB_ALL_META_NAME.toString());
		subCommand.withPermission(NameshiftPlugin.instance().getName() + ".command.whois");
		subCommand.executes((sender, args) ->
		{
			var names = ProfileManager.instance().customNames();
			if (names.length == 0)
			{
				Messages.COMMAND_WHOIS_SUB_ALL_EMPTY.sendTo(sender);
				return;
			}
			var sb = new StringBuilder();
			for (var name : names)
			{
				var player = ProfileManager.instance().getPlayerFromList(name);
				if (ProfileManager.instance().isHidden(player)) continue;
				sb.append(Messages.COMMAND_WHOIS_SUB_ALL_ENTRY.toString()
							  .replace("<player>", player.getName())
							  .replace("<name>", ProfileManager.instance().displayName(player))
				).append("<reset><newline>");
			}
			
			var list = sb.substring(0, sb.length() - 9);
			Messages.COMMAND_WHOIS_SUB_ALL.sendTo(sender, axios.tagBuilder().add("list", list, true).build());
		});
		command.withSubcommand(subCommand);
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
