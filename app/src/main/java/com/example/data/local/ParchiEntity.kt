package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parchi_records")
data class ParchiEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val parchiNumber: String,
  val farmerName: String,
  val traderOrMandiName: String,
  val mobileNumber: String,
  val villageCity: String,
  val cropNameEn: String,
  val cropNameHi: String,
  val weightQuantity: Double,
  val weightUnit: String, // "QUINTAL" or "KG"
  val ratePerUnit: Double,
  val rateUnit: String,   // "PER_QUINTAL" or "PER_KG"
  val totalAmount: Double,
  val totalInvestmentCost: Double,
  val netProfitLoss: Double,
  val isProfit: Boolean,
  val profitMarginPercent: Double,
  val notes: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val pdfFileName: String = ""
)
