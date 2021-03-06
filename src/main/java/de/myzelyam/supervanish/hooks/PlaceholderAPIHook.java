/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.myzelyam.supervanish.hooks;

import de.myzelyam.supervanish.SuperVanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.UUID;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;

public class PlaceholderAPIHook extends PluginHook {

    public PlaceholderAPIHook(SuperVanish superVanish) {
        super(superVanish);
    }

    public static String translatePlaceholders(String msg, Player p) {
        return PlaceholderAPI.setPlaceholders(p, msg);
    }

    @Override
    public void onPluginEnable(Plugin plugin) {
        PlaceholderAPI.unregisterPlaceholderHook(superVanish);
        PlaceholderAPI.registerPlaceholderHook(superVanish, new PlaceholderHook() {
            @Override
            public String onPlaceholderRequest(Player p, String id) {
                try {
                    if (id.equalsIgnoreCase("isvanished")
                            || id.equalsIgnoreCase("isinvisible")
                            || id.equalsIgnoreCase("vanished")
                            || id.equalsIgnoreCase("invisible"))
                        return superVanish.getVanishStateMgr().isVanished(p.getUniqueId()) ? "Yes"
                                : "No";
                    if (id.equalsIgnoreCase("onlinevanishedplayers")
                            || id.equalsIgnoreCase("onlinevanished")
                            || id.equalsIgnoreCase("invisibleplayers")
                            || id.equalsIgnoreCase("vanishedplayers")
                            || id.equalsIgnoreCase("hiddenplayers")) {
                        Collection<UUID> onlineVanishedPlayers = superVanish.getVanishStateMgr()
                                .getOnlineVanishedPlayers();
                        String playerListMessage = "";
                        for (UUID uuid : onlineVanishedPlayers) {
                            Player onlineVanished = Bukkit.getPlayer(uuid);
                            if (onlineVanished == null) continue;
                            if (superVanish.getSettings().getBoolean(
                                    "IndicationFeatures.LayeredPermissions.HideInvisibleInCommands", false)
                                    && !superVanish.hasPermissionToSee(p, onlineVanished)) {
                                continue;
                            }
                            playerListMessage = playerListMessage + onlineVanished.getName() + ", ";
                        }
                        return playerListMessage.length() > 3
                                ? playerListMessage.substring(0, playerListMessage.length() - 2)
                                : playerListMessage;
                    }
                    if (id.equalsIgnoreCase("playercount")
                            || id.equalsIgnoreCase("onlineplayers")) {
                        int playercount = Bukkit.getOnlinePlayers().size();
                        for (UUID uuid : superVanish.getVanishStateMgr()
                                .getOnlineVanishedPlayers()) {
                            Player onlineVanished = Bukkit.getPlayer(uuid);
                            if (onlineVanished == null) continue;
                            if (p == null || !superVanish.canSee(p, onlineVanished)) playercount--;
                        }
                        return playercount + "";
                    }
                } catch (Exception e) {
                    superVanish.logException(e);
                }
                return "";
            }
        });
    }
}
