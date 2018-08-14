//------------------------------------------------------------------------------------------------
//
//   SG Craft - Map feature generation
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.conf.SGConfiguration;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class FeatureGeneration {


    static Field structureMap = BaseReflectionUtils.getFieldDef(MapGenStructure.class,
            "structureMap", "field_75053_d");

    public static void onInitMapGen(InitMapGenEvent e) {
        if (SGConfiguration.DEBUG_STRUCTURE)
            System.out.printf("SGCraft: FeatureGeneration.onInitMapGen: %s\n", e.getType());

        if (SGConfiguration.augmentStructures) {
            switch (e.getType()) {
                case SCATTERED_FEATURE:
                    MapGenBase newGen = e.getNewGen();
                    if (newGen instanceof MapGenStructure) {
                        e.setNewGen(modifyScatteredFeatureGen((MapGenStructure) newGen));
                        if (SGConfiguration.DEBUG_STRUCTURE)
                            System.out.print("SGCraft: FeatureGeneration: Installed SGStructureMap");
                    } else
                        System.out.print("SGCraft: FeatureGeneration: SCATTERED_FEATURE generator is not a MapGenStructure, cannot customise");
                    break;
            }
        }
    }

    static MapGenStructure modifyScatteredFeatureGen(MapGenStructure gen) {
        BaseReflectionUtils.setField(gen, structureMap, new SGStructureMap());
        return gen;
    }

}

@SuppressWarnings("WeakerAccess")
class SGStructureMap extends Long2ObjectOpenHashMap {

    public SGStructureMap() {
        super(1024);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object put(final long key, final Object value) {
        if (SGConfiguration.DEBUG_STRUCTURE)
            System.out.printf("SGCraft: FeatureGeneration: SGStructureMap.put: %s\n", value);
        if (value instanceof StructureStart)
            augmentStructureStart((StructureStart) value);
        return super.put(key, value);
    }

    void augmentStructureStart(StructureStart start) {
        if (SGConfiguration.DEBUG_STRUCTURE) {
            System.out.printf("SGCraft: FeatureGeneration: augmentStructureStart: %s\n", start);
        }
        List<StructureComponent> oldComponents = start.getComponents();
        List<StructureComponent> newComponents = new ArrayList<StructureComponent>();
        for (Object comp : oldComponents) {
            if (SGConfiguration.DEBUG_STRUCTURE) {
                System.out.printf("SGCraft: FeatureGeneration: Found component %s\n", comp);
                System.out.println("SGCraft: Instance: " + comp.getClass().getCanonicalName() + " -- " + comp.getClass().getSimpleName());
            }
            if (comp instanceof ComponentScatteredFeaturePieces.DesertPyramid) {
                StructureBoundingBox box = ((StructureComponent) comp).getBoundingBox();
                if (SGConfiguration.DEBUG_STRUCTURE) {
                    BlockPos boxCenter = new BlockPos(box.minX + (box.maxX - box.minX + 1) / 2, box.minY + (box.maxY - box.minY + 1) / 2, box.minZ + (box.maxZ - box.minZ + 1) / 2);
                    System.out.printf("SGCraft: FeatureGeneration: Augmenting %s at (%s, %s)\n",
                            comp.getClass().getSimpleName(), boxCenter.getX(), boxCenter.getZ());
                }
                StructureComponent newComp = new FeatureUnderDesertPyramid((StructureComponent) comp);
                start.getBoundingBox().expandTo(newComp.getBoundingBox());
                newComponents.add(newComp);
            }
        }
        oldComponents.addAll(newComponents);
    }
}
