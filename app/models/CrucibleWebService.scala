package models

import com.google.inject.Singleton
import play.Logger
import play.api.Play
import play.api.Play.current
import play.api.libs.ws.Response
import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder
import scala.concurrent.Future

trait CrucibleWebService{
  def reviews: Future[Response]
  def reviewItemsForReview(id: String): Future[Response]
}

@Singleton
class CrucibleWebServiceImpl extends CrucibleWebService {
  private[this] def rawBaseUrl = Play.configuration.getString("application.crucible.url").get
  private[this] def token = Play.configuration.getString("application.crucible.token").get
  private[this] def cleanBase = rawBaseUrl.reverse.dropWhile(_ == '/').reverse

  private[this] def createUrl(path: String, params: Map[String, String] = Map.empty): WSRequestHolder = {
    val url = WS.url(cleanBase + path + urlParamString(params + ("FEAUTH" -> token)))
      .withHeaders(
      ("Accept", "application/json"),
      ("Content-Type", "application/json")
    )
    Logger.debug(url.url)
    url
  }

  private[this] def urlParamString(params: Map[String, String]): String =
    params.map(pair => pair._1 + "=" + pair._2) mkString("&") match {
      case empty if empty.isEmpty => empty
      case nonEmpty => "?" + nonEmpty
    }

  def reviews: Future[Response] =
    createUrl("/rest-service/reviews-v1/filter/details").get()

  def reviewItemsForReview(id: String): Future[Response] =
    createUrl(s"/rest-service/reviews-v1/$id/reviewitems").get()
}