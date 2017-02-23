package toledo17

import toledo17.visitors._


object Toledo17 extends App {
  println("T17")
  
  new BetssonVisitor().visit
  new PinnacleVisitor().visit
  
}

// TODO
// - Betsson
// - inny serwis, który czyta z SQS i wkłada do DynamoDB
// - matchowanie/scalanie nazw drużyn (w DynamoDB?)
// - prezentacja (Lift?)


