# Kotlin Brigadier command DSL

The command API in `com.algorithmlx.ecr.api.commands` is a typed Kotlin DSL over Brigadier. It builds native Brigadier nodes, so custom argument types, syntax exceptions, usage generation, parsing, completion and dispatcher inspection continue to work normally.

The compact string syntax is inspired by [HollowCore's CommandBuilder](https://github.com/HollowHorizon/HollowCore/blob/1.20.1/src/main/java/ru/hollowhorizon/hc/common/commands/CommandBuilder.kt), but supports an arbitrary command tree and the rest of Brigadier's builder model.

## Command trees and arguments

```kotlin
dispatcher.commands {
    literal("ecr", "essentialcraft") {
        requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))

        "research" {
            "unlock" {
                argument(
                    "targets",
                    EntityArgument.players(),
                    { context, name -> EntityArgument.getPlayers(context, name) },
                ) { targets ->
                    argument("research", StringArgumentType.string()) { research ->
                        executes {
                            val players by targets
                            val id by research
                            players.count { unlock(it, id) }
                        }
                    }
                }
            }
        }
    }
}
```

`CommandArgument<S, T>` is a typed reference. Call it (`targets()`) or use it as a property delegate (`val players by targets`) inside `executes`, `runs`, suggestion providers and redirect modifiers. The four-argument `argument` overload maps a parser's intermediate value through a domain-specific getter such as `EntityArgument.getPlayers`.

For small commands, arguments can also be read by name:

```kotlin
executes {
    val amount: Int by arguments
    amount
}
```

Use `executes` when the integer result is meaningful. Use `runs` for side effects that should return `Command.SINGLE_SUCCESS`.

## Aliases and suggestions

Aliases may be passed to `literal` or declared inside it. They preserve the literal's direct executor and redirect completion/parsing to its children.

```kotlin
literal("teleport", "tp") {
    aliases("tele", "move")

    argument("destination", StringArgumentType.word()) {
        suggests {
            suggestMatching(knownDestinations)
            suggest("spawn", tooltip)
        }
        runs { teleport(source, argument("destination")) }
    }
}
```

Use `suggests(SuggestionProvider)` for an existing native provider, or `suggestsAsync` when the provider returns its own `CompletableFuture<Suggestions>`.

## Redirects and forks

All Brigadier forwarding variants accept either a native `CommandNode` or a DSL reference:

```kotlin
val destination = literal("destination") {
    "run" { executes { source.result } }
}

"redirected" {
    redirect(destination) { source.withResult(7) }
}

"forked" {
    fork(destination) { selectedSources }
}

"forwarded" {
    forward(destination, forks = false) { listOf(transformedSource) }
}
```

These methods intentionally retain Brigadier's redirect semantics: remaining input is parsed against the target's children. The higher-level alias API also handles a command attached directly to the aliased literal.

## Native escape hatches

- `then(CommandNode)` and `then(ArgumentBuilder)` attach native tree parts.
- `executesRaw(Command)` installs a native command callback.
- `brigadier { ... }` configures the underlying literal or argument builder.
- `CommandTreeScope.dispatcher` and `root` expose the native dispatcher tree.
- `resultConsumer { ... }` wraps `CommandDispatcher.setConsumer`.
- `completionSuggestions(input, source, cursor)` is a small completion helper.

Because the returned value is the original `CommandDispatcher`, parsing, execution, usage generation, path lookup and ambiguity checks remain available through Brigadier itself.
