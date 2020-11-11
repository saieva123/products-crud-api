package storeServices.services

import com.google.inject.Inject
import com.twitter.inject.Logging
import io.catbird.util.Rerunnable
import io.github.hamsters.twitter.Implicits._
import javax.inject.Singleton
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.bson.BSONDocument
import storeServices.ServerMain.scheduler
import storeServices.models.BsonProduct
import storeServices.models.http._
import storeServices.util.MongoImplicitUtil._

import scala.concurrent.{Future => ScalaFuture}

@Singleton
class DeleteProductService @Inject() (mongo: ScalaFuture[BSONCollection])
    extends RerunnableService[DeleteProductsRequest, DeleteProductResponse]
    with Logging {

  final override def apply(request: DeleteProductsRequest): Rerunnable[DeleteProductResponse] = {
    val finder = BSONDocument("productId" -> request.productId)

    Rerunnable.fromFuture(
      mongo
        .map(_.findAndRemove(finder))
        .map {
          _.map { a =>
            a.result[BsonProduct] match {
              case Some(_) => DeleteProductsSuccess
              case _       => DeleteProductEmpty
            }
          }
        }
        .flatten
        .handle {
          case ex: Throwable =>
            error(s"delete product exception: ${ex.getMessage}-${ex.getStackTrace.mkString(",")}")
            DeleteProductFail(ex.getMessage)
        }
    )
  }

}
