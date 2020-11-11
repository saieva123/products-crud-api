package storeServices.services

import java.util.UUID

import com.google.inject.Inject
import com.twitter.inject.Logging
import io.catbird.util.Rerunnable
import io.github.hamsters.twitter.Implicits._
import io.scalaland.chimney.dsl._
import javax.inject.Singleton
import mouse.boolean._
import reactivemongo.api.Cursor
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONRegex}
import storeServices.ServerMain.scheduler
import storeServices.models.BsonProduct
import storeServices.models.http._
import storeServices.util.MongoImplicitUtil._

import scala.concurrent.{Future => ScalaFuture}

@Singleton
class QueryProductService @Inject() (mongo: ScalaFuture[BSONCollection])
    extends RerunnableService[QueryProductsRequest, QueryProductResponse]
    with Logging {

  final override def apply(request: QueryProductsRequest): Rerunnable[QueryProductResponse] = {
    val finder = BSONDocument(
      "productId" -> request.productId,
      "published" -> request.published,
      "tags"      -> request.tag,
      "title"     -> request.title.map(t => BSONRegex(s"^$t" + "$", "i"))
    )

    Rerunnable.fromFuture(
      mongo
        .flatMap(
          _.find(finder)
            .cursor[BsonProduct]()
            .collect[List](-1, Cursor.FailOnError[List[BsonProduct]]())
            .map { t =>
              t.isEmpty.fold(
                QueryProductEmpty,
                QueryProductsSuccess(
                  num = t.size,
                  detail = t.map(_.into[QueryProductInfo].transform)
                )
              )
            }
        )
        .handle {
          case ex: Throwable =>
            error(s"query product exception: ${ex.getMessage}-${ex.getStackTrace.mkString(",")}")
            QueryProductFail(ex.getMessage)
        }
    )
  }

}
