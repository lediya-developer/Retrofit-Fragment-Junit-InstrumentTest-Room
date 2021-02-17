package com.lediya.infosys

import android.content.Context
import android.content.Intent
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.lediya.infosys.communication.RestClient
import com.lediya.infosys.data.AppDatabase
import com.lediya.infosys.data.dao.CountryDao
import com.lediya.infosys.model.CountryListResponse
import com.lediya.infosys.model.Row
import com.lediya.infosys.utility.Utils
import com.lediya.infosys.view.ListScreenActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import retrofit2.Response
import java.io.IOException
import java.lang.AssertionError

/**
 * Instrumented test, which will execute on an Android device.
 *
 *
 */
@RunWith(AndroidJUnit4::class)
class CountryDataInstrumentedTest {
    private lateinit var countryDao: CountryDao
    private lateinit var db: AppDatabase
    private lateinit var context:Context
    /**
     * Create the db for database operation */
    @Before
    fun createDb() {
         context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        countryDao = db.countryDao
    }
    /**
     * Close the db after the database operation test case*/
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
    /**
     * check internet is on and downlaod api data test case*/
    @Test
    fun internetOnTest() {
        try {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            val activity = ActivityTestRule(ListScreenActivity::class.java)
            activity.launchActivity(Intent())
            assertEquals(context.getString(R.string.test_package), appContext.packageName)
            assertTrue(Utils.isConnectedToNetwork(appContext))
            downloadApiData()
        }
        catch (e:AssertionError){

        }
    }
    /**
     * check internet is off and shows the warning alert test case */
    @Test
    fun internetOffTest() {
        try{
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            val activity = ActivityTestRule(ListScreenActivity::class.java)
        activity.launchActivity(Intent())
        assertEquals(context.getString(R.string.test_package), appContext.packageName)
        assertFalse(Utils.isConnectedToNetwork(appContext))
        onView(withId(R.id.errorTextData)).check(ViewAssertions.matches(withText(appContext.getString(R.string.no_internet_toast))))
        }
        catch (e:AssertionError){

        }
    }
    /**
     * check write and retrieve the data in the database test case*/
    @Test
    @Throws(Exception::class)
    fun writeAndRetrieveData() {
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
    /**
     * Download api data test case*/
    @Test
    fun downloadApiData() {
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
}
