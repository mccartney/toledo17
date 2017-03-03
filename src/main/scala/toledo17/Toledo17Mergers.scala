package toledo17

import toledo17.Model.Event
import toledo17.communication.Communication

object Toledo17Mergers extends App {
  println("|Toledo17 Mergers|")

//  println(cartesianProduct(Seq("1", "2", "3"), List("a", "b"), List("X", "Y", "Z", "W")))
//  println(variantsOfTeams("abc abcdef abc New Orleans Pelicans"))


  new Communication[Event].toMergers {
    event =>
      println("Event= "+event)
      val teamNames = event.stakes.unzip._1
      val teamVariantsForEvent:Seq[Iterable[String]] = (teamNames map {variantsOfTeams(_)}).toSeq
      println(cartesianProduct(Seq(event.date.toLocalDate.toString), teamVariantsForEvent:_*))
  }

  def variantsOfTeams(team: String): Iterable[String] = team.split(" ") map {
    _.replaceAll("-", "")
  } filter (_.length > 3)

  def cartesianProduct(one:Iterable[String], rest:Iterable[String]*) :List[String] =
    // http://stackoverflow.com/questions/15502924/passing-a-individual-arguments-and-a-seq-to-a-var-arg-function#comment21950813_15502924
    cartesianProduct(one+:rest:_*)

  def cartesianProduct(degreesOfFreedom : Iterable[String]*) :List[String] = {
    degreesOfFreedom match {
      case Seq() => List()
      case Seq(single) => single.toList
      case head +: tail => (head flatMap {
        elementInFirst =>
          cartesianProduct(tail: _*) map {
            productItemForRest =>
              elementInFirst + "-" + productItemForRest
          }
      }).toList

    }

  }

}
