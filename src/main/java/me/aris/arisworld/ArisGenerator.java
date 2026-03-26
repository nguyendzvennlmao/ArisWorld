package me.aris.arisworld;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArisGenerator extends ChunkGenerator {
    private final boolean isDesert;

    public ArisGenerator(boolean isDesert) {
        this.isDesert = isDesert;
    }

    @Override
    public void generateNoise(WorldInfo wi, Random r, int cx, int cz, ChunkData cd) {
        SimplexOctaveGenerator gen = new SimplexOctaveGenerator(new Random(wi.getSeed()), 8);
        gen.setScale(isDesert ? 0.012D : 0.004D);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double noise = gen.noise(cx * 16 + x, cz * 16 + z, 0.5D, 0.5D) * 0.5D + 0.5D;
                int height = (int) (noise * (isDesert ? 45 : 190) + 60);

                cd.setBlock(x, -64, z, Material.BEDROCK);
                for (int y = -63; y < height; y++) {
                    cd.setBlock(x, y, z, isDesert ? Material.SANDSTONE : Material.STONE);
                }

                if (isDesert) {
                    cd.setBlock(x, height, z, Material.SAND);
                } else {
                    if (height > 185) cd.setBlock(x, height, z, Material.SNOW_BLOCK);
                    else if (height < 70) cd.setBlock(x, height, z, Material.GRAVEL);
                    else cd.setBlock(x, height, z, Material.GRASS_BLOCK);
                }

                if (!isDesert && height < 64) {
                    for (int y = height + 1; y <= 64; y++) {
                        cd.setBlock(x, y, z, Material.WATER);
                    }
                }
            }
        }
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.singletonList(new BlockPopulator() {
            @Override
            public void populate(WorldInfo wi, Random r, int cx, int cz, org.bukkit.generator.LimitedRegion lr) {
                if (r.nextInt(100) < 4) {
                    int x = cx * 16 + r.nextInt(16);
                    int z = cz * 16 + r.nextInt(16);
                    int y = -64;
                    for (int h = 250; h > -64; h--) {
                        Material m = lr.getType(x, h, z);
                        if (m != Material.AIR && m != Material.WATER && m != Material.SNOW_BLOCK) {
                            y = h + 1;
                            break;
                        }
                    }
                    if (y > -60 && y < 240) buildStructure(lr, x, y, z, r);
                }
            }
        });
    }

    private void buildStructure(org.bukkit.generator.LimitedRegion lr, int x, int y, int z, Random r) {
        Material m = isDesert ? Material.CHISELED_SANDSTONE : Material.MOSSY_STONE_BRICKS;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 4; k++) {
                    lr.setBlockData(x + i, y + k, z + j, m.createBlockData());
                }
            }
        }
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                for (int k = 0; k < 3; k++) {
                    lr.setBlockData(x + i, y + k, z + j, Material.AIR.createBlockData());
                }
            }
        }
        lr.setBlockData(x + 2, y, z + 2, Material.CHEST.createBlockData());
        
        org.bukkit.block.BlockState state = lr.getBlockState(x + 2, y, z + 2);
        if (state instanceof org.bukkit.block.Chest) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) state;
            fillChest(chest, r);
        }
    }

    private void fillChest(org.bukkit.block.Chest chest, Random r) {
        int chance = r.nextInt(100);
        if (chance < 2) {
            chest.getInventory().addItem(new ItemStack(Material.ELYTRA));
        } else if (chance < 12) {
            Material[] neth = {Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS};
            chest.getInventory().addItem(new ItemStack(neth[r.nextInt(4)]));
        } else if (chance < 42) {
            Material[] diam = {Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS};
            chest.getInventory().addItem(new ItemStack(diam[r.nextInt(4)]));
        } else if (chance < 77) {
            Material[] diam = {Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS};
            ItemStack item = new ItemStack(diam[r.nextInt(4)]);
            addRandomEnchant(item, r);
            chest.getInventory().addItem(item);
        } else {
            chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, r.nextInt(3) + 1));
            chest.getInventory().addItem(new ItemStack(Material.IRON_INGOT, r.nextInt(5) + 2));
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            addRandomEnchant(book, r);
            chest.getInventory().addItem(book);
        }
    }

    private void addRandomEnchant(ItemStack item, Random r) {
        Enchantment[] enchants = {Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DAMAGE_ALL, Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.KNOCKBACK};
        Enchantment ench = enchants[r.nextInt(enchants.length)];
        int level = r.nextInt(ench.getMaxLevel()) + 1;
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            meta.addStoredEnchant(ench, level, true);
            item.setItemMeta(meta);
        } else {
            item.addUnsafeEnchantment(ench, level);
        }
    }
  }
