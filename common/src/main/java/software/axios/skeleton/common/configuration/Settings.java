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

package software.axios.skeleton.common.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import software.axios.api.Axios;
import software.axios.api.AxiosProvider;
import software.axios.api.configuration.AxiosSettings;
import software.axios.api.configuration.SettingsInterface;

import java.util.List;

@SuppressWarnings("unchecked")
public class Settings<T> implements SettingsInterface
{
	
	
	private final Axios axios = AxiosProvider.get();
	private final AxiosSettings<T, Settings<T>> axiosSettings;
	
	private Settings(String path, Class<T> type, T defaultValue)
	{
		axiosSettings = (AxiosSettings<T, Settings<T>>) axios.axiosSettings(this.getClass(), path, type, defaultValue);
	}
	
	@Override
	public @NonNull String path()
	{
		return axiosSettings.path();
	}
	
	@Override
	public @NonNull List<String> comments()
	{
		return axiosSettings.comments();
	}
	
	@Override
	public @NonNull T get()
	{
		return axiosSettings.get(this);
	}
	
	@Override
	public @NonNull T defaultValue()
	{
		return axiosSettings.defaultValue();
	}
	
	@Override
	public @NonNull Class<T> type()
	{
		return axiosSettings.type();
	}
}
