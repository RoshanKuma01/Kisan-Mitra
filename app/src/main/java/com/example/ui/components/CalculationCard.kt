package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LossColor
import com.example.ui.theme.LossContainer
import com.example.ui.theme.OnLossContainer
import com.example.ui.theme.OnProfitContainer
import com.example.ui.theme.ProfitColor
import com.example.ui.theme.ProfitContainer
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.PdfBillGenerator

@Composable
fun CalculationCard(
  totalAmount: Double,
  totalInvestmentCost: Double,
  netProfitLoss: Double,
  isProfit: Boolean,
  profitMarginPercent: Double,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val statusBgColor by animateColorAsState(
    targetValue = if (isProfit) ProfitContainer else LossContainer,
    animationSpec = tween(400),
    label = "statusBg"
  )
  val statusBorderColor by animateColorAsState(
    targetValue = if (isProfit) ProfitColor else LossColor,
    animationSpec = tween(400),
    label = "statusBorder"
  )
  val statusTextColor by animateColorAsState(
    targetValue = if (isProfit) OnProfitContainer else OnLossContainer,
    animationSpec = tween(400),
    label = "statusText"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("calculation_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Title
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.HINDI) "📊 हिसाब सारांश (Summary)" else "📊 Calculation Summary",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      // Total Sale Amount Display
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = AppStrings.totalAmount(language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "(वजन × भाव)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
        }
        Text(
          text = PdfBillGenerator.formatCurrency(totalAmount),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.testTag("total_amount_text")
        )
      }

      // Total Investment Cost
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = AppStrings.totalInvestment(language),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "- ${PdfBillGenerator.formatCurrency(totalInvestmentCost)}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.error
        )
      }

      Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)

      // Net Profit / Loss Highlight Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(statusBgColor)
          .border(1.5.dp, statusBorderColor, RoundedCornerShape(12.dp))
          .padding(horizontal = 14.dp, vertical = 12.dp)
          .testTag("profit_loss_banner")
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
              imageVector = if (isProfit) Icons.Default.CheckCircle else Icons.Default.Warning,
              contentDescription = null,
              tint = statusBorderColor,
              modifier = Modifier.size(24.dp)
            )
            Column {
              Text(
                text = if (isProfit) AppStrings.netProfit(language) else AppStrings.netLoss(language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = statusTextColor
              )
              if (totalInvestmentCost > 0) {
                Text(
                  text = "${AppStrings.roi(language)}: ${if (isProfit) "+" else ""}${"%.1f".format(profitMarginPercent)}%",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Medium,
                  color = statusTextColor.copy(alpha = 0.85f)
                )
              }
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${if (isProfit) "+" else "-"}${PdfBillGenerator.formatCurrency(Math.abs(netProfitLoss))}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Black,
              color = statusTextColor,
              fontSize = 20.sp
            )
            Text(
              text = if (isProfit) "शुद्ध मुनाफा" else "घाटा",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = statusBorderColor
            )
          }
        }
      }
    }
  }
}
