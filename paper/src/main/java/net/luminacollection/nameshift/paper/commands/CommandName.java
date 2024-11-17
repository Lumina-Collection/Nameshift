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
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import dev.jorel.commandapi.commandsenders.BukkitConsoleCommandSender;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luminacollection.nameshift.common.configuration.Settings;
import net.luminacollection.nameshift.common.i18n.Messages;
import net.luminacollection.nameshift.paper.NameshiftPlugin;
import net.luminacollection.nameshift.paper.profiles.ContainerManager;
import net.luminacollection.nameshift.paper.profiles.ProfileManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import software.axios.api.Axios;
import software.axios.api.command.CommandsInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandName implements CommandsInterface
{
	private static CommandName instance;
	private final NameshiftPlugin plugin = NameshiftPlugin.instance();
	private final Axios axios = plugin.axios();
	private final List<CommandAPICommand> commands = new ArrayList<>();
	private final List<CommandAPICommand> subCommands = new ArrayList<>();
	private final ProfileManager profileManager = ProfileManager.instance();
	private final ContainerManager containerManager = ContainerManager.instance();
	
	private final String NAME = Messages.COMMAND_NAME_META_NAME.toString();
	private final String ARG_GIVE = Messages.COMMAND_NAME_SUB_GIVE_META_NAME.toString();
	private final String ARG_NAME = Messages.COMMAND_NAME_META_ARG_NAME.toString();
	private final String ARG_PLAYER = Messages.COMMAND_NAME_META_ARG_PLAYER.toString();
	private final String ARG_RESET = Messages.COMMAND_NAME_SUB_RESET_META_NAME.toString();
	private final String ARG_PRESETS = Messages.COMMAND_NAME_SUB_PRESETS_META_NAME.toString();
	private final String ARG_PRESETS_ADD = Messages.COMMAND_NAME_SUB_PRESETS_SUB_ADD_META_NAME.toString();
	private final String ARG_PRESETS_REMOVE = Messages.COMMAND_NAME_SUB_PRESETS_SUB_REMOVE_META_NAME.toString();
	private final String ARG_PRESETS_RESET = Messages.COMMAND_NAME_SUB_PRESETS_SUB_RESET_META_NAME.toString();
	private final String ARG_PRESETS_APPLY = Messages.COMMAND_NAME_SUB_PRESETS_SUB_APPLY_META_NAME.toString();
	
	private CommandName()
	{
		subCommandGive();
		subCommandReset();
		subCommandPresets();
		commandWithoutArguments();
		commandWithNameArgument();
	}
	
	public static CommandName instance()
	{
		if (instance == null) instance = new CommandName();
		return instance;
	}
	
	private CommandAPICommand commandBase()
	{
		var command = new CommandAPICommand(NAME);
		command.withHelp(Messages.COMMAND_NAME_META_SHORT_DESCRIPTION.toString(), "");
		return command;
	}
	
	private void commandWithoutArguments()
	{
		var command = commandBase();
		command.withPermission(plugin.getName().toLowerCase() + ".command.name");
		command.executesPlayer((player, args) ->
		{
			var tags = axios.tagBuilder().add("name", profileManager.displayName(player), true).build();
			Messages.COMMAND_NAME_MAIN.sendTo(player, tags);
		});
		command.withSubcommands(subCommands.toArray(new CommandAPICommand[0]));
		commands.add(command);
	}
	
	private void commandWithNameArgument()
	{
		var command = commandBase();
		command.withPermission(plugin.getName().toLowerCase() + ".command.name.modify.own");
		command.withArguments(new GreedyStringArgument(ARG_NAME));
		command.executesPlayer((player, args) ->
		{
			var name = (String) args.get(ARG_NAME);
			changeName(player, name);
			Messages.COMMAND_NAME_MAIN_SELF.sendTo(player, axios.tagBuilder().add("name", profileManager.containerName(player), true).build());
			if (profileManager.isHidden(player))
				Messages.COMMAND_NAME_MSG_HIDDEN.sendTo(player);
		});
		commands.add(command);
	}
	
	private Player[] subCommandGiveAllowedPlayersList(CommandSender sender)
	{
		var players = new ArrayList<Player>();
		var permOwn = plugin.getName().toLowerCase() + ".command.name.modify.own";
		var permOthers = plugin.getName().toLowerCase() + ".command.name.modify.others";
		if (sender instanceof Player && sender.hasPermission(permOwn)) players.add((Player) sender);
		if (sender.hasPermission(permOthers))
			for (var player : plugin.getServer().getOnlinePlayers())
				if (!player.equals(sender)) players.add(player);
		return players.toArray(Player[]::new);
	}
	private void subCommandGive()
	{
		var permOwn = plugin.getName().toLowerCase() + ".command.name.modify.own";
		var permOthers = plugin.getName().toLowerCase() + ".command.name.modify.others";
		var subCommand = new CommandAPICommand(ARG_GIVE);
		subCommand.withRequirement(
			sender ->
				(sender instanceof ConsoleCommandSender
					 && !Bukkit.getOnlinePlayers().isEmpty())
				|| (sender instanceof Player
					 && (sender.hasPermission(permOwn)
							 || (sender.hasPermission(permOthers)
									 && Bukkit.getOnlinePlayers().size() > 1)))
		);
		subCommand.withArguments(new PlayerArgument(ARG_PLAYER).replaceSafeSuggestions(SafeSuggestions.suggest(info -> subCommandGiveAllowedPlayersList(info.sender()))));
		subCommand.withArguments(new GreedyStringArgument(ARG_NAME));
		subCommand.executes((sender, args) ->
		{
			var player = (Player) args.get(ARG_PLAYER);
			if (player == null) return;
			var name = (String) args.get(ARG_NAME);
			var self = sender instanceof Player  && sender.equals(player);
			var mm = MiniMessage.miniMessage();
			if (self && !sender.hasPermission(permOwn))
				throw CommandAPIBukkit.failWithAdventureComponent(mm.deserialize("<lang:commands.help.failed>"));
			if (!self && !sender.hasPermission(permOthers))
				throw CommandAPIBukkit.failWithAdventureComponent(mm.deserialize("<lang:commands.help.failed>"));
			changeName(player, name);
			var tags = axios.tagBuilder().add("player", player.getName()).add("name", profileManager.containerName(player), true).build();
			if (self)
				Messages.COMMAND_NAME_MAIN_SELF.sendTo(sender, tags);
			else
				Messages.COMMAND_NAME_SUB_GIVE.sendTo(sender, tags);
			if (profileManager.isHidden(player))
				Messages.COMMAND_NAME_MSG_HIDDEN.sendTo(sender);
		});
		subCommands.add(subCommand);
	}
	
	private void changeName(Player player, String name) throws WrapperCommandSyntaxException
	{
		assert name != null;
		var mm = MiniMessage.miniMessage();
		var tags = axios.tagBuilder().add("character_limit", Settings.LIMITS_CHARACTER_LIMIT.get()).build();
		if (mm.stripTags(name).length() > Settings.LIMITS_CHARACTER_LIMIT.get())
			throw CommandAPIBukkit.failWithAdventureComponent(mm.deserialize(Messages.COMMAND_NAME_ERROR_NAME_TOO_LONG.toString(player.locale()), tags));
		profileManager.setName(player, name);
	}
	
	private void subCommandReset()
	{
		var subCommand = new CommandAPICommand(ARG_RESET);
		subCommand.withPermission(plugin.getName().toLowerCase() + ".command.name.modify.others");
		subCommand.withOptionalArguments(new PlayerArgument(ARG_PLAYER));
		subCommand.executesPlayer((player, args) ->
		{
			var target = (Player) args.getOptional(ARG_PLAYER).orElse(player);
			profileManager.setName(target, target.getName());
			var tags = axios.tagBuilder().add("target", target.getName()).build();
			if (target == player) Messages.COMMAND_NAME_SUB_RESET_SELF.sendTo(player);
			else Messages.COMMAND_NAME_SUB_RESET.sendTo(player, tags);
		});
		subCommands.add(subCommand);
	}
	
	private void subCommandPresets()
	{
		var subCommand = new CommandAPICommand(ARG_PRESETS);
		subCommand.withPermission(plugin.getName().toLowerCase() + ".command.name.presets");
		subCommand.withSubcommands(subCommandPresetsAdd(), subCommandPresetsRemove(), subCommandPresetsReset(), subCommandPresetsApply());
		subCommand.executesPlayer((player, args) ->
		{
			var locale = player.locale();
			var presets = containerManager.presets(player);
			if (presets.length == 0) {
				Messages.COMMAND_NAME_SUB_PRESETS_EMPTY.sendTo(player);
				return;
			}
			var sb = new StringBuilder();
			for (int i = 0; i < presets.length; i++)
			{
				var preset = presets[i];
				var rawPreset = preset;
				var escapedPreset = preset.replace("'", "\\'");
				preset = "<reset>" + preset + "<reset>";
				sb.append(Messages.COMMAND_NAME_SUB_PRESETS_ENTRY.toString(locale)
							  .replace("<button_change>", player.hasPermission("nameshift.command.name.modify.own") ? (profileManager.name(player).equals(rawPreset) ? "" : Messages.COMMAND_NAME_SUB_PRESETS_BUTTON_CHANGE.toString(locale)
															  .replace("<preset>", escapedPreset)
															  .replace("<cmd_name>", StringUtils.joinWith(" ", NAME, ARG_PRESETS, ARG_PRESETS_APPLY, i))) : ""
							  )
							  .replace("<button_delete>", Messages.COMMAND_NAME_SUB_PRESETS_BUTTON_DELETE.toString(locale)
															  .replace("<preset>", escapedPreset)
															  .replace("<cmd_name_preset_remove>", StringUtils.joinWith(" ", NAME, ARG_PRESETS, ARG_PRESETS_REMOVE))
							  )
							  .replace("  ", " ")
							  .replace("<preset>", preset)
				).append("<newline>");
			}
			var message = sb.substring(0, sb.length() - "<newline>".length());
			Messages.COMMAND_NAME_SUB_PRESETS.sendTo(player, axios.tagBuilder().add("list", message, true).build());
		});
		subCommands.add(subCommand);
	}
	
	private CommandAPICommand subCommandPresetsAdd()
	{
		var subCommand = new CommandAPICommand(ARG_PRESETS_ADD);
		subCommand.withPermission(plugin.getName().toLowerCase() + ".command.name.presets");
		subCommand.withOptionalArguments(new GreedyStringArgument(ARG_NAME));
		subCommand.executesPlayer((player, args) ->
		{
			var presets = containerManager.presets(player);
			var mm = MiniMessage.miniMessage();
			var name = (String) args.getOptional(ARG_NAME).orElse(profileManager.name(player));
			var locale = player.locale();
			var errorLimit = mm.deserialize(Messages.COMMAND_NAME_SUB_PRESETS_SUB_ADD_ERROR_LIMIT_REACHED.toString(locale));
			var errorExists = mm.deserialize(Messages.COMMAND_NAME_SUB_PRESETS_SUB_ADD_ERROR_PRESET_ALREADY_EXISTS.toString(locale));
			if (presets.length >= Settings.LIMITS_PRESETS_LIMIT.get())
				throw CommandAPIBukkit.failWithAdventureComponent(errorLimit);
			if (Arrays.stream(presets).anyMatch(preset -> preset.equalsIgnoreCase(name)))
				throw CommandAPIBukkit.failWithAdventureComponent(errorExists);
			containerManager.addPreset(player, name);
			Messages.COMMAND_NAME_SUB_PRESETS_SUB_ADD.sendTo(player, axios.tagBuilder().add("preset", name + "<reset>", true).build());
		});
		return subCommand;
	}
	
	private CommandAPICommand subCommandPresetsRemove()
	{
		var subCommand = new CommandAPICommand(ARG_PRESETS_REMOVE);
		subCommand.withPermission(plugin.getName().toLowerCase() + ".command.name.presets");
		subCommand.withOptionalArguments(new GreedyStringArgument(ARG_NAME));
		subCommand.executesPlayer((player, args) ->
		{
			var presets = containerManager.presets(player);
			var mm = MiniMessage.miniMessage();
			var name = (String) args.getOptional(ARG_NAME).orElse(profileManager.name(player));
			var locale = player.locale();
			var errorExists = mm.deserialize(Messages.COMMAND_NAME_SUB_PRESETS_SUB_REMOVE_ERROR_NO_SUCH_PRESET_EXISTS.toString(locale));
			if (Arrays.stream(presets).noneMatch(preset -> preset.equalsIgnoreCase(name)))
				throw CommandAPIBukkit.failWithAdventureComponent(errorExists);
			containerManager.removePreset(player, name);
			Messages.COMMAND_NAME_SUB_PRESETS_SUB_REMOVE.sendTo(player, axios.tagBuilder().add("preset", name + "<reset>", true).build());
		});
		return subCommand;
	}
	
	private CommandAPICommand subCommandPresetsReset()
	{
		var subCommand = new CommandAPICommand(ARG_PRESETS_RESET);
		subCommand.withPermission(plugin.getName().toLowerCase() + ".command.name.presets");
		subCommand.executesPlayer((player, args) ->
		{
			containerManager.resetPresets(player);
			Messages.COMMAND_NAME_SUB_PRESETS_SUB_RESET.sendTo(player);
		});
		return subCommand;
	}
	private CommandAPICommand subCommandPresetsApply()
	{
		var subCommand = new CommandAPICommand(ARG_PRESETS_APPLY);
		subCommand.withPermission(plugin.getName().toLowerCase() + ".command.name.modify.own");
		subCommand.withArguments(new IntegerArgument("preset_index"));
		subCommand.executesPlayer((player, args) ->
		{
			var presets = containerManager.presets(player);
			if (presets.length == 0) return; //TODO: send error message
			var index = (Integer) args.get("preset_index");
			if (index == null) return;
			if (index < 0) index = 0;
			if (index >= presets.length) index = presets.length - 1;
			var name = presets[index];
			changeName(player, name);
			Messages.COMMAND_NAME_SUB_PRESETS_SUB_APPLY.sendTo(player);
		});
		return subCommand;
	}
	
	@Override
	public void register()
	{
		commands.forEach(CommandAPICommand::register);
	}
	
	@Override
	public void unregister()
	{
		CommandAPI.unregister(NAME);
	}
}
