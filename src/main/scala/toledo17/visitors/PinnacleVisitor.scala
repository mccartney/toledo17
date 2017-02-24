package toledo17
package visitors

import com.amazonaws.services.sqs.AmazonSQSClient
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

class PinnacleVisitor extends Selenium {
  // https://www.pinnacle.com

  def visit = {
    val pageSource = visitAPageAndWaitUntilItIsLoaded("https://www.pinnacle.com/en/odds/match/soccer/england/england-premier-league?sport=True")

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
  }

  def emitToSQS(what: String) = {
    val client: AmazonSQSClient = new AmazonSQSClient()

    client.sendMessage("https://sqs.eu-west-1.amazonaws.com/214582020536/toledo17-sqs-1", what)
  }

}



