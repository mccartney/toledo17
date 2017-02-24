package toledo17
package visitors

import java.time.LocalDateTime

import com.amazonaws.services.sqs.AmazonSQSClient
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import toledo17.Model.Event

class PinnacleVisitor extends Visitor with Selenium {
  // https://www.pinnacle.com

  override def harvestEvents : Iterable[Event] = {
    val pageSource = visitAPageAndWaitUntilItIsLoaded("https://www.pinnacle.com/en/odds/match/soccer/england/england-premier-league?sport=True")

    val jsoup = JsoupBrowser()
    val doc = jsoup.parseString(pageSource)

    val games = doc >> elementList("""div[ng-show="activeTab == 'period'"] table.odds-data tbody""")
    val extractedBets = games map {
      game =>
        val stakes = (game >> texts("td.name")) zip (game >> texts("td.oddTip.game-moneyline"))
        //TODO somehow challenge the Pinnacle's bug of not publishing the dates for events (without logging in)
        Model.Event(date = LocalDateTime.now, stakes = stakes)
    }
    return extractedBets

  }

}



