# Bedrock GEO models

ECR loads Bedrock geometry and animation JSON directly. The implementation is
part of ECR API and has no GeckoLib dependency. Geometry is parsed and baked on
resource reload. On the fast path, render frames sample immutable animation data
on the CPU and upload only the resulting bone palette. Static vertices, UVs,
normals and bone indices stay in GPU buffers; the vertex shader applies the
animated bone matrices. Solid and cutout objects sharing geometry, texture and
render type are submitted as an instanced batch.

The renderer uses Blaze3D `GpuBuffer`, texel-buffer and `RenderPipeline` APIs,
so the same path works with the OpenGL and Vulkan backends. Set
`BedrockGeoRenderEngine.gpuRenderingEnabled = false` before rendering to force
the legacy CPU vertex path for diagnostics. A GPU prepare or draw failure is
logged once and automatically switches subsequent frames to that fallback.
Translucent and additive models always use the vanilla geometry path so
Minecraft can preserve the expected blending and ordering semantics.

When Iris is installed, the loader bootstrap uses its API to query the shader
pack state. The instanced renderer remains enabled while shaders are disabled;
enabling a shader pack switches Bedrock GEO submissions to the vanilla geometry
path dynamically. Iris is an optional compile-only dependency and is not
required at runtime.

## Resources

Put files in a resource pack under one of these paths:

```text
assets/<namespace>/geo/<name>.geo.json
assets/<namespace>/animations/<name>.animation.json
assets/<namespace>/textures/<name>.png
```

`models/**/*.geo.json` and `geo/**/*.animation.json` are also discovered. A
model references the geometry identifier from the JSON, not its file name:

```kotlin
val MODEL = GeoModel(
    geometry = "geometry.arcane_device",
    texture = "ecreimagined:textures/block/arcane_device.png".rl,
    renderType = GeoRenderType.CUTOUT,
    scale = 1F
)
```

`GeoLightMode.WORLD` is the default and samples Minecraft's lightmap.
Large models can look nearly black when their block entity is inside an opaque
block. Use `GeoLightMode.FULL_BRIGHT` when the texture should remain visible
independently of the light at the render origin:

```kotlin
val MODEL = GeoModel(
    geometry = "geometry.arcane_device",
    texture = "ecreimagined:textures/block/arcane_device.png".rl,
    lightMode = GeoLightMode.FULL_BRIGHT
)
```

## Geometry and UVs

Both Blockbench `Bedrock Block` and entity projects export
`minecraft:geometry`; ECR supports box UV and per-face UV objects from either.
A block model does not need to be converted into an entity model.

`description.texture_width` and `texture_height` define the texel coordinate
space used by every `uv` and `uv_size`. Keep the project texture size, exported
geometry dimensions and UV layout consistent. If a texture is changed from
16x16 to 128x128 as a newly packed atlas, update the project resolution and
re-export the geometry so the UV coordinates are scaled as well. Merely editing
the two description fields changes which region each face samples.

`visible_bounds_offset` is expressed in Bedrock world/block units, unlike bone
and cube coordinates. Invalid hierarchies are rejected during baking: bone names
must be unique, every parent must exist, and parent cycles are not allowed.
Per-face `uv_rotation` values must be multiples of 90 degrees.

For a file containing exactly one geometry, it may instead be referenced by a
Minecraft `Identifier`/ResourceLocation. Both the full resource path and the
short asset ID are supported:

```kotlin
val BY_SHORT_RESOURCE = GeoModel(
    geometry = "ecreimagined:arcane_device".rl,
    texture = "ecreimagined:textures/block/arcane_device.png".rl
)

val BY_FULL_RESOURCE = GeoModel(
    geometry = "ecreimagined:geo/arcane_device.geo.json".rl,
    texture = "ecreimagined:textures/block/arcane_device.png".rl
)
```

The short ID strips the leading `geo/` or `models/` folder and the
`.geo.json` suffix. A resource containing multiple geometries is ambiguous and
must still be addressed by its internal Bedrock identifier.

Supported animation channels are bone position, rotation and scale, including
pre/post keyframes, linear and Catmull-Rom interpolation, file-defined
once/loop/hold behavior, blend weight, start delay, loop delay and MoLang
expressions. More than one geometry or animation may be stored in a source JSON.
Scale channels respect override and additive blending, and zero-scale poses no
longer produce invalid normal matrices.

Animation sound, particle and timeline events, as well as `anim_time_update`,
are parsed for forward compatibility but are not dispatched or applied yet.
Per-face `material_instance` is likewise retained in parsed data, but one
`GeoModel` currently renders with one texture and render type.

## Block entities

Implement `GeoAnimatable` and register the generic renderer:

```kotlin
class ArcaneDeviceBlockEntity(...) : BlockEntity(...), GeoAnimatable {
    override val geoModel = MODEL
    override val geoAnimationState = GeoAnimationState()
}

BlockEntityRenderers.register(TYPE) { context ->
    GeoBlockEntityRenderer<ArcaneDeviceBlockEntity>(context)
}
```

