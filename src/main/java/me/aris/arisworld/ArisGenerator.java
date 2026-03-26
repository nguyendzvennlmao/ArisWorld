package me.aris.arisworld;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArisGenerator extends ChunkGenerator {
    private final String type;

    public ArisGenerator(String type) {
        this.type = type.toLowerCase();
    }

    @Override
    public void generateNoise(WorldInfo wi, Random r, int cx, int cz, ChunkData cd) {
        if (type.equals("spawn")) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    cd.setBlock(x, 60, z, Material.SMOOTH_QUARTZ);
                    for (int y = 50; y < 60; y++) cd.setBlock(x, y, z, Material.STONE);
                    cd.setBlock(x, 0, z, Material.BEDROCK);
                }
            }
            return;
        }

        SimplexOctaveGenerator gen = new SimplexOctaveGenerator(new Random(wi.getSeed()), 8);
        boolean isDesert = type.equals("desert");
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
                    } else cd.setBlock(x, height, z, Material.SAND);
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
                if (type.equals("spawn")) {
                    if (cx == 0 && cz == 0) buildEpicSpawn(lr, 0, 61, 0, r);
                    if (cx == 20 && cz == 0) buildCrateArea(lr, 320, 61, 0, r);
                    return;
                }
                if (type.equals("desert") && r.nextInt(15) == 0) {
                    int x = cx * 16 + r.nextInt(8) + 4;
                    int z = cz * 16 + r.nextInt(8) + 4;
                    int y = 60;
                    for (int h = 100; h > 60; h--) {
                        if (lr.getType(x, h, z) != Material.AIR) { y = h; break; }
                    }
                    createPond(lr, x, y, z, r);
                }
            }
        });
    }

    private void buildEpicSpawn(org.bukkit.generator.LimitedRegion lr, int x, int y, int z, Random r) {
        for (int i = -25; i <= 25; i++) {
            for (int j = -25; j <= 25; j++) {
                if (Math.abs(i) == 25 || Math.abs(j) == 25) {
                    for (int h = 0; h < 15; h++) lr.setBlockData(x + i, y + h, z + j, Material.SEA_LANTERN.createBlockData());
                }
                lr.setBlockData(x + i, y - 1, z + j, Material.GOLD_BLOCK.createBlockData());
            }
        }
        for (int h = 0; h < 10; h++) {
            lr.setBlockData(x, y + h, z, Material.BEACON.createBlockData());
            lr.setBlockData(x, y + h - 1, z, Material.NETHERITE_BLOCK.createBlockData());
        }
    }

    private void buildCrateArea(org.bukkit.generator.LimitedRegion lr, int x, int y, int z, Random r) {
        for (int i = -10; i <= 10; i++) {
            for (int j = -10; j <= 10; j++) {
                lr.setBlockData(x + i, y - 1, z + j, Material.DIAMOND_BLOCK.createBlockData());
            }
        }
        lr.setBlockData(x, y, z, Material.ENDER_CHEST.createBlockData());
    }

    private void createPond(org.bukkit.generator.LimitedRegion lr, int x, int y, int z, Random r) {
        int radius = r.nextInt(4) + 3;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz < radius * radius) {
                    lr.setBlockData(x + dx, y, z + dz, Material.WATER.createBlockData());
                }
            }
        }
    }
    }
