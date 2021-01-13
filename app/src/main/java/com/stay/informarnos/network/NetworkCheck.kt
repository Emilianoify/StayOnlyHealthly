package com.stay.informarnos.network

import java.io.IOException

class NetworkCheck() {

   /* fun isOnline(context: Context):Boolean{

            val stringRequest = StringRequest(Request.Method.GET, "https://www.google.com",
                { _ ->
                    Log.d("INTERNET ON", "Hay internet")

                },
                {  })
            Volley.newRequestQueue(context).add(stringRequest)
        return
    }*/

  fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

}