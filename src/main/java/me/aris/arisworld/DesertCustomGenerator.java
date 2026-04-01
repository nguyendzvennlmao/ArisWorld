package me.aris.arisworld;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import java.util.Random;
import java.util.List;

public class DesertCustomGenerator extends ChunkGenerator {
    private final List<Material> ores = List.of(
        Material.DIAMOND_ORE, Material.GOLD_ORE, Material.IRON_ORE, 
        Material.COAL_ORE, Material.LAPIS_ORE, Material.DEEPSLATE_DIAMOND_ORE
    );

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunkData.setBlock(x, 0, z, Material.BEDROCK);
                
                double frequency = 0.05;
                double noise = Math.sin(x * frequency) * Math.cos(z * frequency);
                int heightVariation = random.nextInt(4) + 1; 
                int height = 60 + (int) (noise * heightVariation);
                
                for (int y = 1; y < height - 3; y++) {
                    if (y < 40 && random.nextInt(100) < 15) {
                        chunkData.setBlock(x, y, z, ores.get(random.nextInt(ores.size())));
                    } else {
                        chunkData.setBlock(x, y, z, y < 30 ? Material.DEEPSLATE : Material.COBBLESTONE);
                    }
                }
                
                for (int y = height - 3; y <= height; y++) {
                    chunkData.setBlock(x, y, z, Material.SAND);
                }

                if (random.nextInt(100) < 4) {
                    Material mat = random.nextBoolean() ? Material.DIRT : Material.MOSS_BLOCK;
                    chunkData.setBlock(x, height, z, mat);
                }

                if (random.nextInt(1000) < 3) {
                    int depth = random.nextInt(4) + 1;
                    for (int ly = height; ly > height - depth; ly--) {
                        chunkData.setBlock(x, ly, z, Material.WATER);
                    }
                }
            }
        }
    }
                        }
