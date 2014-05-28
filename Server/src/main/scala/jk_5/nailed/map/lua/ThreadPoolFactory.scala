package jk_5.nailed.map.lua

import java.util.concurrent.{ThreadFactory, Executors}
import java.util.concurrent.atomic.AtomicInteger

/**
 * No description given
 *
 * @author jk-5
 */
object ThreadPoolFactory {
  def create(threads: Int) = Executors.newScheduledThreadPool(threads, new ThreadFactory() {
      private val baseName = "Nailed-LuaVM-"
      private val threadNumber = new AtomicInteger(1)
      private val group = System.getSecurityManager match {
        case null => Thread.currentThread().getThreadGroup
        case s => s.getThreadGroup
      }
      def newThread(r: Runnable) = {
        val thread = new Thread(group, r, baseName + threadNumber.getAndIncrement)
        if(!thread.isDaemon) thread.setDaemon(true)
        if(thread.getPriority != Thread.MIN_PRIORITY) thread.setPriority(Thread.MIN_PRIORITY)
        thread
      }
    })
}
