package me.aris.arisworld;

import org.bukkit.Material;
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
        if (type.equals("flat")) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    cd.setBlock(x, 64, z, Material.GRASS_BLOCK);
                    for (int y = 60; y < 64; y++) cd.setBlock(x, y, z, Material.DIRT);
                    for (int y = -64; y < 60; y++) cd.setBlock(x, y, z, Material.STONE);
                    cd.setBlock(x, -64, z, Material.BEDROCK);
                }
            }
            return;
        }

        SimplexOctaveGenerator gen = new SimplexOctaveGenerator(new Random(wi.getSeed()), 8);
        gen.setScale(0.012D);
        
        SimplexOctaveGenerator patchGen = new SimplexOctaveGenerator(new Random(wi.getSeed() + 99), 2);
        patchGen.setScale(0.008D);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int rx = cx * 16 + x;
                int rz = cz * 16 + z;
                
                double n = gen.noise(rx, rz, 0.5D, 0.5D);
                int h = (int) (64 + (n * 4));
                
                cd.setBlock(x, -64, z, Material.BEDROCK);
                for (int y = -63; y < h; y++) {
                    cd.setBlock(x, y, z, y < -40 ? Material.DEEPSLATE : Material.STONE);
                }

                double pn = patchGen.noise(rx, rz, 0.5D, 0.5D);
                if (pn > 0.42) {
                    if (pn > 0.62) cd.setBlock(x, h, z, Material.COARSE_DIRT);
                    else cd.setBlock(x, h, z, Material.DIRT);
                    cd.setBlock(x, h - 1, z, Material.DIRT);
                } else {
                    cd.setBlock(x, h, z, Material.SAND);
                }
            }
        }
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(org.bukkit.World world) {
        return Collections.singletonList(new BlockPopulator() {
            @Override
            public void populate(WorldInfo wi, Random r, int cx, int cz, org.bukkit.generator.LimitedRegion lr) {
                if (type.equals("desert") && r.nextInt(55) == 0) {
                    int x = cx * 16 + r.nextInt(16);
                    int z = cz * 16 + r.nextInt(16);
                    int y = 0;
                    for (int h = 75; h > 50; h--) {
                        if (lr.getType(x, h, z) != Material.AIR) { y = h; break; }
                    }
                    if (y > 50) buildFossil(lr, x, y, z, r);
                }
            }
        });
    }

    private void buildFossil(org.bukkit.generator.LimitedRegion lr, int x, int y, int z, Random r) {
        int len = r.nextInt(5) + 7;
        for (int i = 0; i < len; i++) {
            int off = r.nextInt(3) - 1;
            lr.setBlockData(x + i, y + off, z, Material.BONE_BLOCK.createBlockData());
            if (i % 3 == 0) {
                for (int h = 1; h < 4; h++) {
                    lr.setBlockData(x + i, y + off + h, z, Material.BONE_BLOCK.createBlockData());
                    lr.setBlockData(x + i, y + off - h, z, Material.BONE_BLOCK.createBlockData());
                }
            }
        }
    }
            }
