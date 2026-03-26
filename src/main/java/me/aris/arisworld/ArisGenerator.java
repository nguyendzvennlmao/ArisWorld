package me.aris.arisworld;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import java.util.List;
import java.util.Random;

public class ArisGenerator extends ChunkGenerator {
    @Override
    public void generateSurface(WorldInfo wi, Random r, int cx, int cz, ChunkData cd) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                cd.setBlock(x, -64, z, Material.BEDROCK);
                for (int y = -63; y <= 45; y++) cd.setBlock(x, y, z, Material.SANDSTONE);
                for (int y = 46; y <= 50; y++) cd.setBlock(x, y, z, Material.SAND);
            }
        }
    }
    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo wi) {
        return new BiomeProvider() {
            @Override
            public Biome getBiome(WorldInfo wi, int x, int y, int z) { return Biome.DESERT; }
            @Override
            public List<Biome> getBiomes(WorldInfo wi) { return List.of(Biome.DESERT); }
        };
    }
    @Override
    public boolean shouldGenerateCaves() { return true; }
    @Override
    public boolean shouldGenerateDecorations() { return true; }
    @Override
    public boolean shouldGenerateStructures() { return true; }
}
