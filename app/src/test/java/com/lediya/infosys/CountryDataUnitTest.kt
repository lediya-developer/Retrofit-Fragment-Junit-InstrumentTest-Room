package com.lediya.infosys

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lediya.infosys.communication.RestClient
import com.lediya.infosys.data.AppDatabase
import com.lediya.infosys.model.CountryListResponse
import com.lediya.infosys.model.Row
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import retrofit2.Response
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(maxSdk = Build.VERSION_CODES.P, minSdk = Build.VERSION_CODES.P)
class CountryDataUnitTest {
    private lateinit var db: AppDatabase
    private lateinit var context: Context
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

    }

    @After
    fun tearDown() {
       db.close()
    }
    /**
     * Download api data test case*/
    @Test
    fun downloadData() {
        try {
            runBlocking {
                val response: Response<CountryListResponse> =
                    RestClient.getInstance().getList()
                val countryResponse: CountryListResponse? = response.body()
                assertEquals(
                    countryResponse?.title!=null&& countryResponse.title.isNotBlank()&&countryResponse.title.isNotEmpty(),
                    true)
                assertEquals(
                    countryResponse?.rows!=null&& countryResponse.rows.isNotEmpty(),
                    true)
                assertEquals(
                    countryResponse?.rows?.get(0)?.description!=null,
                    true)
                assertEquals(
                    countryResponse?.rows?.get(0)?.imageHref!=null,
                    true)
                assertEquals(
                    countryResponse?.rows?.get(0)?.title!=null,
                    true)
            }
        }
        catch (exception: IOException) {
        }
    }
    /**
     * check write and retrieve the data in the database test case*/
    @Test
    @Throws(Exception::class)
    fun insertAndSelectData() = testDispatcher.runBlockingTest {
        testScope.launch(Dispatchers.IO) {
            val countryDao = db.countryDao
            val rowList = mutableListOf<Row>()
            val row = Row(
                context.getString(R.string.test_des),
                context.getString(R.string.test_image),
                context.getString(R.string.test_title)
            )
            rowList.add(row)
            val modelData = CountryListResponse(
                rowList,
                context.getString(R.string.title)
            )
            runBlocking { countryDao.insertCountryData(modelData) }
            val afterRetrievePostData = runBlocking { countryDao.getCountryData() }
            assertEquals(modelData, afterRetrievePostData)
        }
    }

}