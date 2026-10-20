package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RateUnit
import com.example.data.model.WeightUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kisan Parchi", appName)
  }

  @Test
  fun `verify calculation logic for quintals and rate`() {
    val weight = 40.0 // 40 Quintals
    val rate = 2275.0 // ₹ 2,275 per Quintal
    val total = weight * rate
    assertEquals(91000.0, total, 0.001)

    val cost = 30000.0 // Investment
    val netProfit = total - cost
    assertEquals(61000.0, netProfit, 0.001)
    assertTrue(netProfit > 0)
  }
}
