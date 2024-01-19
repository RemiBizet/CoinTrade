package com.example.coinTrade

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

// Vue affichant la valeur actuelle du bitcoin
class BitcoinPriceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    @SuppressLint("SetTextI18n")
    fun getBitcoinPrice() {
        val url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val bitcoinPrice = response.getJSONObject("bitcoin").getDouble("usd").toString()
                text = bitcoinPrice
            },
            {
                text = "Echec de l'obtention du prix du bitcoin"
            }
        )
        Volley.newRequestQueue(context).add(request)
    }
}