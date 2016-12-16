package rx.lang.kotlin

import io.reactivex.functions.Consumer
import io.reactivex.subscribers.SerializedSubscriber
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.*

class FunctionSubscriber<T>() : Consumer<T> {
    override fun accept(t: T) {
        onCompletedFunctions.forEach { it() }
    }

    private val onCompletedFunctions = ArrayList<() -> Unit>()
    private val onErrorFunctions = ArrayList<(e: Throwable) -> Unit>()
    private val onNextFunctions = ArrayList<(value: T) -> Unit>()
    private val onStartFunctions = ArrayList<() -> Unit>()

    fun onStart() = onStartFunctions.forEach { it() }

    fun onCompleted(onCompletedFunction: () -> Unit): FunctionSubscriber<T> = copy { onCompletedFunctions.add(onCompletedFunction) }
    fun onError(onErrorFunction: (t: Throwable) -> Unit): FunctionSubscriber<T> = copy { onErrorFunctions.add(onErrorFunction) }
    fun onNext(onNextFunction: (t: T) -> Unit): FunctionSubscriber<T> = copy { onNextFunctions.add(onNextFunction) }
    fun onStart(onStartFunction : () -> Unit) : FunctionSubscriber<T> = copy { onStartFunctions.add(onStartFunction) }

    private fun copy(block: FunctionSubscriber<T>.() -> Unit): FunctionSubscriber<T> {
        val newSubscriber = FunctionSubscriber<T>()
        newSubscriber.onCompletedFunctions.addAll(onCompletedFunctions)
        newSubscriber.onErrorFunctions.addAll(onErrorFunctions)
        newSubscriber.onNextFunctions.addAll(onNextFunctions)
        newSubscriber.onStartFunctions.addAll(onStartFunctions)

        newSubscriber.block()

        return newSubscriber
    }
}

class FunctionSingleSubscriber<T>() : Consumer<T>{
    override fun accept(t: T) {
        onSuccessFunctions.forEach { it(t) }
    }

    private val onSuccessFunctions = ArrayList<(value: T) -> Unit>()
    private val onErrorFunctions = ArrayList<(e: Throwable) -> Unit>()

    fun onSuccess(onSuccessFunction: (t: T) -> Unit): FunctionSingleSubscriber<T> = copy { onSuccessFunctions.add(onSuccessFunction) }
    fun onError(onErrorFunction: (e: Throwable) -> Unit): FunctionSingleSubscriber<T> = copy { onErrorFunctions.add(onErrorFunction) }

    private fun copy(block: FunctionSingleSubscriber<T>.() -> Unit): FunctionSingleSubscriber<T> {
        val newSubscriber = FunctionSingleSubscriber<T>()
        newSubscriber.onSuccessFunctions.addAll(onSuccessFunctions)
        newSubscriber.onErrorFunctions.addAll(onErrorFunctions)

        newSubscriber.block()

        return newSubscriber
    }
}

class FunctionSubscriberModifier<T>(init: FunctionSubscriber<T> = subscriber()) {
    var subscriber: FunctionSubscriber<T> = init
        private set

    fun onCompleted(onCompletedFunction: () -> Unit) : Unit { subscriber = subscriber.onCompleted(onCompletedFunction) }
    fun onError(onErrorFunction: (t : Throwable) -> Unit) : Unit { subscriber = subscriber.onError(onErrorFunction) }
    fun onNext(onNextFunction: (t : T) -> Unit) : Unit { subscriber = subscriber.onNext(onNextFunction) }
    fun onStart(onStartFunction : () -> Unit) : Unit { subscriber = subscriber.onStart(onStartFunction) }
}

class FunctionSingleSubscriberModifier<T>(init: FunctionSingleSubscriber<T> = singleSubscriber()) {
    var subscriber: FunctionSingleSubscriber<T> = init
        private set

    fun onSuccess(onSuccessFunction: (t: T) -> Unit): Unit { subscriber = subscriber.onSuccess(onSuccessFunction) }
    fun onError(onErrorFunction: (r: Throwable) -> Unit): Unit {subscriber = subscriber.onError(onErrorFunction) }
}

fun <T> subscriber(): FunctionSubscriber<T> = FunctionSubscriber()
fun <T> singleSubscriber(): FunctionSingleSubscriber<T> = FunctionSingleSubscriber()
fun <T> Subscriber<T>.synchronized(): Subscriber<T> = SerializedSubscriber(this)
//fun Subscriber<*>.add(action: () -> Unit) = thi(Subscriptions.create(action))
