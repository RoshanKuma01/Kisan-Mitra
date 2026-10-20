package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KisanGreenPrimary
import com.example.ui.theme.LossColor
import com.example.ui.theme.ProfitColor
import com.example.ui.viewmodel.ParchiViewModel
import com.example.util.AppLanguage
import com.example.util.PdfBillGenerator

@Composable
fun StatsScreen(
  viewModel: ParchiViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val parchis by viewModel.allParchis.collectAsState()

  // Calculate aggregates
  val totalBills = parchis.size
  val totalSales = parchis.sumOf { it.totalAmount }
  val totalCosts = parchis.sumOf { it.totalInvestmentCost }
  val netGainLoss = totalSales - totalCosts
  val isOverallProfit = netGainLoss >= 0
  val overallRoi = if (totalCosts > 0) (netGainLoss / totalCosts) * 100.0 else 0.0

  val totalWeightQuintals = parchis.sumOf {
    if (it.weightUnit == "QUINTAL") it.weightQuantity else it.weightQuantity / 100.0
  }

  // Group by crop
  val cropGroups = parchis.groupBy { it.cropNameHi }
    .mapValues { entry ->
      val count = entry.value.size
      val sales = entry.value.sumOf { it.totalAmount }
      val weight = entry.value.sumOf {
        if (it.weightUnit == "QUINTAL") it.weightQuantity else it.weightQuantity / 100.0
      }
      Triple(count, sales, weight)
    }
    .toList()
    .sortedByDescending { it.second.second }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = if (language == AppLanguage.HINDI) "🌾 कुल कृषि बिक्री व लाभ रिपोर्ट" else "🌾 Total Crop Sales & Profit Report",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )

    // Master Profit/Loss Highlight Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isOverallProfit) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("stats_summary_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = if (isOverallProfit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
              contentDescription = null,
              tint = if (isOverallProfit) ProfitColor else LossColor,
              modifier = Modifier.size(28.dp)
            )
            Text(
              text = if (isOverallProfit) "कुल शुद्ध मुनाफा (Net Profit)" else "कुल शुद्ध घाटा (Net Loss)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = if (isOverallProfit) Color(0xFF14532D) else Color(0xFF7F1D1D)
            )
          }

          if (totalCosts > 0) {
            Text(
              text = "${if (isOverallProfit) "+" else ""}${"%.1f".format(overallRoi)}% ROI",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = if (isOverallProfit) Color(0xFF15803D) else Color(0xFFDC2626)
            )
          }
        }

        Text(
          text = "${if (isOverallProfit) "+" else "-"}${PdfBillGenerator.formatCurrency(Math.abs(netGainLoss))}",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.ExtraBold,
          color = if (isOverallProfit) Color(0xFF14532D) else Color(0xFF7F1D1D)
        )

        Text(
          text = if (language == AppLanguage.HINDI) {
            "कुल बिक्री: ${PdfBillGenerator.formatCurrency(totalSales)} | कुल लागत: ${PdfBillGenerator.formatCurrency(totalCosts)}"
          } else {
            "Gross Sales: ${PdfBillGenerator.formatCurrency(totalSales)} | Total Costs: ${PdfBillGenerator.formatCurrency(totalCosts)}"
          },
          style = MaterialTheme.typography.bodySmall,
          color = if (isOverallProfit) Color(0xFF166534) else Color(0xFF991B1B)
        )
      }
    }

    // 2x2 Metric Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      MetricCard(
        title = if (language == AppLanguage.HINDI) "कुल पर्चियां" else "Total Bills",
        value = "$totalBills",
        icon = Icons.Default.Receipt,
        iconTint = KisanGreenPrimary,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = if (language == AppLanguage.HINDI) "कुल वजन (क्विंटल)" else "Total Weight (Qtl)",
        value = "%,.1f Q".format(totalWeightQuintals),
        icon = Icons.Default.Scale,
        iconTint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.weight(1f)
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      MetricCard(
        title = if (language == AppLanguage.HINDI) "कुल बिक्री राशि" else "Gross Sales",
        value = PdfBillGenerator.formatCurrency(totalSales),
        icon = Icons.Default.CurrencyRupee,
        iconTint = KisanGreenPrimary,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = if (language == AppLanguage.HINDI) "कुल खर्च / लागत" else "Total Investment",
        value = PdfBillGenerator.formatCurrency(totalCosts),
        icon = Icons.Default.Assessment,
        iconTint = MaterialTheme.colorScheme.error,
        modifier = Modifier.weight(1f)
      )
    }

    // Top Crops by Sales Breakdown
    if (cropGroups.isNotEmpty()) {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = if (language == AppLanguage.HINDI) "फसल अनुसार बिक्री (By Crop)" else "Sales Breakdown by Crop",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )

          cropGroups.forEach { (cropName, data) ->
            val count = data.first
            val sales = data.second
            val weight = data.third

            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🌾 $cropName",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = PdfBillGenerator.formatCurrency(sales),
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = KisanGreenPrimary
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "$count पर्चियां • %,.1f क्विंटल".format(weight),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.outline
                )
              }
              Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
            }
          }
        }
      }
    }
    Spacer(modifier = Modifier.height(60.dp))
  }
}

@Composable
private fun MetricCard(
  title: String,
  value: String,
  icon: ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(iconTint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 17.sp
      )
    }
  }
}
