package me.aris.arisworld;

import org.bukkit.Material;
import org.bukkit.World;
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

                if (isDesert) cd.setBlock(x, height, z, Material.SAND);
                else cd.setBlock(x, height, z, Material.GRASS_BLOCK);
            }
        }
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.singletonList(new BlockPopulator() {
            @Override
            public void populate(WorldInfo wi, Random r, int cx, int cz, org.bukkit.generator.LimitedRegion lr) {
                if (type.equals("spawn")) {
                    if (cx >= -2 && cx <= 2 && cz >= -2 && cz <= 2) buildSpawnCenter(lr, cx, cz);
                    if (cx >= 18 && cx <= 22 && cz >= -2 && cz <= 2) buildCrateArea(lr, cx, cz);
                }
            }
        });
    }

    private void buildSpawnCenter(org.bukkit.generator.LimitedRegion lr, int cx, int cz) {
        for (int x = cx * 16; x < (cx * 16) + 16; x++) {
            for (int z = cz * 16; z < (cz * 16) + 16; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist < 25) {
                    lr.setBlockData(x, 60, z, Material.GOLD_BLOCK.createBlockData());
                    if (dist < 2) {
                        for (int h = 61; h < 75; h++) lr.setBlockData(x, h, z, Material.SEA_LANTERN.createBlockData());
                    }
                }
            }
        }
    }

    private void buildCrateArea(org.bukkit.generator.LimitedRegion lr, int cx, int cz) {
        int centerX = 320;
        int centerZ = 0;
        for (int x = cx * 16; x < (cx * 16) + 16; x++) {
            for (int z = cz * 16; z < (cz * 16) + 16; z++) {
                double dist = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (dist < 15) {
                    lr.setBlockData(x, 60, z, Material.DIAMOND_BLOCK.createBlockData());
                    if (dist > 13) {
                        for (int h = 61; h < 65; h++) lr.setBlockData(x, h, z, Material.NETHER_BRICK_FENCE.createBlockData());
                    }
                }
            }
        }
    }
                      }
