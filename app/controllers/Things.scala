package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Play.current
import play.modules.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.BSONReader
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.BSONWriter

import models._
import dtos._
import dtos.ThingDTOFormat._
import assemblers._

object Things extends Controller with MongoController {

  val db = ReactiveMongoPlugin.db
  val collection = db("things")

  implicit val reader = Thing.ThingBSONReader

  def list = Action {
    Async {
      val things = collection.find(BSONDocument()).toList
      things.map { things =>
        if (things.isEmpty)
          Ok(JsArray()).as(JSON)
        else
          Ok(Json.toJson(ThingAssembler.writeDTOs(things))).as(JSON)
      }
    }
  }

  def insert = Action(parse.json) { request =>
    request.body.validate[ThingDTO].fold(
      valid = { dto =>
        val thing = ThingAssembler.thing(dto)
        Async {
          collection.insert(thing).map { _ =>
            Redirect(routes.Things.find(thing.id.get.stringify))
          }
        }
      },
      invalid = (e => BadRequest))
  }

  def find(id: String) = Action {
    Async {
      val thing = collection.find(BSONDocument("_id" -> BSONObjectID(id))).headOption
      thing.map { thing =>
        thing match {
          case Some(thing) => Ok(Json.toJson(ThingAssembler.writeDTO(thing))).as(JSON)
          case None => NotFound
        }
      }
    }
  }

  def update(id: String) = Action(parse.json) { request =>
    request.body.validate[ThingDTO].fold(
      valid = { dto =>
        Async {
          val thing = ThingAssembler.thing(dto)
          collection.update(BSONDocument("_id" -> BSONObjectID(id)), thing).map { _ =>
            NoContent
          }
        }
      },
      invalid = (e => BadRequest))
  }

  def remove(id: String) = Action {
    Async {
      collection.remove(BSONDocument("_id" -> BSONObjectID(id))).map { _ =>
        NoContent
      }.recover { case _ => InternalServerError }
    }
  }

}