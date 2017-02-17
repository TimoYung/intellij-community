/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.uast.values

interface UValue : UOperand {

    fun merge(other: UValue): UValue

    val dependencies: Set<UDependency>
        get() = emptySet()

    fun toConstant(): UConstant?

    val reachable: Boolean

    companion object {
        val UNREACHABLE: UValue = UNothingValue()
    }
}

fun UValue.toPossibleConstants(): Set<UConstant> {
    val results = mutableSetOf<UConstant>()
    toPossibleConstants(results)
    return results
}

private fun UValue.toPossibleConstants(result: MutableSet<UConstant>) {
    when (this) {
        is UDependentValue -> value.toPossibleConstants(result)
        is UPhiValue -> values.forEach { it.toPossibleConstants(result) }
        else -> toConstant()?.let { result.add(it) }
    }
}
