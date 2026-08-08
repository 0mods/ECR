# Assembled multiblocks

The assembled multiblock API is independent of the pattern multiblock API in
`com.algorithmlx.ecr.api.multiblock`. It replaces a validated set of blocks with
linked formed parts. An opted-in controller block is retained with its original
block entity. Breaking one formed part restores the original states and block
entity data.

## Define a structure

Register definitions during the mod registry lifecycle, after their referenced
blocks can be resolved. Offsets use the controller as `[0, 0, 0]` and describe
a structure facing north.

```kotlin
val teleporterStructure =
    assembledMultiblock(
        "ecreimagined:teleporter".ecRL,
        allowAssemblyFromAnyPart = true
    ) {
        controller(
            AssembledBlockMatcher.block(BlockRegistry.instance.magicalTeleporter),
            Shapes.box(0.125, 0.0, 0.125, 0.875, 0.5, 0.875)
        )
        part(
            1, 0, 0,
            AssembledBlockMatcher.block(BlockRegistry.instance.magicPlating),
            Shapes.box(0.0, 0.0, 0.25, 1.0, 0.25, 0.75)
        )
        part(
            -1, 0, 0,
            AssembledBlockMatcher.block(BlockRegistry.instance.magicPlating),
            Shapes.box(0.0, 0.0, 0.25, 1.0, 0.25, 0.75)
        )

        formedModel(
            geometry = "geometry.teleporter",
            texture = "ecreimagined:textures/block/teleporter.png".rl,
            lightMode = GeoLightMode.FULL_BRIGHT
        )

        // Render the model from this structure part instead of the controller.
        formedModelAnchor(-1, 0, 0)
    }
```

Definitions are stored in the vanilla-style
`ECRegistries.ASSEMBLED_MULTIBLOCK` registry under
`ecreimagined:assembled_multiblock`. The `assembledMultiblock` DSL only creates
a definition and never registers it. This keeps definition construction safe
inside loader-specific registry suppliers.

On NeoForge, definitions that depend on deferred blocks should themselves use
`DeferredRegister.create(ECRegistries.ASSEMBLED_MULTIBLOCK, modId)` and return
the DSL result from the deferred supplier. On Fabric, explicitly pass the DSL
result to Minecraft's `Registry.register` during initialization. There is no
separate ECR registry facade. Avoid registering definitions from gameplay code,
because registries are frozen by then.

```kotlin
// Fabric initializer
val teleporterId = "ecreimagined:teleporter".ecRL
val teleporterStructure = Registry.register(
    ECRegistries.ASSEMBLED_MULTIBLOCK,
    teleporterId,
    assembledMultiblock(teleporterId) {
        // parts and formed model
    }
)
```

There is exactly one formed GEO model per definition. It is rendered by the
controller; the other formed blocks are invisible placeholders. Consequently,
targeting or breaking another part does not split the rendered model or apply a
vanilla breaking overlay to one of its cubes.

`formedModelAnchor(x, y, z)` selects which configured part is the model origin.
The offset uses the same north-facing controller-relative coordinates as
`part`; it rotates with the assembled structure. It defaults to the controller
at `[0, 0, 0]` and must reference one of the definition's parts.

The optional `VoxelShape` passed to `controller` or `part` is local to that
formed block and defaults to a full cube. For a single shape covering the whole
formed structure, use coordinates relative to the model anchor:

```kotlin
formedModelAnchor(0, -1, 0)
formedShape(Shapes.box(0.0, 0.0, 0.0, 1.0, 2.0, 1.0))
```

`formedShape(shape)` uses `formedModelAnchor` regardless of DSL call order.
Use `formedShape(shape, BlockPos(...))` to select a different origin. The API
slices this shape into block-local collision shapes, while retaining the whole
shape for targeting and selection. Hovering any formed part therefore outlines
the complete structure instead of one block. A `BoundGem` uses the same complete
shape and renders it relative to the part stored in the gem, so linking a
different part does not move the outline.

Collision outside the cells occupied by configured parts is clipped. Add formed
parts for every cell that must provide collision. Both the complete selection
shape and the local collision slices rotate with the structure facing.

Direction-based calculations do not throw for a vertical facing. Singular
queries such as `rotate`, `rotateShape`, `worldPosition`, and
`controllerPosition` return `null`; collection queries return an empty list,
`matches` returns `false`, and formed shape lookup falls back to a full block.

`AssembledBlockMatcher.state` checks the complete block state,
`AssembledBlockMatcher.block` accepts every state of a block, and
`AssembledBlockMatcher.tag` accepts a block tag. These built-in matchers also
provide the block state used by book previews. A custom matcher can override
`previewState()`; a part whose matcher returns `null` is omitted from the
unassembled preview. For a tag, pass an explicit state as the second argument
when its automatically selected first block is not the desired example.

## Render in a research book

Use the `ecreimagined:assembled_multiblock` element type with the registered
assembled multiblock ID:

