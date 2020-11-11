package storeServices.modules

import com.google.inject.Provides
import com.twitter.inject.TwitterModule
import javax.inject.Singleton
import reactivemongo.api.bson.collection.{BSONCollection, BSONSerializationPack}
import storeServices.util.AppConfigLib.getConfig

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{AsyncDriver, Cursor, DB, MongoConnection}
import reactivemongo.api.bson.{document, BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}
import reactivemongo.api.indexes.{Index, IndexType, NSIndex}
import storeServices.ServerMain.scheduler
import storeServices.util.PipeOperator._

object MongoModule extends TwitterModule {

  val mongoUri =
    flag[String]("MONGO_URI", getConfig[String]("MONGO_URI").getOrElse("mongodb://localhost:27017/DB"), "DB URI")

  private val driver = new AsyncDriver()

  @Singleton
  @Provides
  def db: Future[DB] = {
    val parsedUri        = MongoConnection.fromString(mongoUri())
    val futureConnection = parsedUri.flatMap(driver.connect(_))
    futureConnection.flatMap(_.database("DB"))
  }

  @Singleton
  @Provides
  def collection: Future[BSONCollection] = {
    val idx = Index(BSONSerializationPack)(
      key = Seq("title" -> IndexType.Ascending),
      unique = true,
      name = None,
      background = true,
      dropDups = false,
      sparse = false,
      expireAfterSeconds = None,
      storageEngine = None,
      weights = None,
      defaultLanguage = None,
      languageOverride = None,
      textIndexVersion = None,
      sphereIndexVersion = None,
      bits = None,
      min = None,
      max = None,
      bucketSize = None,
      collation = None,
      wildcardProjection = None,
      version = None,
      partialFilter = None,
      options = BSONDocument.empty
    )
    db.map(_.collection("product"))
      .map(
        _.$$(
          _.indexesManager
            .create(idx)
        )
      )
  }

}
