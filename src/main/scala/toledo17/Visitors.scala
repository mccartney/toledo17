package toledo17

import java.net.URL

import com.amazonaws.services.sqs.model.Message
import io.atlassian.aws.sqs.{RetriedMessage, SQS, SQSClient, Types, Marshaller}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}

import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.WebBrowser

import argonaut.Argonaut._
import scalaz.{ \/-, -\/, \/ }

import argonaut.CodecJson
import io.atlassian.aws.AmazonRegion

class PinnacleVisitor extends WebBrowser with Eventually with Matchers with Types {
 // https://www.pinnacle.com
 
 implicit val webDriver: WebDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.firefox());

 case class Event(id:Long)


  implicit val EventCodecJson: CodecJson[Event] =
    casecodec1(Event.apply, Event.unapply)("id")

  implicit val EventMarshaller : Marshaller[Event] = Marshaller.jsonBody[Event]

  def visit = {
    try {
      go to "https://www.pinnacle.com/en/odds/match/soccer/england/england-premier-league?sport=True"

      eventually { 
        val documentStatus = executeScript("return document.readyState;")
        documentStatus should be ("complete")
      }
      
      val jsoup = JsoupBrowser()
      val doc = jsoup.parseString(pageSource)
      
      val games = doc >> elementList("""div[ng-show="activeTab == 'period'"] table.odds-data tbody""")
      games foreach {
       game => 
         println((game >> texts("td.name")) zip (game >> texts("td.oddTip.game-moneyline")))
      }
      println(games.length)

    } finally {
      close()
      quit()
    }

    //TODO emit to SQS using https://bitbucket.org/atlassian/aws-scala

    val defaultClient = SQSClient.default
    defaultClient.setRegion(AmazonRegion.orDefault("eu-west-1"))
    val action = SQS.queueURL("toledo17-sqs-1")
    val url = action.unsafePerform(defaultClient).run match {
      case -\/(e) =>
        println(e)
        null //TODO
      case \/-(task) =>
        task
    }
    println(url)

    val req2 = new RetriedMessage[Event](10, new Event(116))
    println(SQS.send(url, req2).unsafePerform(defaultClient).run)
 } 

}



