package com.echosmart.flashlabs.data.repository

import android.content.Context
import com.echosmart.flashlabs.data.model.XGecuChipDevice
import com.echosmart.flashlabs.database.XmlChipParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChipRepository {

    private var cachedList: List<XGecuChipDevice>? = null

    suspend fun searchChips(context: Context, query: String, maxResults: Int = 100): List<XGecuChipDevice> {
        return withContext(Dispatchers.IO) {
            XmlChipParser.parseDatabaseFromAssets(context, "infoic.xml", query, maxResults)
        }
    }

    suspend fun getInitialPopularChips(context: Context): List<XGecuChipDevice> {
        if (cachedList != null) return cachedList!!
        val popular = searchChips(context, "25Q64", 30)
        cachedList = popular
        return popular
    }
}
