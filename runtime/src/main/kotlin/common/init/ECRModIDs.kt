package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.api.utils.ecRL

object ECRModIDs {
    // Block Type Codecs
    const val CLUSTER = "cluster"
    const val CRYSTAL = "crystal"

    // Universal
    const val SOLAR_PRISM = "solar_prism"
    const val MITHRILINE_FURNACE = "mithriline_furnace"
    const val MITHRILINE_CRYSTAL = "mithriline_crystal"
    const val MATRIX_DESTRUCTOR = "matrix_destructor"
    const val SOUL_STONE = "soul_stone"
    const val BOUND_GEM = "bound_gem"
    const val MAGIC_TABLE = "magic_table"
    const val COLD_DISTILLER = "cold_distiller"

    // Blocks
    const val MITHRILINE_PLATING = "mithriline_plating"
    const val VOID_STONE = "void_stone"
    const val PALE_BLOCK = "pale_block"
    const val PALE_PLATING = "pale_plating"
    const val MAGIC_PLATING = "magic_plating"
    const val DEMONIC_PLATING = "demonic_plating"
    const val FLAME_CLUSTER = "flame_cluster"
    const val WATER_CLUSTER = "water_cluster"
    const val EARTH_CLUSTER = "earth_cluster"
    const val AIR_CLUSTER = "air_cluster"

    // Data Components
    const val BOOK_TYPE = "book_type"

    // BookTypes
    const val BASIC = "basic"
    const val MRU = "mru"
    const val ENGINEER = "engineer"
    const val HOANNA = "hoanna"
    const val SHADE = "shade"

    // Items
    const val RESEARCH_BOOK = "research_book"

    const val WEAKNESS_ELEMENTAL_AXE = "weakness_elemental_axe"
    const val WEAKNESS_ELEMENTAL_HOE = "weakness_elemental_hoe"
    const val WEAKNESS_ELEMENTAL_PICKAXE = "weakness_elemental_pickaxe"
    const val WEAKNESS_ELEMENTAL_SHOVEL = "weakness_elemental_shovel"
    const val WEAKNESS_ELEMENTAL_SWORD = "weakness_elemental_sword"

    const val ELEMENTAL_GEM = "elemental_gem"
    const val FLAME_GEM = "flame_gem"
    const val WATER_GEM = "water_gem"
    const val EARTH_GEM = "earth_gem"
    const val AIR_GEM = "air_gem"

    const val ELEMENTAL_CORE = "elemental_core"
    const val COMBINED_MAGIC_ALLOYS = "combined_magic_alloys"
    const val DEMONIC_CORE = "demonic_core"
    const val DIAMOND_PLATE = "diamond_plate"
    const val EMERALD_PLATE = "emerald_plate"
    const val ENDER_SCALE_ALLOY = "ender_scale_alloy"
    const val FORCEFIELD_CORE = "forcefield_core"
    const val FORCIFIELD_PLATING = "forcefield_plating"
    const val FORTIFIED_FRAME = "fortified_frame"
    const val MAGIC_FORTIFIED_PLATING = "magic_fortified_plating"
    const val MAGIC_PLATE = "magic_plate"
    const val MAGIC_PURIFIED_BLAZE_ALLOY = "magic_purified_blaze_alloy"
    const val MAGIC_PURIFIED_ENDER_SCALE_ALLOY = "magic_purified_ender_scale_alloy"
    const val MAGIC_PURIFIED_GLASS_ALLOY = "magic_purified_glass_alloy"
    const val OBSIDIAN_PLATE = "obsidian_plate"
    const val PALE_CORE = "pale_core"
    const val PALE_PLATE = "pale_plate"
    const val PARTICLE_CATCHER = "particle_catcher"
    const val PARTICLE_EMITTER = "particle_emitter"
    const val SUN_IMBUED_GLASS = "sun_imbued_glass"
    const val VOID_PLATING = "void_plating"
    const val MITHRILINE_INGOT = "mithriline_ingot"
    const val MAGICAL_INGOT = "magical_ingot"
    const val MITHRILINE_DUST = "mithriline_dust"
    const val HEATING_ROD = "heating_rod"
    const val MITHRILINE_CRYSTAL_GEM = "mithriline_crystal_gem"
    const val MRU_RESONATING_CRYSTAL = "mru_resonating_crystal"
    const val FADING_CRYSTAL = "fading_crystal"

    // MRU Types
    const val UBMRU = "ubmru"
    const val ESPE = "espe"

    // Multiblock Matcher
    const val TAG = "tag"
    const val BLOCK = "block"
    const val LIST = "list"

    // Multiblocks
    const val FLAME_CRYSTAL = "flame_crystal"
    const val WATER_CRYSTAL = "water_crystal"
    const val EARTH_CRYSTAL = "earth_crystal"
    const val AIR_CRYSTAL = "air_crystal"
    const val LIGHTNING_COLLECTOR = "lightning_collector"

    // Recipes
    const val STRUCTURE = "structure"

    // Creative Tabs
    const val TAB_ITEMS = "tab_items"
    const val TAB_BLOCKS = "tab_blocks"

    // Ingredients
    const val COUNT = "count"

    fun guiLocation(id: String) = "textures/gui/$id.png".ecRL
}
