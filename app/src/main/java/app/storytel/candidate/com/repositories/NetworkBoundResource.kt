package app.storytel.candidate.com.repositories

import androidx.lifecycle.*
import app.storytel.candidate.com.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.lang.Exception
import kotlin.coroutines.resume

/**
 * This class provides a [LiveData] containing a [Resource] that is backed by both the Room database
 * and the network.
 */
abstract class NetworkBoundResource<ResultType, RequestType> {
    private val result: LiveData<Resource<ResultType>> = fetchResult()

    private fun fetchResult(forceFetch: Boolean = false): LiveData<Resource<ResultType>> {
        return liveData(Dispatchers.IO) {
            emit(Resource.loading<ResultType>())
            val dbSource = loadFromDb().map {
                Resource.success(it)
            }

            val mediator: MediatorLiveData<Resource<ResultType>> = MediatorLiveData()
            emitSource(mediator)
            val data = withContext(Dispatchers.Main.immediate) {
                suspendCancellableCoroutine<ResultType?> { continuation ->
                    mediator.addSource(dbSource) {
                        continuation.resume(it.data)
                        mediator.removeSource(dbSource)
                    }
                    continuation.invokeOnCancellation {
                        mediator.removeSource(dbSource)
                    }
                }
            }

            if (forceFetch || shouldFetch(data)) {
                try {
                    val response = createCall()
                    if (response.isSuccessful) {
                        val body = response.body()
                        // If the body is empty, or the response code is 204, there is no need
                        // to save the data to the db since its empty.
                        if (body != null && response.code() != 204) {
                            saveCallResult(body)
                            emitSource(dbSource)
                        }
                    } else {
                        val msg = response.errorBody()?.string()
                        val errorMsg = if (msg.isNullOrEmpty()) response.message() else msg
                        emit(Resource.error<ResultType>(errorMsg, data))
                    }
                } catch (e: Exception) {
                    // TODO: This could be handled better with adding an interceptor to retrofit
                    //  and throw NoNetworkConnection.
                    onFetchFailed()
                    emit(Resource.error<ResultType>(e.message ?: e.toString(), data))
                }
            } else {
                emitSource(dbSource)
            }
        }
    }

    /**
     * Called when fetching the data from the network failed.
     */
    protected open fun onFetchFailed() {}

    /**
     * Returns this [NetworkBoundResource] as a [LiveData].
     */
    fun asLiveData() = result

    /**
     * Saves the result from the network call.
     */
    protected abstract fun saveCallResult(item: RequestType)

    /**
     * Checks if the data should be fetched from the network.
     */
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    /**
     * Loads the data from the database.
     */
    protected abstract fun loadFromDb(): LiveData<ResultType>

    /**
     * Creates the network call to be invoked.
     */
    protected abstract suspend fun createCall(): Response<RequestType>
}