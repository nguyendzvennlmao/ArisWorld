package me.aris.arisworld;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
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
        gen.setScale(isDesert ? 0.015D : 0.004D);

        SimplexOctaveGenerator patchGen = new SimplexOctaveGenerator(new Random(wi.getSeed() + 2), 4);
        patchGen.setScale(0.01D);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int rx = cx * 16 + x;
                int rz = cz * 16 + z;

                double noise = gen.noise(rx, rz, 0.5D, 0.5D) * 0.5D + 0.5D;
                int height = (int) (noise * (isDesert ? 14 : 190) + 64);

                cd.setBlock(x, -64, z, Material.BEDROCK);

                for (int y = -63; y < height; y++) {
                    if (y < -40) cd.setBlock(x, y, z, Material.DEEPSLATE);
                    else cd.setBlock(x, y, z, Material.STONE);
                }

                if (isDesert) {
                    double patchNoise = patchGen.noise(rx, rz, 0.5D, 0.5D);
                    if (patchNoise > 0.58) {
                        cd.setBlock(x, height, z, Material.COARSE_DIRT);
                        cd.setBlock(x, height - 1, z, Material.DIRT);
                    } else {
                        cd.setBlock(x, height, z, Material.SAND);
                    }
                } else {
                    if (height > 185) cd.setBlock(x, height, z, Material.SNOW_BLOCK);
                    else cd.setBlock(x, height, z, Material.GRASS_BLOCK);
                }
            }
        }
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.singletonList(new BlockPopulator() {
            @Override
            public void populate(WorldInfo wi, Random r, int cx, int cz, org.bukkit.generator.LimitedRegion lr) {
                if (isDesert && r.nextInt(15) == 0) {
                    int x = cx * 16 + r.nextInt(8) + 4;
                    int z = cz * 16 + r.nextInt(8) + 4;
                    int y = 0;
                    for (int h = 100; h > 60; h--) {
                        if (lr.getType(x, h, z) != Material.AIR) {
                            y = h;
                            break;
                        }
                    }
                    if (y > 60) createNaturalPond(lr, x, y, z, r);
                }

                if (r.nextInt(100) < 4) {
                    int sx = cx * 16 + r.nextInt(16);
                    int sz = cz * 16 + r.nextInt(16);
                    int sy = -64;
                    for (int h = 250; h > -64; h--) {
                        if (lr.getType(sx, h, sz) != Material.AIR && lr.getType(sx, h, sz) != Material.WATER) {
                            sy = h + 1;
                            break;
                        }
                    }
                    if (sy > -60 && sy < 240) buildStructure(lr, sx, sy, sz, r);
                }
            }
        });
    }

    private void createNaturalPond(org.bukkit.generator.LimitedRegion lr, int x, int y, int z, Random r) {
        int radius = r.nextInt(4) + 3;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = dx * dx + dz * dz;
                if (dist < radius * radius - r.nextInt(2)) {
                    int rx = x + dx;
                    int rz = z + dz;
                    lr.setBlockData(rx, y, rz, Material.WATER.createBlockData());
                    lr.setBlockData(rx, y - 1, rz, Material.SAND.createBlockData());
                }
            }
        }
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
            fillChest((org.bukkit.block.Chest) state, r);
        }
    }

    private void fillChest(org.bukkit.block.Chest chest, Random r) {
        int chance = r.nextInt(100);
        if (chance < 2) chest.getInventory().addItem(new ItemStack(Material.ELYTRA));
        else if (chance < 12) chest.getInventory().addItem(new ItemStack(Material.NETHERITE_CHESTPLATE));
        else if (chance < 42) chest.getInventory().addItem(new ItemStack(Material.DIAMOND_CHESTPLATE));
        else {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, r.nextInt(4) + 2);
            chest.getInventory().addItem(item);
        }
    }
          }
