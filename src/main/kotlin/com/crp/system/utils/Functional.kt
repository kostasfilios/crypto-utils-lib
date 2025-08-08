package com.crp.system.utils


/**
 *
 * @return create a chain of curried type functions e.x sum(a: Int, b: Int): Int this is not curried functions
 * sum(a: Int): (Int) -> Int is the same curried function
 */
inline fun <reified A, reified B> A.pipeForward(crossinline g: (A) -> B): B {
    return g.invoke(this)
}

/**
 *
 * @return create a chain of curried type functions e.x sum(a: Int, b: Int): Int this is not curried functions
 * sum(a: Int): (Int) -> Int is the same curried function
 */
inline fun <reified A, reified B, reified C> ((A) -> B).curriedChain(crossinline g: (B) -> C): (A) -> C {
    return { it -> g.invoke(this.invoke(it)) }
}


/**
 * Pair(condition function, curriedFunction)
 * curriedFunction is going to triggered when condition is true
 * @return a curried function A -> C
 */
inline fun <reified A, reified B, reified C> ((A) -> B).pairConditionedCurriedChain(conditions: List<Pair<((B) -> Boolean), (B) -> C>>): (A) -> C? {
    return { a ->
        val b = this.invoke(a)
        var c: C? = null
        conditions.forEach {
            val (condition, func) = it
            if (condition.invoke(b)) {
                c = func.invoke(b)
            }
        }
        c
    }
}

/**
 * Pair(condition function, curriedFunction)
 * curriedFunction is going to triggered when condition is true
 * @return a curried function A -> C
 */
inline fun <reified A, reified B, reified C> ((A) -> B).ifElseCurriedChain(vararg conditions: Pair<((B) -> Boolean), (B) -> C>, crossinline `else`: (B) -> C): (A) -> C {
    return { a ->
        val b = this.invoke(a)
        var c: C? = null
        for (pair in conditions) {
            val (condition, func) = pair
            if (condition.invoke(b)) {
                c = func.invoke(b)
                break
            }
        }
        c ?: `else`.invoke(b)
    }
}


/**
 * Pair(condition function, curriedFunction)
 * curriedFunction is going to triggered when condition is true
 * @return a curried function A -> C
 */
inline fun <reified A, reified B> A.pipeForwardConditionedChain(conditions: List<Pair<((A) -> Boolean), (A) -> B>>): B? {
    var b: B? = null
    conditions.forEach {
        val (condition, func) = it
        if (condition.invoke(this)) {
            b = func.invoke(this)
        }
    }
    return b
}

inline fun <reified A, reified B> ifElse(vararg conditions: Pair<((A) -> Boolean), (A) -> B>, crossinline `else`: (A) -> B): (A) -> B {
    return { a ->
        var b: B? = null

        for (pair in conditions) {
            val (condition, func) = pair
            if (condition.invoke(a)) {
                b = func.invoke(a)
                break
            }
        }
        b ?: `else`.invoke(a)
    }
}


/**
 * @return compose two functions serial computing
 */
inline fun <reified A, reified B, reified C> ((A) -> B).compose(crossinline g: (A) -> C): (A) -> Pair<B, C> {
    return { a ->
        val b = this.invoke(a)
        val c = g.invoke(a)
        Pair(b, c)
    }
}

/**
 * @return compose two functions serial computing and return first response as curried function
 */
inline fun <reified A, reified B, reified C> ((A) -> B).curriedChainFirstCompose(crossinline g: (B) -> C, crossinline f: (B) -> Unit): (A) -> C {
    return { it ->
        val b = this.invoke(it)
        f.invoke(b)
        g.invoke(b)
    }
}

/**
 * @return compose two functions serial computing and discard return
 */
inline fun <reified A, reified B, reified C, reified D> ((A) -> B).composeWithUnusedResult(crossinline g: (B) -> C, crossinline f: (B) -> D): (A) -> Unit {
    return { it ->
        val b = this.invoke(it)
        g.invoke(b)
        f.invoke(b)
    }
}

/**
 * @return itself
 */
inline fun <reified A> A.noOp(): () -> A {
    return { this }
}

/**
 * @return itself
 */
inline fun <reified A, reified B> B.noOpCurried(): (A) -> B {
    return { this }
}

/**
 * @return itself
 */
inline fun <reified A> A.boolCheck(crossinline condition: (A) -> Boolean): (A) -> Boolean {
    return { condition(it) }
}

typealias λ<A, B> = (A) -> B

/**
 * @return itself by λ
 */
inline fun <reified A, reified B> λ(crossinline f: (A) -> B): (A) -> B {
    return { f.invoke(it) }
}

//////////////////////////// Functional Extensions ////////////////////////////

inline fun <reified A> Boolean.mySelf(): (A) -> Boolean {
    return { this }
}

inline fun <reified A> Boolean.myOpposite(): (A) -> Boolean {
    return { !this }

}