package toledo17
package visitors

import java.time.LocalDateTime

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import toledo17.Model.Event

class PinnacleVisitor extends Visitor with Selenium with JSoupParser {
  // https://www.pinnacle.com

  override def extractHtmlWithEvents : String = {
    return visitAPageAndWaitUntilItIsLoaded(
      "https://www.pinnacle.com/en/odds/match/basketball/usa/nba?sport=True")
      // "https://www.pinnacle.com/en/odds/match/soccer/england/england-premier-league?sport=True")
  }

  override def parse(doc:Document): Iterable[Event] = {
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



