package br.trcraft.Stargate;

import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import java.util.concurrent.*;
import java.util.logging.*;
import org.bukkit.plugin.messaging.*;

import br.trcraft.Stargate.event.*;

import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import java.io.*;
import org.bukkit.event.player.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.world.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.server.*;

public class Stargate extends JavaPlugin {
	public static Logger log;
	private FileConfiguration newConfig;
	private PluginManager pm;
	public static Server server;
	public static Stargate stargate;
	private static LangLoader lang;
	private static String portalFolder;
	private static String gateFolder;
	private static String langFolder;
	private static String defNetwork;
	private static boolean destroyExplosion;
	public static int maxGates;
	private static String langName;
	private static final int activeTime = 10;
	private static final int openTime = 10;
	public static boolean destMemory;
	public static boolean handleVehicles;
	public static boolean sortLists;
	public static boolean protectEntrance;
	public static boolean enableBungee;
	public static ChatColor signColor;
	public static boolean ignoreEntrance;
	public static boolean debug;
	public static boolean permDebug;
	public static ConcurrentLinkedQueue<Portal> openList;
	public static ConcurrentLinkedQueue<Portal> activeList;
	public static Queue<BloxPopulator> blockPopulatorQueue;
	public static Map<String, String> bungeeQueue;

	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("?4 ____  _                        _       ");
		Bukkit.getConsoleSender().sendMessage("?4/ ___|| |_ __ _ _ __ __ _  __ _| |_ ___ ");
		Bukkit.getConsoleSender().sendMessage("?4\\___ \\| __/ _` | '__/ _` |/ _` | __/ _ \\");
		Bukkit.getConsoleSender().sendMessage("?4 ___) | || (_| | | | (_| | (_| | ||  __/");
		Bukkit.getConsoleSender().sendMessage("?4|____/ \\__\\__,_|_|  \\__, |\\__,_|\\__\\___|");
		Bukkit.getConsoleSender().sendMessage("?4                    |___/               ");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("?4############################################################");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("                ?f?lPlugin STARGATE" + "?4?l DESATIVADO");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("                 ?aCreditos?f: TrDSTYLEE");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("?4############################################################");
		Portal.closeAllGates();
		Portal.clearGates();
		this.getServer().getScheduler().cancelTasks((Plugin) this);
	}

	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("?a ____  _                        _       ");
		Bukkit.getConsoleSender().sendMessage("?a/ ___|| |_ __ _ _ __ __ _  __ _| |_ ___ ");
		Bukkit.getConsoleSender().sendMessage("?a\\___ \\| __/ _` | '__/ _` |/ _` | __/ _ \\");
		Bukkit.getConsoleSender().sendMessage("?a ___) | || (_| | | | (_| | (_| | ||  __/");
		Bukkit.getConsoleSender().sendMessage("?a|____/ \\__\\__,_|_|  \\__, |\\__,_|\\__\\___|");
		Bukkit.getConsoleSender().sendMessage("?a                    |___/               ");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("?b############################################################");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("                ?f?lPlugin STARGATE" + "?a?l ATIVADO");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("                 ?aCreditos?f: TrDSTYLEE");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("?b############################################################");
		final PluginDescriptionFile pdfFile = this.getDescription();
		this.pm = this.getServer().getPluginManager();
		this.newConfig = this.getConfig();
		Stargate.log = Logger.getLogger("Minecraft");
		Stargate.server = this.getServer();
		Stargate.stargate = this;
		Stargate.portalFolder = this.getDataFolder().getPath().replaceAll("\\\\", "/") + "/portals/";
		Stargate.gateFolder = this.getDataFolder().getPath().replaceAll("\\\\", "/") + "/gates/";
		Stargate.langFolder = this.getDataFolder().getPath().replaceAll("\\\\", "/") + "/lang/";
		Stargate.log.log(Level.INFO, "{0} v.{1} is enabled.", new Object[] { pdfFile.getName(), pdfFile.getVersion() });
		this.pm.registerEvents((Listener) new pListener(), (Plugin) this);
		this.pm.registerEvents((Listener) new bListener(), (Plugin) this);
		this.pm.registerEvents((Listener) new vListener(), (Plugin) this);
		this.pm.registerEvents((Listener) new eListener(), (Plugin) this);
		this.pm.registerEvents((Listener) new wListener(), (Plugin) this);
		this.pm.registerEvents((Listener) new sListener(), (Plugin) this);
		this.loadConfig();
		if (Stargate.enableBungee) {
			Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin) this, "BungeeCord");
			Bukkit.getMessenger().registerIncomingPluginChannel((Plugin) this, "BungeeCord",
					(PluginMessageListener) new pmListener());
		}
		Stargate.lang = new LangLoader(Stargate.langFolder, Stargate.langName);
		this.migrate();
		this.reloadGates();
		if (iConomyHandler.setupeConomy(this.pm) && iConomyHandler.economy != null) {
			Stargate.log.log(Level.INFO, "[Stargate] Vault v{0} found",
					iConomyHandler.vault.getDescription().getVersion());
		}
		this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, (Runnable) new SGThread(), 0L, 100L);
		this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, (Runnable) new BlockPopulatorThread(),
				0L, 1L);
	}

	public void loadConfig() {
		this.reloadConfig();
		this.newConfig = this.getConfig();
		this.newConfig.options().copyDefaults(true);
		Stargate.portalFolder = this.newConfig.getString("portal-folder");
		Stargate.gateFolder = this.newConfig.getString("gate-folder");
		Stargate.defNetwork = this.newConfig.getString("default-gate-network").trim();
		Stargate.destroyExplosion = this.newConfig.getBoolean("destroyexplosion");
		Stargate.maxGates = this.newConfig.getInt("maxgates");
		Stargate.langName = this.newConfig.getString("lang");
		Stargate.destMemory = this.newConfig.getBoolean("destMemory");
		Stargate.ignoreEntrance = this.newConfig.getBoolean("ignoreEntrance");
		Stargate.handleVehicles = this.newConfig.getBoolean("handleVehicles");
		Stargate.sortLists = this.newConfig.getBoolean("sortLists");
		Stargate.protectEntrance = this.newConfig.getBoolean("protectEntrance");
		Stargate.enableBungee = this.newConfig.getBoolean("enableBungee");
		final String sc = this.newConfig.getString("signColor");
		try {
			Stargate.signColor = ChatColor.valueOf(sc.toUpperCase());
		} catch (Exception ignore) {
			Stargate.log
					.warning("[Stargate] You have specified an invalid color in your config.yml. Defaulting to BLACK");
			Stargate.signColor = ChatColor.BLACK;
		}
		Stargate.debug = this.newConfig.getBoolean("debug");
		Stargate.permDebug = this.newConfig.getBoolean("permdebug");
		iConomyHandler.useiConomy = this.newConfig.getBoolean("useiconomy");
		iConomyHandler.createCost = this.newConfig.getInt("createcost");
		iConomyHandler.destroyCost = this.newConfig.getInt("destroycost");
		iConomyHandler.useCost = this.newConfig.getInt("usecost");
		iConomyHandler.toOwner = this.newConfig.getBoolean("toowner");
		iConomyHandler.chargeFreeDestination = this.newConfig.getBoolean("chargefreedestination");
		iConomyHandler.freeGatesGreen = this.newConfig.getBoolean("freegatesgreen");
		this.saveConfig();
	}

	public void reloadGates() {
		for (final Portal p : Stargate.openList) {
			p.close(true);
		}
		Gate.loadGates(Stargate.gateFolder);
		if (Gate.getGateByName("nethergate.gate") == null || Gate.getGateByName("nethergate.gate").getExit() == null) {
			Gate.populateDefaults(Stargate.gateFolder);
		}
		Stargate.log.log(Level.INFO, "[Stargate] Loaded {0} gate layouts", Gate.getGateCount());
		for (final World world : this.getServer().getWorlds()) {
			Portal.loadAllGates(world);
		}
	}

	private void migrate() {
		final File newPortalDir = new File(Stargate.portalFolder);
		if (!newPortalDir.exists()) {
			newPortalDir.mkdirs();
		}
		final File newFile = new File(Stargate.portalFolder, this.getServer().getWorlds().get(0).getName() + ".db");
		if (!newFile.exists()) {
			newFile.getParentFile().mkdirs();
			final File oldishFile = new File("plugins/Stargate/stargate.db");
			if (oldishFile.exists()) {
				Stargate.log.info("[Stargate] Migrating existing stargate.db");
				oldishFile.renameTo(newFile);
			}
		}
		final File oldDir = new File("stargates");
		if (oldDir.exists()) {
			final File newDir = new File(Stargate.gateFolder);
			if (!newDir.exists()) {
				newDir.mkdirs();
			}
			for (final File file : oldDir.listFiles(new Gate.StargateFilenameFilter())) {
				Stargate.log.log(Level.INFO, "[Stargate] Migrating existing gate {0}", file.getName());
				file.renameTo(new File(Stargate.gateFolder, file.getName()));
			}
		}
	}

	public static void debug(final String rout, final String msg) {
		if (Stargate.debug) {
			Stargate.log.log(Level.INFO, "[Stargate::{0}] {1}", new Object[] { rout, msg });
		} else {
			Stargate.log.log(Level.FINEST, "[Stargate::{0}] {1}", new Object[] { rout, msg });
		}
	}

	public static void sendMessage(final CommandSender player, final String message) {
		sendMessage(player, message, true);
	}

	public static void sendMessage(final CommandSender player, String message, final boolean error) {
		if (message.isEmpty()) {
			return;
		}
		message = message.replaceAll("(&([a-f0-9]))", "?$2");
		if (error) {
			player.sendMessage(ChatColor.RED + getString("prefix") + ChatColor.WHITE + message);
		} else {
			player.sendMessage(ChatColor.GREEN + getString("prefix") + ChatColor.WHITE + message);
		}
	}

	public static void setLine(final Sign sign, final int index, final String text) {
		sign.setLine(index, Stargate.signColor + text);
	}

	public static String getSaveLocation() {
		return Stargate.portalFolder;
	}

	public static String getGateFolder() {
		return Stargate.gateFolder;
	}

	public static String getDefaultNetwork() {
		return Stargate.defNetwork;
	}

	public static String getString(final String name) {
		return Stargate.lang.getString(name);
	}

	public static void openPortal(final Player player, final Portal portal) {
		final Portal destination = portal.getDestination();
		if (portal.isAlwaysOn()) {
			return;
		}
		if (portal.isRandom()) {
			return;
		}
		if (destination == null || destination == portal) {
			sendMessage((CommandSender) player, getString("invalidMsg"));
			return;
		}
		if (portal.isOpen()) {
			if (portal.getActivePlayer() == player) {
				portal.close(false);
			}
			return;
		}
		if (!portal.isFixed() && portal.isActive() && portal.getActivePlayer() != player) {
			sendMessage((CommandSender) player, getString("denyMsg"));
			return;
		}
		if (portal.isPrivate() && !canPrivate(player, portal)) {
			sendMessage((CommandSender) player, getString("denyMsg"));
			return;
		}
		if (destination.isOpen() && !destination.isAlwaysOn()) {
			sendMessage((CommandSender) player, getString("blockMsg"));
			return;
		}
		portal.open(player, false);
	}

	public static boolean hasPerm(final Player player, final String perm) {
		if (Stargate.permDebug) {
			debug("hasPerm::SuperPerm(" + player.getName() + ")", perm + " => " + player.hasPermission(perm));
		}
		return player.hasPermission(perm);
	}

	public static boolean hasPermDeep(final Player player, final String perm) {
		if (!player.isPermissionSet(perm)) {
			if (Stargate.permDebug) {
				debug("hasPermDeep::SuperPerm", perm + " => true");
			}
			return true;
		}
		if (Stargate.permDebug) {
			debug("hasPermDeep::SuperPerms", perm + " => " + player.hasPermission(perm));
		}
		return player.hasPermission(perm);
	}

	public static boolean canAccessWorld(final Player player, final String world) {
		if (hasPerm(player, "stargate.use") || hasPerm(player, "stargate.world")) {
			return hasPermDeep(player, "stargate.world." + world);
		}
		return hasPerm(player, "stargate.world." + world);
	}

	public static boolean canAccessNetwork(final Player player, final String network) {
		if (hasPerm(player, "stargate.use") || hasPerm(player, "stargate.network")) {
			return hasPermDeep(player, "stargate.network." + network);
		}
		if (hasPerm(player, "stargate.network." + network)) {
			return true;
		}
		String playerName = player.getName();
		if (playerName.length() > 11) {
			playerName = playerName.substring(0, 11);
		}
		return network.equals(playerName) && hasPerm(player, "stargate.create.personal");
	}

	public static boolean canAccessServer(final Player player, final String server) {
		if (hasPerm(player, "stargate.use") || hasPerm(player, "stargate.servers")) {
			return hasPermDeep(player, "stargate.server." + server);
		}
		return hasPerm(player, "stargate.server." + server);
	}

	public static boolean canAccessPortal(final Player player, final Portal portal, final boolean deny) {
		final StargateAccessEvent event = new StargateAccessEvent(player, portal, deny);
		Stargate.server.getPluginManager().callEvent((Event) event);
		return !event.getDeny();
	}

	public static boolean isFree(final Player player, final Portal src, final Portal dest) {
		return src.isFree() || (hasPerm(player, "stargate.free") || hasPerm(player, "stargate.free.use"))
				|| (dest != null && !iConomyHandler.chargeFreeDestination && dest.isFree());
	}

	public static boolean canSee(final Player player, final Portal portal) {
		return !portal.isHidden() || (hasPerm(player, "stargate.admin") || hasPerm(player, "stargate.admin.hidden"))
				|| portal.getOwner().equalsIgnoreCase(player.getName());
	}

	public static boolean canPrivate(final Player player, final Portal portal) {
		return portal.getOwner().equalsIgnoreCase(player.getName()) || hasPerm(player, "stargate.admin")
				|| hasPerm(player, "stargate.admin.private");
	}

	public static boolean canOption(final Player player, final String option) {
		return hasPerm(player, "stargate.option") || hasPerm(player, "stargate.option." + option);
	}

	public static boolean canCreate(final Player player, final String network) {
		if (hasPerm(player, "stargate.create")) {
			return true;
		}
		if (hasPerm(player, "stargate.create.network")) {
			return hasPermDeep(player, "stargate.create.network." + network);
		}
		return hasPerm(player, "stargate.create.network." + network);
	}

	public static boolean canCreatePersonal(final Player player) {
		return hasPerm(player, "stargate.create") || hasPerm(player, "stargate.create.personal");
	}

	public static boolean canCreateGate(final Player player, final String gate) {
		if (hasPerm(player, "stargate.create")) {
			return true;
		}
		if (hasPerm(player, "stargate.create.gate")) {
			return hasPermDeep(player, "stargate.create.gate." + gate);
		}
		return hasPerm(player, "stargate.create.gate." + gate);
	}

	public static boolean canDestroy(final Player player, final Portal portal) {
		final String network = portal.getNetwork();
		if (hasPerm(player, "stargate.destroy")) {
			return true;
		}
		if (hasPerm(player, "stargate.destroy.network")) {
			return hasPermDeep(player, "stargate.destroy.network." + network);
		}
		return hasPerm(player, "stargate.destroy.network." + network)
				|| (player.getName().equalsIgnoreCase(portal.getOwner())
						&& hasPerm(player, "stargate.destroy.personal"));
	}

	public static boolean chargePlayer(final Player player, final String target, final int cost) {
		return cost == 0 || !iConomyHandler.useiConomy() || iConomyHandler.chargePlayer(player.getName(), target, cost);
	}

	public static int getUseCost(final Player player, final Portal src, final Portal dest) {
		if (!iConomyHandler.useiConomy()) {
			return 0;
		}
		if (src.isFree()) {
			return 0;
		}
		if (dest != null && !iConomyHandler.chargeFreeDestination && dest.isFree()) {
			return 0;
		}
		if (src.getGate().getToOwner() && src.getOwner().equalsIgnoreCase(player.getName())) {
			return 0;
		}
		if (hasPerm(player, "stargate.free") || hasPerm(player, "stargate.free.use")) {
			return 0;
		}
		return src.getGate().getUseCost();
	}

	public static int getCreateCost(final Player player, final Gate gate) {
		if (!iConomyHandler.useiConomy()) {
			return 0;
		}
		if (hasPerm(player, "stargate.free") || hasPerm(player, "stargate.free.create")) {
			return 0;
		}
		return gate.getCreateCost();
	}

	public static int getDestroyCost(final Player player, final Gate gate) {
		if (!iConomyHandler.useiConomy()) {
			return 0;
		}
		if (hasPerm(player, "stargate.free") || hasPerm(player, "stargate.free.destroy")) {
			return 0;
		}
		return gate.getDestroyCost();
	}

	private Plugin checkPlugin(final String p) {
		final Plugin plugin = this.pm.getPlugin(p);
		return this.checkPlugin(plugin);
	}

	private Plugin checkPlugin(final Plugin plugin) {
		if (plugin != null && plugin.isEnabled()) {
			Stargate.log.log(Level.INFO, "[Stargate] Found {0} (v{1})",
					new Object[] { plugin.getDescription().getName(), plugin.getDescription().getVersion() });
			return plugin;
		}
		return null;
	}

	public static String replaceVars(String format, final String[] search, final String[] replace) {
		if (search.length != replace.length) {
			return "";
		}
		for (int i = 0; i < search.length; ++i) {
			format = format.replace(search[i], replace[i]);
		}
		return format;
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		final String cmd = command.getName();
		if (!cmd.equalsIgnoreCase("sg")) {
			return false;
		}
		if (args.length != 1) {
			return false;
		}
		if (args[0].equalsIgnoreCase("about")) {
			sender.sendMessage("Stargate Plugin created by Drakia");
			if (!Stargate.lang.getString("author").isEmpty()) {
				sender.sendMessage("Language created by " + Stargate.lang.getString("author"));
			}
			return true;
		}
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (!hasPerm(p, "stargate.admin") && !hasPerm(p, "stargate.admin.reload")) {
				sendMessage(sender, "Permission Denied");
				return true;
			}
		}
		if (args[0].equalsIgnoreCase("reload")) {
			for (final Portal p2 : Stargate.activeList) {
				p2.deactivate();
			}
			for (final Portal p2 : Stargate.openList) {
				p2.close(true);
			}
			Stargate.activeList.clear();
			Stargate.openList.clear();
			Portal.clearGates();
			Gate.clearGates();
			final boolean oldEnableBungee = Stargate.enableBungee;
			this.loadConfig();
			this.reloadGates();
			Stargate.lang.setLang(Stargate.langName);
			Stargate.lang.reload();
			if (iConomyHandler.useiConomy && iConomyHandler.economy == null && iConomyHandler.setupeConomy(this.pm)
					&& iConomyHandler.economy != null) {
				Stargate.log.log(Level.INFO, "[Stargate] Vault v{0} found",
						iConomyHandler.vault.getDescription().getVersion());
			}
			if (!iConomyHandler.useiConomy) {
				iConomyHandler.vault = null;
				iConomyHandler.economy = null;
			}
			if (oldEnableBungee != Stargate.enableBungee) {
				if (Stargate.enableBungee) {
					Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin) this, "BungeeCord");
					Bukkit.getMessenger().registerIncomingPluginChannel((Plugin) this, "BungeeCord",
							(PluginMessageListener) new pmListener());
				} else {
					Bukkit.getMessenger().unregisterIncomingPluginChannel((Plugin) this, "BungeeCord");
					Bukkit.getMessenger().unregisterOutgoingPluginChannel((Plugin) this, "BungeeCord");
				}
			}
			sendMessage(sender, "Stargate reloaded");
			return true;
		}
		return false;
	}

	static {
		Stargate.defNetwork = "central";
		Stargate.destroyExplosion = false;
		Stargate.maxGates = 0;
		Stargate.langName = "en";
		Stargate.destMemory = false;
		Stargate.handleVehicles = true;
		Stargate.sortLists = false;
		Stargate.protectEntrance = false;
		Stargate.enableBungee = true;
		Stargate.ignoreEntrance = false;
		Stargate.debug = false;
		Stargate.permDebug = false;
		Stargate.openList = new ConcurrentLinkedQueue<Portal>();
		Stargate.activeList = new ConcurrentLinkedQueue<Portal>();
		Stargate.blockPopulatorQueue = new LinkedList<BloxPopulator>();
		Stargate.bungeeQueue = new HashMap<String, String>();
	}

	private class vListener implements Listener {
		@EventHandler
		public void onVehicleMove(final VehicleMoveEvent event) {
			if (!Stargate.handleVehicles) {
				return;
			}
			final Entity passenger = event.getVehicle().getPassenger();
			final Vehicle vehicle = event.getVehicle();
			final Portal portal = Portal.getByEntrance(event.getTo());
			if (portal == null || !portal.isOpen()) {
				return;
			}
			if (portal.isBungee()) {
				return;
			}
			if (passenger instanceof Player) {
				final Player player = (Player) passenger;
				if (!portal.isOpenFor(player)) {
					Stargate.sendMessage((CommandSender) player, Stargate.getString("denyMsg"));
					return;
				}
				final Portal dest = portal.getDestination(player);
				if (dest == null) {
					return;
				}
				boolean deny = false;
				if (!Stargate.canAccessNetwork(player, portal.getNetwork())) {
					deny = true;
				}
				if (!Stargate.canAccessWorld(player, dest.getWorld().getName())) {
					deny = true;
				}
				if (!Stargate.canAccessPortal(player, portal, deny)) {
					Stargate.sendMessage((CommandSender) player, Stargate.getString("denyMsg"));
					player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
					portal.close(false);
					return;
				}
				final int cost = Stargate.getUseCost(player, portal, dest);
				if (cost > 0) {
					final String target = portal.getGate().getToOwner() ? portal.getOwner() : null;
					if (!Stargate.chargePlayer(player, target, cost)) {
						Stargate.sendMessage((CommandSender) player, Stargate.getString("inFunds"));
						player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
						portal.close(false);
						return;
					}
					String deductMsg = Stargate.getString("ecoDeduct");
					deductMsg = Stargate.replaceVars(deductMsg, new String[] { "%cost%", "%portal%" },
							new String[] { iConomyHandler.format(cost), portal.getName() });
					Stargate.sendMessage((CommandSender) player, deductMsg, false);
					if (target != null) {
						final Player p = Stargate.server.getPlayer(target);
						if (p != null) {
							String obtainedMsg = Stargate.getString("ecoObtain");
							obtainedMsg = Stargate.replaceVars(obtainedMsg, new String[] { "%cost%", "%portal%" },
									new String[] { iConomyHandler.format(cost), portal.getName() });
							Stargate.sendMessage((CommandSender) p, obtainedMsg, false);
						}
					}
				}
				Stargate.sendMessage((CommandSender) player, Stargate.getString("teleportMsg"), false);
				player.playEffect(player.getLocation(),Effect.CLOUD, 10);
				player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 0.5F, 0.5F);
				dest.teleport(vehicle);
				portal.close(false);
			} else {
				final Portal dest2 = portal.getDestination();
				if (dest2 == null) {
					return;
				}
				dest2.teleport(vehicle);
			}
		}
	}

	private class pListener implements Listener {
		@EventHandler
		public void onPlayerJoin(final PlayerJoinEvent event) {
			if (!Stargate.enableBungee) {
				return;
			}
			final Player player = event.getPlayer();
			final String destination = Stargate.bungeeQueue.remove(player.getName().toLowerCase());
			if (destination == null) {
				return;
			}
			final Portal portal = Portal.getBungeeGate(destination);
			if (portal == null) {
				Stargate.debug("PlayerJoin", "Error fetching destination portal: " + destination);
				return;
			}
			portal.teleport(player, portal, null);
			player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
		}

		@EventHandler
		public void onPlayerPortal(final PlayerPortalEvent event) {
			if (event.isCancelled()) {
				return;
			}
			final Location from = event.getFrom();
			if (from == null) {
				Stargate.debug("onPlayerPortal", "From location is null. Stupid Bukkit");
				return;
			}
			final World world = from.getWorld();
			final int cX = from.getBlockX();
			final int cY = from.getBlockY();
			final int cZ = from.getBlockZ();
			for (int i = -2; i < 2; ++i) {
				for (int j = -2; j < 2; ++j) {
					for (int k = -2; k < 2; ++k) {
						final Block b = world.getBlockAt(cX + i, cY + j, cZ + k);
						final Portal portal = Portal.getByEntrance(b);
						if (portal != null) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}

		@EventHandler
		public void onPlayerMove(final PlayerMoveEvent event) {
			if (event.isCancelled()) {
				return;
			}
			if (event.getFrom().getBlockX() == event.getTo().getBlockX()
					&& event.getFrom().getBlockY() == event.getTo().getBlockY()
					&& event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
				return;
			}
			final Player player = event.getPlayer();
			final Portal portal = Portal.getByEntrance(event.getTo());
			if (portal == null || !portal.isOpen()) {
				return;
			}
			if (!portal.isOpenFor(player)) {
				Stargate.sendMessage((CommandSender) player, Stargate.getString("denyMsg"));
				portal.teleport(player, portal, event);
				player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
				return;
			}
			final Portal destination = portal.getDestination(player);
			if (!portal.isBungee() && destination == null) {
				return;
			}
			boolean deny = false;
			if (portal.isBungee()) {
				if (!Stargate.canAccessServer(player, portal.getNetwork())) {
					deny = true;
				}
			} else {
				if (!Stargate.canAccessNetwork(player, portal.getNetwork())) {
					deny = true;
				}
				if (!Stargate.canAccessWorld(player, destination.getWorld().getName())) {
					deny = true;
				}
			}
			if (!Stargate.canAccessPortal(player, portal, deny)) {
				Stargate.sendMessage((CommandSender) player, Stargate.getString("denyMsg"));
				portal.teleport(player, portal, event);
				player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
				portal.close(false);
				return;
			}
			final int cost = Stargate.getUseCost(player, portal, destination);
			if (cost > 0) {
				final String target = portal.getGate().getToOwner() ? portal.getOwner() : null;
				if (!Stargate.chargePlayer(player, target, cost)) {
					Stargate.sendMessage((CommandSender) player, "Insufficient Funds");
					player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
					portal.close(false);
					return;
				}
				String deductMsg = Stargate.getString("ecoDeduct");
				deductMsg = Stargate.replaceVars(deductMsg, new String[] { "%cost%", "%portal%" },
						new String[] { iConomyHandler.format(cost), portal.getName() });
				Stargate.sendMessage((CommandSender) player, deductMsg, false);
				if (target != null) {
					final Player p = Stargate.server.getPlayer(target);
					if (p != null) {
						String obtainedMsg = Stargate.getString("ecoObtain");
						obtainedMsg = Stargate.replaceVars(obtainedMsg, new String[] { "%cost%", "%portal%" },
								new String[] { iConomyHandler.format(cost), portal.getName() });
						Stargate.sendMessage((CommandSender) p, obtainedMsg, false);
					}
				}
			}
			Stargate.sendMessage((CommandSender) player, Stargate.getString("teleportMsg"), false);
			if (!portal.isBungee()) {
				destination.teleport(player, portal, event);
				player.playEffect(player.getLocation(),Effect.CLOUD, 10);
				
				player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 0.5F, 0.5F);			
				//player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 2.0f, 6.0f);
				portal.close(false);
				return;
			}
			if (!Stargate.enableBungee) {
				player.sendMessage(Stargate.getString("bungeeDisabled"));
				player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
				portal.close(false);
				return;
			}
			portal.teleport(player, portal, event);
			try {
				final String msg = event.getPlayer().getName() + "#@#" + portal.getDestinationName();
				final ByteArrayOutputStream bao = new ByteArrayOutputStream();
				final DataOutputStream msgData = new DataOutputStream(bao);
				msgData.writeUTF("Forward");
				msgData.writeUTF(portal.getNetwork());
				msgData.writeUTF("SGBungee");
				msgData.writeShort(msg.length());
				msgData.writeBytes(msg);
				player.sendPluginMessage((Plugin) Stargate.stargate, "BungeeCord", bao.toByteArray());
			} catch (IOException ex) {
				Stargate.log.severe("[Stargate] Error sending BungeeCord teleport packet");
				return;
			}
			try {
				final ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
				final DataOutputStream msgData2 = new DataOutputStream(bao2);
				msgData2.writeUTF("Connect");
				msgData2.writeUTF(portal.getNetwork());
				player.sendPluginMessage((Plugin) Stargate.stargate, "BungeeCord", bao2.toByteArray());
				bao2.reset();
			} catch (IOException ex) {
				Stargate.log.severe("[Stargate] Error sending BungeeCord connect packet");
				return;
			}
			portal.close(false);
		}

		@EventHandler
		public void onPlayerInteract(final PlayerInteractEvent event) {
			final Player player = event.getPlayer();
			Block block = null;
			Label_0047: {
				if (event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_AIR) {
					try {
						player.getTargetBlock((Set) null, 5);
						break Label_0047;
					} catch (IllegalStateException ex) {
						return;
					}
				}
				block = event.getClickedBlock();
			}
			if (block == null) {
				return;
			}
			if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (block.getType() == Material.WALL_SIGN) {
						final Portal portal = Portal.getByBlock(block);
						if (portal == null) {
							return;
						}
						event.setUseInteractedBlock(Event.Result.DENY);
						if (player.getGameMode().equals((Object) GameMode.CREATIVE)) {
							event.setCancelled(true);
						}
						boolean deny = false;
						if (!Stargate.canAccessNetwork(player, portal.getNetwork())) {
							deny = true;
						}
						if (!Stargate.canAccessPortal(player, portal, deny)) {
							Stargate.sendMessage((CommandSender) player, Stargate.getString("denyMsg"));
							return;
						}
						if (!portal.isOpen() && !portal.isFixed()) {
							portal.cycleDestination(player, -1);
						}
					} else if (block.getType() == Material.STONE_BUTTON) {
						final Portal portal = Portal.getByBlock(block);
						if (portal == null) {
							return;
						}
						event.setUseInteractedBlock(Event.Result.DENY);
						if (player.getGameMode().equals((Object) GameMode.CREATIVE)) {
							event.setCancelled(true);
						}
						boolean deny = false;
						if (!Stargate.canAccessNetwork(player, portal.getNetwork())) {
							deny = true;
						}
						if (!Stargate.canAccessPortal(player, portal, deny)) {
							Stargate.sendMessage((CommandSender) player, Stargate.getString("denyMsg"));
							return;
						}
						Stargate.openPortal(player, portal);
					}
				}
				return;
			}
			if (block.getType() != Material.WALL_SIGN) {
				if (block.getType() == Material.STONE_BUTTON) {
					final Portal portal = Portal.getByBlock(block);
					if (portal == null) {
						return;
					}
					event.setUseItemInHand(Event.Result.DENY);
					event.setUseInteractedBlock(Event.Result.DENY);
					boolean deny = false;
					if (!Stargate.canAccessNetwork(player, portal.getNetwork())) {
						deny = true;
					}
					if (!Stargate.canAccessPortal(player, portal, deny)) {
						Stargate.sendMessage((CommandSender) player, Stargate.getString("denyMsg"));
						return;
					}
					Stargate.openPortal(player, portal);
					if (portal.isOpenFor(player)) {
						event.setUseInteractedBlock(Event.Result.ALLOW);
					}
				}
				return;
			}
			final Portal portal = Portal.getByBlock(block);
			if (portal == null) {
				return;
			}
			event.setUseItemInHand(Event.Result.DENY);
			event.setUseInteractedBlock(Event.Result.DENY);
			boolean deny = false;
			if (!Stargate.canAccessNetwork(player, portal.getNetwork())) {
				deny = true;
			}
			if (!Stargate.canAccessPortal(player, portal, deny)) {
				Stargate.sendMessage((CommandSender) player, Stargate.getString("denyMsg"));
				return;
			}
			if (!portal.isOpen() && !portal.isFixed()) {
				portal.cycleDestination(player);
			}
		}
	}

	private class bListener implements Listener {
		@EventHandler
		public void onSignChange(final SignChangeEvent event) {
			if (event.isCancelled()) {
				return;
			}
			final Player player = event.getPlayer();
			final Block block = event.getBlock();
			if (block.getType() != Material.WALL_SIGN) {
				return;
			}
			final Portal portal = Portal.createPortal(event, player);
			if (portal == null) {
				return;
			}
			Stargate.sendMessage((CommandSender) player, Stargate.getString("createMsg"), false);
			Stargate.debug("onSignChange", "Initialized stargate: " + portal.getName());
			Stargate.server.getScheduler().scheduleSyncDelayedTask((Plugin) Stargate.stargate,
					(Runnable) new Runnable() {
						@Override
						public void run() {
							portal.drawSign();
						}
					}, 1L);
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onBlockBreak(final BlockBreakEvent event) {
			if (event.isCancelled()) {
				return;
			}
			final Block block = event.getBlock();
			final Player player = event.getPlayer();
			Portal portal = Portal.getByBlock(block);
			if (portal == null && Stargate.protectEntrance) {
				portal = Portal.getByEntrance(block);
			}
			if (portal == null) {
				return;
			}
			boolean deny = false;
			String denyMsg = "";
			if (!Stargate.canDestroy(player, portal)) {
				denyMsg = "Permission Denied";
				deny = true;
				Stargate.log.log(Level.INFO, "[Stargate] {0} tried to destroy gate", player.getName());
			}
			int cost = Stargate.getDestroyCost(player, portal.getGate());
			final StargateDestroyEvent dEvent = new StargateDestroyEvent(portal, player, deny, denyMsg, cost);
			Stargate.server.getPluginManager().callEvent((Event) dEvent);
			if (dEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			}
			if (dEvent.getDeny()) {
				Stargate.sendMessage((CommandSender) player, dEvent.getDenyReason());
				event.setCancelled(true);
				return;
			}
			cost = dEvent.getCost();
			if (cost != 0) {
				if (!Stargate.chargePlayer(player, null, cost)) {
					Stargate.debug("onBlockBreak", "Insufficient Funds");
					Stargate.sendMessage((CommandSender) player, Stargate.getString("inFunds"));
					event.setCancelled(true);
					return;
				}
				if (cost > 0) {
					String deductMsg = Stargate.getString("ecoDeduct");
					deductMsg = Stargate.replaceVars(deductMsg, new String[] { "%cost%", "%portal%" },
							new String[] { iConomyHandler.format(cost), portal.getName() });
					Stargate.sendMessage((CommandSender) player, deductMsg, false);
				} else if (cost < 0) {
					String refundMsg = Stargate.getString("ecoRefund");
					refundMsg = Stargate.replaceVars(refundMsg, new String[] { "%cost%", "%portal%" },
							new String[] { iConomyHandler.format(-cost), portal.getName() });
					Stargate.sendMessage((CommandSender) player, refundMsg, false);
				}
			}
			portal.unregister(true);
			Stargate.sendMessage((CommandSender) player, Stargate.getString("destroyMsg"), false);
		}

		@EventHandler
		public void onBlockPhysics(final BlockPhysicsEvent event) {
			final Block block = event.getBlock();
			Portal portal = null;
			if (block.getTypeId() == 90) {
				portal = Portal.getByEntrance(block);
			} else if (block.getTypeId() == 77) {
				portal = Portal.getByControl(block);
			}
			if (portal != null) {
				event.setCancelled(true);
			}
		}

		@EventHandler
		public void onBlockFromTo(final BlockFromToEvent event) {
			final Portal portal = Portal.getByEntrance(event.getBlock());
			if (portal != null) {
				event.setCancelled(event.getBlock().getY() == event.getToBlock().getY());
			}
		}

		@EventHandler
		public void onPistonExtend(final BlockPistonExtendEvent event) {
			for (final Block block : event.getBlocks()) {
				final Portal portal = Portal.getByBlock(block);
				if (portal != null) {
					event.setCancelled(true);
				}
			}
		}

		@EventHandler
		public void onPistonRetract(final BlockPistonRetractEvent event) {
			if (!event.isSticky()) {
				return;
			}
			final Block affected = event.getRetractLocation().getBlock();
			final Portal portal = Portal.getByBlock(affected);
			if (portal != null) {
				event.setCancelled(true);
			}
		}
	}

	private class wListener implements Listener {
		@EventHandler
		public void onWorldLoad(final WorldLoadEvent event) {
			final World w = event.getWorld();
			if (w.getBlockAt(w.getSpawnLocation()).getWorld() != null) {
				Portal.loadAllGates(w);
			}
		}

		@EventHandler
		public void onWorldUnload(final WorldUnloadEvent event) {
			Stargate.debug("onWorldUnload", "Reloading all Stargates");
			final World w = event.getWorld();
			Portal.clearGates();
			for (final World world : Stargate.server.getWorlds()) {
				if (world.equals(w)) {
					continue;
				}
				Portal.loadAllGates(world);
			}
		}
	}

	private class eListener implements Listener {
		@EventHandler
		public void onEntityExplode(final EntityExplodeEvent event) {
			if (event.isCancelled()) {
				return;
			}
			for (final Block b : event.blockList()) {
				final Portal portal = Portal.getByBlock(b);
				if (portal == null) {
					continue;
				}
				if (Stargate.destroyExplosion) {
					portal.unregister(true);
				} else {
					Stargate.blockPopulatorQueue.add(new BloxPopulator(new Blox(b), b.getTypeId(), b.getData()));
					event.setCancelled(true);
				}
			}
		}
	}

	private class sListener implements Listener {
		@EventHandler
		public void onPluginEnable(final PluginEnableEvent event) {
			if (iConomyHandler.setupVault(event.getPlugin())) {
				Stargate.log.log(Level.INFO, "[Stargate] Vault v{0} found",
						iConomyHandler.vault.getDescription().getVersion());
			}
		}

		@EventHandler
		public void onPluginDisable(final PluginDisableEvent event) {
			if (iConomyHandler.checkLost(event.getPlugin())) {
				Stargate.log.info("[Stargate] Vault plugin lost.");
			}
		}
	}

	private class BlockPopulatorThread implements Runnable {
		@Override
		public void run() {
			final long sTime = System.nanoTime();
			while (System.nanoTime() - sTime < 50000000L) {
				final BloxPopulator b = Stargate.blockPopulatorQueue.poll();
				if (b == null) {
					return;
				}
				b.getBlox().getBlock().setTypeId(b.getMat());
				b.getBlox().getBlock().setData(b.getData());
			}
		}
	}

	private class SGThread implements Runnable {
		@Override
		public void run() {
			final long time = System.currentTimeMillis() / 1000L;
			Iterator<Portal> iter = Stargate.openList.iterator();
			while (iter.hasNext()) {
				final Portal p = iter.next();
				if (p.isAlwaysOn()) {
					continue;
				}
				if (!p.isOpen()) {
					continue;
				}
				if (time <= p.getOpenTime() + 10L) {
					continue;
				}
				p.close(false);
				iter.remove();
			}
			iter = Stargate.activeList.iterator();
			while (iter.hasNext()) {
				final Portal p = iter.next();
				if (!p.isActive()) {
					continue;
				}
				if (time <= p.getOpenTime() + 10L) {
					continue;
				}
				p.deactivate();
				iter.remove();
			}
		}
	}
}
