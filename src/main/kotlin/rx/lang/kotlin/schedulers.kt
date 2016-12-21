package rx.lang.kotlin

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ahmad on 19/12 Dec/2016.
 */

val ioScheduler: Scheduler
get() = Schedulers.io()

val computationScheduler: Scheduler
    get() = Schedulers.computation()

val newThreadScheduler: Scheduler
    get() = Schedulers.newThread()

val singleScheduler: Scheduler
    get() = Schedulers.single()

val trampolineScheduler: Scheduler
    get() = Schedulers.trampoline()