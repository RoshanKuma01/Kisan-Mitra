package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Crop
import com.example.data.model.DEFAULT_CROPS
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun CropSelectorSection(
  selectedCrop: Crop,
  customCropName: String,
  language: AppLanguage,
  onSelectCrop: (Crop) -> Unit,
  onCustomCropChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = AppStrings.selectCrop(language),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    // Horizontal Scrolling Crop Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      DEFAULT_CROPS.forEach { crop ->
        val isSelected = selectedCrop.id == crop.id
        val cropName = if (language == AppLanguage.HINDI) crop.nameHi else crop.nameEn

        FilterChip(
          selected = isSelected,
          onClick = { onSelectCrop(crop) },
          label = {
            Text(
              text = "${crop.iconEmoji} $cropName",
              style = MaterialTheme.typography.bodyMedium
            )
          },
          leadingIcon = if (isSelected) {
            {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                modifier = Modifier.size(16.dp)
              )
            }
          } else null,
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("crop_chip_${crop.id}")
        )
      }
    }

    // If Custom Crop is selected or user wants to enter custom name
    AnimatedVisibility(visible = selectedCrop.id == "custom") {
      OutlinedTextField(
        value = customCropName,
        onValueChange = onCustomCropChange,
        label = { Text(AppStrings.customCropPlaceholder(language)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null
          )
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp)
          .testTag("custom_crop_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
      )
    }
  }
}
