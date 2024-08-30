package team._0mods.ecr.common.compact.kubejs

import dev.latvian.mods.kubejs.KubeJSPlugin
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent
import team._0mods.ecr.common.compact.kubejs.schema.MithrilineFurnaceSchema
import team._0mods.ecr.common.init.registry.ECRegistry

class ECKubeJSPlugin: KubeJSPlugin() {
    override fun registerRecipeSchemas(event: RegisterRecipeSchemasEvent) {
        event.register(ECRegistry.mithrilineFurnaceRecipe.id, MithrilineFurnaceSchema.SCHEMA)
    }
}