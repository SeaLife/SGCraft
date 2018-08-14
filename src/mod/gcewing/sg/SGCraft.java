//------------------------------------------------------------------------------------------------
//
//   SG Craft - Main Class
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.conf.SGConfiguration;
import gcewing.sg.conf.SGSounds;
import gcewing.sg.ic2.gdo.GDOItem;
import gcewing.sg.ic2.zpm.ZPMItem;
import gcewing.sg.ic2.zpm.ZpmContainer;
import gcewing.sg.ic2.zpm.ZpmInterfaceCart;
import gcewing.sg.ic2.zpm.ZpmInterfaceCartTE;
import gcewing.sg.oc.OCIntegration;
import gcewing.sg.rf.RFIntegration;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static java.util.Objects.requireNonNull;
import static net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import static net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(
        name = Info.modName,
        modid = Info.modID,
        version = Info.versionNumber,
        dependencies = Info.dependencies,
        acceptableRemoteVersions = Info.versionBounds
)
public class SGCraft extends BaseMod<SGCraftClient> {

    // region (Static access to SGCraft)
    private static SGCraft instance;

    public static SGCraft getInstance() {
        return instance;
    }
    // endregion

    public static final Material machineMaterial = new Material(MapColor.IRON);

    public static SGChannel          channel;
    public static BaseTEChunkManager chunkManager;

    // MINECRAFT :: BLOCKS
    public static SGBaseBlock sgBaseBlock;
    public static SGRingBlock sgRingBlock;
    public static DHDBlock    sgControllerBlock;
    public static Block       naquadahBlock;
    public static Block       naquadahOre;

    // MINECRAFT :: ITEMS
    public static Item naquadah;
    public static Item naquadahIngot;
    public static Item sgCoreCrystal;
    public static Item sgControllerCrystal;
    public static Item sgChevronUpgrade;
    public static Item sgIrisUpgrade;
    public static Item sgIrisBlade;

    // MINECRAFT :: IC2
    public static Block ic2PowerUnit;
    public static Item  ic2Capacitor;

    // MINECRAFT :: RF Tools
    public static Block rfPowerUnit;

    public static boolean             addOresToExistingWorlds;
    public static NaquadahOreWorldGen naquadahOreGenerator;

    public static BaseSubsystem ic2Integration; //[IC2]
    public static IIntegration  ccIntegration; //[CC]
    public static OCIntegration ocIntegration; //[OC]
    public static RFIntegration rfIntegration; //[RF]

    public static Block zpm_interface_cart;
    public static Item  zpm, gdo;

    public static CreativeTabs creativeTabs;

    // Villager Profession for Generators
    public static VillagerProfession tokraProfession;

    // Block Harvests
    public static boolean canHarvestDHD         = false;
    public static boolean canHarvestSGBaseBlock = false;
    public static boolean canHarvestSGRingBlock = false;

    //Client Options
    public static boolean useHDEventHorizionTexture = true;
    public static boolean saveAddressToClipboard    = false;


    public SGCraft() {
        instance = this;
    }


    @Override
    @Mod.EventHandler
    @SuppressWarnings("SpellCheckingInspection")
    public void preInit(FMLPreInitializationEvent e) {
        this.creativeTab = new CreativeTabs("sgcraft:sgcraft") {
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(Item.getItemFromBlock(sgBaseBlock));
            }
        };
        FMLCommonHandler.instance().bus().register(this);
        rfIntegration = (RFIntegration) integrateWithMod("redstoneflux", "gcewing.sg.rf.RFIntegration"); //[RF]
        ic2Integration = integrateWithMod("ic2", "gcewing.sg.ic2.IC2Integration"); //[IC2]
        ccIntegration = (IIntegration) integrateWithMod("computercraft", "gcewing.sg.cc.CCIntegration"); //[CC]
        ocIntegration = (OCIntegration) integrateWithMod("opencomputers", "gcewing.sg.oc.OCIntegration"); //[OC]

