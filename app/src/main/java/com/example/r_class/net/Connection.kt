package com.example.r_class.net

import kotlinx.coroutines.*
import java.io.*
import java.lang.Exception
import java.net.Socket

class Connection {
    suspend fun get(message : String) : String = withContext(Dispatchers.IO) {
        var readline = ""
        while (Pools.socket == null){
            try {
                Pools.socket = Socket("119.23.225.4", 8100)
                PrintWriter(
                    OutputStreamWriter(Pools.socket?.getOutputStream(), "UTF-8"),
                    true
                ).println(message)
                val br = BufferedReader(InputStreamReader(Pools.socket?.getInputStream(), "UTF-8"))
                while (true) {
                    readline = br.readLine()
                    if (readline != null) {
                        Pools.socket = null
                        return@withContext readline
                    }
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
        return@withContext ""
    }
}
