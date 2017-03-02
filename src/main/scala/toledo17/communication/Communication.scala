package toledo17.communication

import java.io.Serializable

import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.Message

import scala.collection.JavaConverters._

class Communication[Payload <: Serializable] {

  private[this] def client : AmazonSQSClient = new AmazonSQSClient()

  def fromVisitors(items:Iterable[Payload]) = {
      items foreach {
        item =>
          println("  Submitting "+item.toString)
          val serialized = Serializer.serialize[Payload](item)
          //TODO consider using sendMessageBatch
          //TODO make it generic, don't hard-code queue name
          client.sendMessage(Infrastructure.SQS_FROM_VISITORS_TO_MERGERS, serialized)
      }
  }

  def toMergers(action : Payload => Unit) = {
    //TODO consider receiving in batches (?)
    val messages = client.receiveMessage(Infrastructure.SQS_FROM_VISITORS_TO_MERGERS).getMessages.asScala
    messages foreach {
      msg:Message =>
        try {
          val body = msg.getBody
          println(body)
          val item = Serializer.deserialize[Payload](body)
          action(item)
        } finally {
          //TODO should it be deleted only on success?
          client.deleteMessage(Infrastructure.SQS_FROM_VISITORS_TO_MERGERS, msg.getReceiptHandle)
        }
    }
  }
}
