package rx.lang.kotlin

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ahmad on 19/12 Dec/2016.
 */

val io : Scheduler
get() = Schedulers.io()

val computation : Scheduler
    get() = Schedulers.computation()

val newThread : Scheduler
    get() = Schedulers.newThread()

val single : Scheduler
    get() = Schedulers.single()

val trampoline : Scheduler
    get() = Schedulers.trampoline()