package app.storytel.candidate.com

import app.storytel.candidate.com.network.Resource
import org.junit.Assert
import org.junit.Test

/**
 * Unit test for the [Resource] class.
 */
class ResourceUnitTest {

    @Test
    @Throws(Exception::class)
    fun resource_success_is_correct() {
        val resource = Resource.success(createData())
        assertResource(resource, Resource.Status.SUCCESS)
    }

    @Test
    @Throws(Exception::class)
    fun resource_error_is_correct() {
        val message = "Something went bonkerz"
        val resource = Resource.error(message, createData())
        assertResource(resource, Resource.Status.ERROR, message)
    }

    @Test
    @Throws(Exception::class)
    fun resource_loading_is_correct() {
        val resource = Resource.loading(createData())
        assertResource(resource, Resource.Status.LOADING)
    }

    private fun assertResource(
            resource: Resource<Pair<Int, String>>,
            status: Resource.Status,
            message: String? = null) {
        Assert.assertEquals(resource.data, createData())
        Assert.assertEquals(resource.message, message)
        Assert.assertEquals(resource.status, status)
    }

    private fun createData(): Pair<Int, String> {
        return Pair(1334, "storytel loading")
    }
}