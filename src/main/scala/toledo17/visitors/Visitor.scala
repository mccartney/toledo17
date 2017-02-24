package toledo17.visitors

import com.amazonaws.services.sqs.AmazonSQSClient
import toledo17.Infrastructure
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
      println("  Submitting "+event.toString)
      //TODO consider using sendMessageBatch
      client.sendMessage(Infrastructure.SQS_FROM_VISITORS_TO_MERGERS, event.toString)
    }
  }
}
