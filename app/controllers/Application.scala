package controllers

import com.google.inject.Inject
import domain.Review
import models.Crucible
import play.api.libs.iteratee.Enumeratee
import play.api.libs.json._
import play.api.mvc._
import scala.language.reflectiveCalls

class Application @Inject()(crucible: Crucible) extends Controller {

  implicit val reviewToJson = Json.writes[Review]
  val toJson = Enumeratee.map[Review](r => Json.toJson(r))

  def index = Action { Ok(views.html.index("message here")) }

  def reviews = Action {
    Ok.stream(crucible.reviews("mdrago") &> toJson)
  }
}
