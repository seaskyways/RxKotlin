import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import rx.lang.kotlin.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import kotlin.comparisons.compareBy

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

        private lateinit var observableCounter: Observable<Long>
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

    @Test
    fun test4() {
        val x = ""
        println("you just typed : $x")
    }

    @Test
    fun test5() {
        for (i in 1..3){
            println("Test $i\n\n")
            val schedulers = listOf(
                    io to "IO",
                    computation to "Computation",
                    newThread to "NewThread",
                    single to "Single"
//                trampoline to "Trampoline"
            )
            val resultList = mutableMapOf<Long, String>()
            val range = 0L..Math.pow(10.0, 10.0).toLong()
            println("range is $range")

            for ((scheduler, schedulerName) in schedulers) {
                var lastValue: Long = 0

                val subscriber = Flowable.create<Long>({
                    for (i in range) {
                        it.onNext(i)
                    }
                    it.onComplete()
                }, BackpressureStrategy.DROP)
                        .subscribeOn(scheduler)
                        .onBackpressureDrop { println("dropped $it") }
                        .subscribe({ lastValue = it })
                Thread.sleep(5000)
                println("Finished $schedulerName with ${lastValue}")
                resultList.put(lastValue, schedulerName)
                subscriber.dispose()
            }

            println(resultList.toSortedMap().toString())
        }
    }
}
