package com.tinashe.hymnal.data.model.response

import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.data.model.constants.Status.ERROR
import com.tinashe.hymnal.data.model.constants.Status.LOADING
import com.tinashe.hymnal.data.model.constants.Status.SUCCESS

class Resource<out T> private constructor(
    val status: Status = LOADING,
    val data: T?,
    val error: Throwable?
) {

    val isSuccessFul: Boolean get() = data != null

    companion object {

        fun <T> success(data: T): Resource<T> {
            return Resource(SUCCESS, data, null)
        }

        fun <T> error(error: Throwable): Resource<T> {
            return Resource(ERROR, null, error)
        }

        fun <T> loading(): Resource<T> {
            return Resource(LOADING, null, null)
        }
    }
}
