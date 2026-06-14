package com.example.budget_buddie_sa.utils

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.example.budget_buddie_sa.viewmodel.ReportData
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class to generate a PDF report for spending.
 */
class PdfReportGenerator(private val context: Context) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    fun generateReport(reportData: ReportData): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var yPos = 50f
        val margin = 50f
        val contentWidth = pageInfo.pageWidth - (2 * margin)

        // 1. Header
        paint.color = Color.parseColor("#2D1B6B")
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Budget Buddie SA", margin, yPos, paint)

        yPos += 30f
        paint.color = Color.parseColor("#7C3AED")
        paint.textSize = 18f
        canvas.drawText("Spending Report", margin, yPos, paint)

        yPos += 25f
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val period = "Period: ${dateFormat.format(Date(reportData.startDate))} - ${dateFormat.format(Date(reportData.endDate))}"
        canvas.drawText(period, margin, yPos, paint)

        yPos += 40f
        paint.strokeWidth = 1f
        paint.color = Color.LTGRAY
        canvas.drawLine(margin, yPos, margin + contentWidth, yPos, paint)

        // 2. Summary Statistics
        yPos += 30f
        paint.color = Color.parseColor("#2D1B6B")
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Summary Statistics", margin, yPos, paint)

        yPos += 25f
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        
        drawSummaryRow(canvas, paint, "Total Spending:", currencyFormat.format(reportData.totalSpending), margin, yPos)
        yPos += 20f
        drawSummaryRow(canvas, paint, "Total Categories Used:", reportData.totalCategoriesUsed.toString(), margin, yPos)
        yPos += 20f
        drawSummaryRow(canvas, paint, "Highest Category:", reportData.highestSpendingCategory, margin, yPos)
        yPos += 20f
        drawSummaryRow(canvas, paint, "Lowest Category:", reportData.lowestSpendingCategory, margin, yPos)
        yPos += 20f
        drawSummaryRow(canvas, paint, "Expenses Recorded:", reportData.numberOfExpensesRecorded.toString(), margin, yPos)
        yPos += 20f
        drawSummaryRow(canvas, paint, "Average Daily Spending:", currencyFormat.format(reportData.averageDailySpending), margin, yPos)

        // 3. Category Breakdown
        yPos += 40f
        paint.color = Color.parseColor("#2D1B6B")
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Category Breakdown", margin, yPos, paint)

        yPos += 25f
        paint.textSize = 12f
        paint.color = Color.BLACK
        reportData.categoryBreakdown.forEach { (category, amount) ->
            drawSummaryRow(canvas, paint, category.name, currencyFormat.format(amount), margin, yPos)
            yPos += 20f
            if (yPos > 750f) { // Simple page break handling for breakdown
                // In a real app we'd start a new page here
            }
        }

        // 4. Expense List (Top 10 most recent in range for brevity in PDF)
        yPos += 20f
        paint.color = Color.parseColor("#2D1B6B")
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Recent Expenses", margin, yPos, paint)

        yPos += 25f
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.BLACK
        
        reportData.expenseList.take(15).forEach { expense ->
            val dateStr = dateFormat.format(Date(expense.date))
            val text = "$dateStr - ${expense.description}"
            canvas.drawText(text, margin, yPos, paint)
            canvas.drawText(currencyFormat.format(expense.amount), margin + contentWidth - 60f, yPos, paint)
            yPos += 18f
        }

        // 5. Footer
        yPos = 810f
        paint.color = Color.GRAY
        paint.textSize = 10f
        val generatedOn = "Generated on: ${dateTimeFormat.format(Date())}"
        canvas.drawText(generatedOn, margin, yPos, paint)

        pdfDocument.finishPage(page)

        // Save the PDF
        val fileName = "BudgetReport_${SimpleDateFormat("MMM_yyyy", Locale.getDefault()).format(Date(reportData.startDate))}.pdf"
        val file = File(context.cacheDir, fileName)
        
        return try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    private fun drawSummaryRow(canvas: Canvas, paint: Paint, label: String, value: String, x: Float, y: Float) {
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(label, x, y, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val textWidth = paint.measureText(value)
        canvas.drawText(value, 595 - 50 - textWidth, y, paint)
    }
}
