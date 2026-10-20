package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ParchiEntity
import com.example.data.model.Crop
import com.example.data.model.DEFAULT_CROPS
import com.example.data.model.RateUnit
import com.example.data.model.WeightUnit
import com.example.data.repository.ParchiRepository
import com.example.util.AppLanguage
import com.example.util.PdfBillGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ParchiViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: ParchiRepository

  init {
    val dao = AppDatabase.getDatabase(application).parchiDao()
    repository = ParchiRepository(dao)
  }

  // Language state
  private val _currentLanguage = MutableStateFlow(AppLanguage.HINDI)
  val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  fun toggleLanguage() {
    _currentLanguage.value = if (_currentLanguage.value == AppLanguage.HINDI) {
      AppLanguage.ENGLISH
    } else {
      AppLanguage.HINDI
    }
  }

  // Crop State
  private val _selectedCrop = MutableStateFlow(DEFAULT_CROPS[0]) // Wheat default
  val selectedCrop: StateFlow<Crop> = _selectedCrop.asStateFlow()

  private val _customCropName = MutableStateFlow("")
  val customCropName: StateFlow<String> = _customCropName.asStateFlow()

  private val _cropSearchQuery = MutableStateFlow("")
  val cropSearchQuery: StateFlow<String> = _cropSearchQuery.asStateFlow()

  // Form Inputs
  private val _weightInput = MutableStateFlow("40")
  val weightInput: StateFlow<String> = _weightInput.asStateFlow()

  private val _weightUnit = MutableStateFlow(WeightUnit.QUINTAL)
  val weightUnit: StateFlow<WeightUnit> = _weightUnit.asStateFlow()

  private val _rateInput = MutableStateFlow("2275")
  val rateInput: StateFlow<String> = _rateInput.asStateFlow()

  private val _rateUnit = MutableStateFlow(RateUnit.PER_QUINTAL)
  val rateUnit: StateFlow<RateUnit> = _rateUnit.asStateFlow()

  private val _investmentCostInput = MutableStateFlow("32000")
  val investmentCostInput: StateFlow<String> = _investmentCostInput.asStateFlow()

  // Metadata
  private val _farmerName = MutableStateFlow("")
  val farmerName: StateFlow<String> = _farmerName.asStateFlow()

  private val _traderOrMandiName = MutableStateFlow("")
  val traderOrMandiName: StateFlow<String> = _traderOrMandiName.asStateFlow()

  private val _mobileNumber = MutableStateFlow("")
  val mobileNumber: StateFlow<String> = _mobileNumber.asStateFlow()

  private val _villageCity = MutableStateFlow("")
  val villageCity: StateFlow<String> = _villageCity.asStateFlow()

  private val _notes = MutableStateFlow("")
  val notes: StateFlow<String> = _notes.asStateFlow()

  // Calculations
  val totalAmount: StateFlow<Double> = combine(
    _weightInput,
    _weightUnit,
    _rateInput,
    _rateUnit
  ) { weightStr, wUnit, rateStr, rUnit ->
    val weight = weightStr.toDoubleOrNull() ?: 0.0
    val rate = rateStr.toDoubleOrNull() ?: 0.0

    calculateTotalAmount(weight, wUnit, rate, rUnit)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val netProfitLoss: StateFlow<Double> = combine(
    totalAmount,
    _investmentCostInput
  ) { total, costStr ->
    val cost = costStr.toDoubleOrNull() ?: 0.0
    total - cost
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val isProfit: StateFlow<Boolean> = combine(netProfitLoss) { it[0] >= 0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

  val profitMarginPercent: StateFlow<Double> = combine(
    netProfitLoss,
    _investmentCostInput
  ) { net, costStr ->
    val cost = costStr.toDoubleOrNull() ?: 0.0
    if (cost > 0) (net / cost) * 100.0 else 0.0
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  // Dialog & PDF Preview State
  private val _activeParchiForPreview = MutableStateFlow<ParchiEntity?>(null)
  val activeParchiForPreview: StateFlow<ParchiEntity?> = _activeParchiForPreview.asStateFlow()

  private val _activePdfFile = MutableStateFlow<File?>(null)
  val activePdfFile: StateFlow<File?> = _activePdfFile.asStateFlow()

  // History & Database
  private val _historySearchQuery = MutableStateFlow("")
  val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

  private val _historyFilter = MutableStateFlow("ALL") // "ALL", "PROFIT", "LOSS"
  val historyFilter: StateFlow<String> = _historyFilter.asStateFlow()

  val allParchis: StateFlow<List<ParchiEntity>> = combine(
    repository.allParchis,
    _historySearchQuery,
    _historyFilter
  ) { list, query, filter ->
    list.filter { item ->
      val matchesQuery = query.isBlank() ||
        item.cropNameHi.contains(query, ignoreCase = true) ||
        item.cropNameEn.contains(query, ignoreCase = true) ||
        item.farmerName.contains(query, ignoreCase = true) ||
        item.parchiNumber.contains(query, ignoreCase = true) ||
        item.traderOrMandiName.contains(query, ignoreCase = true)

      val matchesFilter = when (filter) {
        "PROFIT" -> item.isProfit
        "LOSS" -> !item.isProfit
        else -> true
      }

      matchesQuery && matchesFilter
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Mutators
  fun selectCrop(crop: Crop) {
    _selectedCrop.value = crop
    if (crop.id != "custom" && crop.defaultRate > 0) {
      _rateInput.value = crop.defaultRate.toString()
    }
  }

  fun setCustomCropName(name: String) {
    _customCropName.value = name
  }

  fun setCropSearchQuery(query: String) {
    _cropSearchQuery.value = query
  }

  fun setWeightInput(value: String) {
    _weightInput.value = value
  }

  fun setWeightUnit(unit: WeightUnit) {
    _weightUnit.value = unit
  }

  fun addWeight(delta: Double) {
    val current = _weightInput.value.toDoubleOrNull() ?: 0.0
    val updated = (current + delta).coerceAtLeast(0.0)
    _weightInput.value = if (updated % 1.0 == 0.0) updated.toLong().toString() else "%.2f".format(updated)
  }

  fun setRateInput(value: String) {
    _rateInput.value = value
  }

  fun setRateUnit(unit: RateUnit) {
    _rateUnit.value = unit
  }

  fun setInvestmentCostInput(value: String) {
    _investmentCostInput.value = value
  }

  fun setFarmerName(name: String) {
    _farmerName.value = name
  }

  fun setTraderOrMandiName(name: String) {
    _traderOrMandiName.value = name
  }

  fun setMobileNumber(mobile: String) {
    _mobileNumber.value = mobile
  }

  fun setVillageCity(village: String) {
    _villageCity.value = village
  }

  fun setNotes(notes: String) {
    _notes.value = notes
  }

  fun setHistorySearchQuery(query: String) {
    _historySearchQuery.value = query
  }

  fun setHistoryFilter(filter: String) {
    _historyFilter.value = filter
  }

  fun dismissPreviewDialog() {
    _activeParchiForPreview.value = null
    _activePdfFile.value = null
  }

  fun viewPdfForParchi(parchi: ParchiEntity) {
    viewModelScope.launch {
      val file = PdfBillGenerator.generatePdf(getApplication(), parchi)
      _activeParchiForPreview.value = parchi
      _activePdfFile.value = file
    }
  }

  fun saveParchiAndGeneratePdf() {
    viewModelScope.launch {
      val crop = _selectedCrop.value
      val cropHi = if (crop.id == "custom") _customCropName.value.ifBlank { "अन्य फसल" } else crop.nameHi
      val cropEn = if (crop.id == "custom") _customCropName.value.ifBlank { "Custom Crop" } else crop.nameEn

      val weight = _weightInput.value.toDoubleOrNull() ?: 0.0
      val rate = _rateInput.value.toDoubleOrNull() ?: 0.0
      val cost = _investmentCostInput.value.toDoubleOrNull() ?: 0.0
      val total = calculateTotalAmount(weight, _weightUnit.value, rate, _rateUnit.value)
      val profitLoss = total - cost
      val profit = profitLoss >= 0
      val margin = if (cost > 0) (profitLoss / cost) * 100.0 else 0.0

      val dateStamp = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
      val randomCode = Random.nextInt(100, 999)
      val parchiNum = "KP-$dateStamp-$randomCode"

      val entity = ParchiEntity(
        parchiNumber = parchiNum,
        farmerName = _farmerName.value.trim(),
        traderOrMandiName = _traderOrMandiName.value.trim(),
        mobileNumber = _mobileNumber.value.trim(),
        villageCity = _villageCity.value.trim(),
        cropNameEn = cropEn,
        cropNameHi = cropHi,
        weightQuantity = weight,
        weightUnit = _weightUnit.value.name,
        ratePerUnit = rate,
        rateUnit = _rateUnit.value.name,
        totalAmount = total,
        totalInvestmentCost = cost,
        netProfitLoss = profitLoss,
        isProfit = profit,
        profitMarginPercent = margin,
        notes = _notes.value.trim(),
        timestamp = System.currentTimeMillis()
      )

      val savedId = repository.insertParchi(entity)
      val finalRecord = entity.copy(id = savedId)

      // Generate PDF
      val pdfFile = PdfBillGenerator.generatePdf(getApplication(), finalRecord)
      _activeParchiForPreview.value = finalRecord
      _activePdfFile.value = pdfFile
    }
  }

  fun deleteParchi(id: Long) {
    viewModelScope.launch {
      repository.deleteById(id)
    }
  }

  fun resetForm() {
    _selectedCrop.value = DEFAULT_CROPS[0]
    _customCropName.value = ""
    _weightInput.value = "40"
    _weightUnit.value = WeightUnit.QUINTAL
    _rateInput.value = "2275"
    _rateUnit.value = RateUnit.PER_QUINTAL
    _investmentCostInput.value = "32000"
    _farmerName.value = ""
    _traderOrMandiName.value = ""
    _mobileNumber.value = ""
    _villageCity.value = ""
    _notes.value = ""
  }

  private fun calculateTotalAmount(
    weight: Double,
    wUnit: WeightUnit,
    rate: Double,
    rUnit: RateUnit
  ): Double {
    return when {
      // Both Quintals
      wUnit == WeightUnit.QUINTAL && rUnit == RateUnit.PER_QUINTAL -> weight * rate
      // Weight in kg, rate per Quintal (1 Quintal = 100 kg)
      wUnit == WeightUnit.KG && rUnit == RateUnit.PER_QUINTAL -> (weight / 100.0) * rate
      // Weight in Quintals, rate per kg
      wUnit == WeightUnit.QUINTAL && rUnit == RateUnit.PER_KG -> (weight * 100.0) * rate
      // Both kg
      else -> weight * rate
    }
  }
}
