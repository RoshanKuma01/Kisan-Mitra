package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RateUnit
import com.example.data.model.WeightUnit
import com.example.ui.components.CalculationCard
import com.example.ui.components.CropSelectorSection
import com.example.ui.theme.KisanGreenPrimary
import com.example.ui.viewmodel.ParchiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun NewBillScreen(
  viewModel: ParchiViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val selectedCrop by viewModel.selectedCrop.collectAsState()
  val customCropName by viewModel.customCropName.collectAsState()

  val weightInput by viewModel.weightInput.collectAsState()
  val weightUnit by viewModel.weightUnit.collectAsState()
  val rateInput by viewModel.rateInput.collectAsState()
  val rateUnit by viewModel.rateUnit.collectAsState()
  val investmentCostInput by viewModel.investmentCostInput.collectAsState()

  val farmerName by viewModel.farmerName.collectAsState()
  val traderOrMandiName by viewModel.traderOrMandiName.collectAsState()
  val mobileNumber by viewModel.mobileNumber.collectAsState()
  val villageCity by viewModel.villageCity.collectAsState()
  val notes by viewModel.notes.collectAsState()

  val totalAmount by viewModel.totalAmount.collectAsState()
  val netProfitLoss by viewModel.netProfitLoss.collectAsState()
  val isProfit by viewModel.isProfit.collectAsState()
  val profitMarginPercent by viewModel.profitMarginPercent.collectAsState()

  var isDetailsExpanded by remember { mutableStateOf(false) }

  val weightVal = weightInput.toDoubleOrNull() ?: 0.0
  val costVal = investmentCostInput.toDoubleOrNull() ?: 0.0

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Crop Selection Section
    CropSelectorSection(
      selectedCrop = selectedCrop,
      customCropName = customCropName,
      language = language,
      onSelectCrop = { viewModel.selectCrop(it) },
      onCustomCropChange = { viewModel.setCustomCropName(it) }
    )

    // 2. Weight & Quantity Card
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Scale,
              contentDescription = null,
              tint = KisanGreenPrimary,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = AppStrings.weightQuantity(language),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          // Unit Switcher Tabs: Quintal / KG
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(2.dp)
          ) {
            Row {
              val isQ = weightUnit == WeightUnit.QUINTAL
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isQ) KisanGreenPrimary else MaterialTheme.colorScheme.surfaceVariant)
                  .clickable { viewModel.setWeightUnit(WeightUnit.QUINTAL) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("unit_quintal_tab")
              ) {
                Text(
                  text = if (language == AppLanguage.HINDI) "क्विंटल" else "Quintal",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (isQ) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (!isQ) KisanGreenPrimary else MaterialTheme.colorScheme.surfaceVariant)
                  .clickable { viewModel.setWeightUnit(WeightUnit.KG) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("unit_kg_tab")
              ) {
                Text(
                  text = if (language == AppLanguage.HINDI) "कि.ग्रा." else "kg",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (!isQ) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = weightInput,
          onValueChange = { viewModel.setWeightInput(it) },
          label = {
            Text(
              if (weightUnit == WeightUnit.QUINTAL) "वजन (क्विंटल में / In Quintals)" else "वजन (किलोग्राम में / In kg)"
            )
          },
          placeholder = { Text("उदा. 45.5") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("weight_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KisanGreenPrimary
          )
        )

        // Equivalence hint and Quick increment pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          val altText = if (weightUnit == WeightUnit.QUINTAL) {
            "= %,.1f कि.ग्रा. (kg)".format(weightVal * 100.0)
          } else {
            "= %,.2f क्विंटल (Qtl)".format(weightVal / 100.0)
          }
          Text(
            text = altText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = KisanGreenPrimary
          )

          // Quick add buttons (+1, +5, +10)
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AssistChip(
              onClick = { viewModel.addWeight(1.0) },
              label = { Text("+1", fontSize = 11.sp) }
            )
            AssistChip(
              onClick = { viewModel.addWeight(5.0) },
              label = { Text("+5", fontSize = 11.sp) }
            )
            AssistChip(
              onClick = { viewModel.addWeight(10.0) },
              label = { Text("+10", fontSize = 11.sp) }
            )
          }
        }
      }
    }

    // 3. Rate & Unit Card
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CurrencyRupee,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = AppStrings.ratePerUnit(language),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          // Rate Unit Switcher: Per Quintal vs Per Kg
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(2.dp)
          ) {
            Row {
              val isPerQ = rateUnit == RateUnit.PER_QUINTAL
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isPerQ) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant)
                  .clickable { viewModel.setRateUnit(RateUnit.PER_QUINTAL) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("rate_unit_per_quintal_tab")
              ) {
                Text(
                  text = if (language == AppLanguage.HINDI) "प्रति क्विंटल" else "Per Qtl",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (isPerQ) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (!isPerQ) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant)
                  .clickable { viewModel.setRateUnit(RateUnit.PER_KG) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("rate_unit_per_kg_tab")
              ) {
                Text(
                  text = if (language == AppLanguage.HINDI) "प्रति कि.ग्रा." else "Per kg",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (!isPerQ) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = rateInput,
          onValueChange = { viewModel.setRateInput(it) },
          label = {
            Text(
              if (rateUnit == RateUnit.PER_QUINTAL) "भाव (₹ प्रति क्विंटल)" else "भाव (₹ प्रति कि.ग्रा.)"
            )
          },
          placeholder = { Text("उदा. 2275") },
          leadingIcon = {
            Text(
              text = "₹",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.secondary
            )
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rate_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary
          )
        )
      }
    }

    // 4. Investment / Cost Input Card
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Calculate,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = AppStrings.totalInvestment(language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        Text(
          text = if (language == AppLanguage.HINDI) "बीज, खाद, कटाई, जुताई, भाड़ा आदि का कुल खर्च" else "Total expenses (seeds, fertilizer, labor, transport, mandi fee)",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )

        OutlinedTextField(
          value = investmentCostInput,
          onValueChange = { viewModel.setInvestmentCostInput(it) },
          label = { Text("कुल खर्च / लागत राशि (₹)") },
          placeholder = { Text("उदा. 25000") },
          leadingIcon = {
            Text(
              text = "₹",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.error
            )
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("investment_cost_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.error
          )
        )
      }
    }

    // 5. Automatic Calculation Card (Live Total, Profit/Loss, ROI)
    CalculationCard(
      totalAmount = totalAmount,
      totalInvestmentCost = costVal,
      netProfitLoss = netProfitLoss,
      isProfit = isProfit,
      profitMarginPercent = profitMarginPercent,
      language = language
    )

    // 6. Optional Farmer & Mandi Details (Collapsible)
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isDetailsExpanded = !isDetailsExpanded }
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = KisanGreenPrimary,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = if (language == AppLanguage.HINDI) "किसान व मंडी पर्ची विवरण (वैकल्पिक)" else "Farmer & Mandi Details (Optional)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (farmerName.isNotBlank() || traderOrMandiName.isNotBlank()) "नाम दर्ज है" else "पर्ची पर छापने के लिए",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }

          Icon(
            imageVector = if (isDetailsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
          )
        }

        AnimatedVisibility(visible = isDetailsExpanded) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = farmerName,
              onValueChange = { viewModel.setFarmerName(it) },
              label = { Text(AppStrings.farmerName(language)) },
              placeholder = { Text("उदा. रमेश कुमार") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("farmer_name_input")
            )

            OutlinedTextField(
              value = traderOrMandiName,
              onValueChange = { viewModel.setTraderOrMandiName(it) },
              label = { Text(AppStrings.traderMandiName(language)) },
              placeholder = { Text("उदा. अनाज मंडी, कोटा / गुप्ता ट्रेडिंग") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("trader_name_input")
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = mobileNumber,
                onValueChange = { viewModel.setMobileNumber(it) },
                label = { Text(AppStrings.mobileOptional(language)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .testTag("mobile_number_input")
              )

              OutlinedTextField(
                value = villageCity,
                onValueChange = { viewModel.setVillageCity(it) },
                label = { Text(AppStrings.villageCity(language)) },
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .testTag("village_city_input")
              )
            }

            OutlinedTextField(
              value = notes,
              onValueChange = { viewModel.setNotes(it) },
              label = { Text(if (language == AppLanguage.HINDI) "टिप्पणी / नोट" else "Notes") },
              placeholder = { Text("उदा. नमी 12%, बारदाना किसान का") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

    // 7. Actions: Generate PDF Parchi & Reset
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp, bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = { viewModel.saveParchiAndGeneratePdf() },
        colors = ButtonDefaults.buttonColors(
          containerColor = KisanGreenPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .weight(1f)
          .height(54.dp)
          .testTag("generate_parchi_button")
      ) {
        Icon(
          imageVector = Icons.Default.PictureAsPdf,
          contentDescription = null,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = AppStrings.generateParchi(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      OutlinedButton(
        onClick = { viewModel.resetForm() },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .height(54.dp)
          .testTag("reset_form_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reset",
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
