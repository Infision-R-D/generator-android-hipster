package <%= appPackage %>.extensions

import <%= appPackage %>.domain.repository.NetworkResult
import <%= appPackage %>.util.ExtractDeferredErrorUtil
import kotlinx.coroutines.Deferred

@Suppress("TooGenericExceptionCaught")
suspend fun <T : Any> Deferred<T>.awaitResult(): NetworkResult<T> = try {
    NetworkResult.Success(await())
} catch (e: Exception) {
    NetworkResult.Error(ExtractDeferredErrorUtil.extractError(e))
}