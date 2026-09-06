# Radiating Chamber recipes

Recipes belong in `data/<namespace>/recipe/` and use `type: escr:radiating_chamber`.

`input` is the required ingredient in the upper slot. `secondary` is the ingredient in the lower slot; omitting it requires that slot to be empty. Ingredient order matters. A completed recipe consumes the count requested by each ingredient, defaulting to one item per occupied input slot.

`time` is a positive number of processing ticks. `mru` is the positive MRU cost per processing tick and defaults to `1`. `result` contains an item `id` and an optional `count` that defaults to `1`.

For example, this recipe consumes one diamond and one emerald to produce one MRU Resonating Crystal after 100 ticks, using 50 MRU each tick:

```yaml
type: escr:radiating_chamber
input: minecraft:diamond
secondary: minecraft:emerald
time: 100
mru: 50
ignore_balance: true
result:
  id: escr:mru_resonating_crystal
  count: 1
```

## Balance

`balance` restricts both components of the machine balance with optional `min` and `max`. Explicit bounds are exclusive, matching EC3: the balance must be greater than `min` and less than `max`. Omitting either bound leaves that side unrestricted within the valid balance interval. Without `balance`, any finite balance from `0` through `2`, including both endpoints, is accepted.

`ignore_balance: true` skips balance matching, even when the recipe contains a `balance` restriction. It defaults to `false`. The bundled unrestricted recipes use it explicitly.

This custom recipe requires EC3's magic balance interval:

```yaml
type: escr:radiating_chamber
input: escr:soul_stone
time: 20000
mru: 1
balance:
  min: 0.69
  max: 1.49
result:
  id: escr:magical_ingot
  count: 1
```

The lower ingredient slot must be empty for this example. It illustrates the format and is not bundled as a recipe. Adding `ignore_balance: true` makes it accept any balance without removing the configured interval.

Use only `min` for a high balance recipe, accepting balances greater than `1.5` through `2`:

```yaml
balance:
  min: 1.5
```

Use only `max` for a low balance recipe, accepting balances from `0` up to, but excluding, `0.5`:

```yaml
balance:
  max: 0.5
```

EC3's matrix transformation recipes used these strict thresholds:

| Transformation | Balance condition | Processing ticks | MRU per tick |
| --- | --- | ---: | ---: |
| High balance | Greater than `1.5` | 10000 | 1 |
| Low balance | Greater than `Float.MIN_VALUE` and less than `0.5` | 10000 | 1 |
| Magic balance | Greater than `0.69` and less than `1.49` | 20000 | 1 |

EC3 represented the unrestricted upper bound as `Float.MAX_VALUE`. Its low and unrestricted recipes technically excluded zero because their lower bound was the smallest positive float. To reproduce that lower threshold exactly, add `min: 1.401298464324817E-45`. The port's unrestricted recipes deliberately ignore balance, including at zero.

## Bundled recipes

| Upper input | Lower input | Result | Count | Processing ticks | MRU per tick |
| --- | --- | --- | ---: | ---: | ---: |
| Stone | Iron Ingot | Fortified Stone | 4 | 10 | 1 |
| Glass | Iron Ingot | Fortified Glass | 4 | 10 | 1 |
| Stone | Elemental Gem | Pale Core | 1 | 100 | 10 |
| Diamond | Emerald | MRU Resonating Crystal | 1 | 100 | 50 |

These recipes preserve EC3's ingredient order, output counts, processing duration, and default per-tick MRU costs. EC3 named the duration field `mruRequired`, but its machine advanced that field once per tick and charged `costModifier` MRU per tick with the default usage multiplier.

Recipes for Magical Essence, Pale Pearl, Pale Ingot, Pale Gem, MRU Magnet, and the matrix projection variants are not bundled because those items are not registered in this port yet.

## Machine operation

The chamber crafting recipe follows EC3's layout and uses the registered Particle Catcher in place of the original MRU Catcher, which is not yet available in this port.

The chamber stores 5000 MRU. Insert a linked Bound Gem into its connection slot to receive MRU and balance from the source. A redstone signal pauses processing while the chamber can still receive energy. Insufficient MRU pauses progress; invalid ingredients, balance, or output space reset it. Progress is saved with the active recipe and cannot carry over to a different recipe. Reloading a changed datapack recipe also resets its progress.

Automation inserts the upper ingredient from above, the lower ingredient from the sides, and extracts the result from below. The output slot rejects insertion.

## Generator balance

Original EC3 used one scalar balance. In this port, generators write that value to both components of `MRUBalance`, so transfer and recipe restrictions use the same values.

| Generator | Default balance | Configuration |
| --- | --- | --- |
| Heat Generator | Random in `[0, 2)` once on the server | `heat_generator.default_balance`: `-1` for random, or a fixed value from `0` to `2` |
| Ultra Heat Generator | Random in `[0, 2)` once on the server | Preserves the existing balance when upgrading a Heat Generator |
| Cold Distiller | `0` | `cold_distiller.balance_produced` |
| Matrix Destructor | `1` | `matrix_destructor.balance_produced` |

The Heat Generator saves its initialization flag and balance, so chunk reloads and server restarts do not reroll it. Existing generators without the flag initialize once after updating. Cold Distiller and Matrix Destructor enforce their configured balance each server tick, including when idle.

Generator sources: [Heat Generator](https://github.com/Modbder/EssentialCraft3/blob/master/java/ec3/common/tile/TileHeatGenerator.java), [Ultra Heat Generator](https://github.com/Modbder/EssentialCraft3/blob/master/java/ec3/common/tile/TileUltraHeatGenerator.java), [Cold Distillator](https://github.com/Modbder/EssentialCraft3/blob/master/java/ec3/common/tile/TileColdDistillator.java), and [Matrix Absorber](https://github.com/Modbder/EssentialCraft3/blob/master/java/ec3/common/tile/TileMatrixAbsorber.java).

Source: [EC3 recipe registry](https://github.com/Modbder/EssentialCraft3/blob/master/java/ec3/common/registry/RecipeRegistry.java), [recipe matching](https://github.com/Modbder/EssentialCraft3/blob/master/java/ec3/api/RadiatingChamberRecipes.java), [machine processing](https://github.com/Modbder/EssentialCraft3/blob/master/java/ec3/common/tile/TileRadiatingChamber.java), and [item metadata names](https://github.com/Modbder/EssentialCraft3/blob/master/java/ec3/common/item/ItemGenericEC3.java).
