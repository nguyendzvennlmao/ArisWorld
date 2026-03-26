package me.aris.arisworld;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
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
        gen.setScale(0.008D);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int rx = cx * 16 + x;
                int rz = cz * 16 + z;

                double noise = gen.noise(rx, rz, 0.5D, 0.5D);
                int height;
                
                if (isDesert) {
                    height = 50; 
                } else {
                    height = (int) (noise * 60D + 100D);
                }

                cd.setBlock(x, -64, z, Material.BEDROCK);
                
                for (int y = -63; y < height; y++) {
                    if (y < height - 4) {
                        cd.setBlock(x, y, z, Material.STONE);
                    } else {
                        if (isDesert) {
                            cd.setBlock(x, y, z, Material.SANDSTONE);
                        } else {
                            cd.setBlock(x, y, z, Material.DIRT);
                        }
                    }
                }

                if (isDesert) {
                    cd.setBlock(x, height, z, Material.SAND);
                } else {
                    if (height > 130) {
                        cd.setBlock(x, height, z, Material.SNOW_BLOCK);
                    } else {
                        cd.setBlock(x, height, z, Material.GRASS_BLOCK);
                    }
                }
                
                if (!isDesert && height < 75) {
                    for (int y = height + 1; y <= 75; y++) {
                        cd.setBlock(x, y, z, Material.WATER);
                    }
                }
            }
        }
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo wi) {
        return new BiomeProvider() {
            @Override
            public Biome getBiome(WorldInfo wi, int x, int y, int z) {
                return isDesert ? Biome.DESERT : Biome.PLAINS;
            }
            @Override
            public List<Biome> getBiomes(WorldInfo wi) {
                return isDesert ? List.of(Biome.DESERT) : List.of(Biome.PLAINS);
            }
        };
    }

    @Override
    public boolean shouldGenerateStructures() { return true; }
    @Override
    public boolean shouldGenerateDecorations() { return true; }
                                         }
