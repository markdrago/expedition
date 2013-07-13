package models

import domain.{ReviewItem, Review}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.iteratee.{Enumeratee, Enumerator}
import play.api.libs.json.Reads._
import play.api.libs.json._
import scala.language.reflectiveCalls
import com.google.inject.{Inject, Singleton}

trait Crucible {
  def reviews(author: String): Enumerator[Review]
}

class CrucibleImpl @Inject()(crucibleWebService: CrucibleWebService) extends Crucible {

  implicit val readReviews: Reads[Seq[Review]] =
    (__ \ "detailedReviewData").read(
      seq(
        ((__ \ "permaId" \ "id").read[String] and
          (__ \ "author" \ "userName").read[String] and
          (__ \ "name").read[String] and
          (__ \ "createDate").read[String])
          .tupled
      ).map {
        reviews => reviews.collect {
          case (id, author, name, creationDate) => Review(id, author, name, DateTime.parse(creationDate), 0)
        }
      }
    )

  implicit val readReviewItems: Reads[Seq[ReviewItem]] =
    (__ \ "reviewItem").read(
      seq(
        (__ \ "permId" \ "id").read[String]
      ).map {
        items => items.collect {
          case (id) => ReviewItem(id)
        }
      }
    )

  def getAllReviewsForAuthor(author: String): Enumerator[Review] =
    Enumerator.flatten(
      crucibleWebService.reviews(author).map { resp =>
        Enumerator.enumerate(resp.json.as[Seq[Review]])
      }
    )

  def getFileCountForReview: Enumeratee[Review, Review] = {
    Enumeratee.mapM { r =>
      crucibleWebService.reviewItemsForReview(r.id).map { resp =>
        val items = resp.json.as[Seq[ReviewItem]].toSet
        r.updateFileCount(items.size)
      }
    }
  }

  def reviews(author: String): Enumerator[Review] =
    (getAllReviewsForAuthor(author) &> getFileCountForReview) >>> Enumerator.eof
}