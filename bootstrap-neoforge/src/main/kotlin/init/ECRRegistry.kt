package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.common.api.ClusterBlock
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.UnionRegistry
import com.algorithmlx.ecr.common.item.NamedBlockItem
import com.algorithmlx.ecr.common.item.SoulStone
import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import com.algorithmlx.ecr.common.recipe.MithrilineFurnaceRecipe
import com.mojang.serialization.MapCodec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

class ECRRegistry(bus: IEventBus): UnionRegistry {
    private val blockTypes = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, ModId)
    private val blocks = DeferredRegister.createBlocks(ModId)
    private val items = DeferredRegister.createItems(ModId)
    private val dataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModId)
    private val menuType = DeferredRegister.create(Registries.MENU, ModId)
    private val recipeTypes = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ModId)
    private val recipeSerializers = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ModId)

    init {
        blockTypes.register(bus)
        blocks.register(bus)
        items.register(bus)
        dataComponents.register(bus)
        menuType.register(bus)
        recipeTypes.register(bus)
        recipeSerializers.register(bus)
    }

    // Block Types
    private val solarPrismType = blockTypes.register("solar_prism") { _ ->
        BlockBehaviour.simpleCodec(::SolarPrism)
    }
    private val clusterType = blockTypes.register("cluster") { _ ->
        BlockBehaviour.simpleCodec(::ClusterBlock)
    }
    private val crystalType = blockTypes.register("crystal") { _ ->
        BlockBehaviour.simpleCodec(::CrystalBlock)
    }

    // Blocks
    private val mithrilineFurnaceReg = registerBlock("mithriline_furnace", ::MithrilineFurnace)

    // Items
    private val soulStoneItem = registerItem("soul_stone", ::SoulStone)

    // Data Components
    private val soulStoneComponentRegistry = dataComponents.registerComponentType("soul_stone") { builder ->
        builder.persistent(SoulStoneComponent.codec)
            .networkSynchronized(SoulStoneComponent.codecStream)
    }

    // Menu Types
    private val mithrilineFurnaceMenuReg = menuType.register("mithriline_furnace") { _ ->
        IMenuTypeExtension.create(::MithrilineFurnaceMenu)
    }

    // Recipe Types
    private val mithrilineFurnaceRTReg = recipeTypes.register("mithriline_furnace") { rk ->
        RecipeType.simple<MithrilineFurnaceRecipe>(rk)
    }

    // Recipe Serializers
    private val mithrilineFurnaceRSReg = recipeSerializers.register("mithriline_furnace") { _ ->
        RecipeSerializer(MithrilineFurnaceRecipe.codec, MithrilineFurnaceRecipe.streamCodec)
    }

    // Implements
    override val solarPrismCodec: MapCodec<SolarPrism> by lazy { solarPrismType.get() }
    override val clusterBlockCodec: MapCodec<ClusterBlock> by lazy { clusterType.get() }
    override val crystalBlockCodec: MapCodec<CrystalBlock> by lazy { crystalType.get() }

    override val mithrilineFurnace: MithrilineFurnace by lazy { mithrilineFurnaceReg.get() }

    override val soulStone: SoulStone by lazy { soulStoneItem.get() }

    override val mithrilineFurnaceMenu: MenuType<MithrilineFurnaceMenu> by lazy { mithrilineFurnaceMenuReg.get() }

    override val soulStoneComponent: DataComponentType<SoulStoneComponent> by lazy { soulStoneComponentRegistry.get() }

    override val mithrilineRecipeType: RecipeType<MithrilineFurnaceRecipe> by lazy { mithrilineFurnaceRTReg.get() }
    override val mithrilineRecipeSerializer: RecipeSerializer<MithrilineFurnaceRecipe> by lazy {
        mithrilineFurnaceRSReg.get()
    }

    private fun <B: Block> registerBlock(
        id: String,
        block: (BlockBehaviour.Properties) -> B,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        shouldRegisterItem: Boolean = true
    ): DeferredBlock<B> {
        val blockKey = { it: Identifier -> ResourceKey.create(Registries.BLOCK, it) }
        val bl = blocks.register(id) { rk ->
            block(properties.setId(blockKey(rk)))
        }

        if (shouldRegisterItem) {
            items.register(id) { rk ->
                NamedBlockItem(
                    bl.get(),
                    Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, rk))
                        .useBlockDescriptionPrefix()
                )
            }
        }

        return bl
    }

    private fun <I: Item> registerItem(
        id: String,
        item: (Item.Properties) -> I,
        properties: Item.Properties = Item.Properties()
    ): DeferredItem<I> {
        val itemKey = { it: Identifier -> ResourceKey.create(Registries.ITEM, it) }
        return items.register(id) { rk -> item(properties.setId(itemKey(rk))) }
    }

    override val mithrilineFurnaceEntity: BlockEntityType<MithrilineFurnaceEntity>
        get() = TODO("Not yet implemented")
    override val mithrilineFurnaceMultiblock: Multiblock
        get() = TODO("Not yet implemented")
}
