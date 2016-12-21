package rx.lang.kotlin

import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.Disposable
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

fun <T> single(body: (s: SingleEmitter<in T>) -> Unit): Single<T> = Single.create(body)
fun <T> singleOf(value: T): Single<T> = Single.just(value)
fun <T> Future<T>.toSingle(): Single<T> = Single.fromFuture(this)
fun <T> Callable<T>.toSingle(): Single<T> = Single.fromCallable { this.call() }
fun <T> Throwable.toSingle(): Single<T> = Single.error(this)
fun <T> timer(delay: Long, timeUnit: TimeUnit, body: SingleEmitter<in T>.() -> Unit) =
        Single.create<T> { it.body() }
                .delay(delay, timeUnit)!!
/**
 * Subscribe with a subscriber that is configured inside body
 */
inline fun <T> Single<T>.subscribeWith(body: FunctionSingleSubscriberModifier<T>.() -> Unit): Disposable? {
    val modifier = FunctionSingleSubscriberModifier(singleSubscriber<T>())
    modifier.body()
    return subscribe(modifier.subscriber)
}
