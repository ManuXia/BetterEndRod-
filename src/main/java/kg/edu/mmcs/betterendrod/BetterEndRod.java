package kg.edu.mmcs.betterendrod;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class BetterEndRod extends JavaPlugin {

    private int expInterval;
    private int expAmount;
    private int radius;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

        getLogger().info("BetterEndRod enabled🤓");
        startEndRodExpSpawner();
    }

    @Override
    public void onDisable() {
        getLogger().info("BetterEndRod disabled🤓");
    }

    private void loadConfigValues() {
        expInterval = getConfig().getInt("exp-interval-ticks", 20);
        expAmount = getConfig().getInt("exp-amount", 5);
        radius = getConfig().getInt("radius", 16);
    }

    private void startEndRodExpSpawner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    spawnExpNearPlayer(player);
                }
            }
        }.runTaskTimer(this, 0L, expInterval);
    }

    private void spawnExpNearPlayer(Player player) {
        World world = player.getWorld();
        Location playerLoc = player.getLocation();

        int px = playerLoc.getBlockX();
        int py = playerLoc.getBlockY();
        int pz = playerLoc.getBlockZ();

        Set<Block> visited = new HashSet<>();

        for (int x = px - radius; x <= px + radius; x++) {
            for (int y = py - radius; y <= py + radius; y++) {
                for (int z = pz - radius; z <= pz + radius; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (visited.contains(block)) continue;
                    visited.add(block);

                    if (block.getType() == Material.END_ROD) {
                        Bukkit.getScheduler().runTask(thisPlugin(), () -> spawnExpAbove(block));
                    }
                }
            }
        }
    }

    private void spawnExpAbove(Block block) {
        Location loc = block.getLocation().add(0.5, 1.2, 0.5);
        World world = block.getWorld();
        world.spawn(loc, ExperienceOrb.class, orb -> orb.setExperience(expAmount));
        world.spawnParticle(Particle.ENCHANT, loc, 5, 0.2, 0.2, 0.2, 0.05);
    }

    private JavaPlugin thisPlugin() {
        return this;
    }
}
