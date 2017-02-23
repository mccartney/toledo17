package toledo17
package visitors

import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.amazonaws.services.sqs.AmazonSQSClient
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Seconds, Span}
import org.scalatest.selenium.WebBrowser


class BetssonVisitor extends WebBrowser with Eventually with Matchers {
  // https://www.betsson.com/en

  implicit val webDriver: WebDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.firefox());

  def visit = {
    try {
      go to "https://sportsbook.betsson.com/en/football/england/fa-premier-league"

      eventually(timeout(Span(30, Seconds)), interval(Span(5, Seconds))) {
        val areWeThereYet = findAll(className("bets-markets"))
        println("Are we there yet? "+ areWeThereYet)
        areWeThereYet should not be empty
      }
      val jsoup = JsoupBrowser()
      val doc = jsoup.parseString(pageSource)
      val games = doc >> elementList("table.bets-markets-listing-container tr.event-row")
      println("Found "+games.length+" games")
      val extractedBets = games map {
        game =>
          val teams = (game >> text("div.bets-data-title-game-title")).split(" - ").toList
          val stakes= (game >> text("td.bet-group-1")).split(" ").toList

          val dateTime = LocalDateTime.parse(s"""${game >> text(".date")} ${game >> text(".time")}""",
                           DateTimeFormatter.ofPattern("yy/MM/dd HH:mm")) 

          val teamsComplete :List[String] = teams match{
            case List(one, two) => List(one, "X", two)
            case other => other
          }

          Model.Event(date=dateTime, teamsComplete zip stakes)
      }
      println(extractedBets)
      println(extractedBets.length)

      extractedBets foreach { eb => emitToSQS(eb.toString) }
    } finally {
      close()
      quit()
    }

  }

  def emitToSQS(what: String) = {
    val client: AmazonSQSClient = new AmazonSQSClient()

    client.sendMessage("https://sqs.eu-west-1.amazonaws.com/214582020536/toledo17-sqs-1", what)
  }

}



