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

package software.axios.skeleton.paper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import software.axios.api.Axios;
import software.axios.api.AxiosApiPlugin;
import software.axios.api.command.CommandsInterface;
import software.axios.skeleton.common.configuration.Settings;
import software.axios.skeleton.common.i18n.Messages;
import software.axios.skeleton.paper.commands.CommandPaperSkeleton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaperSkeletonPlugin extends JavaPlugin implements AxiosApiPlugin
{
	private final List<CommandsInterface> commands = new ArrayList<>();
	private Axios axios;
	
	private static PaperSkeletonPlugin instance;
	public static PaperSkeletonPlugin instance()
	{
		return instance;
	}
	
	@Override
	public void onLoad()
	{
		instance = this;
	}
	
	private void setupAxios()
	{
		RegisteredServiceProvider<Axios> provider = Bukkit.getServicesManager().getRegistration(Axios.class);
		if (provider != null) axios = provider.getProvider();
		else throw new RuntimeException("Axios not found!");
	}
	
	private void setupSettings()
	{
		axios.configManager().setup(this, Settings.class);
	}
	
	private void setupMessages()
	{
		axios.i18nManager().setup(this, Messages.class);
	}
	
	private void setupCommands()
	{
		commands.addAll(Arrays.asList(
			CommandPaperSkeleton.instance()
		));
		
		commands.forEach(CommandsInterface::register);
	}
	
	public void reload()
	{
		setupSettings();
		setupMessages();
	}
	
	@Override
	public void onEnable()
	{
		setupAxios();
		reload();
		setupCommands();
	}
	
	@Override
	public void onDisable()
	{
		commands.forEach(CommandsInterface::unregister);
	}
	
	@Override
	public @NonNull File pluginFolder()
	{
		return getDataFolder();
	}
	
	@Override
	public void saveResources(String s, boolean b)
	{
		saveResource(s, b);
	}
	
	public Axios axios()
	{
		return axios;
	}
}
