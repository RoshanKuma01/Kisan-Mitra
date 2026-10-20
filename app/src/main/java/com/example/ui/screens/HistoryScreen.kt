package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ParchiEntity
import com.example.ui.theme.KisanGreenPrimary
import com.example.ui.theme.LossColor
import com.example.ui.theme.ProfitColor
import com.example.ui.viewmodel.ParchiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.PdfBillGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  viewModel: ParchiViewModel,
  onNavigateToNewBill: () -> Unit,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val parchis by viewModel.allParchis.collectAsState()
  val searchQuery by viewModel.historySearchQuery.collectAsState()
  val filter by viewModel.historyFilter.collectAsState()

  var parchiToDelete by remember { mutableStateOf<ParchiEntity?>(null) }
  val context = LocalContext.current

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { viewModel.setHistorySearchQuery(it) },
      placeholder = {
        Text(
          text = if (language == AppLanguage.HINDI) "फसल, किसान या पर्ची नंबर खोजें..." else "Search crop, farmer, or bill no..."
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search"
        )
      },
      singleLine = true,
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("history_search_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = KisanGreenPrimary
      )
    )

    // Filter Chips: All, Profit Only, Loss Only
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = filter == "ALL",
        onClick = { viewModel.setHistoryFilter("ALL") },
        label = { Text(if (language == AppLanguage.HINDI) "सभी (${parchis.size})" else "All (${parchis.size})") },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
        )
      )

      FilterChip(
        selected = filter == "PROFIT",
        onClick = { viewModel.setHistoryFilter("PROFIT") },
        label = { Text(if (language == AppLanguage.HINDI) "केवल फायदा" else "Profit Only") },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = Color(0xFFDCFCE7),
          selectedLabelColor = Color(0xFF14532D)
        )
      )

      FilterChip(
        selected = filter == "LOSS",
        onClick = { viewModel.setHistoryFilter("LOSS") },
        label = { Text(if (language == AppLanguage.HINDI) "केवल नुकसान" else "Loss Only") },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = Color(0xFFFEE2E2),
          selectedLabelColor = Color(0xFF7F1D1D)
        )
      )
    }

    if (parchis.isEmpty()) {
      // Empty State
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 60.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.padding(24.dp)
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ReceiptLong,
              contentDescription = null,
              tint = KisanGreenPrimary,
              modifier = Modifier.size(36.dp)
            )
          }

          Text(
            text = AppStrings.emptyHistory(language),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          OutlinedButton(
            onClick = onNavigateToNewBill,
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(AppStrings.newBillTab(language))
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 64.dp)
      ) {
        items(parchis, key = { it.id }) { item ->
          ParchiHistoryCard(
            parchi = item,
            language = language,
            onViewPdf = { viewModel.viewPdfForParchi(item) },
            onShare = {
              val file = PdfBillGenerator.generatePdf(context, item)
              val cropTitle = if (language == AppLanguage.HINDI) item.cropNameHi else item.cropNameEn
              PdfBillGenerator.sharePdf(context, file, cropTitle)
            },
            onDelete = { parchiToDelete = item }
          )
        }
      }
    }
  }

  // Delete Confirmation Dialog
  parchiToDelete?.let { parchi ->
    AlertDialog(
      onDismissRequest = { parchiToDelete = null },
      title = { Text(if (language == AppLanguage.HINDI) "पर्ची हटाएं?" else "Delete Bill?") },
      text = { Text(AppStrings.deleteConfirm(language) + "\n\n${parchi.parchiNumber} - ${parchi.cropNameHi}") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.deleteParchi(parchi.id)
            parchiToDelete = null
          }
        ) {
          Text(
            text = if (language == AppLanguage.HINDI) "हटाएं" else "Delete",
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
          )
        }
      },
      dismissButton = {
        TextButton(onClick = { parchiToDelete = null }) {
          Text(if (language == AppLanguage.HINDI) "रद्द करें" else "Cancel")
        }
      }
    )
  }
}

@Composable
fun ParchiHistoryCard(
  parchi: ParchiEntity,
  language: AppLanguage,
  onViewPdf: () -> Unit,
  onShare: () -> Unit,
  onDelete: () -> Unit
) {
  val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(parchi.timestamp))
  val cropDisplay = if (language == AppLanguage.HINDI) parchi.cropNameHi else parchi.cropNameEn

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onViewPdf() }
      .testTag("history_parchi_card_${parchi.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Header: Crop Name & Profit/Loss Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = cropDisplay,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${parchi.parchiNumber} • $dateStr",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
        }

        // Status pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (parchi.isProfit) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${if (parchi.isProfit) "+" else "-"}${PdfBillGenerator.formatCurrency(Math.abs(parchi.netProfitLoss))}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (parchi.isProfit) Color(0xFF15803D) else Color(0xFFDC2626)
          )
        }
      }

      // Quantity & Rate row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val unitLabel = if (parchi.weightUnit == "QUINTAL") "क्विंटल" else "कि.ग्रा."
        val rateUnitLabel = if (parchi.rateUnit == "PER_QUINTAL") "/क्विं." else "/कि.ग्रा."

        Text(
          text = "मात्रा: %.2f %s  |  भाव: ₹%,.0f %s".format(
            parchi.weightQuantity,
            unitLabel,
            parchi.ratePerUnit,
            rateUnitLabel
          ),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
          text = PdfBillGenerator.formatCurrency(parchi.totalAmount),
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = KisanGreenPrimary
        )
      }

      if (parchi.farmerName.isNotBlank() || parchi.traderOrMandiName.isNotBlank()) {
        Text(
          text = listOfNotNull(
            parchi.farmerName.takeIf { it.isNotBlank() }?.let { "किसान: $it" },
            parchi.traderOrMandiName.takeIf { it.isNotBlank() }?.let { "मंडी: $it" }
          ).joinToString(" • "),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }

      Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)

      // Action row: View PDF, Share, Delete
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedButton(
            onClick = onViewPdf,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = KisanGreenPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (language == AppLanguage.HINDI) "पर्ची PDF" else "View PDF",
              fontSize = 12.sp
            )
          }

          IconButton(
            onClick = onShare,
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              tint = KisanGreenPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(34.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
