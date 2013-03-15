package models

import reactivemongo.bson._
import reactivemongo.bson.handlers._

case class Thing(
  id: Option[BSONObjectID] = None,
  title: String,
  description: String)

object Thing {

  implicit object ThingBSONReader extends BSONReader[Thing] {
    def fromBSON(document: BSONDocument): Thing = {
      val doc = document.toTraversable
      Thing(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONString]("title").get.value,
        doc.getAs[BSONString]("description").get.value
      )
    }
  }

  implicit object ThingBSONWriter extends BSONWriter[Thing] {
    def toBSON(thing: Thing) = {
      val bson = BSONDocument(
        "_id" -> thing.id.getOrElse(BSONObjectID.generate),
        "title" -> BSONString(thing.title),
        "description" -> BSONString(thing.description)
      )
      bson
    }
  }

}