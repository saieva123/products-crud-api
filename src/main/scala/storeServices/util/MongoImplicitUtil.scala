package storeServices.util

import reactivemongo.api.bson.{BSONDocumentHandler, Macros}
import storeServices.models.BsonProduct

object MongoImplicitUtil {

  implicit def productHandler: BSONDocumentHandler[BsonProduct] =
    Macros.handler[BsonProduct]
}
