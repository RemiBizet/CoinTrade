package com.example.coinTrade

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


class BitcoinPriceView(context: Context) : androidx.appcompat.widget.AppCompatTextView(context) {

    fun getBitcoinPrice() {
        val url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val bitcoinPrice = response.getJSONObject("bitcoin").getDouble("usd")
                text = bitcoinPrice.toString()
            },
            {
                text = "Failed to fetch Bitcoin price"
            }
        )

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(context).add(request)
    }
}