```json
{
  "type": "ecreimagined:assembled_multiblock",
  "multiblock": "ecreimagined:teleporter",
  "assembled": false,
  "scale": 0.9,
  "rotation_x": 25,
  "rotation_y": -30
}
```

The preview starts unassembled by default. Its arrow and layer buttons behave
like the regular multiblock element. The hammer button uses
`textures/item/hammer.png` and switches between the original block structure
and the definition's single formed GEO model. Set `assembled` to `true` to
start with the formed model. The button is disabled when `formedModel` is not
configured.

Custom recipe renderers can embed the same interactive preview:

```kotlin
builder.assembledMultiblock(
    teleporterStructure,
    x = 16,
    y = 24,
    width = 150,
    height = 150
)
```

## Form a structure

Call the runtime adapter on the logical server from the controller block, an
item, or another activation event:

```kotlin
val result = AssembledMultiblockRuntime.assemble(
    level,
    teleporterStructure,
    controllerPos,
    facing
)

when (result) {
    is AssemblyResult.Success -> Unit
    is AssemblyResult.Failure -> {
        // result.reason and result.position identify the failed validation.
    }
}
```

When `allowAssemblyFromAnyPart` is `false` (the default), `controllerPos` must
be the controller as before. When it is `true`, that argument may be the
position of any matching structure part. The runtime subtracts the rotated
part offset, validates each possible controller candidate and stores the
resolved controller in `Success.snapshot.controllerPos`.

A registered definition can also be addressed by its registry ID:

```kotlin
AssembledMultiblockRuntime.assemble(
    level,
    "ecreimagined:teleporter".ecRL,
    selectedPartPos,
    facing
)
```

The operation validates all positions and loaded chunks before changing the
world. A failed replacement rolls every changed position back to its captured
state.

Using the registered ECR hammer on a matching structure performs the same
operation automatically. With `allowAssemblyFromAnyPart = false`, use it on the
controller block. When that option is enabled, the hammer may be used on any
part of the structure. Definitions are checked in Minecraft registry order and
the first complete match is assembled.

## Custom formed blocks

`AssembledMultiblockRuntime` uses ECR's generic formed part. A machine that
needs to retain its typed controller block and block entity can implement
`AssembledMultiblockControllerBlock`. Its `assembledState` method returns the
formed state of the same block, normally by enabling an `assembled` block-state
property. The controller block entity must implement
`AssembledMultiblockPartEntity`, persist its data through
`AssembledMultiblockDataIO`, and clear it when requested.

The typed controller receives `assembledMultiblockData` only after every part
has been replaced successfully. Machine ticks, menus, capabilities, and other
behavior that must be unavailable before formation should check
`isAssembledMultiblock`. The current controller inventory and machine data are
preserved when the structure is disassembled.

Register `AssembledMultiblockRenderer<YourControllerBlockEntity>` for each
retained controller block entity type on the client. The renderer ignores an
unassembled controller and uses the definition's formed model after assembly.

Code that resolves MRU by a world position should use
`level.resolveMRUDevice(pos)` instead of casting `level.getBlockEntity(pos)`.
For a formed placeholder this returns the retained controller's `MRUDevice`,
so a `BoundGem` may be linked to any part without making every placeholder
implement or proxy `MRUDevice`.

For other custom formed-state rules, call `AssembledMultiblocks.assemble`
directly with an `AssembledStateFactory`. The factory receives the part, its
original state, whether it is the controller, and the structure facing. Every
returned state must create a block entity implementing
`AssembledMultiblockPartEntity`.

The controller and one backup part persist the full structure snapshot. Other
parts persist their link and their own original block. Loaded parts validate
the structure every second, so command or explosion removal also triggers
recovery. Parts in unloaded chunks restore themselves after their controller
is loaded in the unformed state.

Breaking a formed part restores the structure first, then breaks the original
block at the selected position through its own loot and tool handling. All
other original blocks remain in the world.

## Play an animation

The position may refer to any formed part; the API resolves the controller that
owns the single rendered model:

```kotlin
AssembledMultiblockRuntime.playAnimation(
    level,
    selectedPartPos,
    "animation.teleporter.open",
    AnimationType.PLAY_FREEZE
)
```

On the logical server this sends a clientbound animation command to nearby
clients. On the client the same call invokes the client animation handler
directly. The identifier is the exact key under `animations` in a Bedrock
`.animation.json` file. If it is not loaded, the client logs an error and keeps
the currently playing animation unchanged.

The last argument is optional and defaults to `AnimationType.PLAY_ONCE`.
`PLAY_FREEZE` holds the final frame, `PLAY_LOOPED` repeats the clip and
`PLAY_REVERSED` plays it once from the last frame to the first.

Stop the main animation from either side using any formed part position:

```kotlin
AssembledMultiblockRuntime.stopAnimation(level, selectedPartPos)
```

The server sends a stop command to the same nearby clients used by `playAnimation`;
the client call stops its local state immediately.

See [bedrock-geo.md](bedrock-geo.md) for resource locations and the reusable
block, item, and entity API.
