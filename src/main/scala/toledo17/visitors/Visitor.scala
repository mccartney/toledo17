package toledo17
package visitors

import toledo17.Model.Event
import toledo17.communication.Communication

trait Visitor {

  def visit = {
    println("----------------- "+this)
    val events = harvestEvents
    println(s" Found ${events.size} events")
    new Communication[Event].fromVisitors(events)
  }

  def harvestEvents : Iterable[Event]

}
