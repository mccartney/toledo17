package toledo17
package visitors

import com.amazonaws.services.sqs.AmazonSQSClient
import toledo17.Model.Event
import toledo17.communication.{Infrastructure, Serializer}

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
      val serialized = Serializer.serialize[Event](event)
      //TODO consider using sendMessageBatch
      client.sendMessage(Infrastructure.SQS_FROM_VISITORS_TO_MERGERS, serialized)
    }
  }
}
