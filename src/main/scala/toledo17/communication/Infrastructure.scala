package toledo17.communication

object Infrastructure {

  //TODO it should rather be a dynamically created queue based on some hashcode(Model.Event.class)
  // so that whenever the definition of the Event case class changes, a new queue is created.
  // Thus achieving homogeneity of the queue content
  val SQS_FROM_VISITORS_TO_MERGERS = "https://sqs.eu-west-1.amazonaws.com/214582020536/toledo17-sqs-1"

  val DYNAMO_DB_IDENTIFIERS = "toledo17-identifiers"

}
