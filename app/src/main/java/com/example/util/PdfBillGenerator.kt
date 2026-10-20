package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.ParchiEntity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfBillGenerator {

  private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
    maximumFractionDigits = 2
    minimumFractionDigits = 2
  }

  fun formatCurrency(amount: Double): String {
    return try {
      currencyFormatter.format(amount)
    } catch (_: Exception) {
      "₹ %.2f".format(amount)
    }
  }

  fun generatePdf(context: Context, parchi: ParchiEntity): File {
    val pdfDir = File(context.cacheDir, "parchi_pdfs").apply {
      if (!exists()) mkdirs()
    }
    val safeParchiNum = parchi.parchiNumber.replace(Regex("[^a-zA-Z0-9_-]"), "_")
    val pdfFile = File(pdfDir, "Kisan_Parchi_$safeParchiNum.pdf")

    val document = PdfDocument()
    // A4 Standard Page: 595 x 842 points
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas: Canvas = page.canvas

    drawParchiReceipt(canvas, parchi)

    document.finishPage(page)

    FileOutputStream(pdfFile).use { out ->
      document.writeTo(out)
    }
    document.close()

    return pdfFile
  }

  private fun drawParchiReceipt(canvas: Canvas, parchi: ParchiEntity) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Background
    canvas.drawColor(Color.WHITE)

    // Outer Decorative Border
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2.5f
    paint.color = Color.rgb(27, 107, 56) // Kisan Green
    canvas.drawRect(20f, 20f, 575f, 822f, paint)

    paint.strokeWidth = 0.8f
    paint.color = Color.rgb(200, 210, 195)
    canvas.drawRect(24f, 24f, 571f, 818f, paint)

    // Header Green Banner
    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(27, 107, 56)
    canvas.drawRoundRect(RectF(28f, 28f, 567f, 108f), 6f, 6f, paint)

    // Header Title
    paint.color = Color.WHITE
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 21f
    paint.textAlign = Paint.Align.CENTER
    canvas.drawText("KISAN PARCHI • किसान बिल पर्ची", 297.5f, 58f, paint)

    paint.textSize = 11.5f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = Color.rgb(230, 248, 235)
    canvas.drawText("कृषि उपज बिक्री व शुद्ध लाभ/हानि रसीद (Crop Sale & Net Profit Receipt)", 297.5f, 79f, paint)
    paint.textSize = 9.5f
    canvas.drawText("Mandi & Farmer Direct Settlement Slip", 297.5f, 96f, paint)

    // Meta Section: Bill No and Date
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = sdf.format(Date(parchi.timestamp))

    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 10f
    paint.color = Color.rgb(40, 50, 40)
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("पर्ची क्र. / Bill No: ${parchi.parchiNumber}", 36f, 130f, paint)

    paint.textAlign = Paint.Align.RIGHT
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    canvas.drawText("दिनांक / Date: $dateStr", 559f, 130f, paint)

    // Divider Line
    paint.strokeWidth = 1f
    paint.color = Color.rgb(220, 225, 220)
    canvas.drawLine(36f, 138f, 559f, 138f, paint)

    // 2-Column Info Card (Farmer Details & Mandi/Trader Details)
    // Left Box: Farmer Info
    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(245, 248, 244)
    canvas.drawRoundRect(RectF(36f, 146f, 290f, 218f), 4f, 4f, paint)
    paint.style = Paint.Style.STROKE
    paint.color = Color.rgb(215, 228, 216)
    canvas.drawRoundRect(RectF(36f, 146f, 290f, 218f), 4f, 4f, paint)

    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 10f
    paint.color = Color.rgb(27, 107, 56)
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("किसान विवरण / Farmer Details", 46f, 164f, paint)

    paint.textSize = 9.5f
    paint.color = Color.BLACK
    val farmerDisplayName = if (parchi.farmerName.isNotBlank()) parchi.farmerName else "किसान (Farmer)"
    canvas.drawText("नाम / Name: $farmerDisplayName", 46f, 182f, paint)
    val villageText = if (parchi.villageCity.isNotBlank()) parchi.villageCity else "-"
    canvas.drawText("गांव / Village: $villageText", 46f, 198f, paint)
    val mobText = if (parchi.mobileNumber.isNotBlank()) parchi.mobileNumber else "-"
    canvas.drawText("मोबाइल / Mobile: $mobText", 46f, 212f, paint)

    // Right Box: Trader / Mandi Info
    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(254, 249, 242)
    canvas.drawRoundRect(RectF(305f, 146f, 559f, 218f), 4f, 4f, paint)
    paint.style = Paint.Style.STROKE
    paint.color = Color.rgb(240, 220, 195)
    canvas.drawRoundRect(RectF(305f, 146f, 559f, 218f), 4f, 4f, paint)

    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(180, 83, 9)
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 10f
    canvas.drawText("मंडी / खरीदार / Mandi & Trader", 315f, 164f, paint)

    paint.textSize = 9.5f
    paint.color = Color.BLACK
    val traderDisplayName = if (parchi.traderOrMandiName.isNotBlank()) parchi.traderOrMandiName else "कृषि उपज मंडी (Mandi Market)"
    canvas.drawText("मंडी / व्यापारी: $traderDisplayName", 315f, 182f, paint)
    canvas.drawText("स्थान / Location: $villageText", 315f, 198f, paint)
    canvas.drawText("भुगतान स्थिति / Payment: नकद / खाता Settlement", 315f, 212f, paint)

    // TABLE SECTION: Crop Particulars
    val tableTop = 236f
    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(27, 107, 56)
    canvas.drawRect(36f, tableTop, 559f, tableTop + 26f, paint)

    paint.color = Color.WHITE
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 9.5f
    paint.textAlign = Paint.Align.LEFT
    canvas.drawText("फसल विवरण (Crop)", 46f, tableTop + 17f, paint)
    canvas.drawText("वजन / मात्रा (Weight)", 200f, tableTop + 17f, paint)
    canvas.drawText("भाव / रेट (Rate)", 340f, tableTop + 17f, paint)
    paint.textAlign = Paint.Align.RIGHT
    canvas.drawText("कुल बिक्री मूल्य (Amount)", 549f, tableTop + 17f, paint)

    // Table Row
    val rowY = tableTop + 26f
    paint.color = Color.rgb(255, 255, 255)
    canvas.drawRect(36f, rowY, 559f, rowY + 54f, paint)
    paint.style = Paint.Style.STROKE
    paint.color = Color.rgb(210, 220, 210)
    canvas.drawRect(36f, rowY, 559f, rowY + 54f, paint)

    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(20, 30, 20)
    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 12f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    val cropTitle = if (parchi.cropNameHi.isNotBlank() && parchi.cropNameEn.isNotBlank() && parchi.cropNameHi != parchi.cropNameEn) {
      "${parchi.cropNameHi} (${parchi.cropNameEn})"
    } else parchi.cropNameHi.ifBlank { parchi.cropNameEn }
    canvas.drawText(cropTitle, 46f, rowY + 22f, paint)

    paint.textSize = 9f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = Color.rgb(100, 110, 100)
    canvas.drawText("Agri Commodity Standard", 46f, rowY + 38f, paint)

    // Weight Column
    paint.color = Color.BLACK
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 11f
    val weightStr = "%.2f %s".format(
      parchi.weightQuantity,
      if (parchi.weightUnit == "QUINTAL") "क्विंटल (Qtl)" else "कि.ग्रा. (kg)"
    )
    canvas.drawText(weightStr, 200f, rowY + 22f, paint)

    paint.textSize = 9f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = Color.rgb(90, 100, 90)
    val altWeightStr = if (parchi.weightUnit == "QUINTAL") {
      "= %,.1f kg".format(parchi.weightQuantity * 100.0)
    } else {
      "= %,.2f Qtl".format(parchi.weightQuantity / 100.0)
    }
    canvas.drawText(altWeightStr, 200f, rowY + 38f, paint)

    // Rate Column
    paint.color = Color.BLACK
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 11f
    val rateUnitLabel = if (parchi.rateUnit == "PER_QUINTAL") "/ क्विंटल (Qtl)" else "/ कि.ग्रा. (kg)"
    canvas.drawText("₹ %,.2f".format(parchi.ratePerUnit), 340f, rowY + 22f, paint)
    paint.textSize = 9f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = Color.rgb(90, 100, 90)
    canvas.drawText(rateUnitLabel, 340f, rowY + 38f, paint)

    // Total Sale Column
    paint.textAlign = Paint.Align.RIGHT
    paint.color = Color.rgb(27, 107, 56)
    paint.textSize = 13f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText(formatCurrency(parchi.totalAmount), 549f, rowY + 28f, paint)

    // SUMMARY & PROFIT/LOSS BREAKDOWN SECTION
    val sumTop = rowY + 70f
    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(248, 250, 246)
    canvas.drawRoundRect(RectF(36f, sumTop, 559f, sumTop + 140f), 6f, 6f, paint)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    paint.color = Color.rgb(215, 225, 215)
    canvas.drawRoundRect(RectF(36f, sumTop, 559f, sumTop + 140f), 6f, 6f, paint)

    // Summary Items
    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 11f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = Color.rgb(60, 70, 60)

    canvas.drawText("1. कुल बिक्री राशि (Gross Revenue):", 52f, sumTop + 30f, paint)
    paint.textAlign = Paint.Align.RIGHT
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = Color.BLACK
    canvas.drawText(formatCurrency(parchi.totalAmount), 543f, sumTop + 30f, paint)

    paint.textAlign = Paint.Align.LEFT
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = Color.rgb(60, 70, 60)
    canvas.drawText("2. कुल लागत व खर्च (Total Investment / Cost):", 52f, sumTop + 56f, paint)
    paint.textAlign = Paint.Align.RIGHT
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = Color.rgb(180, 50, 50)
    canvas.drawText("- " + formatCurrency(parchi.totalInvestmentCost), 543f, sumTop + 56f, paint)

    // Dashed / solid line before profit
    paint.strokeWidth = 0.8f
    paint.color = Color.rgb(200, 210, 200)
    canvas.drawLine(52f, sumTop + 70f, 543f, sumTop + 70f, paint)

    // PROFIT / LOSS HIGHLIGHT BANNER
    val bannerY = sumTop + 80f
    val isProfit = parchi.isProfit
    val bannerBg = if (isProfit) Color.rgb(220, 252, 231) else Color.rgb(254, 226, 226)
    val bannerBorder = if (isProfit) Color.rgb(22, 163, 74) else Color.rgb(220, 38, 38)
    val bannerText = if (isProfit) Color.rgb(20, 83, 45) else Color.rgb(127, 29, 29)

    paint.style = Paint.Style.FILL
    paint.color = bannerBg
    canvas.drawRoundRect(RectF(52f, bannerY, 543f, bannerY + 46f), 5f, 5f, paint)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1.5f
    paint.color = bannerBorder
    canvas.drawRoundRect(RectF(52f, bannerY, 543f, bannerY + 46f), 5f, 5f, paint)

    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.LEFT
    paint.color = bannerText
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 12f

    val profitLabel = if (isProfit) "★ शुद्ध फायदा (NET PROFIT)" else "⚠ शुद्ध नुकसान (NET LOSS)"
    canvas.drawText(profitLabel, 64f, bannerY + 28f, paint)

    paint.textAlign = Paint.Align.RIGHT
    paint.textSize = 13.5f
    val sign = if (isProfit) "+" else "-"
    val profitVal = formatCurrency(Math.abs(parchi.netProfitLoss))
    val marginText = if (parchi.totalInvestmentCost > 0) " (%.1f%%)".format(parchi.profitMarginPercent) else ""
    canvas.drawText("$sign$profitVal$marginText", 531f, bannerY + 28f, paint)

    // Optional Notes Section
    var currentY = sumTop + 160f
    if (parchi.notes.isNotBlank()) {
      paint.style = Paint.Style.FILL
      paint.textAlign = Paint.Align.LEFT
      paint.textSize = 9.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      paint.color = Color.rgb(90, 100, 90)
      canvas.drawText("विशेष टिप्पणी / Notes: ${parchi.notes}", 42f, currentY, paint)
      currentY += 24f
    }

    // SIGNATURE & STAMP BOX
    val sigTop = Math.max(currentY + 20f, 540f)

    // Farmer Sign Box
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    paint.color = Color.rgb(200, 210, 200)
    canvas.drawRoundRect(RectF(46f, sigTop, 260f, sigTop + 85f), 4f, 4f, paint)
    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(90, 100, 90)
    paint.textSize = 9f
    paint.textAlign = Paint.Align.CENTER
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    canvas.drawText("किसान के हस्ताक्षर / अंगूठा निशान", 153f, sigTop + 72f, paint)
    canvas.drawText("(Farmer Signature / Thumb)", 153f, sigTop + 82f, paint)

    // Trader / Mandi Stamp Box
    paint.style = Paint.Style.STROKE
    paint.color = Color.rgb(200, 210, 200)
    canvas.drawRoundRect(RectF(335f, sigTop, 549f, sigTop + 85f), 4f, 4f, paint)
    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(90, 100, 90)
    canvas.drawText("व्यापारी / आढ़तिया मोहर व हस्ताक्षर", 442f, sigTop + 72f, paint)
    canvas.drawText("(Authorized Mandi Signature & Stamp)", 442f, sigTop + 82f, paint)

    // Verification Watermark / Badge
    paint.style = Paint.Style.FILL
    paint.color = Color.rgb(240, 245, 238)
    canvas.drawRoundRect(RectF(180f, sigTop + 110f, 415f, sigTop + 140f), 15f, 15f, paint)
    paint.color = Color.rgb(27, 107, 56)
    paint.textSize = 9.5f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("✔ प्रमाणित कृषि गणना (Verified Agri Calculation)", 297.5f, sigTop + 128f, paint)

    // FOOTER
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 8.5f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = Color.rgb(130, 140, 130)
    canvas.drawText("यह डिजिटल पर्ची Kisan Parchi मोबाइल ऐप द्वारा बनाई गई है।", 297.5f, 792f, paint)
    canvas.drawText("Farmer Friendly Billing & Settlement • For Support & Queries Contact Mandi Office", 297.5f, 806f, paint)
  }

  fun sharePdf(context: Context, file: File, cropTitle: String) {
    try {
      val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "किसान पर्ची - $cropTitle (Kisan Parchi)")
        putExtra(
          Intent.EXTRA_TEXT,
          "किसान बिल पर्ची (Kisan Parchi Receipt)\nफसल: $cropTitle\nपीडीएफ संलग्न है।"
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(shareIntent, "पर्ची भेजें / Share Parchi"))
    } catch (e: Exception) {
      Toast.makeText(context, "पर्ची शेयर करने में त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  fun openPdf(context: Context, file: File) {
    try {
      val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val viewIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(viewIntent)
    } catch (_: Exception) {
      Toast.makeText(context, "कृपया PDF खोलने के लिए PDF रीडर इंस्टॉल करें", Toast.LENGTH_SHORT).show()
    }
  }

  fun printPdf(context: Context, file: File, jobName: String = "Kisan_Parchi") {
    try {
      val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
      if (printManager == null) {
        Toast.makeText(context, "प्रिंट सेवा उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
        return
      }

      val printAdapter = object : PrintDocumentAdapter() {
        override fun onLayout(
          oldAttributes: PrintAttributes?,
          newAttributes: PrintAttributes?,
          cancellationSignal: android.os.CancellationSignal?,
          callback: LayoutResultCallback?,
          extras: android.os.Bundle?
        ) {
          if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
          }
          val printDocInfo = android.print.PrintDocumentInfo.Builder(jobName)
            .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            .build()
          callback?.onLayoutFinished(printDocInfo, true)
        }

        override fun onWrite(
          pages: Array<out android.print.PageRange>?,
          destination: android.os.ParcelFileDescriptor?,
          cancellationSignal: android.os.CancellationSignal?,
          callback: WriteResultCallback?
        ) {
          if (destination == null) {
            callback?.onWriteFailed("No destination file")
            return
          }
          try {
            FileInputStream(file).use { input ->
              FileOutputStream(destination.fileDescriptor).use { output ->
                input.copyTo(output)
              }
            }
            callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
          } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
          }
        }
      }

      printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    } catch (e: Exception) {
      Toast.makeText(context, "प्रिंट करने में त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }
}
