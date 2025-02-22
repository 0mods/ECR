package team._0mods.ecr.api.mru

@Deprecated("It is not needed, use universal variant", ReplaceWith(
    "MRUHolder", "team._0mods.ecr.api.mru.MRUHolder"
))
interface MRUGenerator : MRUHolder {
    override val holderType: MRUHolder.MRUHolderType
        get() = MRUHolder.MRUHolderType.TRANSLATOR
}