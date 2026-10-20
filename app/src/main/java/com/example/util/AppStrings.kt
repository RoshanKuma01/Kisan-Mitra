package com.example.util

enum class AppLanguage {
  HINDI,
  ENGLISH
}

object AppStrings {
  fun appTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "किसान पर्ची व हिसाब"
    AppLanguage.ENGLISH -> "Kisan Parchi & Bill"
  }

  fun newBillTab(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "नई पर्ची"
    AppLanguage.ENGLISH -> "New Bill"
  }

  fun historyTab(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "पुरानी पर्चियां"
    AppLanguage.ENGLISH -> "Bill History"
  }

  fun statsTab(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "कुल हिसाब"
    AppLanguage.ENGLISH -> "Summary"
  }

  fun selectCrop(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "फसल चुनें या नाम लिखें"
    AppLanguage.ENGLISH -> "Select Crop or Type Name"
  }

  fun customCropPlaceholder(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "फसल का नाम दर्ज करें (जैसे: सरसों, धान)"
    AppLanguage.ENGLISH -> "Enter custom crop name"
  }

  fun weightQuantity(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "कुल वजन / मात्रा"
    AppLanguage.ENGLISH -> "Total Weight / Quantity"
  }

  fun ratePerUnit(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "भाव / रेट"
    AppLanguage.ENGLISH -> "Rate / Price"
  }

  fun totalInvestment(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "कुल लागत / खर्च (खेत से मंडी तक)"
    AppLanguage.ENGLISH -> "Total Investment / Cost"
  }

  fun totalAmount(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "कुल बिक्री मूल्य"
    AppLanguage.ENGLISH -> "Total Sale Amount"
  }

  fun netProfit(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "शुद्ध फायदा (मुनाफा)"
    AppLanguage.ENGLISH -> "Net Profit"
  }

  fun netLoss(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "शुद्ध नुकसान (घाटा)"
    AppLanguage.ENGLISH -> "Net Loss"
  }

  fun generateParchi(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "पर्ची बनाएं व सहेजें (PDF)"
    AppLanguage.ENGLISH -> "Generate & Save Parchi (PDF)"
  }

  fun farmerName(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "किसान / विक्रेता का नाम"
    AppLanguage.ENGLISH -> "Farmer / Seller Name"
  }

  fun traderMandiName(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "मंडी / व्यापारी / आढ़तिया का नाम"
    AppLanguage.ENGLISH -> "Mandi / Trader / Buyer Name"
  }

  fun mobileOptional(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "मोबाइल नंबर (वैकल्पिक)"
    AppLanguage.ENGLISH -> "Mobile Number (Optional)"
  }

  fun villageCity(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "गांव / शहर"
    AppLanguage.ENGLISH -> "Village / City"
  }

  fun shareWhatsApp(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "व्हाट्सएप पर पर्ची भेजें"
    AppLanguage.ENGLISH -> "Share via WhatsApp"
  }

  fun printParchi(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "प्रिंट निकालें"
    AppLanguage.ENGLISH -> "Print Receipt"
  }

  fun viewPdf(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "PDF पर्ची खोलें"
    AppLanguage.ENGLISH -> "Open PDF Receipt"
  }

  fun deleteConfirm(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "क्या आप इस पर्ची को हटाना चाहते हैं?"
    AppLanguage.ENGLISH -> "Do you want to delete this bill record?"
  }

  fun reset(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "रीसेट करें"
    AppLanguage.ENGLISH -> "Reset Form"
  }

  fun roi(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "मुनाफा प्रतिशत (ROI)"
    AppLanguage.ENGLISH -> "Profit Margin (ROI)"
  }

  fun emptyHistory(lang: AppLanguage) = when (lang) {
    AppLanguage.HINDI -> "अभी कोई पर्ची नहीं बनी है। ऊपर नई पर्ची बनाएं!"
    AppLanguage.ENGLISH -> "No bills generated yet. Create a new bill above!"
  }
}
