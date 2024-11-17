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

package net.luminacollection.nameshift.common.configuration;

import org.jetbrains.annotations.NotNull;
import software.axios.api.Axios;
import software.axios.api.AxiosProvider;
import software.axios.api.configuration.AxiosSettings;
import software.axios.api.configuration.SettingsField;
import software.axios.api.configuration.SettingsInterface;

import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Settings<T> implements SettingsInterface
{
	@SettingsField
	public static final Settings<Integer> LIMITS_CHARACTER_LIMIT = new Settings<>("limits.character-limit", Integer.class, 40);
	@SettingsField
	public static final Settings<List> LIMITS_ALLOWED_TAGS = new Settings<>("limits.allowed-tags", List.class, List.of("color", "bold", "italic", "underlined", "strikethrough", "obfuscated", "gradient", "rainbow", "font"));
	@SettingsField
	public static final Settings<Boolean> DEBUG = new Settings<>("general.debug", Boolean.class, false);
	@SettingsField
	public static final Settings<Integer> LIMITS_PRESETS_LIMIT = new Settings<>("limits.presets-limit", Integer.class, 10);
	@SettingsField
	public static final Settings<Boolean> PROXY_MODE = new Settings<>("general.proxy-mode", Boolean.class, false);
	@SettingsField
	public static final Settings<List> CUSTOM_COLORS = new Settings<>("custom-colors", List.class, List.of("custom_color:#ff6600", "custom_gradient:#ff6600:#ff0000"));
	
	private final Axios axios = AxiosProvider.get();
	private final AxiosSettings<T, Settings<T>> axiosSettings;
	
	private Settings(String path, Class<T> type, T defaultValue)
	{
		axiosSettings = (AxiosSettings<T, Settings<T>>) axios.axiosSettings(this.getClass(), path, type, defaultValue);
	}
	
	@Override
	public @NotNull String path()
	{
		return axiosSettings.path();
	}
	
	@Override
	public @NotNull List<String> comments()
	{
		return axiosSettings.comments();
	}
	
	@Override
	public @NotNull T get()
	{
		return axiosSettings.get(this);
	}
	
	@Override
	public @NotNull T defaultValue()
	{
		return axiosSettings.defaultValue();
	}
	
	@Override
	public @NotNull Class<T> type()
	{
		return axiosSettings.type();
	}
}
