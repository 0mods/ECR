@file:JvmName("MRUReceiveUtils")
package team._0mods.ecr.api.mru

@Deprecated("It is not needed, use universal variant", ReplaceWith(
    "MRUHolder", "team._0mods.ecr.api.mru.MRUHolder"
))
interface MRUReceivable: MRUHolder {
    override val holderType: MRUHolder.MRUHolderType
        get() = MRUHolder.MRUHolderType.RECEIVER
}
