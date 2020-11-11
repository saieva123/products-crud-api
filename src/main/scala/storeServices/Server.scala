package storeServices

import storeServices.modules.{CustomJacksonModule, MongoModule, ServiceSwaggerModule}
import storeServices.controllers.AdminController
import storeServices.controllers.MainController
import storeServices.filters.CommonFilters
import storeServices.util.AppConfigLib._
import storeServices.util.PipeOperator._
import com.jakehschwartz.finatra.swagger.DocsController
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.util.Var
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import perfolation._

object ServerMain extends Server

class Server extends HttpServer {
  val health = Var("good")

  implicit lazy val scheduler: SchedulerService = Scheduler.io("services")

  final override protected def modules = Seq(ServiceSwaggerModule, MongoModule)
  override def jacksonModule           = CustomJacksonModule

  final override def defaultHttpPort = getConfig[String]("FINATRA_HTTP_PORT").fold(":9999")(x => p":$x")
  final override val name            = "productService"

  final override def configureHttp(router: HttpRouter): Unit =
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[DocsController]
      .add[AdminController]
      .add[MainController]
      .|>(_ => ())
}
