package com.bokoup.lib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

/**
 * Utility for presenting a static value as a [StateFlow]
 */
fun <T> stateFlowOf(valueProvider: () -> T): StateFlow<T> {
    return MutableStateFlow(valueProvider.invoke()).asStateFlow()
}

/**
 * Converts a `suspend` method into a `Flow<Resource>`
 */
fun <T> resourceFlowOf(
    context: CoroutineContext = Dispatchers.IO,
    action: suspend () -> T,
): Flow<Resource<T>> {
    return flow<Resource<T>> {
        emit(Resource.Loading())
        runCatching {
            action.invoke()
        }.onSuccess {
            emit(Resource.Success(it))
        }.onFailure {
            emit(Resource.Error(it))
        }
    }.flowOn(context)
}

fun <T> Flow<T>.asResourceFlow(): Flow<Resource<T>> {
    return this.map { Resource.Success(it) as Resource<T> }
        .onStart { emit(Resource.Loading<T>(null)) }
        .catch { Resource.Error<T>(it) }
}

fun <T> Flow<Resource<T>>.onEachSuccess(
    action: suspend (T) -> Unit
): Flow<Resource<T>> {
    return onEach { resource ->
        if (resource is Resource.Success) {
            action.invoke(resource.data)
        }
    }
}

fun <T, R> Flow<Resource<T>>.mapData(
    mapper: suspend (T) -> R
): Flow<Resource<R>> {
    return map { resource ->
        val newData = resource.dataOrNull()?.let { mapper.invoke(it) }
        when (resource) {
            is Resource.Error -> Resource.Error(resource.error, newData)
            is Resource.Loading -> Resource.Loading(newData)
            is Resource.Success -> {
                if (newData == null) {
                    Resource.Error(IllegalStateException("Required data was missing"))
                } else {
                    Resource.Success(newData)
                }
            }
        }
    }
}