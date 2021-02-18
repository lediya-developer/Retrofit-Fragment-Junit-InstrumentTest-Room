package com.lediya.infosys

import android.content.Context
import android.os.Build
import androidx.appcompat.widget.AppCompatTextView
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lediya.infosys.communication.RestClient
import com.lediya.infosys.data.AppDatabase
import com.lediya.infosys.model.CountryListResponse
import com.lediya.infosys.model.Row
import com.lediya.infosys.utility.Utils
import com.lediya.infosys.view.ListScreenActivity
import com.lediya.infosys.view.ListScreenFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
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
    /**
     * Check response is successful */
    @Test
    fun checkResponseSuccessful()= testDispatcher.runBlockingTest {
        testScope.launch(Dispatchers.IO) {
            try {
                    val response: Response<CountryListResponse> =
                        RestClient.getInstance().getList()
                    assertTrue(response.isSuccessful)
                    assertEquals(200, response.code())
            } catch (exception: IOException) {
            }
        }
    }
    /**
     * checks title use case*/
    @Test
    fun checkTitle()=testDispatcher.runBlockingTest {
        testScope.launch(Dispatchers.IO) {
            val activity = Robolectric.setupActivity(ListScreenActivity::class.java)
            assertNotEquals(
                context.getString(R.string.title),
                activity.supportActionBar?.title.toString().trim()
            )
            assertEquals(
                context.getString(R.string.app_name),
                activity.supportActionBar?.title.toString().trim()
            )
        }
    }
    /**check connectivity not available use case*/
    @Test
    fun checkNotInternet() = testDispatcher.runBlockingTest {
        testScope.launch (Dispatchers.IO ){
            val fragment =  ListScreenFragment()
            assertFalse(Utils.isConnectedToNetwork(context))
            assertEquals(context.getString(R.string.no_internet_toast),
                fragment.activity?.findViewById<AppCompatTextView>(R.id.errorTextData)?.text
            )
        }
    }
    /**check connectivity available use case*/
    @Test
    fun checkInternet() = testDispatcher.runBlockingTest {
        testScope.launch (Dispatchers.IO ){
            assertTrue(Utils.isConnectedToNetwork(context))
            downloadData()
        }
    }
    /**check connectivity not available and failed error message */
    @Test
    fun checkFailedToast() = testDispatcher.runBlockingTest {
        testScope.launch (Dispatchers.IO ){
            val fragment =  ListScreenFragment()
            assertFalse(Utils.isConnectedToNetwork(context))
            assertEquals(context.getString(R.string.failure_toast),
                fragment.activity?.findViewById<AppCompatTextView>(R.id.errorTextData)?.text
            )
        }
    }
    /**check data not available in local database with connectivity not available and connectivity available */
    @Test
    @Throws(Exception::class)
    fun noDataFromDatabase() = testDispatcher.runBlockingTest {
        testScope.launch(Dispatchers.IO) {
            val countryDao = db.countryDao
            val afterRetrievePostData = countryDao.getCountryData()
            if (afterRetrievePostData==null||afterRetrievePostData.equals(" ")){
                checkNotInternet()
            }else{
                downloadData()
            }
        }
    }
}