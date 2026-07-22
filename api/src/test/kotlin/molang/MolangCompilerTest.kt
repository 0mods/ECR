package com.algorithmlx.ecr.api.molang

import com.algorithmlx.ecr.api.molang.compiler.MolangCompiler
import com.algorithmlx.ecr.api.molang.compiler.eval
import com.algorithmlx.ecr.api.molang.runtime.MolangContext
import com.algorithmlx.ecr.api.molang.runtime.Query
import com.algorithmlx.ecr.api.molang.runtime.VariablesMap
import kotlin.test.Test
import kotlin.test.assertEquals

class MolangCompilerTest {
    @Test
    fun evaluatesStatementSequences() {
        val variables = VariablesMap()
        val context = MolangContext(Query.EMPTY, variables)

        val result = MolangCompiler
            .compileFloat("variable.size = 0.03; variable.lifetime = 2.2;")
            .eval(context)

        assertEquals(0.03f, variables["size"])
        assertEquals(2.2f, variables["lifetime"])
        assertEquals(2.2f, result)
    }

    @Test
    fun evaluatesQueriesAndMathFunctions() {
        val query = object : Query {
            override val health = 6f
        }
        val context = MolangContext(query)

        assertEquals(4f, MolangCompiler.compileFloat("query.health / 2 + 1").eval(context))
        assertEquals(1f, MolangCompiler.compileFloat("math.sin(90)").eval(context), 0.0001f)
        assertEquals(kotlin.math.PI.toFloat(), MolangCompiler.compileFloat("math.pi").eval(context))
    }
}
