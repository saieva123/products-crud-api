package storeServices.services

import com.google.inject.Inject
import com.twitter.inject.Logging
import io.catbird.util.Rerunnable
import io.github.hamsters.twitter.Implicits._
import io.scalaland.chimney.dsl._
import javax.inject.Singleton
import mouse.boolean._
import reactivemongo.api.Cursor
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.FindAndModifyCommand._
import reactivemongo.bson.{BSONDocument, BSONRegex}
import storeServices.ServerMain.scheduler
import storeServices.models.BsonProduct
import storeServices.models.http._
import storeServices.util.MongoImplicitUtil._

import scala.concurrent.{Future => ScalaFuture}

@Singleton
class UpdateProductService @Inject() (mongo: ScalaFuture[BSONCollection])
    extends RerunnableService[UpdateProductsRequest, UpdateProductResponse]
    with Logging {

  final override def apply(request: UpdateProductsRequest): Rerunnable[UpdateProductResponse] = {
    val finder = BSONDocument("productId" -> request.productId)
    val updater = BSONDocument(
      "$set" -> BSONDocument(
        "title"     -> request.title,
        "showName"  -> request.showName,
        "desc"      -> request.desc,
        "tags"      -> request.tags,
        "published" -> request.published,
        "images"    -> request.images
      )
    )
    Rerunnable.fromFuture(
      mongo
        .map(_.findAndUpdate(finder, updater))
        .map {
          _.map { a =>
            a.result[BsonProduct] match {
              case Some(_) => UpdateProductsSuccess
              case _       => UpdateProductEmpty
            }
          }
        }
        .flatten
        .handle {
          case ex: Throwable =>
            error(s"update product exception: ${ex.getMessage}-${ex.getStackTrace.mkString(",")}")
            UpdateProductFail(ex.getMessage)
        }
    )
  }

}
