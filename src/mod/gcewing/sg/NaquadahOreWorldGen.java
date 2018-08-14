//------------------------------------------------------------------------------------------------
//
//   SG Craft - Naquadah ore world generation
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;

import gcewing.sg.conf.SGConfiguration;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.*;

public class NaquadahOreWorldGen implements IWorldGenerator {

    private Random random;
    private World  world;
    private Chunk  chunk;
    private int    x0, z0;

    private Block       stone    = Blocks.STONE;
    private Block       lava     = Blocks.LAVA;
    private IBlockState naquadah = SGCraft.naquadahOre.getDefaultState();

    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        //System.out.printf("NaquadahOreWorldGen: chunk (%d, %d)\n", chunkX, chunkZ);
        this.random = random;
        this.world = world;
        x0 = chunkX * 16;
        z0 = chunkZ * 16;
        chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        generateChunk();
    }

    public void regenerate(Chunk chunk) {
        this.chunk = chunk;
        world = chunk.getWorld();
        int  chunkX    = chunk.x;
        int  chunkZ    = chunk.z;
        long worldSeed = world.getSeed();
        random = new Random(worldSeed);
        long xSeed = random.nextLong() >> 2 + 1L;
        long zSeed = random.nextLong() >> 2 + 1L;
        random.setSeed((xSeed * chunkX + zSeed * chunkZ) ^ worldSeed);
        x0 = chunkX * 16;
        z0 = chunkZ * 16;
        generateChunk();
    }

    Block getBlock(int x, int y, int z) {
        return getBlock(new BlockPos(x, y, z));
    }

    Block getBlock(BlockPos pos) {
        return chunk.getBlockState(pos).getBlock();
    }

    boolean isSolidBlock(int x, int y, int z) {
        return isSolidBlock(new BlockPos(x, y, z));
    }

    boolean isSolidBlock(BlockPos pos) {
        IBlockState state = chunk.getBlockState(pos);
        return state.getMaterial().isSolid();
    }

    void setBlockState(BlockPos pos, IBlockState state) {
        chunk.setBlockState(pos, state);
    }

    void generateNode(IBlockState id, int x, int y, int z, int sx, int sy, int sz) {
        int dx = random.nextInt(sx);
        int dy = random.nextInt(sy);
        int dz = random.nextInt(sz);
        int h  = world.getHeight();
        if (SGConfiguration.DEBUG_RANDOM && SGConfiguration.DEBUG_LEVEL >= 1)
            System.out.printf("NaquadahOreWorldGen: %d x %d x %d node at (%d, %d, %d)\n",
                    dx + 1, dy + 1, dz + 1, x0 + x, y, z0 + z);
        for (int i = x; i <= x + dx; i++)
            for (int j = y; j <= y + dy; j++)
                for (int k = z; k <= z + dz; k++)
                    if (i < 16 && j < h && k < 16) {
                        BlockPos pos = new BlockPos(i, j, k);
                        if (isSolidBlock(pos)) {
                            if (SGConfiguration.DEBUG_RANDOM && SGConfiguration.DEBUG_LEVEL >= 2)
                                System.out.printf("NaquadahOreWorldGen: block at (%d, %d, %d)\n",
                                        x0 + i, j, z0 + k);
                            setBlockState(pos, id);
                        }
                    }
    }

    boolean odds(int n) {
        return random.nextInt(n) == 0;
    }

    void generateChunk() {
        SGChunkData.forChunk(chunk).oresGenerated = true;
        if (odds(SGConfiguration.genUnderLavaOdds)) {
            int n = random.nextInt(SGConfiguration.maxNodesUnderLava) + 1;
            for (int i = 0; i < n; i++) {
                int x = random.nextInt(16);
                int z = random.nextInt(16);
                for (int y = 1; y < 64; y++)
                    if (isSolidBlock(x, y, z) && getBlock(x, y + 1, z) == lava) {
                        if (SGConfiguration.DEBUG_LAVA)
                            System.out.printf("NaquadahOreWorldGen: generating under lava at (%d, %d, %d)\n", x0 + x, y, z0 + z);
                        generateNode(naquadah, x, y, z, 3, 1, 3);
                    }
            }
        }
        if (odds(SGConfiguration.genIsolatedOdds)) {
            int n = random.nextInt(SGConfiguration.maxIsolatedNodes) + 1;
            for (int i = 0; i < n; i++) {
                int x = random.nextInt(16);
                int y = random.nextInt(16) + 16;
                int z = random.nextInt(16);
                if (isSolidBlock(x, y, z)) {
                    if (SGConfiguration.DEBUG_RANDOM)
                        System.out.printf("NaquadahOreWorldGen: generating randomly at (%d, %d, %d)\n", x0 + x, y, z0 + z);
                    generateNode(naquadah, x, y, z, 2, 2, 2);
                }
            }
        }
    }

}
