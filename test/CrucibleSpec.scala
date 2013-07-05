import domain.Review
import org.joda.time.{DateTimeZone, DateTime}
import org.specs2.mutable._
import org.specs2.specification.Scope
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.json.Json
import play.api.libs.ws.Response
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import test.TestApplication

class CrucibleSpec extends Specification with TestApplication {
  override val crucible = new Crucible()

  trait context extends Scope {
    val response = mock[Response]
    response.status returns OK
  }

  private def reviewUnpacker(enumerator: Enumerator[Review]): Set[Review] = {
    val unpacker = Iteratee.fold[Review, Set[Review]](Set.empty) { (rs, r) => rs + r}
    val reviewsFuture = enumerator(unpacker) flatMap (_.run)
    Await.result(reviewsFuture, Duration(500, MILLISECONDS))
  }

  "Crucible.getAllReviewsForAuthor" should {
    "correctly parse the response from crucible in to a List of Reviews" in new context {
      //setup mock results
      response.json returns reviewsJsonResult
      crucibleWebService.reviews("author") returns Future(response)

      //prepare expected value
      val expectedDate = new DateTime(2013, 7, 4, 8, 0, 0, 0, DateTimeZone.forOffsetHours(-5))
      val expected = Review("ABC-123", "author", "review name here", expectedDate, 0)

      //call method under test and get back enumerator
      val enumerator = crucible.getAllReviewsForAuthor("author")

      //validate
      reviewUnpacker(enumerator) should equalTo (Set(expected))
    }
  }

  "Crucible.getFileCountForReview" should {
    "parse the reply from crucible and update the file count for reviews" in new context {
      //setup mocks
      response.json returns reviewItemsJsonResult
      crucibleWebService.reviewItemsForReview(anyString) returns Future(response)

      //prepare input
      val aGoodDay = new DateTime(2013, 7, 5, 8, 15, 0, 0, DateTimeZone.forOffsetHours(-5))
      val r1 = Review("ABC-1", "author", "name1", aGoodDay, 0)
      val r2 = Review("ABC-2", "author", "name2", aGoodDay, 0)
      val input = Enumerator.enumerate(Seq(r1, r2))

      //prepare expected output
      val expected = Set(r1.updateFileCount(2), r2.updateFileCount(2))

      //call method under test and get back a new enumerator
      val enumerator = input through crucible.getFileCountForReview

      //validate
      reviewUnpacker(enumerator) should equalTo(expected)
    }
  }

  val reviewsJsonResult = {
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

  val reviewItemsJsonResult = {
    Json.obj(
      "reviewItem" -> Json.arr(
        Json.obj(
          "permId" -> Json.obj(
            "id" -> "FOO-123"
          )
        ),
        Json.obj(
          "permId" -> Json.obj(
            "id" -> "FOO-456"
          )
        )
      )
    )
  }
}
