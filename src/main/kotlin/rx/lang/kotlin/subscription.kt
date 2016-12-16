package rx.lang.kotlin

import org.reactivestreams.Subscription

/**
 * subscription += observable.subscribe{}
 */
//operator fun CompositeSubscription.plusAssign(subscription: Subscription) : Unit = add(subscription)

/**
 * Add the subscription to a CompositeSubscription.
 * @param compositeSubscription CompositeSubscription to add this subscription to
 * @return this instance
 */
//fun Subscription.addTo(compositeSubscription: CompositeSubscription) : Subscription {
//    compositeSubscription.add(this)
//    return this
//}