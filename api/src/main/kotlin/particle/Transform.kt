package com.algorithmlx.ecr.api.particle

import org.joml.Quaternionf
import org.joml.Vector3f

interface Transform {
    val parent: Transform?
    val isValid: Boolean
    val position: Vector3f
    val rotation: Quaternionf
    val velocity: Vector3f

    object Zero : Transform {
        override val parent: Transform? = null
        override val isValid: Boolean = true
        override val position: Vector3f get() = Vector3f()
        override val rotation: Quaternionf get() = Quaternionf()
        override val velocity: Vector3f get() = Vector3f()
    }

    companion object {
        fun create(
            position: Vector3f = Vector3f(),
            rotation: Quaternionf = Quaternionf(),
        ): Transform = object : Transform {
            override val parent: Transform? = null
            override val isValid: Boolean = true
            override val position: Vector3f get() = position
            override val rotation: Quaternionf get() = rotation
            override val velocity: Vector3f get() = Vector3f()
        }
    }
}
