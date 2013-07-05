import domain.Review
import org.joda.time.{DateTimeZone, DateTime}
import org.specs2.mutable._
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.Json
import play.api.libs.ws.Response
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import test.TestApplication

class CrucibleSpec extends Specification with TestApplication {
  override val crucible = new Crucible()

  val response = mock[Response]

  "Crucible" should {
    "get all reviews for author" in {
      response.status returns OK
      response.json returns exampleReviewsJsonResult
      crucibleWebService.reviews("author") returns Future(response)
      val expectedDate = new DateTime(2013, 7, 4, 8, 0, 0, 0, DateTimeZone.forOffsetHours(-5))
      val expected = Review("ABC-123", "author", "review name here", expectedDate, 0)

      val enumerator = crucible.getAllReviewsForAuthor("author")

      val reviewsFuture = enumerator(reviewUnpacker) flatMap (_.run)
      val reviews = Await.result(reviewsFuture, Duration.Inf)

      reviews should equalTo (List(expected))
    }
  }

  val exampleReviewsJsonResult = {
    Json.obj(
      "detailedReviewData" -> Json.arr(
        Json.obj(
          "permaId" -> Json.obj(
            "id" -> "ABC-123"
          ),
          "author" -> Json.obj(
            "userName" -> "author"
          ),
          "name" -> "review name here",
          "createDate" -> "2013-07-04T08:00:00.000-0500"
        )
      )
    )
  }

  val reviewUnpacker = Iteratee.fold[Review, List[Review]](Nil) { (rs, r) => r :: rs}
}
