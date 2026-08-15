package com.algorithmlx.ecr.api.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CommandDslTest {
    private data class Source(
        val allowed: Boolean = true,
        val value: Int = 0,
    )

    @Test
    fun `builds arbitrary subcommands and reads typed mapped arguments`() {
        val dispatcher = commandDispatcher<Source> {
            literal("calculate", "calc") {
                literal("sum") {
                    alias("add")
                    argument("left", IntegerArgumentType.integer()) { leftArgument ->
                        argument(
                            "right",
                            IntegerArgumentType.integer(),
                            { context, name -> context.getArgument(name, Int::class.java) * 2 },
                        ) { rightArgument ->
                            executes {
                                val left by leftArgument
                                val doubledRight by rightArgument
                                left + doubledRight
                            }
                        }
                    }
                }
            }
        }

        assertEquals(8, dispatcher.execute("calculate sum 2 3", Source()))
        assertEquals(10, dispatcher.execute("calc add 4 3", Source()))
        assertNotNull(dispatcher.findNode(listOf("calculate", "sum", "left", "right")))
    }

    @Test
    fun `copies requirements to aliases and keeps command result values`() {
        val dispatcher = commandDispatcher<Source> {
            literal("secure", "s") {
                requires { allowed }
                executes { source.value }
            }
        }

        assertEquals(42, dispatcher.execute("secure", Source(value = 42)))
        assertEquals(42, dispatcher.execute("s", Source(value = 42)))
        assertFailsWith<CommandSyntaxException> { dispatcher.execute("secure", Source(allowed = false)) }
        assertFailsWith<CommandSyntaxException> { dispatcher.execute("s", Source(allowed = false)) }
    }

    @Test
    fun `supports synchronous asynchronous and native suggestion providers`() {
        val dispatcher = commandDispatcher<Source> {
            "paint" {
                argument("color", StringArgumentType.word()) {
                    suggests { suggestMatching(listOf("amber", "blue", "aqua")) }
                    runs { }
                }
            }
            "async" {
                argument("value", StringArgumentType.word()) {
                    suggestsAsync {
                        +"future"
                        buildFuture()
                    }
                    runs { }
                }
            }
            "native" {
                argument("value", StringArgumentType.word()) {
                    suggests(SuggestionProvider { _, builder ->
                        builder.suggest("provider").buildFuture()
                    })
                    runs { }
                }
            }
        }

        val matching = dispatcher.completionSuggestions("paint a", Source())
            .get(1, TimeUnit.SECONDS)
            .list
            .map { it.text }
            .sorted()
        val async = dispatcher.completionSuggestions("async ", Source())
            .get(1, TimeUnit.SECONDS)
            .list
            .single()
            .text
        val native = dispatcher.completionSuggestions("native ", Source())
            .get(1, TimeUnit.SECONDS)
            .list
            .single()
            .text

        assertEquals(listOf("amber", "aqua"), matching)
        assertEquals("future", async)
        assertEquals("provider", native)
    }

    @Test
    fun `supports redirects source modifiers forks and general forwarding`() {
        val dispatcher = commandDispatcher<Source> {
            val target = literal("value") {
                "get" {
                    executes { source.value }
                }
            }
            "redirected" {
                redirect(target) { source.copy(value = 7) }
            }
            "forked" {
                fork(target) {
                    listOf(source.copy(value = 1), source.copy(value = 2))
                }
            }
            "forwarded" {
                forward(target, forks = false) {
                    listOf(source.copy(value = 9))
                }
            }
        }

        assertEquals(7, dispatcher.execute("redirected get", Source()))
        assertEquals(2, dispatcher.execute("forked get", Source()))
        assertEquals(9, dispatcher.execute("forwarded get", Source()))
    }

    @Test
    fun `accepts native nodes commands and reports global results`() {
        val completed = mutableListOf<CommandResult<Source>>()
        val dispatcher = commandDispatcher<Source> {
            resultConsumer { completed += this }
            then(LiteralArgumentBuilder.literal<Source>("native-node").executes { 11 })
            "native-command" {
                executesRaw(Command { 12 })
            }
            "unit" {
                runs { }
            }
        }

        assertEquals(11, dispatcher.execute("native-node", Source()))
        assertEquals(12, dispatcher.execute("native-command", Source()))
        assertEquals(Command.SINGLE_SUCCESS, dispatcher.execute("unit", Source()))

        assertEquals(listOf(11, 12, Command.SINGLE_SUCCESS), completed.map { it.result })
        assertTrue(completed.all(CommandResult<Source>::success))
        assertFalse(completed.isEmpty())
    }
}
