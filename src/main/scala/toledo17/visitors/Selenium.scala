package toledo17.visitors

import java.net.URL

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Seconds, Span}

trait Selenium extends WebBrowser with Eventually with Matchers {

  def visitAPageAndWaitUntilItIsLoaded(urlToVisit: String, whatShouldBeThere: Option[Query] = None) : String = {
    implicit val webDriver : WebDriver = Selenium.getWebDriver

    try {
      go to urlToVisit

      eventually {
        val documentStatus = executeScript("return document.readyState;")
        documentStatus should be("complete")
      }

      whatShouldBeThere match {
        case Some(q: Query) => {
          eventually(timeout(Span(30, Seconds)), interval(Span(5, Seconds))) {
            val areWeThereYet = findAll(q)
            println("Are we there yet? " + areWeThereYet)
            areWeThereYet should not be empty
          }
        }
        case None => // do nothing
      }

      return pageSource
    } finally {
      close()
      quit()
    }
  }
}

object Selenium {
  def getWebDriver: WebDriver = { return new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.firefox()); }
}
