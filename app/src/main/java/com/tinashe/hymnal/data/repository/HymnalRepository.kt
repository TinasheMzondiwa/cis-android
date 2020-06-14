package com.tinashe.hymnal.data.repository

import android.content.Context
import com.tinashe.hymnal.data.db.dao.HymnalsDao
import com.tinashe.hymnal.data.db.dao.HymnsDao
import timber.log.Timber

class HymnalRepository(
    private val context: Context,
    private val hymnalsDao: HymnalsDao,
    private val hymnsDao: HymnsDao
) {

    fun act() {
        Timber.d("This is ${javaClass.name} act")
    }
}