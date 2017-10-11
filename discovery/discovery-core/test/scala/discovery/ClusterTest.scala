package discovery

import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.test.TestingServer
import org.junit.runner.RunWith
import org.scalatest.concurrent.Eventually
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

import scala.util.Success

@RunWith(classOf[JUnitRunner])
class ClusterTest extends FunSuite with Matchers with BeforeAndAfterAll with Eventually {

  var zookeeper: TestingServer = _
  var curator: CuratorFramework = _

  override def beforeAll(): Unit = {
    // Setup ZooKeeper.
    this.zookeeper = new TestingServer(true)
    this.curator = CuratorFrameworkFactory.newClient(
      this.zookeeper.getConnectString,
      new ExponentialBackoffRetry(1000, 3)
    )

    // Connect to ZooKeeper.
    this.curator.start()
    this.curator.blockUntilConnected()
  }

  override def afterAll(): Unit = {
    this.curator.close()
    this.zookeeper.close()
  }

  test("Execute works on in-memory server.") {
    // Bootstrap a registry.
    val registry = Registry(this.curator, "/services/caustic")
    val cluster = Cluster(registry, x => x)

    // Register an address.
    registry.register(Address("localhost", 9000))
    eventually(cluster.clients shouldBe 'nonEmpty)
    cluster.get shouldEqual Success(Address("localhost", 9000))
    
    // Close the cluster.
    cluster.close()
  }

}
