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

package net.luminacollection.nameshift.common.i18n;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import software.axios.api.Axios;
import software.axios.api.AxiosProvider;
import software.axios.api.i18n.AxiosMessages;
import software.axios.api.i18n.MessagesInterface;

import java.util.Locale;

public class Messages implements MessagesInterface
{
	public static final Messages COMMAND_NAMESHIFT_META_SHORT_DESCRIPTION = new Messages("command.nameshift.meta.short-description");
	public static final Messages COMMAND_NAMESHIFT_MAIN = new Messages("command.nameshift.main");
	public static final Messages COMMAND_NAMESHIFT_SUB_RELOAD = new Messages("command.nameshift.sub.reload");
	public static final Messages COMMAND_NAME_META_SHORT_DESCRIPTION = new Messages("command.name.meta.short-description");
	public static final Messages COMMAND_NAME_MAIN = new Messages("command.name.main");
	public static final Messages COMMAND_NAME_MAIN_SELF = new Messages("command.name.main.self");
	public static final Messages COMMAND_NAME_SUB_GIVE = new Messages("command.name.sub.give");
	public static final Messages COMMAND_WHOIS_MAIN = new Messages("command.whois.main");
	public static final Messages COMMAND_WHOIS_MAIN_NOT_FOUND = new Messages("command.whois.main.not-found");
	public static final Messages COMMAND_WHOIS_SUB_ALL = new Messages("command.whois.sub.all");
	public static final Messages COMMAND_WHOIS_SUB_ALL_EMPTY = new Messages("command.whois.sub.all.empty");
	public static final Messages COMMAND_WHOIS_SUB_ALL_ENTRY = new Messages("command.whois.sub.all.entry");
	public static final Messages COMMAND_WHOIS_META_SHORT_DESCRIPTION = new Messages("command.whois.meta.short-description");
	public static final Messages COMMAND_WHOIS_META_NAME = new Messages("command.whois.meta.name");
	public static final Messages COMMAND_WHOIS_SUB_ALL_META_NAME = new Messages("command.whois.sub.all.meta.name");
	public static final Messages COMMAND_NAME_META_NAME = new Messages("command.name.meta.name");
	public static final Messages COMMAND_NAME_SUB_GIVE_META_NAME = new Messages("command.name.sub.give.meta.name");
	public static final Messages COMMAND_NAME_META_ARG_NAME = new Messages("command.name.meta.arg.name");
	public static final Messages COMMAND_NAME_META_ARG_PLAYER = new Messages("command.name.meta.arg.player");
	public static final Messages COMMAND_WHOIS_META_ARG_NAME = new Messages("command.whois.meta.arg.name");
	public static final Messages COMMAND_NAME_SUB_RESET_SELF = new Messages("command.name.sub.reset.self");
	public static final Messages COMMAND_NAME_SUB_RESET = new Messages("command.name.sub.reset");
	public static final Messages COMMAND_NAME_SUB_RESET_META_NAME = new Messages("command.name.sub.reset.meta.name");
	public static final Messages COMMAND_NAME_ERROR_NAME_TOO_LONG = new Messages("command.name.error.name-too-long");
	public static final Messages COMMAND_NAME_MSG_HIDDEN = new Messages("command.name.msg.hidden");
	public static final Messages COMMAND_NAME_SUB_PRESETS = new Messages("command.name.sub.presets");
	public static final Messages COMMAND_NAME_SUB_PRESETS_META_NAME = new Messages("command.name.sub.presets.meta.name");
	public static final Messages COMMAND_NAME_SUB_PRESETS_ENTRY = new Messages("command.name.sub.presets.entry");
	public static final Messages COMMAND_NAME_SUB_PRESETS_EMPTY = new Messages("command.name.sub.presets.empty");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_ADD = new Messages("command.name.sub.presets.sub.add");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_ADD_ERROR_PRESET_ALREADY_EXISTS = new Messages("command.name.sub.presets.sub.add.error.preset-already-exists");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_REMOVE = new Messages("command.name.sub.presets.sub.remove");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_ADD_META_NAME = new Messages("command.name.sub.presets.sub.add.meta.name");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_REMOVE_META_NAME = new Messages("command.name.sub.presets.sub.remove.meta.name");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_REMOVE_ERROR_NO_SUCH_PRESET_EXISTS = new Messages("command.name.sub.presets.sub.remove.error.no-such-preset-exists");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_ADD_ERROR_LIMIT_REACHED = new Messages("command.name.sub.presets.sub.add.error.limit-reached");
	public static final Messages COMMAND_NAME_SUB_PRESETS_BUTTON_CHANGE = new Messages("command.name.sub.presets.button.change");
	public static final Messages COMMAND_NAME_SUB_PRESETS_BUTTON_DELETE = new Messages("command.name.sub.presets.button.delete");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_RESET_META_NAME = new Messages("command.name.sub.presets.sub.reset.meta.name");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_RESET = new Messages("command.name.sub.presets.sub.reset");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_APPLY_META_NAME = new Messages("command.name.sub.presets.sub.apply.meta.name");
	public static final Messages COMMAND_NAME_SUB_PRESETS_SUB_APPLY = new Messages("command.name.sub.presets.sub.apply");
	
	
	private final Axios axios = AxiosProvider.get();
	private final AxiosMessages axiosMessages;
	
	private Messages(String path)
	{
		axiosMessages = axios.axiosMessages(this.getClass(), path);
	}
	
	@Override
	public @NotNull String toString(Locale locale)
	{
		return axiosMessages.toString(locale);
	}
	
	@Override
	public @NotNull String toString()
	{
		return axiosMessages.toString();
	}
	
	@Override
	public void sendTo(Audience audience, TagResolver placeholder)
	{
		axiosMessages.sendTo(audience, placeholder);
	}
	
	@Override
	public void sendTo(Audience audience)
	{
		axiosMessages.sendTo(audience);
	}
	
	public static Messages byPath(String path)
	{
		return new Messages(path);
	}
}
