package com.example.data.model

data class Crop(
  val id: String,
  val nameEn: String,
  val nameHi: String,
  val defaultRate: Double = 0.0,
  val iconEmoji: String = "🌾"
)

enum class WeightUnit(val labelEn: String, val labelHi: String, val shortEn: String, val shortHi: String) {
  QUINTAL("Quintal", "क्विंटल", "Qtl", "क्विं."),
  KG("Kilogram", "किलोग्राम", "kg", "कि.ग्रा.")
}

enum class RateUnit(val labelEn: String, val labelHi: String, val perTextEn: String, val perTextHi: String) {
  PER_QUINTAL("Per Quintal", "प्रति क्विंटल", "₹ / Quintal", "₹ / क्विंटल"),
  PER_KG("Per Kilogram", "प्रति कि.ग्रा.", "₹ / kg", "₹ / कि.ग्रा.")
}

data class CostItem(
  val titleEn: String,
  val titleHi: String,
  val amount: Double
)

val DEFAULT_CROPS = listOf(
  Crop(id = "wheat", nameEn = "Wheat", nameHi = "गेहूं", defaultRate = 2275.0, iconEmoji = "🌾"),
  Crop(id = "paddy", nameEn = "Paddy", nameHi = "धान", defaultRate = 2300.0, iconEmoji = "🌾"),
  Crop(id = "cotton", nameEn = "Cotton", nameHi = "नरमा - कपास", defaultRate = 7122.0, iconEmoji = "🌱"),
  Crop(id = "mustard", nameEn = "Mustard", nameHi = "सरसों", defaultRate = 5650.0, iconEmoji = "🌼"),
  Crop(id = "gram", nameEn = "Gram (Chana)", nameHi = "चना", defaultRate = 5440.0, iconEmoji = "🫘"),
  Crop(id = "soybean", nameEn = "Soybean", nameHi = "सोयाबीन", defaultRate = 4892.0, iconEmoji = "🌱"),
  Crop(id = "maize", nameEn = "Maize (Makka)", nameHi = "मक्का", defaultRate = 2090.0, iconEmoji = "🌽"),
  Crop(id = "sugarcane", nameEn = "Sugarcane", nameHi = "गन्ना", defaultRate = 340.0, iconEmoji = "🎋"),
  Crop(id = "bajra", nameEn = "Pearl Millet (Bajra)", nameHi = "बाजरा", defaultRate = 2500.0, iconEmoji = "🌾"),
  Crop(id = "groundnut", nameEn = "Groundnut", nameHi = "मूंगफली", defaultRate = 6783.0, iconEmoji = "🥜"),
  Crop(id = "onion", nameEn = "Onion", nameHi = "प्याज", defaultRate = 1800.0, iconEmoji = "🧅"),
  Crop(id = "potato", nameEn = "Potato", nameHi = "आलू", defaultRate = 1200.0, iconEmoji = "🥔"),
  Crop(id = "garlic", nameEn = "Garlic", nameHi = "लहसुन", defaultRate = 8500.0, iconEmoji = "🧄"),
  Crop(id = "barley", nameEn = "Barley (Jau)", nameHi = "जौ", defaultRate = 1850.0, iconEmoji = "🌾"),
  Crop(id = "moong", nameEn = "Moong Dal", nameHi = "मूंग दाल", defaultRate = 8558.0, iconEmoji = "🫘"),
  Crop(id = "custom", nameEn = "Other / Custom Crop", nameHi = "अन्य / नई फसल", defaultRate = 0.0, iconEmoji = "🌿")
)
