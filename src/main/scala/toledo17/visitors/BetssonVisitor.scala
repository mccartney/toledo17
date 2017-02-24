package toledo17
package visitors

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.amazonaws.services.sqs.AmazonSQSClient
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import toledo17.Model.Event


class BetssonVisitor extends Visitor with Selenium {
  // https://www.betsson.com/en

  override def harvestEvents : Iterable[Event] = {
    val pageSource = visitAPageAndWaitUntilItIsLoaded("https://sportsbook.betsson.com/en/football/england/fa-premier-league",
      Some(className("bets-markets")))

    val jsoup = JsoupBrowser()
    val doc = jsoup.parseString(pageSource)
    val games = doc >> elementList("table.bets-markets-listing-container tr.event-row")
    val extractedBets = games map {
      game =>
        val teams = (game >> text("div.bets-data-title-game-title")).split(" - ").toList
        val stakes = (game >> text("td.bet-group-1")).split(" ").toList

        val dateTime = LocalDateTime.parse(
          s"""${game >> text(".date")} ${game >> text(".time")}""",
          DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))

        val teamsComplete: List[String] = teams match {
          case List(one, two) => List(one, "X", two)
          case other => other
        }

        Model.Event(date = dateTime, teamsComplete zip stakes)
    }
    return extractedBets
  }


}



