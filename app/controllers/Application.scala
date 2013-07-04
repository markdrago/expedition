package controllers

import domain.Review
import models.{CrucibleWebServiceComponent, CrucibleComponent}
import play.api.libs.iteratee.Enumeratee
import play.api.libs.json._
import play.api.mvc._

trait ApplicationComponent extends Controller {
  this: CrucibleComponent =>

  implicit val reviewToJson = Json.writes[Review]
  val toJson = Enumeratee.map[Review](r => Json.toJson(r))

  def index = Action { Ok(views.html.index("message here")) }

  def reviews = Action {
    Ok.stream(crucible.reviews("mdrago") &> toJson)
  }
}

object Application extends ApplicationComponent
                   with CrucibleComponent
                   with CrucibleWebServiceComponent {
  override val crucibleWebService = new CrucibleWebService()
  override val crucible = new Crucible()
}
