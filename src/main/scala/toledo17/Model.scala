package toledo17


object Model {

  /**
    * A betting event with stakes for various outcomes.
    */
  sealed case class Event(date: java.time.LocalDateTime, stakes: Iterable[(String, String)])

}
