package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.research.BookCategory
import com.algorithmlx.ecr.mixin.RenderPipelinesAccessor
import com.mojang.blaze3d.pipeline.RenderPipeline
import java.util.concurrent.ConcurrentHashMap

object BookRenderPipelines {
    private val custom = ConcurrentHashMap<net.minecraft.resources.Identifier, RenderPipeline>()

    @JvmField
    val SPACE: RenderPipeline = create("pipeline/book_space".ecRL, "core/book_space".ecRL, "core/book_space".ecRL)
    @JvmField
    val THREAD: RenderPipeline = create("pipeline/book_thread".ecRL, "core/book_thread".ecRL, "core/book_thread".ecRL)

    fun forCategory(category: BookCategory?): RenderPipeline {
        val shader = category?.shader ?: return SPACE
        return custom.computeIfAbsent(category.id) {
            create(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(category.id.namespace, "pipeline/book/${category.id.path}"),
                shader.vertex,
                shader.fragment
            )
        }
    }

    private fun create(
        location: net.minecraft.resources.Identifier,
        vertex: net.minecraft.resources.Identifier,
        fragment: net.minecraft.resources.Identifier
    ): RenderPipeline = RenderPipelinesAccessor.ecrRegister(
        RenderPipeline.builder(RenderPipelinesAccessor.ecrGuiSnippet())
            .withLocation(location)
            .withVertexShader(vertex)
            .withFragmentShader(fragment)
            .build()
    )
}
