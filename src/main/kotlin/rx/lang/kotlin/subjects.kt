package rx.lang.kotlin

//import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.*


fun <T> BehaviorSubject() : BehaviorSubject<T> = BehaviorSubject.create()
fun <T> BehaviorSubject(default : T) : BehaviorSubject<T> = BehaviorSubject.createDefault(default)
fun <T> AsyncSubject() : AsyncSubject<T> = AsyncSubject.create()
fun <T> PublishSubject() : PublishSubject<T> = PublishSubject.create()
fun <T> ReplaySubject(capacity : Int = 16) : ReplaySubject<T> = ReplaySubject.create(capacity)

fun <T> Subject<T>.synchronized() : Subject<T> = this.synchronized()
//fun <T> TestSubject(scheduler: TestScheduler) : TestSubject<T> = TestSubject.create(scheduler)
