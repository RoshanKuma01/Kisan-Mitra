package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.ParchiEntity
import com.example.ui.theme.KisanGreenPrimary
import com.example.ui.theme.LossColor
import com.example.ui.theme.ProfitColor
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.PdfBillGenerator
import java.io.File

@Composable
fun PdfPreviewDialog(
  parchi: ParchiEntity,
  pdfFile: File?,
  language: AppLanguage,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val cropDisplay = if (language == AppLanguage.HINDI) parchi.cropNameHi else parchi.cropNameEn

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .clip(RoundedCornerShape(20.dp))
        .testTag("pdf_preview_dialog"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Header
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
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = KisanGreenPrimary,
              modifier = Modifier.size(28.dp)
            )
            Column {
              Text(
                text = if (language == AppLanguage.HINDI) "किसान पर्ची तैयार है" else "Parchi Generated",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = parchi.parchiNumber,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("dialog_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close"
            )
          }
        }

        Divider()

        // Parchi Preview Box (Styled like a Mandi Slip)
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Mandi & Farmer header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "🌾 $cropDisplay",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = KisanGreenPrimary
              )
              Text(
                text = if (parchi.isProfit) "★ मुनाफा" else "⚠ नुकसान",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (parchi.isProfit) ProfitColor else LossColor
              )
            }

            if (parchi.farmerName.isNotBlank() || parchi.traderOrMandiName.isNotBlank()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                if (parchi.farmerName.isNotBlank()) {
                  Text(
                    text = "किसान: ${parchi.farmerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
                if (parchi.traderOrMandiName.isNotBlank()) {
                  Text(
                    text = "मंडी: ${parchi.traderOrMandiName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Details Grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "वजन (Weight):",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              val unitText = if (parchi.weightUnit == "QUINTAL") "क्विंटल" else "कि.ग्रा."
              Text(
                text = "%.2f %s".format(parchi.weightQuantity, unitText),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "भाव (Rate):",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              val rateUnitText = if (parchi.rateUnit == "PER_QUINTAL") "प्रति क्विंटल" else "प्रति कि.ग्रा."
              Text(
                text = "₹ %.2f %s".format(parchi.ratePerUnit, rateUnitText),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "कुल बिक्री (Gross Sale):",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = PdfBillGenerator.formatCurrency(parchi.totalAmount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = KisanGreenPrimary
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "कुल लागत (Investment):",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = PdfBillGenerator.formatCurrency(parchi.totalInvestmentCost),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
              )
            }

            // Net Profit Banner inside preview
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (parchi.isProfit) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (parchi.isProfit) "शुद्ध फायदा (Net Profit)" else "शुद्ध नुकसान (Net Loss)",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (parchi.isProfit) Color(0xFF14532D) else Color(0xFF7F1D1D)
                )
                Text(
                  text = "${if (parchi.isProfit) "+" else "-"}${PdfBillGenerator.formatCurrency(Math.abs(parchi.netProfitLoss))}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (parchi.isProfit) Color(0xFF14532D) else Color(0xFF7F1D1D)
                )
              }
            }
          }
        }

        // Action Buttons: WhatsApp Share, Open PDF, Print
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Primary Action: Share on WhatsApp / System
          Button(
            onClick = {
              if (pdfFile != null) {
                PdfBillGenerator.sharePdf(context, pdfFile, cropDisplay)
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = KisanGreenPrimary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("share_parchi_button")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.shareWhatsApp(language),
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Open PDF
            OutlinedButton(
              onClick = {
                if (pdfFile != null) {
                  PdfBillGenerator.openPdf(context, pdfFile)
                }
              },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("open_pdf_button")
            ) {
              Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = AppStrings.viewPdf(language),
                style = MaterialTheme.typography.labelMedium
              )
            }

            // Print Parchi
            OutlinedButton(
              onClick = {
                if (pdfFile != null) {
                  PdfBillGenerator.printPdf(context, pdfFile, "Parchi_${parchi.parchiNumber}")
                }
              },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("print_parchi_button")
            ) {
              Icon(
                imageVector = Icons.Default.Print,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = AppStrings.printParchi(language),
                style = MaterialTheme.typography.labelMedium
              )
            }
          }
        }
      }
    }
  }
}
