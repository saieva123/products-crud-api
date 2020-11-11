package storeServices.services

import java.util.UUID

import com.google.inject.Inject
import com.twitter.inject.Logging
import io.catbird.util.Rerunnable
import javax.inject.Singleton
import perfolation._
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import storeServices.models.http.{
  CreateProductFail,
  CreateProductIdDuplicate,
  CreateProductRequest,
  CreateProductResponse,
  CreateProductSuccess
}
import storeServices.ServerMain.scheduler
import io.github.hamsters.twitter.Implicits._
import mouse.boolean._
import scala.concurrent.{Future => ScalaFuture}
import io.scalaland.chimney.dsl._
import storeServices.models.BsonProduct
import storeServices.util.MongoImplicitUtil._

@Singleton
class CreateProductService @Inject() (mongo: ScalaFuture[BSONCollection])
    extends RerunnableService[CreateProductRequest, CreateProductResponse]
    with Logging {

  final override def apply(request: CreateProductRequest): Rerunnable[CreateProductResponse] = {
    val id = UUID.randomUUID().toString
    val r = request
      .into[BsonProduct]
      .withFieldComputed(_.productId, _ => id)
      .transform

    Rerunnable.fromFuture(
      mongo
        .flatMap(
          _.insert.one[BsonProduct](r)
        )
        .map(_ => CreateProductSuccess(id))
        .handle {
          case ex: Throwable =>
            error(s"create product exception: ${ex.getMessage}-${ex.getStackTrace.mkString(",")}")
            ex.getMessage.contains("E11000").fold(CreateProductIdDuplicate, CreateProductFail(ex.getMessage))
        }
    )
  }

}
