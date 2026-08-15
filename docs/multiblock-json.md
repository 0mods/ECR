# JSON multiblocks

ECR loads pattern multiblocks from
`data/<namespace>/multiblocks/<path>.json` and assembled multiblocks from
`data/<namespace>/assembled_multiblocks/<path>.json`. The resource path is the
definition ID: `data/example/multiblocks/machine/core.json` defines
`example:machine/core`.

JSON definitions take priority over code definitions. Removing an overriding
resource on the next data reload restores the code definition. The normalized
resources are synchronized to clients, so research-book previews and assembled
rendering use the server's structure on a dedicated server as well.

## Pattern and layer order

`pattern` is an array of Y layers. The first layer is the foundation, and each
following layer is one block higher. A layer is an array of Z rows; characters
inside a row advance along X:

```json
{
  "pattern": [
    ["AAA", "AAA", "AAA"],
    ["BBB", "BBB", "BBB"]
  ],
  "keys": {
    "A": "minecraft:stone",
    "B": "#minecraft:logs"
  }
}
```

The example is decoded in this exact order:

```text
A, A, A,
A, A, A,
A, A, A,
B, B, B,
B, B, B,
B, B, B
```

In other words, X is the fastest coordinate, then Z, then Y. All layers must
have the same row count and all rows must have the same width. A space without
a key is an optional/ignored cell in a regular multiblock and an absent part in
an assembled multiblock.

## Keys

Every used non-space character must occur in `keys`. Supported concise values
are:

- `"minecraft:stone"`: any block state of this block;
- `"#minecraft:logs"`: any block in this tag;
- `null`: optional air/ignored cell.

An object is decoded through the registered `MultiblockMatcher` codec, so
built-in and third-party matcher types remain extensible. A type without a
namespace uses `escr`:

```json
{
  "type": "block",
  "state": {
    "Name": "minecraft:oak_log",
    "Properties": {
      "axis": "y"
    }
  },
  "ignore_tag": false,
  "required": true
}
```

The built-in full type IDs are `escr:block`, `escr:tag`, and `escr:list`.

## Assembled multiblocks

An assembled definition uses the same `pattern` and `keys`, plus a required
`controller` coordinate. Coordinates are `[x, y, z]` indices inside the JSON
pattern, before conversion to controller-relative offsets:

```json
{
  "pattern": [
    ["BBB", "BBB", "BBB"],
    [" A ", "ACA", " A "]
  ],
  "controller": [1, 1, 1],
  "keys": {
    "A": "minecraft:copper_block",
    "B": "minecraft:stone_bricks",
    "C": "escr:ray_tower"
  },
  "allow_assembly_from_any_part": true,
  "model_anchor": [1, 0, 1]
}
```

When this JSON overrides a code-registered assembled multiblock, omitted
metadata is inherited from the code definition: the formed GEO model,
assembly-from-any-part flag, model anchor, unified formed shape, shape origin,
and per-part formed shapes at offsets that still exist. The JSON pattern and
keys always replace the code structure.

For a JSON-only assembled definition, metadata has these defaults:

- `allow_assembly_from_any_part`: `false`;
- `model_anchor`: the controller;
- formed collision shape: full blocks;
- formed GEO model: absent.

An optional formed model can be declared directly:

```json
{
  "formed_model": {
    "geometry_resource": "example:machine",
    "texture": "example:textures/block/machine.png",
    "render_type": "cutout",
    "scale": 1.0,
    "shadow_radius": 0.0,
    "light_mode": "world",
    "block_rotation": {
      "enabled": true,
      "opposite": false
    }
  }
}
```

Use `geometry` instead of `geometry_resource` to address an internal Bedrock
identifier such as `geometry.example.machine`. Exactly one of those two fields
is required inside `formed_model`. Setting `formed_model` to JSON `null`
explicitly disables an inherited model. `formed_shape_origin`, when present,
uses the same absolute pattern-coordinate format as `model_anchor`.

## Which IDs may be loaded

A data pack cannot introduce arbitrary IDs by itself. A JSON file is accepted
only when one of these is true:

1. the same regular/assembled ID is registered in code;
2. a developer registered it as JSON-only;
3. a player added it to the corresponding config allowlist.

Register a mandatory JSON-only definition during mod initialization:

```kotlin
MultiblockDefinitions.registerJsonOnly("example:ritual")
MultiblockDefinitions.registerJsonOnlyAssembled("example:assembled_machine")
```

If its JSON file is missing, data-resource loading fails immediately. The same
strict check applies to player-configured custom IDs. In the ECR config
(`config/ecr.json` on Fabric or `config/escr.json` on NeoForge):

```json
{
  "multiblocks": {
    "custom_ids": ["example:ritual"],
    "custom_assembled_ids": ["example:assembled_machine"]
  }
}
```

IDs without a namespace use `escr`. An unregistered JSON resource is rejected
instead of being silently loaded. Code that needs to resolve custom or
overridden definitions should use `MultiblockDefinitions[id]`,
`MultiblockDefinitions.assembled(id)`, `all()`, or `allAssembled()` rather than
reading the backing frozen registries directly.
