package toledo17

import toledo17.visitors._


object Toledo17Visitors extends App {
  println("!Toledo17 Visitors!")
  
  new BetssonVisitor().visit
  new PinnacleVisitor().visit
  
}

// TODO
// - inny serwis, który czyta z SQS i wkłada do DynamoDB
// - matchowanie/scalanie nazw drużyn (w DynamoDB?) ręczne
// - matchowanie/scalanie nazw drużyn heurystyczne
// - prezentacja (Lift?)
// - inny serwis: generator zleceń dla visitorów


