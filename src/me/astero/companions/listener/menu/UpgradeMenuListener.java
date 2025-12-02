package me.astero.companions.listener.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.astero.companions.CompanionsPlugin;
import me.astero.companions.companiondata.PlayerCache;
import me.astero.companions.companiondata.PlayerData;

public class UpgradeMenuListener implements Listener {

    private final CompanionsPlugin main;

    public UpgradeMenuListener(CompanionsPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        try {
            boolean upgradeMenu = ChatColor.translateAlternateColorCodes('&', event.getView().getTitle())
                    .equals(ChatColor.translateAlternateColorCodes('&', main.getFileHandler().getUpgradeAbilitiesTitle()));
            if (!upgradeMenu || event.getCurrentItem() == null) {
                return;
            }

            event.setCancelled(true);
            String clickedName = event.getCurrentItem().getItemMeta().getDisplayName();

            if (clickedName.equals(ChatColor.translateAlternateColorCodes('&', main.getFileHandler().getGoBackUDName()))) {
                Bukkit.dispatchCommand(player, main.getFileHandler().getUpgradeGoBackCommand());
                return;
            }

            if (!PlayerData.instanceOf(player).hasActiveCompanionSelected()) {
                noCompanionMessage(player);
                return;
            }

            if (clickedName.equals(ChatColor.translateAlternateColorCodes('&', main.getFileHandler().getAbilityLevelName()))) {
                handleAbilityLevelClick(player, event.getClick());
                return;
            }

            if (clickedName.equals(ChatColor.translateAlternateColorCodes('&', main.getFileHandler().getAbilityLevelMName()))
                    && event.getClick() == ClickType.RIGHT) {
                main.getCompanionUtil().buyUpgradeAbility(player, false);
                Bukkit.dispatchCommand(player, "companions upgrade");
                return;
            }

            if (clickedName.equals(ChatColor.translateAlternateColorCodes('&', main.getFileHandler().getRenameCompanionName()))) {
                main.getCompanionUtil().buyUpgradeRename(player);
            } else if (clickedName.equals(ChatColor.translateAlternateColorCodes('&', main.getFileHandler().getHideCompanionName()))) {
                main.getCompanionUtil().buyUpgradeHideName(player);
            } else if (clickedName.equals(ChatColor.translateAlternateColorCodes('&', main.getFileHandler().getChangeWeaponName()))) {
                main.getCompanionUtil().buyUpgradeChangeWeapon(player);
            }
        } catch (NullPointerException ignored) {
            // Slot or display name was missing; safe to ignore.
        }
    }

    private void handleAbilityLevelClick(Player player, ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            main.getCompanionUtil().buyUpgradeAbility(player, true);
        } else if (clickType == ClickType.RIGHT) {
            String active = PlayerData.instanceOf(player).getActiveCompanionName().toLowerCase();
            int level = PlayerCache.instanceOf(player.getUniqueId()).getOwnedCache().get(active).getAbilityLevel();

            if (level != 1) {
                main.getCompanionUtil().buyUpgradeAbility(player, false);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        main.getCompanionUtil().getPrefix() + main.getFileHandler().getAbilityDowngradedMaxedMessage()));
            }
        }

        Bukkit.dispatchCommand(player, "companions upgrade");
    }

    private void noCompanionMessage(Player player) {
        player.closeInventory();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                main.getCompanionUtil().getPrefix() + main.getFileHandler().getNoActiveCompanionMessage()));
    }
}

