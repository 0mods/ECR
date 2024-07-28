package team._0mods.ecr.common

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import team._0mods.ecr.common.init.registry.ECTabs
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

val String.rl: ResourceLocation
    get() = ResourceLocation(this)

fun makeBERegistry(modId: String): Pair<Pair<DeferredRegister<Block>, DeferredRegister<Item>>, DeferredRegister<BlockEntityType<*>>> {
    val b = DeferredRegister.create(ForgeRegistries.BLOCKS, modId)
    val be = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modId)
    val item = DeferredRegister.create(ForgeRegistries.ITEMS, modId)
    return b to item to be
}

fun Pair<Pair<DeferredRegister<Block>, DeferredRegister<Item>>, DeferredRegister<BlockEntityType<*>>>.register(bus: IEventBus) {
    val b = this.first.first
    val i = this.first.second
    val be = this.second

    b.register(bus)
    i.register(bus)
    be.register(bus)
}

@JvmName("registerAutoItem")
fun <T: Block, X: BlockEntity> Pair<
        Pair<DeferredRegister<Block>, DeferredRegister<Item>>,
        DeferredRegister<BlockEntityType<*>>
        >.register(
    id: String,
    block: () -> T,
    be: (BlockPos, BlockState) -> X,
    props: Item.Properties.() -> Unit = { tab(ECTabs.tabBlocks) }
): Lazy<Pair<T, BlockEntityType<X>>> =
    this.register(id, block, be) { it: Block -> BlockItem(it, Item.Properties().apply(props)) }

fun <T: Block, X: BlockEntity> Pair<
        Pair<DeferredRegister<Block>, DeferredRegister<Item>>,
        DeferredRegister<BlockEntityType<*>>
>.register(id: String, block: () -> T, be: (BlockPos, BlockState) -> X, item: (T) -> Item): Lazy<Pair<T, BlockEntityType<X>>> {
    val blocks = this.first.first
    val items = this.first.second
    val entities = this.second

    val b = blocks.register(id, block)
    items.register(id) { item(b.get()) }
    val ber = entities.register(id) { BlockEntityType.Builder.of({ p, s -> be(p, s) }, b.get()).build(null) }

    return lazy { b.get() to ber.get() }
}

// fast registry utils
class DelegatedRegistry<T>(val regObj: RegistryObject<T>): ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = regObj.get()
}
