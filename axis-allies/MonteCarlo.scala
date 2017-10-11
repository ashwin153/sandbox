import scala.util.Random

object MonteCarlo {

  implicit val random = Random

  def main(args: Array[String]): Unit =
    println(battle(
      trials = 500,
      attacker = Array(1, 2, 4, 2, 0, 0),
      defender = Array(0, 2, 2, 1, 0, 0),
      firststrike = Array(0, 0, 0, 2, 0, 0)
    ))

  /**
   *
   * @param army
   * @param random
   * @return
   */
  def hits(army: Array[Int])(implicit random: Random) =
    army.zipWithIndex.foldLeft(0) { case (hits, (number, strength)) =>
      hits + (0 until number).count(_ => random.nextInt(6) <= strength)
    }

  /**
   *
   * @param army
   * @param n
   */
  def kill(army: Array[Int], n: Int) = {
    var count = n
    var index = 0

    while (count > 0 && index < army.length) {
      if (army(index) > count) {
        army(index) -= count
        count = 0
      } else {
        count -= army(index)
        army(index) = 0
        index += 1
      }
    }
  }

  /**
   *
   * @param trials
   * @param attacker
   * @param defender
   * @param firststrike
   * @param random
   * @return
   */
  def battle(
    trials: Int,
    attacker: Array[Int],
    defender: Array[Int],
    firststrike: Array[Int] = Array.empty
  )(
    implicit random: Random
  ): Double = {
    // Construct a distribution to store results of Monte Carlo simulation.
    val distribution = Distribution.empty[Boolean]

    (0 until trials).foreach { _ =>
      // Clone the attacker and defender for each trial.
      val atrial = attacker.clone()
      val dtrial = defender.clone()

      // Perform first strike attacks.
      kill(defender, hits(firststrike))

      // Battle while the attacker and defender have troops remaining.
      while (atrial.exists(_ > 0) && dtrial.exists(_ > 0)) {
        val ahits = hits(atrial)
        val dhits = hits(dtrial)

        kill(atrial, dhits)
        kill(dtrial, ahits)
      }

      // The winner of the battle is whoever who has troops left.
      distribution += atrial.exists(_ > 0)
    }

    // Return the probability that the attacker wins.
    distribution.probability(true)
  }

}
