import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.funktionale.either.Either
import org.junit.Test
import rx.lang.kotlin.*
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
            observableCounter = timer(1, unit, trampolineScheduler)
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

    data class End<out T>(val lastValue: T)

    @Test
    fun test5() {
        for (i in 1..3) {
            println("Test $i\n\n")
            val schedulers = listOf(
                    ioScheduler to "IO",
                    computationScheduler to "Computation",
                    newThreadScheduler to "NewThread",
                    singleScheduler to "Single"
//                trampolineScheduler to "Trampoline"
            )
            val resultList = mutableMapOf<Long, String>()
            val range = 0L..Math.pow(10.0, 10.0).toLong()
            println("range is $range")

            for ((scheduler, schedulerName) in schedulers) {
                var lastValue: Long = 0
                var shouldStop = false
                timer<Unit>(5000, TimeUnit.MILLISECONDS) {
                    println("should stop now")
                    shouldStop = true
                }
                val subscriber = Flowable.create<Either<Long, End<Long>>>({ emitter ->
                    range.forEach {
                        if (shouldStop) {
                            emitter.onNext(Either.Right(End(it)))
                            emitter.onComplete()
                        } else {
                            emitter.onNext(Either.Left(it))
                        }
                    }
                }, BackpressureStrategy.DROP)
                        .subscribeOn(scheduler)
                        .onBackpressureDrop { println("dropped $it") }
                        .filter { it.isRight() }
                        .map { it.component1()!! }
                        .blockingSubscribe({
                            lastValue = it
                        }, {}, {
                            println("Finished $schedulerName with ${lastValue}")
                            resultList.put(lastValue, schedulerName)
                        })
            }
            println(resultList.toSortedMap().toString())
        }
        Thread.sleep(7000)
    }

    @Test fun parallelExecution() {
//        val clock = Clock()
        val o1 = Observable.range(0, 10)
                .delay(1, TimeUnit.SECONDS, computationScheduler)
        val o2 = Observable.range(5, 5)
                .map { "Hello from o2 : $it" }
                .delay(2, TimeUnit.SECONDS, computationScheduler)
//        clock.start()
//        clock.map { "Clock is ticking : $it" }
//                .subscribe(::println)
        observableZip(o1, o2) { i, s -> "$s , and from the zipper we have int $i" }
                .subscribe(::println)
//        Thread.sleep(12000)
    }

}
