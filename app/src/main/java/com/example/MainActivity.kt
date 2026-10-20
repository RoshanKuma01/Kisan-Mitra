package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PdfPreviewDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.NewBillScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.KisanGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ParchiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

enum class AppScreen {
  NEW_BILL,
  HISTORY,
  STATS
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        KisanParchiApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KisanParchiApp(
  parchiViewModel: ParchiViewModel = viewModel()
) {
  val language by parchiViewModel.currentLanguage.collectAsState()
  val activeParchiForPreview by parchiViewModel.activeParchiForPreview.collectAsState()
  val activePdfFile by parchiViewModel.activePdfFile.collectAsState()

  var currentScreen by rememberSaveable { mutableStateOf(AppScreen.NEW_BILL) }

  // Back handling: If on secondary tab, back button returns to New Bill
  if (currentScreen != AppScreen.NEW_BILL) {
    BackHandler {
      currentScreen = AppScreen.NEW_BILL
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "🌾 " + AppStrings.appTitle(language),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimary
            )
          }
        },
        actions = {
          // Language Switcher Badge Button
          Box(
            modifier = Modifier
              .padding(end = 12.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
              .border(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
              .clickable { parchiViewModel.toggleLanguage() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("language_toggle_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Language",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (language == AppLanguage.HINDI) "English" else "हिंदी",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
              )
            }
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = KisanGreenPrimary,
          titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        NavigationBarItem(
          selected = currentScreen == AppScreen.NEW_BILL,
          onClick = { currentScreen = AppScreen.NEW_BILL },
          icon = {
            Icon(
              imageVector = Icons.Default.AddCircle,
              contentDescription = AppStrings.newBillTab(language)
            )
          },
          label = {
            Text(
              text = AppStrings.newBillTab(language),
              fontWeight = if (currentScreen == AppScreen.NEW_BILL) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KisanGreenPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_new_bill")
        )

        NavigationBarItem(
          selected = currentScreen == AppScreen.HISTORY,
          onClick = { currentScreen = AppScreen.HISTORY },
          icon = {
            Icon(
              imageVector = Icons.Default.ReceiptLong,
              contentDescription = AppStrings.historyTab(language)
            )
          },
          label = {
            Text(
              text = AppStrings.historyTab(language),
              fontWeight = if (currentScreen == AppScreen.HISTORY) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KisanGreenPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_history")
        )

        NavigationBarItem(
          selected = currentScreen == AppScreen.STATS,
          onClick = { currentScreen = AppScreen.STATS },
          icon = {
            Icon(
              imageVector = Icons.Default.Analytics,
              contentDescription = AppStrings.statsTab(language)
            )
          },
          label = {
            Text(
              text = AppStrings.statsTab(language),
              fontWeight = if (currentScreen == AppScreen.STATS) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KisanGreenPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_stats")
        )
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        AppScreen.NEW_BILL -> {
          NewBillScreen(
            viewModel = parchiViewModel
          )
        }
        AppScreen.HISTORY -> {
          HistoryScreen(
            viewModel = parchiViewModel,
            onNavigateToNewBill = { currentScreen = AppScreen.NEW_BILL }
          )
        }
        AppScreen.STATS -> {
          StatsScreen(
            viewModel = parchiViewModel
          )
        }
      }

      // PDF Preview Modal
      activeParchiForPreview?.let { parchi ->
        PdfPreviewDialog(
          parchi = parchi,
          pdfFile = activePdfFile,
          language = language,
          onDismiss = { parchiViewModel.dismissPreviewDialog() }
        )
      }
    }
  }
}
