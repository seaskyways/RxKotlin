import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import rx.lang.kotlin.computation
import rx.lang.kotlin.io
import rx.lang.kotlin.observable
import rx.lang.kotlin.trampoline
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class Test2 {
    val counter = AtomicLong(0)
    @Test
    fun main() {
        val mObservable1 = observable<String> {
            for (i in 0..5) {
                it.onNext("Hello for the $i time!")
            }
            it.onComplete()
        }
                .publish()


        (1 until 3).forEach { i ->
            mObservable1
                    .sample(1, TimeUnit.NANOSECONDS)
                    .map { "from subscriber $i : $it" }.
                    subscribe(
                            ::println,
                            Throwable::printStackTrace,
                            {
                                println("Just completed stream !")
                            }
                    )
        }
        mObservable1.connect()
    }

    @Test
    fun test2() {
//        println("finished subscribe")
        Observable.fromCallable { counter.andIncrement }
                .repeat(10001)
                .delay(1, TimeUnit.MILLISECONDS, Schedulers.trampoline())
                .buffer(1000)
//                .sample
                .subscribe {
                    println("Seconds consumed : ${it[0]}")
                }
    }

    class SetOnceVariable<T>(default: T) {
        var isSet = false; private set
        var value: T = default
            set(value) {
                if (!isSet) {
                    field = value
                    isSet = true
                }
            }
            get
    }

    class Clock(val unit: TimeUnit = TimeUnit.SECONDS) : Observable<Long>() {
        fun start() {
            observableCounter = timer(1, unit, trampoline)
                    .repeat()
                    .map { mCounter.andIncrement }
        }

        private lateinit var observableCounter : Observable<Long>
        private val mCounter = AtomicLong(0)
        override fun subscribeActual(observer: Observer<in Long>) {
            observableCounter.subscribeWith(observer)
        }
    }

    class CountDownTimer(clock: Clock, maxTime: Long, onTick: (remainingTicks: Int) -> Unit) {
        init {
            var initalTime = SetOnceVariable(-1L)
            clock.doOnNext { initalTime.value = it }
                    .map { maxTime - (it + initalTime.value) }
                    .filter { it >= 0 }
                    .subscribe {
                        onTick(it.toInt())
                    }
        }
    }

    @Test
    fun test3() {
        var _subscriber = 0
//        var shouldStop = false

        val counterObservable = Clock()
        counterObservable.start()
        counterObservable.subscribe {
            println(if (it % 2 == 0L) "Tick" else "Tock")
        }
        CountDownTimer(counterObservable, 30) {
            println("Ticker 3 : ticks remaining are $it")
        }
        Thread.sleep(7000)
    }
}
