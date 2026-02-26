package org.ethereum.lists.chains.https

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ethereum.lists.chains.model.Chain

fun getChains(okhttpClient: OkHttpClient = OkHttpClient()): List<Chain>? {
    val request = Request.Builder()
        .url("https://chainid.network/chains.json")
        .build()

    val listMyData = Types.newParameterizedType(MutableList::class.java, Chain::class.java)
    val adapter: JsonAdapter<List<Chain>> = Moshi.Builder().build().adapter(listMyData)

    val response = okhttpClient.newCall(request).execute()
    return response.body?.let { adapter.fromJson(it.source()) }
}