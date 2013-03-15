package dtos

//import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class ThingDTO(
  id: Option[String],
  title: String,
  description: String,
  created: Option[Long])

object ThingDTOFormat {
  implicit val format = Json.format[ThingDTO]
}