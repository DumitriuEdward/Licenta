package com.example.petoibittlecontrol.util

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors

class AppRxSchedulers : RxSchedulers {

    override fun androidUI(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun network(): Scheduler {
        return NETWORK_SCHEDULER
    }

    override fun immediate(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun background(): Scheduler {
        return BACKGROUND_SCHEDULER
    }

    companion object {

        private val NETWORK_EXECUTOR = Executors.newCachedThreadPool()
        private val BACKGROUND_EXECUTOR = Executors.newCachedThreadPool()
        private val NETWORK_SCHEDULER = Schedulers.from(NETWORK_EXECUTOR)
        private val BACKGROUND_SCHEDULER = Schedulers.from(BACKGROUND_EXECUTOR)
        }
}