import com.google.common.collect.{ConcurrentHashMultiset, Multiset}
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random

/**
 * A discrete probability distribution. Implementation is a simple wrapper around a Guava
 * [[Multiset]], with Scala collection semantics. Provides a convenient interface for taking
 * weighted random samples and determining the probabilities of elements within a set. A
 * distribution backed by a thread-safe Multiset, is, itself, thread-safe and may be queried and
 * modified in parallel.
 *
 * @param elements Initial elements.
 * @tparam X Type of elements.
 */
class Distribution[X](elements: Multiset[X]) extends mutable.Set[X] {

  private[this] val lock = new Lock

  override def +=(x: X): Distribution.this.type = {
    this.lock.shared(this.elements.add(x))
    this
  }

  override def -=(x: X): Distribution.this.type = {
    this.lock.shared(this.elements.remove(x))
    this
  }

  override def contains(x: X): Boolean =
    this.lock.shared(this.elements.contains(x))

  override def iterator: Iterator[X] =
    this.lock.shared(this.elements.elementSet()).iterator().asScala

  /**
   * Returns the probability with which the specified element occurs in the distribution. Calculated
   * as the frequency of the element divided by total number of elements in the distribution. O(1)
   *
   * @param x Element in distribution.
   * @return Probability of element.
   */
  def probability(x: X): Double =
    this.lock.exclusive {
      if (this.elements.isEmpty) 0.0 else this.elements.count(x) / this.elements.size().toDouble
    }

  /**
   * Returns a probability-weighted random element from the distribution. Selects a uniformly random
   * number less than the total number of elements in the distribution, and subtract the frequency
   * of each element in the distribution until the number is negative. O(n)
   *
   * @throws NoSuchElementException If the distribution is empty.
   * @return Weighted random element.
   */
  def sample(): X = {
    this.lock.exclusive {
      var rand = Random.nextInt(this.elements.size())
      this.elements.elementSet().asScala.dropWhile { x =>
        rand -= this.elements.count(x)
        rand >= 0
      }.head
    }
  }

}

object Distribution {

  /**
   * Constructs an empty distribution backed by a Guava [[ConcurrentHashMultiset]].
   *
   * @tparam X Type of elements.
   * @return Empty distribution.
   */
  def empty[X]: Distribution[X] =
    new Distribution[X](ConcurrentHashMultiset.create())

  /**
   * Constructs a distribution backed by a Guava [[ConcurrentHashMultiset]] and initializes it with
   * the specified iterable collection of elements.
   *
   * @param iterable Initial elements.
   * @tparam X Type of elements.
   * @return Distribution with the specified initial elements.
   */
  def apply[X](iterable: Iterable[X]): Distribution[X] =
    new Distribution[X](ConcurrentHashMultiset.create(iterable.asJava))

  /**
   * Constructs a distribution and initializes the underlying probabilities of elements to reflect
   * the provided element frequencies.
   *
   * @param counts Frequencies of elements.
   * @tparam X Type of elements.
   * @return Distribution initialized with the provided frequencies.
   */
  def apply[X](counts: (X, Int)*): Distribution[X] = {
    val dist = ConcurrentHashMultiset.create[X]()
    counts.foreach { case (element, count) => dist.setCount(element, count) }
    new Distribution(dist)
  }

}