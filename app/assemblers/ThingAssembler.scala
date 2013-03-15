package assemblers

import reactivemongo.bson.BSONObjectID

import models.Thing
import dtos.ThingDTO

object ThingAssembler {

  def writeDTO(thing: Thing): ThingDTO = {
    ThingDTO(
      thing.id.map(_.stringify),
      thing.title,
      thing.description,
      thing.id.map(_.time))
  }

  def writeDTOs(things: List[Thing]): List[ThingDTO] = things.map(writeDTO(_))

  def thing(dto: ThingDTO): Thing = {
    Thing(
      Some(dto.id.map(BSONObjectID(_)).getOrElse(BSONObjectID.generate)),
      dto.title,
      dto.description)
  }

}