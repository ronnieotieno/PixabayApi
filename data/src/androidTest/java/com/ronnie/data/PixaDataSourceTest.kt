package com.ronnie.data
import android.content.Context
import androidx.paging.PagingSource
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.datasource.PixaDataSource
import com.ronnie.domain.models.Image
import com.ronnie.domain.models.ImageResponse
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.junit.MockitoJUnitRunner
import java.io.InputStream

@RunWith(MockitoJUnitRunner::class)
class PixaDataSourceTest {
   lateinit var context:Context
    @Mock
    private lateinit var apiService: PixaBayApi
    private lateinit var pagingSource: PixaDataSource
    private lateinit var imageList: List<Image>
    private val gson = Gson()
    private lateinit var responseImageResponse:ImageResponse

    @Before
    fun setup() {
        initMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val jsonStream: InputStream = context.resources.assets.open("response.json")
        val jsonBytes: ByteArray = jsonStream.readBytes()

        responseImageResponse = gson.fromJson(String(jsonBytes), ImageResponse::class.java)
        imageList = responseImageResponse.images

        pagingSource = PixaDataSource("fruits", apiService, context)

    }

    @Test
    fun load_returns_page_when_success() = runBlocking {
        Mockito.`when`(apiService.searchImages(anyString(), anyInt(), anyInt())).thenReturn(responseImageResponse)
        assertThat(
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            ),
            CoreMatchers.equalTo(
                PagingSource.LoadResult.Page(
                    data = imageList,
                    prevKey = null,
                    nextKey = 2
                )
            ),
        )
    }

}