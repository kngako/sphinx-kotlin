package chat.sphinx.feature_repository.mappers

import io.matthewnelson.concept_coroutines.CoroutineDispatchers
import kotlinx.coroutines.withContext

@Suppress("NOTHING_TO_INLINE")
internal suspend inline fun<From, To> ClassMapper<From, To>.mapListFrom(value: List<From>): List<To> =
    withContext(dispatchers.default) {
        value.map { mapFrom(it) }
    }

@Suppress("NOTHING_TO_INLINE")
internal suspend inline fun<From, To> ClassMapper<From, To>.mapListTo(value: List<To>): List<From> =
    withContext(dispatchers.default) {
        value.map { mapTo(it) }
    }

internal abstract class ClassMapper<From, To>(val dispatchers: CoroutineDispatchers) {
    abstract fun mapFrom(value: From): To
    abstract fun mapTo(value: To): From
}
