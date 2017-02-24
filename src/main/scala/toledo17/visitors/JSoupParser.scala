package toledo17.visitors

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document
import toledo17.Model.Event

trait JSoupParser extends Visitor {
  def harvestEvents : Iterable[Event] = {
    val pageSource = extractHtmlWithEvents
    val doc = JsoupBrowser().parseString(pageSource)
    return parse(doc)
  }

  def extractHtmlWithEvents : String
  def parse(doc : Document) : Iterable[Event]
}
