package br.trcraft.Stargate;

import net.milkbowl.vault.*;
import net.milkbowl.vault.economy.*;
import org.bukkit.plugin.*;

public class iConomyHandler
{
    public static boolean useiConomy;
    public static Vault vault;
    public static Economy economy;
    public static int useCost;
    public static int createCost;
    public static int destroyCost;
    public static boolean toOwner;
    public static boolean chargeFreeDestination;
    public static boolean freeGatesGreen;
    
    public static double getBalance(final String player) {
        if (!iConomyHandler.useiConomy) {
            return 0.0;
        }
        if (iConomyHandler.economy != null) {
            return iConomyHandler.economy.getBalance(player);
        }
        return 0.0;
    }
    
    public static boolean chargePlayer(final String player, final String target, final double amount) {
        if (!iConomyHandler.useiConomy) {
            return true;
        }
        if (iConomyHandler.economy == null) {
            return false;
        }
        if (player.equals(target)) {
            return true;
        }
        if (!iConomyHandler.economy.has(player, amount)) {
            return false;
        }
        iConomyHandler.economy.withdrawPlayer(player, amount);
        if (target != null) {
            iConomyHandler.economy.depositPlayer(target, amount);
        }
        return true;
    }
    
    public static boolean useiConomy() {
        return iConomyHandler.useiConomy && iConomyHandler.economy != null;
    }
    
    public static String format(final int amt) {
        if (iConomyHandler.economy != null) {
            return iConomyHandler.economy.format((double)amt);
        }
        return "";
    }
    
    public static boolean setupeConomy(final PluginManager pm) {
        if (!iConomyHandler.useiConomy) {
            return false;
        }
        final Plugin p = pm.getPlugin("Vault");
        return p != null && setupVault(p);
    }
    
    public static boolean setupVault(final Plugin p) {
        if (!iConomyHandler.useiConomy) {
            return false;
        }
        if (p == null || !p.isEnabled()) {
            return false;
        }
        if (!p.getDescription().getName().equals("Vault")) {
            return false;
        }
        final RegisteredServiceProvider<Economy> economyProvider = (RegisteredServiceProvider<Economy>)Stargate.server.getServicesManager().getRegistration((Class)Economy.class);
        if (economyProvider != null) {
            iConomyHandler.vault = (Vault)p;
            iConomyHandler.economy = (Economy)economyProvider.getProvider();
        }
        return iConomyHandler.economy != null;
    }
    
    public static boolean setupRegister(final Plugin p) {
        return iConomyHandler.useiConomy && iConomyHandler.vault == null && p != null && p.isEnabled();
    }
    
    public static boolean checkLost(final Plugin p) {
        if (p.equals(iConomyHandler.vault)) {
            iConomyHandler.economy = null;
            iConomyHandler.vault = null;
            return true;
        }
        return false;
    }
    
    static {
        iConomyHandler.useiConomy = false;
        iConomyHandler.vault = null;
        iConomyHandler.economy = null;
        iConomyHandler.useCost = 0;
        iConomyHandler.createCost = 0;
        iConomyHandler.destroyCost = 0;
        iConomyHandler.toOwner = false;
        iConomyHandler.chargeFreeDestination = true;
        iConomyHandler.freeGatesGreen = false;
    }
}