        GameRegistry.registerTileEntity(ZpmInterfaceCartTE.class, new ResourceLocation(this.modID));
        super.preInit(e);
    }

    @Override
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        super.init(e);
        System.out.print("SGCraft.init");
        configure();
        channel = new SGChannel(Info.modID);
        chunkManager = new BaseTEChunkManager(this);
    }

    @Override
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    protected SGCraftClient initClient() {
        return new SGCraftClient(this);
    }

    @Override
    void configure() {
        // TODO: move DHDTE configuration to SGConfiguration
        DHDTE.configure(config);
        SGConfiguration.configure(config);

        // Server-Side Options
        addOresToExistingWorlds = config.getBoolean("options", "addOresToExistingWorlds", false);

        // Client-Side Options
        useHDEventHorizionTexture = config.getBoolean("client", "useHDEventHorizonTexture", useHDEventHorizionTexture);
        saveAddressToClipboard = config.getBoolean("client", "saveAddressToClipboard", saveAddressToClipboard);
    }

    @Override
    protected void registerOther() {
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
    }

    @Override
    protected void registerBlocks() {
        sgRingBlock = newBlock("stargateRing", SGRingBlock.class, SGRingItem.class);
        sgBaseBlock = newBlock("stargateBase", SGBaseBlock.class);
        sgControllerBlock = newBlock("stargateController", DHDBlock.class);
        naquadahBlock = newBlock("naquadahBlock", NaquadahBlock.class);
        naquadahOre = newBlock("naquadahOre", NaquadahOreBlock.class);
        if (isModLoaded("ic2")) {
            zpm_interface_cart = newBlock("zpm_interface_cart", ZpmInterfaceCart.class);
        }

        this.setupBlockHarvests();
    }

    @Override
    protected void registerItems() {
        naquadah = newItem("naquadah"); //, "Naquadah");
        naquadahIngot = newItem("naquadahIngot"); //, "Naquadah Alloy Ingot");
        sgCoreCrystal = newItem("sgCoreCrystal"); //, "Stargate Core Crystal");
        sgControllerCrystal = newItem("sgControllerCrystal"); //, "Stargate Controller Crystal");
        sgChevronUpgrade = addItem(new SGChevronUpgradeItem(), "sgChevronUpgrade");
        sgIrisUpgrade = addItem(new SGIrisUpgradeItem(), "sgIrisUpgrade");
        sgIrisBlade = newItem("sgIrisBlade");
        if (isModLoaded("ic2") || !isModLoaded("thermalexpansion")) {
            ic2Capacitor = newItem("ic2Capacitor");
        }
        if (isModLoaded("ic2")) {
            zpm = addItem(new ZPMItem(), "zpm");
        }

        gdo = addItem(new GDOItem(), "gdo");
    }

    @SideOnly(Side.CLIENT)
    public static void playSound(SoundSource source, SoundEvent sound) {
        playSound(source, sound, SoundCategory.AMBIENT);
    }

    @SideOnly(Side.CLIENT)
    public static void playSound(SoundSource source, SoundEvent sound, SoundCategory category) {
        SoundHandler soundHandler = getSoundHandler();
        soundHandler.playSound(new Sound(source, sound, category));
    }

    @SideOnly(Side.CLIENT)
    private static SoundHandler getSoundHandler() {
        return Minecraft.getMinecraft().getSoundHandler();
    }

    public static boolean isValidStargateUpgrade(Item item) {
        return item == sgChevronUpgrade || item == sgIrisUpgrade;
    }

    @Override
    protected void registerOres() {
        addOre("oreNaquadah", naquadahOre);
        addOre("naquadah", naquadah);
        addOre("ingotNaquadahAlloy", naquadahIngot);
    }

    @Override
    protected void registerRecipes() {
        ItemStack chiselledSandstone = new ItemStack(Blocks.SANDSTONE, 1, 1);
        ItemStack smoothSandstone    = new ItemStack(Blocks.SANDSTONE, 1, 2);
        ItemStack sgChevronBlock     = new ItemStack(sgRingBlock, 1, 1);
        ItemStack blueDye            = new ItemStack(Items.DYE, 1, 4);
        ItemStack orangeDye          = new ItemStack(Items.DYE, 1, 14);

        if (config.getBoolean("recipes", "naquadah", false)) {
            newShapelessRecipe("naquada", naquadah, 1, Ingredient.fromItems(Items.COAL, Items.SLIME_BALL, Items.BLAZE_POWDER));
        }

        if (config.getBoolean("recipes", "naquadahIngot", true)) {
            newShapelessRecipe("naquadahingot", naquadahIngot, 1, Ingredient.fromItem(Items.IRON_INGOT), Ingredient.fromItem(naquadah));
        }

        if (config.getBoolean("recipes", "naquadahIngotFromBlock", true)) {
            newRecipe("naquadahingot_from_block", naquadahIngot, 9, "B", 'B', naquadahBlock);
        }

        if (config.getBoolean("recipes", "naquadahBlock", true)) {
            newRecipe("naquadahblock", naquadahBlock, 1, "NNN", "NNN", "NNN", 'N', "ingotNaquadahAlloy");
        }

        if (config.getBoolean("recipes", "sgRingBlock", true)) {
            newRecipe("sgringblock", sgRingBlock, 1, "CCC", "NNN", "SSS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone);
        }

        if (config.getBoolean("recipes", "sgChevronBlock", true)) {
            newRecipe("sgcheveronblock", sgChevronBlock, "CgC", "NpN", "SrS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone, 'g', Items.GLOWSTONE_DUST, 'r', Items.REDSTONE, 'p', Items.ENDER_PEARL);
        }

        if (config.getBoolean("recipes", "sgBaseBlock", true)) {
            newRecipe("sgbaseblock", sgBaseBlock, 1, "CrC", "NeN", "ScS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone, 'r', Items.REDSTONE, 'e', Items.ENDER_EYE, 'c', sgCoreCrystal);
        }

        if (config.getBoolean("recipes", "sgControllerBlock", true)) {
            newRecipe("sgcontrollerblock", sgControllerBlock, 1, "bbb", "OpO", "OcO", 'b', Blocks.STONE_BUTTON, 'O', Blocks.OBSIDIAN, 'p', Items.ENDER_PEARL, 'c', sgControllerCrystal);
        }

        if (config.getBoolean("recipes", "sgChevronUpgradeItem", true)) {
            newRecipe("sgchevronupgrade", sgChevronUpgrade, 1, "g g", "pNp", "r r", 'N', "ingotNaquadahAlloy", 'g', Items.GLOWSTONE_DUST, 'r', Items.REDSTONE, 'p', Items.ENDER_PEARL);
        }

        if (config.getBoolean("recipes", "sgIrisBladeItem", true)) {
            newRecipe("sgirisblade", sgIrisBlade, 1, " ii", "ic ", "i  ", 'i', Items.IRON_INGOT, 'c', new ItemStack(Items.COAL, 1, 1));
        }

        if (config.getBoolean("recipes", "sgIrisUpgradeItem", true)) {
            newRecipe("sgirisupgrade", sgIrisUpgrade, 1, "bbb", "brb", "bbb", 'b', sgIrisBlade, 'r', Items.REDSTONE);
        }

        if (config.getBoolean("recipes", "sgCoreCrystalItem", false)) {
            newRecipe("sgcorecrystal", sgCoreCrystal, 1, "bbr", "rdb", "brb", 'b', blueDye, 'r', Items.REDSTONE, 'd', Items.DIAMOND);
        }

        if (config.getBoolean("recipes", "sgControllerCrystalItem", false)) {
            newRecipe("sgcontrollercrystal", sgControllerCrystal, 1, "roo", "odr", "oor", 'o', orangeDye, 'r', Items.REDSTONE, 'd', Items.DIAMOND);
        }

        if (!isModLoaded("ic2"))
            addGenericCapacitorRecipe();
    }

    protected void addGenericCapacitorRecipe() {
        if (config.getBoolean("recipes", "genericCapacitorItem", true)) {
            newRecipe("ic2capacitor", ic2Capacitor, 1, "iii", "ppp", "iii", 'i', "ingotIron", 'p', "paper");
        }
    }

    @Override
    protected void registerContainers() {
        addContainer(SGGui.SGBase, SGBaseContainer.class);
        addContainer(SGGui.DHDFuel, DHDFuelContainer.class);
        addContainer(SGGui.PowerUnit, PowerContainer.class);
        addContainer(SGGui.ZPMInterfaceCart, ZpmContainer.class);
    }

    @Override
    protected void registerWorldGenerators() {
        if (config.getBoolean("options", "enableNaquadahOre", true)) {
            System.out.printf("SGCraft: Registering NaquadahOreWorldGen\n");
            naquadahOreGenerator = new NaquadahOreWorldGen();
            GameRegistry.registerWorldGenerator(naquadahOreGenerator, 0);
        }
        MapGenStructureIO.registerStructureComponent(FeatureUnderDesertPyramid.class, "SGCraft:FeatureUnderDesertPyramid");
    }

    @Override //[VILL]
    protected void registerVillagers() {
        tokraProfession = new VillagerProfession("sgcraft:tokra", "sgcraft:textures/skins/tokra.png", "sgcraft:textures/skins/tokra.png");
        // Update: Needs new skin for Zombie mode?
        VillagerCareer tokraCareer = new VillagerCareer(tokraProfession, "sgcraft:tokra");
        tokraCareer.addTrade(1, new SGTradeHandler());
        ForgeRegistries.VILLAGER_PROFESSIONS.register(tokraProfession);
    }

    @Override
    protected void registerEntities() {
        addEntity(EntityStargateIris.class, "stargate_iris", SGEntity.Iris, 1000000, false);
    }

    @Override
    protected void registerSounds() {
        SGSounds.registerSounds(this);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load e) {
        Chunk chunk = e.getChunk();
        SGChunkData.onChunkLoad(e);
    }

    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save e) {
        Chunk chunk = e.getChunk();
        SGChunkData.onChunkSave(e);
    }

    @SubscribeEvent
    public void onInitMapGen(InitMapGenEvent e) {
        FeatureGeneration.onInitMapGen(e);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        switch (e.phase) {
            case START: {
                for (BaseSubsystem om : subsystems)
                    if (om instanceof IIntegration)
                        ((IIntegration) om).onServerTick();
                break;
            }
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload e) {
        Chunk chunk = e.getChunk();
        if (!chunk.getWorld().isRemote) {
            for (Object obj : chunk.getTileEntityMap().values()) {
                if (obj instanceof SGBaseTE) {
                    SGBaseTE te = (SGBaseTE) obj;
                    te.disconnect();
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onModelRegistry(ModelRegistryEvent event) {
        // Register Complex Block Models
        // Note: Complex Item Models register within their creation class because their registration order isn't important.
        if (isModLoaded("ic2") && SGCraft.zpm_interface_cart != null) {
            registerModel(Item.getItemFromBlock(SGCraft.zpm_interface_cart));
        }
    }

    @SideOnly(Side.CLIENT)
    private void registerModel(Item item) {
        this.registerInventoryModel(item, requireNonNull(item.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    private void registerInventoryModel(Item item, ResourceLocation blockName) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(blockName, "inventory"));
    }

    private void setupBlockHarvests() {
        canHarvestDHD = config.getBoolean("block-harvest", "dhdBlock", canHarvestDHD);
        canHarvestSGBaseBlock = config.getBoolean("block-harvest", "sgBaseBlock", canHarvestSGBaseBlock);
        canHarvestSGRingBlock = config.getBoolean("block-harvest", "sgRingBlock", canHarvestSGRingBlock);
    }
}
