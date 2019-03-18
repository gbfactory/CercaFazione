package it.factorymine.cerca;

import org.bukkit.plugin.java.*;
import java.util.logging.*;
import net.milkbowl.vault.economy.*;
import net.milkbowl.vault.permission.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.*;
import java.util.*;

public class Main extends JavaPlugin implements Listener
{
    private static final Logger log;
    private static Economy econ;
    private static Permission perms;
    public ArrayList<Player> gui;
    
    static {
        log = Logger.getLogger("Minecraft");
        Main.econ = null;
        Main.perms = null;
    }
    
    public Main() {
        this.gui = new ArrayList<Player>();
    }
    
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        if (!this.setupEconomy()) {
            Main.log.severe(String.format("[%s] - Plugin disabilitato - Manca Vault!", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.setupPermissions();
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        if (e.getInventory().getTitle().equals("§c§lRicerca Fazione")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            Bukkit.dispatchCommand((CommandSender)e.getWhoClicked(), "f invite " + ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
        }
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String string, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("ricerca") && sender instanceof Player) {
            final Player player = (Player)sender;
            if (args.length == 0) {
            	player.sendMessage("§8§m<------------------§r§8[ §6Ricerca Fazione §8]§8§m------------------>");
            	player.sendMessage(" §7Per entrare nella lista dei giocatori che cercano una fazione");
            	player.sendMessage(" §7digita il comando §6/ricerca entra§7. Una volta entrato nella");
            	player.sendMessage(" §7lista, gli altri giocatori potranno vedere le tue statistiche");
            	player.sendMessage(" §7nella GUI accessibile con §6/ricerca lista§7. Se un giocatore di un");
            	player.sendMessage(" §7altra fazione vuole invitarti dovrà semplicemente cliccare");
            	player.sendMessage(" §7la tua testa nella GUI. Se vuoi uscire dalla lista di ricerca.");
            	player.sendMessage(" §7fazione basta digitare il comando §6/ricerca esci§7. Buona Ricerca");
            	player.sendMessage("§8§m<---------------------------------------------------->");
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("entra")) {
                    if (this.gui.contains(player)) {
                        player.sendMessage("§6Factions §9» §7Sei già entrato nella lista di ricerca fazioni.");
                    }
                    else {
                        this.gui.add(player);
                        player.sendMessage("§6Factions §9» §7Sei entrato nella lista di ricerca fazioni.");
                    }
                }
                if (args[0].equalsIgnoreCase("esci")) {
                    if (this.gui.contains(player)) {
                        this.gui.remove(player);
                        player.sendMessage("§6Factions §9» §7Sei uscito dalla lista di ricerca fazioni.");
                    }
                    else {
                        player.sendMessage("§6Factions §9» §7Non sei nella lista di ricerca fazioni.");
                    }
                }    
                if (args[0].equalsIgnoreCase("lista")) {
                    	this.openGUI(player);
                }
            }
        }
        return false;
    }
    
    private boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = (RegisteredServiceProvider<Permission>)this.getServer().getServicesManager().getRegistration((Class)Permission.class);
        Main.perms = (Permission)rsp.getProvider();
        return Main.perms != null;
    }
    
    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (rsp == null) {
            return false;
        }
        Main.econ = (Economy)rsp.getProvider();
        return Main.econ != null;
    }
    
    public void openGUI(final Player player) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, this.getCorrectSize(), "§c§lRicerca Fazione");
        for (final Player p : this.gui) {
            final ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
            final SkullMeta playerheadmeta = (SkullMeta)playerhead.getItemMeta();
            playerheadmeta.setOwner(p.getName());
            playerheadmeta.setDisplayName(ChatColor.GOLD + p.getName());
            final ArrayList<String> lore = new ArrayList<String>();
            lore.add("§8§m<------------------->");
            lore.add(" §b* §7Kills &§» &§" + p.getStatistic(Statistic.PLAYER_KILLS) + " &§⚔");
            lore.add(" §b* §7Morti &§» &§" + p.getStatistic(Statistic.DEATHS) + " &§☠");
            lore.add(" ");
            lore.add(" §b* §7Soldi &§» &§" + Main.econ.getBalance((OfflinePlayer)p) + " &§⛁");
            lore.add(" §b* §7Rank &§» &§" + Main.perms.getPrimaryGroup(p) + " &§⚜");
            lore.add("§8§m<------------------->");
            playerheadmeta.setLore((List)lore);
            playerhead.setItemMeta((ItemMeta)playerheadmeta);
            inv.addItem(new ItemStack[] { playerhead });
        }
        player.openInventory(inv);
    }
    
    public int getCorrectSize() {
        if (this.gui.size() <= 9) {
            return 9;
        }
        if (this.gui.size() <= 18) {
            return 18;
        }
        if (this.gui.size() <= 27) {
            return 27;
        }
        if (this.gui.size() <= 36) {
            return 36;
        }
        if (this.gui.size() <= 45) {
            return 9;
        }
        if (this.gui.size() <= 54) {
            return 9;
        }
        return 9;
    }
}
