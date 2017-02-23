package toledo17

import java.net.URL

import com.amazonaws.services.sqs.AmazonSQSClient
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.WebBrowser

class PinnacleVisitor extends WebBrowser with Eventually with Matchers {
  // https://www.pinnacle.com

  implicit val webDriver: WebDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.firefox());

  def visit = {
    try {
      go to "https://www.pinnacle.com/en/odds/match/soccer/england/england-premier-league?sport=True"

      eventually {
        val documentStatus = executeScript("return document.readyState;")
        documentStatus should be("complete")
      }

      val jsoup = JsoupBrowser()
      val doc = jsoup.parseString(pageSource)

      val games = doc >> elementList("""div[ng-show="activeTab == 'period'"] table.odds-data tbody""")
      val extractedBets = games map {
        game =>
          (game >> texts("td.name")) zip (game >> texts("td.oddTip.game-moneyline"))
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



