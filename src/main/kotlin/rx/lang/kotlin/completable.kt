package rx.lang.kotlin

//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader
import io.reactivex.Completable
import java.util.concurrent.Callable
import java.util.concurrent.Future

fun <T> completableOf(f: Function0<T>): Completable = Completable.fromAction { f.invoke() }
fun <T> Callable<T>.toCompletable(): Completable = Completable.fromCallable { this.call() }
fun <T> Future<T>.toCompletable(): Completable = Completable.fromFuture(this)
//fun XsiNilLoader.Single.toCompletable(): Completable = Completable.fromSingle(this)