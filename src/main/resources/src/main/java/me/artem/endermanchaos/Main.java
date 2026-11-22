package me.artem.endermanchaos;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EndermanListener(this), this);
        getLogger().info("EndermanChaos enabled!");
    }
}
