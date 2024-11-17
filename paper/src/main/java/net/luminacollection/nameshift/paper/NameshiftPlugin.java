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

package net.luminacollection.nameshift.paper;

import net.luckperms.api.LuckPerms;
import net.luminacollection.nameshift.common.hooks.LuckPermsHook;
import net.luminacollection.nameshift.paper.commands.CommandName;
import net.luminacollection.nameshift.paper.commands.CommandNameshift;
import net.luminacollection.nameshift.paper.commands.CommandWhoIs;
import net.luminacollection.nameshift.paper.listeners.NameshiftListeners;
import net.luminacollection.nameshift.paper.listeners.VanishListeners;
import net.luminacollection.nameshift.paper.listeners.SayanVanishListeners;
import net.luminacollection.nameshift.paper.profiles.ProfileManager;
import net.luminacollection.nameshift.paper.profiles.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import software.axios.api.Axios;
import software.axios.api.AxiosApiPlugin;
import software.axios.api.command.CommandsInterface;
import net.luminacollection.nameshift.common.configuration.Settings;
import net.luminacollection.nameshift.common.i18n.Messages;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class NameshiftPlugin extends JavaPlugin implements AxiosApiPlugin
{
	private final List<CommandsInterface> commands = new ArrayList<>();
	private Axios axios;
	
	private final Locale[] extraSupportedLanguages = new Locale[] { Locale.GERMAN };
	
	private static NameshiftPlugin instance;
	public static NameshiftPlugin instance()
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
	
	private void setupLuckPerms()
	{
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) LuckPermsHook.instance().init(provider.getProvider());
		else throw new RuntimeException("ProxyMode is enabled but LuckPerms not found!");
	}
	
	private void setupSettings()
	{
		axios.configManager().setup(this, Settings.class);
	}
	
	private void setupMessages()
	{
		axios.i18nManager().setup(this, Messages.class, extraSupportedLanguages);
	}
	
	private void setupCommands()
	{
		commands.addAll(Arrays.asList(
			CommandNameshift.instance(),
			CommandName.instance(),
			CommandWhoIs.instance()
		));
		
		commands.forEach(CommandsInterface::register);
	}
	
	public void reload()
	{
		setupSettings();
		setupMessages();
		if (Settings.PROXY_MODE.get()) setupLuckPerms();
		ProfileManager.instance().refresh();
	}
	
	private void setupHooks()
	{
		if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish"))
			VanishListeners.instance();
		if (Bukkit.getPluginManager().isPluginEnabled("VelocityVanish"))
			SayanVanishListeners.instance();
	}
	
	@Override
	public void onEnable()
	{
		setupAxios();
		reload();
		setupCommands();
		NameshiftListeners.instance();
		TeamManager.instance();
		setupHooks();
	}
	
	@Override
	public void onDisable()
	{
		commands.forEach(CommandsInterface::unregister);
		TeamManager.instance().unregister();
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
	
	public boolean isLocaleSupported(Locale locale)
	{
		for (Locale supportedLocale : extraSupportedLanguages)
		{
			if (supportedLocale.equals(locale)) return true;
			var language = locale.getLanguage();
			if (supportedLocale.getLanguage().equals(language)) return true;
		}
		return false;
	}
	
	public void debug(String message)
	{
		if (Settings.DEBUG.get()) getLogger().info("[DEBUG] " + message);
	}
}
