package com.algorithmlx.ecr.api.mru

import com.algorithmlx.ecr.api.assembled.AssembledMultiblocks
import com.algorithmlx.ecr.api.item.BoundGem
import com.algorithmlx.ecr.api.mru.storage.IOMRUStorage
import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

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
    val deviceType: DeviceType

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
        IO,

        /**
         * Represents an entity that can be bounded but can not translate MRU.
         */
        CONNECTABLE_RECEIVER,

        /**
         * Represents an entity that cannot be connected, but stores MRU.
         */
        UNCONNECTABLE;

        /**
         * Determines if this device is capable of exporting MRU.
         *
         * @return `true` if this type is either [TRANSLATOR] or [IO], otherwise `false`.
         */
        val isExporter: Boolean get() = this == TRANSLATOR || this.isUniversal

        /**
         * Determines if this device type supports both importing and exporting MRU.
         *
         * @return `true` if this type is [IO], otherwise `false`.
         */
        val isUniversal: Boolean get() = this == IO

        /**
         * Determines if this device is capable of receiving MRU.
         *
         * @return `true` if this type is either [RECEIVER] or [IO], otherwise `false`.
         */
        val isReceiver: Boolean get() = this == RECEIVER || this.isUniversal

        /**
         * Determines if this device type supports connecting with bound gem
         *
         * @return `true` if this type is [CONNECTABLE_RECEIVER], [TRANSLATOR] or [IO], otherwise `false`
         */
        val isConnectable: Boolean get() = this == CONNECTABLE_RECEIVER || this.isExporter
    }

    /**
     * Data structure containing locator storage information.
     *
     * @property locatorStorage the container associated with this locator.
     * @property locatorSlot the slot index within the storage.
     */
    data class LocatorData(
        val locatorStorage: Container,
        val locatorSlot: Int,
        /** Position whose distance to the linked block is constrained by the gem radius. */
        val position: BlockPos? = (locatorStorage as? BlockEntity)?.blockPos?.immutable()
    )
}

/** Resolves a direct device or the controller owning an assembled part at [pos]. */
fun Level.resolveMRUDevice(pos: BlockPos): MRUDevice? {
    val direct = getBlockEntity(pos)
    if (direct is MRUDevice) return direct
    return AssembledMultiblocks.controllerBlockEntity(this, pos) as? MRUDevice
}

/** Starts receive procedure for [MRUDevice], if it has a configured [MRUDevice.locator]. */
fun MRUDevice.processReceive(level: Level) {
    if (level.isClientSide) return

    val locatorData = this.locator ?: return
    val stack = locatorData.locatorStorage.getItem(locatorData.locatorSlot)
    val item = stack.item as? BoundGem ?: return

    val pos = item.getBoundPos(stack) ?: return
    val server = level.server ?: return
    val world = item.getWorld(stack)

    val logicalLevel = world?.let { server.getLevel(it) } ?: level
    val outsideRadius = locatorData.position?.let { receiverPos ->
        logicalLevel !== level || !item.isWithinBoundRadius(receiverPos, pos)
    } ?: false
    if (item.setOutsideBoundRadius(stack, outsideRadius))
        locatorData.locatorStorage.setChanged()
    if (outsideRadius) return

    val exporter = logicalLevel.resolveMRUDevice(pos) ?: return

    if (!exporter.deviceType.isExporter || !this.deviceType.isReceiver) return

    val currentContainer = this.mruStorage
    val generator = exporter.mruStorage

    generator.transferTo(currentContainer, item.transferStrength)
}
