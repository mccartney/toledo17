package toledo17

import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.Message
import toledo17.Model.Event
import toledo17.communication.{Infrastructure, Serializer}

import scala.collection.JavaConverters._

object Toledo17Mergers extends App {
  println("|Toledo17 Mergers|")

  val client: AmazonSQSClient = new AmazonSQSClient()

  //TODO consider receiving in batches (?)
  val messages = client.receiveMessage(Infrastructure.SQS_FROM_VISITORS_TO_MERGERS).getMessages.asScala
  messages foreach {
    msg:Message =>
      try {
        val body = msg.getBody
        println(body)
        val event = Serializer.deserialize[Event](body)
        println("Event= "+event)

      } finally {
        client.deleteMessage(Infrastructure.SQS_FROM_VISITORS_TO_MERGERS, msg.getReceiptHandle)
      }
  }
}
