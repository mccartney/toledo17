package toledo17
package visitors

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.amazonaws.services.sqs.AmazonSQSClient
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._


class BetssonVisitor extends Selenium {
  // https://www.betsson.com/en

  def visit = {
    val pageSource = visitAPageAndWaitUntilItIsLoaded("https://sportsbook.betsson.com/en/football/england/fa-premier-league",
      Some(className("bets-markets")))

    val jsoup = JsoupBrowser()
    val doc = jsoup.parseString(pageSource)
    val games = doc >> elementList("table.bets-markets-listing-container tr.event-row")
    println("Found " + games.length + " games")
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
    println(extractedBets)
    println(extractedBets.length)

    extractedBets foreach { eb => emitToSQS(eb.toString) }
  }

  def emitToSQS(what: String) = {
    val client: AmazonSQSClient = new AmazonSQSClient()

    client.sendMessage("https://sqs.eu-west-1.amazonaws.com/214582020536/toledo17-sqs-1", what)
  }

}



