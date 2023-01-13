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

@Suppress("UNCHECKED_CAST")
fun <T, R> Flow<Resource<T>>.flatMapSuccess(mapper: (T) -> Flow<Resource<R>>): Flow<Resource<R>> {
    return flatMapConcat { resource ->
        when (resource) {
            is Resource.Success -> mapper.invoke(resource.data)
            is Resource.Loading -> flowOf(Resource.Loading(data = null))
            is Resource.Error -> flowOf(Resource.Error(resource.error))
        }
    }.catch { error ->
        emit(Resource.Error(error as? Exception ?: RuntimeException(error)))
    }.distinctUntilChanged { old, new ->
        // Consider loading/error emissions to be the same, as we dont change any of the
        // wrapped values
        (old is Resource.Loading && new is Resource.Loading) ||
                (old is Resource.Error && new is Resource.Error)
    }.map { it as Resource<R> }
}