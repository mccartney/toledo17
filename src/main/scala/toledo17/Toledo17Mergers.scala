package toledo17

import toledo17.Model.Event
import toledo17.communication.Communication

object Toledo17Mergers extends App {
  println("|Toledo17 Mergers|")

  new Communication[Event].toMergers {
    event =>
      println("Event= "+event)
  }

}
