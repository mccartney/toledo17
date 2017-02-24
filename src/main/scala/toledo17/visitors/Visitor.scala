package toledo17.visitors

import com.amazonaws.services.sqs.AmazonSQSClient
import toledo17.Model.Event

trait Visitor {

  def visit = {
    println("----------------- "+this)
    val events = harvestEvents
    println(s" Found ${events.size} events")
    emitToSQS(events)
  }

  def harvestEvents : Iterable[Event]

  def emitToSQS(events:Iterable[Event]) = {
    val client: AmazonSQSClient = new AmazonSQSClient()
    events foreach { event =>
      client.sendMessage("https://sqs.eu-west-1.amazonaws.com/214582020536/toledo17-sqs-1", event.toString)
    }
  }
}