### Block-state rotation

The generic block-entity renderer can rotate a model from the block's standard
horizontal `facing` property. Rotation is opt-in, and the resolved facing may
be inverted independently:

```kotlin
val MODEL = GeoModel(
    geometry = "geometry.arcane_device",
    texture = "escr:textures/block/arcane_device.png".rl,
    blockRotation = GeoBlockRotation(
        enabled = true,
        opposite = true
    )
)
```

`GeoBlockRotation.FACING`, `GeoBlockRotation.OPPOSITE`, and
`GeoBlockRotation.NONE` are predefined configurations. `NONE` is the default,
so existing models keep their previous orientation. The mapping uses north as
the unrotated model direction, just like a normal horizontal block state.

Play by block position. Server levels send a packet; client levels call the
validated client handler immediately:

```kotlin
GeoAnimationNetwork.play(level, blockPos, "animation.arcane_device.open")
```

Every `play` method accepts an optional `AnimationType` after the animation ID:

```kotlin
GeoAnimationNetwork.play(
    level,
    blockPos,
    "animation.arcane_device.open",
    AnimationType.PLAY_FREEZE
)
```

The default is `PLAY_ONCE`. `PLAY_FREEZE` holds the last frame,
`PLAY_LOOPED` repeats the clip, and `PLAY_REVERSED` plays once from the last
frame to the first. The selected type is included in server-to-client packets.
Completed `PLAY_ONCE` and `PLAY_REVERSED` entries are removed automatically
during client animation sampling.

Use the matching stop overloads to stop the main animation explicitly:

```kotlin
GeoAnimationNetwork.stop(level, blockPos)
GeoAnimationNetwork.stop(entity)
GeoAnimationNetwork.stopItem(player, EquipmentSlot.MAINHAND)
```

As with playback, a logical-server call sends a clientbound command and a
client-level call changes local state directly.

## Entities

An entity uses the same `GeoAnimatable` contract and `GeoEntityRenderer`:

```kotlin
EntityRenderers.register(TYPE) { context ->
    GeoEntityRenderer<ArcaneConstruct>(context)
}

GeoAnimationNetwork.play(
    entity,
    "animation.arcane_construct.attack",
    AnimationType.PLAY_ONCE
)
```

`GeoModel.shadowRadius` controls the entity shadow. The generic renderer also
keeps the vanilla name, leash and fire submissions from `EntityRenderer`.

### Attach Bedrock particles to a bone

On the client, create a live particle transform from a GEO entity and a bone
name, then pass it to the normal Bedrock particle spawn call:

```kotlin
val effect = BedrockParticles["escr:arcane_construct/hand".rl] ?: return
val hand = Transform.bone(entity, "right_hand")
val emitter = ClientParticleSystems.system(entity.level()).spawn(
    effect,
    transform = hand
)
```

An optional third argument is a bone-local offset in model-space blocks:

```kotlin
val hand = Transform.bone(entity, "right_hand", Vector3f(0F, 0.125F, 0F))
```

The transform samples the current animation pose and follows the model scale,
entity body rotation, world position, and velocity. The emitter expires when
the entity is removed or when its model/bone can no longer be resolved. Spawn
fails immediately with a descriptive error if the entity is not
`GeoAnimatable`, the geometry is not loaded, or the requested bone is absent.
The emitter origin always follows the bone. To make already emitted particles
remain bone-local too, enable the appropriate position/rotation flags in the
effect's `minecraft:emitter_local_space` component; without it, particles leave
the moving bone in world space after they spawn, matching Bedrock semantics.

## Items

Implement `GeoItemProvider` on the `Item`. The default animation state is kept
per `ItemStack`, so equal item types do not share playback:

```kotlin
class ArcaneTool(properties: Properties) : Item(properties), GeoItemProvider {
    override fun geoModel(stack: ItemStack) = MODEL
}
```

Use the ECR special renderer in
`assets/<namespace>/items/<item_id>.json`:

```json
{
  "model": {
    "type": "minecraft:special",
    "base": "minecraft:item/generated",
    "model": {
      "type": "ecreimagined:bedrock_geo"
    }
  }
}
```

For an equipped item, use its owning living entity and equipment slot so the
server can address the same stack on every client:

```kotlin
GeoAnimationNetwork.playItem(
    player,
    EquipmentSlot.MAINHAND,
    "animation.arcane_tool.use",
    AnimationType.PLAY_REVERSED
)
```

Client-only UI code may call
`ClientGeoAnimations.play(stack, "animation.arcane_tool.inspect")` directly.

## Missing identifiers

Playback is applied only after the receiving client finds both the animation ID
and the model geometry in its loaded resources. A missing ID or geometry logs an
error and returns without clearing or replacing the current animation.
