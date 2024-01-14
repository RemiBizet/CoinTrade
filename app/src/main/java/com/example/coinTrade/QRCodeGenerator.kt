package com.example.coinTrade

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import android.graphics.Bitmap
import android.graphics.Color

class QRCodeGenerator {

    @Throws(WriterException::class)
    fun generateQRCode(content: String, width: Int, height: Int): Bitmap {
        val bitMatrix: BitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}