package io.github.ReadyMadeProgrammer.CircularArray

import java.util.*

open class CircularArray<E> constructor(val capacity: Int, private val array: Array<E?>): MutableList<E>, Deque<E> {
    private var start = 0
    private var length = 0

    companion object{
        inline fun <reified T> create(capacity: Int): CircularArray<T>{
            return CircularArray(capacity,Array<T?>(capacity,{null}))
        }
    }

    override val size: Int
        get() = length
    private fun inBounder(index: Int) = (index+capacity)%capacity
    private fun checkBound(index: Int)
        = index in 0 until length
    private fun checkBoundAndThrow(index: Int){
        if(!checkBound(index)) throw IndexOutOfBoundsException("length: $length, access: $index")
    }
    override fun add(element: E): Boolean {
        if(length>=capacity) return false
        length++
        array[inBounder(start+length-1)] = element
        return true
    }

    override fun contains(element: E): Boolean{
        for(i in 0 until length) if(this[i]==element) return true
        return false
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        val col = HashSet<E>(elements)
        for(i in 0 until length) col.remove(this[i])
        return col.isEmpty()
    }

    override fun get(index: Int): E{
        checkBoundAndThrow(index)
        return array[inBounder(start+index)]!!
    }

    override fun indexOf(element: E): Int{
        for(i in 0 until length) if(this[i]==element) return i
        return -1
    }

    override fun isEmpty(): Boolean
        = length==0

    override fun iterator(): MutableIterator<E>
        = object: MutableIterator<E>{
        private var index = 0
        override fun hasNext(): Boolean
            = index<length

        override fun next(): E
            = this@CircularArray[index++]

        override fun remove() {
            removeAt(index-1)
            index--
        }
    }

    override fun lastIndexOf(element: E): Int {
        for(i in length-1 downTo 0) if(this[i]==element) return i
        return -1
    }

    override fun add(index: Int, element: E) {
        addAll(index,listOf(element))
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val list = ArrayList<E>(elements)
        if(length+elements.size>capacity) return false
        length+=elements.size
        for(i in length-elements.size-1 downTo index)this[i+elements.size]=this[i]
        for(i in 0 until elements.size) this[index+i] = list[i]
        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        if(length+elements.size>capacity) return false
        elements.forEach{add(it)}
        return true
    }

    override fun clear() {
        start = 0
        length = 0
    }

    override fun listIterator(): MutableListIterator<E>
        =listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<E>
        =object: MutableListIterator<E>{
        private var ind = index
        override fun hasPrevious(): Boolean
                = this.ind >0

        override fun nextIndex(): Int
                = this.ind

        override fun previous(): E
                =this@CircularArray[--this.ind]!!

        override fun previousIndex(): Int
                = this.ind -1

        override fun add(element: E) {
            this@CircularArray.add(ind,element)
            this.ind++
        }

        override fun hasNext(): Boolean
                = this.ind <length

        override fun next(): E
                =this@CircularArray[this.ind++]!!

        override fun remove() {
            this@CircularArray.removeAt(this.ind -1)
            this.ind--
        }

        override fun set(element: E) {
            this@CircularArray[this.ind -1] = element
        }
    }

    override fun remove(element: E): Boolean {
        val position = indexOf(element)
        if(position == -1) return false
        removeAt(position)
        return true
    }

    override fun remove(): E {
        if(length==0) throw NoSuchElementException()
        length--
        val returnValue = this[0]
        start = inBounder(start+1)
        return returnValue
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        var result = false
        elements.forEach{while(remove(it))result = true}
        return result
    }

    override fun removeAt(index: Int): E {
        checkBoundAndThrow(index)
        val returnValue = this[index]
        for(i in index until length-1)this[i] = this[i+1]
        length--
        return returnValue
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val iterator = this.iterator()
        var removed = false
        while(iterator.hasNext()){
            val datum = iterator.next()
            if(!elements.contains(datum)){
                iterator.remove()
                removed = true
            }
        }
        return removed
    }

    override fun set(index: Int, element: E): E {
        checkBoundAndThrow(index)
        val returnValue = this[index]
        array[inBounder(start+index)] = element
        return returnValue
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        checkBoundAndThrow(fromIndex)
        checkBoundAndThrow(toIndex)
        val returnValue = ArrayList<E>(toIndex-fromIndex)
        for(i in fromIndex until toIndex)returnValue.add(this[i])
        return returnValue
    }

    override fun peekLast(): E? {
        if(length==0)return null
        return this[length-1]
    }

    override fun element(): E
        = first

    override fun push(e: E)
        = addFirst(e)


    override fun getLast(): E {
        if(length==0)throw NoSuchElementException()
        return this[length-1]
    }

    override fun addLast(e: E) {
        if(length>=capacity) throw IllegalStateException()
        length++
        this[length-1]=e
    }

    override fun addFirst(e: E) {
        if(length>=capacity) throw IllegalStateException()
        length++
        start = inBounder(start-1)
        this[0]=e
    }

    override fun offer(e: E): Boolean
        =offerLast(e)

    override fun peek(): E?
        =peekFirst()

    override fun offerLast(e: E): Boolean {
        if(length>=capacity) throw IllegalStateException()
        return add(e)
    }

    override fun removeFirst(): E {
        if(length==0) throw NoSuchElementException()
        val returnValue = this[0]
        start= inBounder(start+1)
        length--
        return returnValue
    }

    override fun getFirst(): E {
        if(length==0) throw NoSuchElementException()
        return this[0]
    }

    override fun removeLastOccurrence(o: Any?): Boolean {
        val index = lastIndexOf(o as E)
        if(index==-1) return false
        removeAt(index)
        return true
    }

    override fun peekFirst(): E? {
        if(length==0) return null
        return this[0]
    }

    override fun removeLast(): E{
        if(length==0) throw NoSuchElementException()
        val returnValue = last
        length--
        return returnValue
    }

    override fun offerFirst(e: E): Boolean {
        if(length>=capacity) return false
        length++
        start = inBounder(start-1)
        this[0] = e
        return true
    }

    override fun pollFirst(): E? {
        if(length==0) return null
        return removeFirst()
    }

    override fun pollLast(): E? {
        if(length==0) return null
        return removeLast()
    }

    override fun pop(): E
        =removeFirst()

    override fun removeFirstOccurrence(o: Any?): Boolean {
        val index = indexOf(o as E)
        if(index==-1) return false
        removeAt(index)
        return true
    }

    override fun poll(): E?
        =pollFirst()

    override fun descendingIterator(): MutableIterator<E>
        = object:MutableIterator<E>{
        private var index = length-1
        override fun hasNext(): Boolean
            = index>=0

        override fun next(): E
            =this@CircularArray[index--]

        override fun remove() {
            removeAt(index+1)
        }
    }
}