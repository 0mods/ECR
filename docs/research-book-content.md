# Research book content

## Inline book multiblocks

`book_multiblock` embeds a preview directly in a research page. It does not need an entry in the multiblock registry:

```json
{
  "type": "escr:book_multiblock",
  "pattern": [
    [
      "SSS",
      "SCS",
      "SSS"
    ]
  ],
  "key": {
    "S": "minecraft:stone",
    "C": "minecraft:crafting_table"
  }
}
```

The matcher forms accepted by regular JSON multiblocks are also accepted in `key`. `keys` is supported as an input alias for compatibility, but `key` is the canonical field.

The preview has a world-placement control. It anchors the pattern by its center cell at the block under the crosshair (or on the selected face if that block is not the expected center block), uses the player's horizontal direction, and closes the book. Only missing or incorrect cells remain highlighted; the projection disappears when every required cell matches.

## Groups and spacing

A group lays children out from left to right and moves a whole child to the next row when it no longer fits:

```json
{
  "type": "escr:group",
  "elements": [
    {
      "type": "escr:item",
      "item": "minecraft:iron_ingot",
      "tooltip": true
    },
    {
      "type": "escr:vertical_space",
      "width": 6
    },
    {
      "type": "escr:item",
      "item": "minecraft:gold_ingot"
    }
  ]
}
```

Inside a group, `vertical_space` reserves horizontal room between children. The existing `space` finishes the current row and reserves vertical room. Item tooltips are disabled by default and can be enabled per item with `"tooltip": true`.

## Navigation research nodes

A navigation node has `link` instead of `pages`. The target can be a category, a research node, or a one-based content page:

```json
{
  "title": "book.example.navigation.title",
  "category": "example:basics",
  "icon": { "item": "minecraft:compass" },
  "position": { "x": 80, "y": 40 },
  "link": {
    "type": "page",
    "target": "example:machine",
    "page": 2
  }
}
```

Canonical link forms are:

```json
{ "type": "category", "target": "example:advanced" }
{ "type": "research", "target": "example:machine" }
{ "type": "page", "target": "example:machine", "page": 2 }
```

Compact forms such as `{ "category": "advanced" }`, `{ "research": "machine" }`, and string links like `"research://example:machine?page=2"` are accepted as input. Link nodes must not contain `pages`. Category tooltips show the percentage of completed, non-link research nodes in that category.
