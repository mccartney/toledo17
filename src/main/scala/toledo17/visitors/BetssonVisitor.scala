package toledo17
package visitors

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import toledo17.Model.Event


class BetssonVisitor extends Visitor with Selenium with JSoupParser {
  // https://www.betsson.com/en

  override def extractHtmlWithEvents : String = {
    return visitAPageAndWaitUntilItIsLoaded("https://sportsbook.betsson.com/en/basketball/nba/nba-regular-season",
      //"https://sportsbook.betsson.com/en/football/england/fa-premier-league",
      Some(className("bets-markets")))
  }

  override def parse(doc : Document) : Iterable[Event] = {
    val games = doc >> elementList("table.bets-markets-listing-container tr.event-row")
    val extractedBets = games map {
      game =>
        val teams = (game >> text("div.bets-data-title-game-title")).split(" - ").toList
        val stakes = (game >> text("td.bet-group-1")).split(" ").toList

        val dateTime = LocalDateTime.parse(
          s"""${game >> text(".date")} ${game >> text(".time")}""",
          DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))

        val teamsComplete: List[String] = teams match {
          case List(one, two) if (stakes.length==3) => List(one, "X", two)
          case other => other
        }
        Model.Event(date = dateTime, teamsComplete zip stakes)
    }
    return extractedBets
  }


}



