/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.babylon.orbit2.livedata

import androidx.lifecycle.asFlow
import com.babylon.orbit2.Operator
import com.babylon.orbit2.OrbitDslPlugin
import com.babylon.orbit2.VolatileContext
import com.babylon.orbit2.idling.withIdlingFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn

/**
 * Orbit plugin providing LiveData DSL operators:
 *
 * * [transformLiveData]
 */
object LiveDataDslPlugin : OrbitDslPlugin {

    @Suppress("UNCHECKED_CAST", "EXPERIMENTAL_API_USAGE")
    override fun <S : Any, E, SE : Any> apply(
        containerContext: OrbitDslPlugin.ContainerContext<S, SE>,
        flow: Flow<E>,
        operator: Operator<S, E>,
        createContext: (event: E) -> VolatileContext<S, E>
    ): Flow<Any?> {
        return when (operator) {
            is LiveDataOperator<*, *, *> -> flow.flatMapConcat {
                containerContext.withIdlingFlow(operator as LiveDataOperator<S, E, Any>) {
                    createContext(it).block().asFlow().flowOn(containerContext.backgroundDispatcher)
                }
            }
            else -> flow
        }
    }
}