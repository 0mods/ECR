package team._0mods.ecr.client

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import java.util.function.Consumer

class ArmorTestModel(material: ArmorMaterial, slot: EquipmentSlot, props: Item.Properties): ArmorItem(material, slot, props) {
    override fun initializeClient(consumer: Consumer<IClientItemExtensions?>) {
        super.initializeClient(consumer)

    }

}