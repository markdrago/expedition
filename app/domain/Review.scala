package domain

import org.joda.time.DateTime

case class Review(
  id: String,
  author: String,
  title: String,
  dateCreated: DateTime,
  fileCount: Int
) {
  def updateFileCount(newFileCount: Int): Review = {
    Review(id, author, title, dateCreated, newFileCount)
  }
}
