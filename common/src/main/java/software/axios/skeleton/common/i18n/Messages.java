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

package software.axios.skeleton.common.i18n;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.NonNull;
import software.axios.api.Axios;
import software.axios.api.AxiosProvider;
import software.axios.api.i18n.AxiosMessages;
import software.axios.api.i18n.MessagesInterface;

import java.util.Locale;

public class Messages implements MessagesInterface
{
	public static final Messages COMMAND_PAPERSKELETON_META_SHORT_DESCRIPTION = new Messages("command.paperskeleton.meta.short-description");
	public static final Messages COMMAND_PAPERSKELETON_MAIN = new Messages("command.paperskeleton.main");
	public static final Messages COMMAND_PAPERSKELETON_SUB_RELOAD = new Messages("command.paperskeleton.sub.reload");
	
	private final Axios axios = AxiosProvider.get();
	private final AxiosMessages axiosMessages;
	
	private Messages(String path)
	{
		axiosMessages = axios.axiosMessages(this.getClass(), path);
	}
	
	@Override
	public @NonNull String toString(Locale locale)
	{
		return axiosMessages.toString(locale);
	}
	
	@Override
	public @NonNull String toString()
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
