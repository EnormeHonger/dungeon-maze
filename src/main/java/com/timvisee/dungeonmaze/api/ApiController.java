package com.timvisee.dungeonmaze.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.timvisee.dungeonmaze.Core;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnusedDeclaration")
public class ApiController {

	/** Defines whether the API is enabled. */
	boolean apiEnabled = false;
	/** Holds a list of currently active API sessions. */
	List<DungeonMazeApi> apiSessions = new ArrayList<>();
	
	/**
	 * Constructor. This will automatically enable the Dungeon Maze API.
	 */
	public ApiController() {
		this(true);
	}
	
	/**
	 * Constructor.
	 * @param enableApi True to enable the Dungeon Maze API, false to keep the API disabled.
	 */
	public ApiController(boolean enableApi) {
		setEnabled(apiEnabled);
	}
	
	/**
	 * Register an API session.
	 *
	 * @param api The API instance to register.
	 *
	 * @return True on success, false on failure. True will also be returned if the API session was already registered.
	 */
	public boolean registerApiSession(DungeonMazeApi api) {
		// Make sure the instance isn't null
		if(api == null)
			return false;

		// Make sure the plugin instance is valid
		if(api.getPlugin() == null)
			return false;

		if(isApiSession(api))
			return true;
		
		// Add the API session
		this.apiSessions.add(api);
		
		// Show a hooked message, return the result
		Core.getLogger().info(api.getPlugin().getName() + " hooked into Dungeon Maze!");
		return true;
	}
	
	/**
	 * Check if the param instance is a registered API session.
	 *
	 * @param api Dungeon Maze API instance to validate.
	 *
	 * @return True if the API session is a currently valid session, false otherwise.
	 */
	public boolean isApiSession(DungeonMazeApi api) {
		return this.apiSessions.contains(api);
	}
	
	/**
	 * Check whether a plugin is hooked into Dungeon Maze.
	 *
	 * @param p Plugin to check for.
	 *
	 * @return True if this plugin was hooked into Dungeon Maze.
	 */
	public boolean isHooked(Plugin p) {
		for(DungeonMazeApi entry : this.apiSessions)
			if(entry.getPlugin().equals(p))
				return true;
		return false;
	}
	
	/**
	 * Get the amount of active API sessions.
	 *
	 * @return Amount of active API sessions.
	 */
	public int getApiSessionsCount() {
		return this.apiSessions.size();
	}
	
	/**
	 * Unregister the an API session, automatically forces the plugin of the API session to unhook Dungeon Maze.
	 *
	 * @param api Dungeon Maze API (layer) instance.
	 */
	public void unregisterApiSession(DungeonMazeApi api) {
		unregisterApiSession(api, true);
	}
	
	/**
	 * Unregister the an API session.
	 * @param api Dungeon Maze API (layer) instance.
	 * @param forceUnhook True to force the plugin to unhook Dungeon Maze.
	 */
	public void unregisterApiSession(DungeonMazeApi api, boolean forceUnhook) {
		// Should the plugin unhook Dungeon Maze
		if(forceUnhook)
			api.unhook();
		
		// Make sure this api session was registered
		if(!isApiSession(api))
			return;
		
		// Remove/unregister the api instance
		this.apiSessions.remove(api);

		// Show an 'unhooked' message
		if(api.getPlugin() != null)
			Core.getLogger().info(api.getPlugin().getName() + " unhooked from Dungeon Maze!");
	}
	
	/**
	 * Unregister all active API sessions, automatically forces the plugins of the sessions to unhook.
	 */
	public void unregisterAllApiSessions() {
		for(int i = 0; i < this.apiSessions.size(); i++) {
			// Get the current entry
			DungeonMazeApi api = this.apiSessions.get(i);
			
			// Make sure the entry is not null
			if(api == null)
				continue;
			
			// Unregister the current entry
			unregisterApiSession(api);
			i--;
		}
	}
	
	/**
	 * Unhook a plugin from Dungeon Maze and unhook all it's API sessions from Dungeon Maze.
	 *
	 * @param p Plugin to unhook.
	 */
	public void unhookPlugin(Plugin p) {
		List<DungeonMazeApi> unregister = this.apiSessions.stream().filter(entry -> entry.getPlugin().equals(p)).collect(Collectors.toList());
		unregister.forEach(this::unregisterApiSession);
	}
	
	/**
	 * Set if the Dungeon Maze API is enabled or not.
	 *
	 * @param enabled True to enable the Dungeon Maze API.
	 */
	public void setEnabled(boolean enabled) {
		// Make sure the value is different than before
		if(this.apiEnabled != enabled) {
			// Enable or disable the API
			this.apiEnabled = enabled;
			
			// Show a status message
			if(enabled)
				Core.getLogger().info("Dungeon Maze API enabled!");
			else
				Core.getLogger().info("Dungeon Maze API disabled!");
			
			// Unregister all api sessions if the API was disabled
			if(!enabled)
				unregisterAllApiSessions();
		}
	}
	
	/**
	 * Check if the Dungeon Maze API is enabled.
	 *
	 * @return True if the API is enabled.
	 */
	public boolean isEnabled() {
		return this.apiEnabled;
	}
}
