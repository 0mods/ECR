package com.algorithmlx.ecr.api.mru

import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import net.minecraft.world.Container
import net.minecraft.world.level.Level

/**
 * Represents an entity that can store, receive, or transfer MRU (Magical Radiation Units).
 *
 * This interface defines the core behavior for MRU holders, including storage access,
 * locator data for slot-based MRU interactions, and the specific type of MRU functionality.
 *
 * Can be applied to:
 *
 * [net.minecraft.world.level.block.entity.BlockEntity]
 */
interface MRUDevice {
    /**
     * Returns the current MRU storage associated with this holder.
     *
     * @return an instance of [IOMRUStorage] representing the stored MRU.
     */
    val mruStorage: IOMRUStorage

    /**
     * Provides locator data used for slot-based MRU access.
     *
     * If `null`, this holder cannot receive MRU from a generator.
     *
     * @return an optional [LocatorData] instance, or `null` if unavailable.
     */
    val locator: LocatorData? get() = null

    /**
     * Specifies the type of MRU functionality this holder provides.
     *
     * This is necessary for the MRU pick-up and distribution process.
     *
     * @return the [DeviceType] of this holder.
     */
    val holderType: DeviceType

    /**
     * Enum representing the different types of MRU Holders and their capabilities.
     */
    enum class DeviceType {
        /**
         * Represents an entity that can receive MRU but does not export it.
         */
        RECEIVER,

        /**
         * Represents an entity that can both receive and export MRU, acting as an intermediary.
         */
        TRANSLATOR,

        /**
         * Represents an entity that can both send and receive MRU universally.
         */
        IO;

        /**
         * Determines if this holder is capable of exporting MRU.
         *
         * @return `true` if this type is either [TRANSLATOR] or [IO], otherwise `false`.
         */
        val isExporter: Boolean get() = this == TRANSLATOR || this.isUniversal

        /**
         * Determines if this holder type supports both importing and exporting MRU.
         *
         * @return `true` if this type is [IO], otherwise `false`.
         */
        val isUniversal: Boolean get() = this == IO

        /**
         * Determines if this holder is capable of receiving MRU.
         *
         * @return `true` if this type is either [RECEIVER] or [IO], otherwise `false`.
         */
        val isReceiver: Boolean get() = this == RECEIVER || this.isUniversal
    }

    /**
     * Data structure containing locator storage information.
     *
     * @property locatorStorage the container associated with this locator.
     * @property locatorSlot the slot index within the storage.
     */
    data class LocatorData(val locatorStorage: Container, val locatorSlot: Int)
}

fun MRUDevice.processReceive(level: Level) {
    if (level.isClientSide) return
}
