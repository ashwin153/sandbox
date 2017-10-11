package discovery

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache._

import java.io.Closeable
import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.util.{Failure, Random, Success, Try}

/**
 *
 * @param instances
 * @param clients
 * @param builder
 * @tparam T
 */
case class Cluster[T](
  instances: PathChildrenCache,
  clients: mutable.Map[String, T],
  builder: Address => T
) extends PathChildrenCacheListener with Closeable {

  // Setup the ZooKeeper cache.
  this.instances.getListenable.addListener(this)
  this.instances.start()

  override def close(): Unit = {
    // Avoid race by closing the cache first.
    this.instances.close()
    this.clients.values foreach {
      case x: Closeable => x.close()
      case _ =>
    }
  }

  override def childEvent(curator: CuratorFramework, event: PathChildrenCacheEvent): Unit =
    event.getType match {
      case PathChildrenCacheEvent.Type.CHILD_ADDED | PathChildrenCacheEvent.Type.CHILD_UPDATED =>
        this.clients += event.getData.getPath -> builder(Address(event.getData.getData))
      case PathChildrenCacheEvent.Type.CHILD_REMOVED =>
        this.clients.remove(event.getData.getPath) collect { case x: Closeable => x.close() }
      case _ =>
    }

  /**
   *
   * @return
   */
  def get: Try[T] = {
    // Avoids race by caching the available clients.
    val current = this.clients.values.toSeq
    if (current.isEmpty) {
      // If there are no available clients, then throw an error.
      Failure(new IndexOutOfBoundsException("No available servers."))
    } else {
      // Avoid race by synchronizing execution on the randomized client.
      Success(current(Random.nextInt(current.length)))
    }
  }

}

object Cluster {

  /**
   *
   * @param registry
   * @param builder
   * @tparam T
   * @return
   */
  def apply[T](registry: Registry, builder: Address => T): Cluster[T] = {
    val instances = new PathChildrenCache(registry.curator, registry.namespace, true)
    Cluster(instances, TrieMap.empty[String, T], builder)
  }

}
