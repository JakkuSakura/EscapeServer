package messages

import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}

class MessageQueue() {
    private val queues = new ConcurrentHashMap[Class[_], LinkedBlockingQueue[Any]]()


    def getQueue[T](clazz: Class[T]): LinkedBlockingQueue[T] = {
        var queue = queues.get(clazz)
        if (queue == null) {
            queue = new LinkedBlockingQueue[Any]()
            queues.put(clazz, queue)
        }
        queue.asInstanceOf[LinkedBlockingQueue[T]]
    }

    def broadcast[T](msg: T): Unit = {
        getQueue(msg.getClass).asInstanceOf[LinkedBlockingQueue[Any]].put(msg)
    }

    def getMessage[T](clazz: Class[T]) : T ={
        getQueue(clazz).poll()
    }
}
