package messages

import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}

class MessageQueue() {
    private val queues = new ConcurrentHashMap[Object, LinkedBlockingQueue[Object]]()


    def getQueue[T](category: Object): LinkedBlockingQueue[T] = {
        var queue = queues.get(category)
        if (queue == null) {
            queue = new LinkedBlockingQueue[Object]()
            println("New queue for " + category)
            queues.put(category, queue)
        }
        queue.asInstanceOf[LinkedBlockingQueue[T]]
    }

    def broadcast[T](category: Class[_], msg: T): Unit = {
        println("Broadcast " + msg + " in " + category.toString)
        getQueue[T](category).put(msg)
    }

    def broadcast[T](msg: T): Unit = {
        println("Broadcast " + msg)
        getQueue(msg.getClass).put(msg)
    }

    def getMessage[T](clazz: Class[T]) : T ={
        val result = getQueue(clazz).poll()
        println("Get message " + result)
        result.asInstanceOf[T]
    }

    def processAll[T](clazz: Class[T], process: Object => Unit): Unit = {
        val queue = getQueue(clazz)
        while (!queue.isEmpty) {
            process(queue.poll())
        }
    }
}
