package storeServices.modules

import com.google.inject.Provides
import com.jakehschwartz.finatra.swagger.SwaggerModule
import io.swagger.models.{Contact, Info, Swagger}
import io.swagger.models.auth.BasicAuthDefinition

object ServiceSwaggerModule extends SwaggerModule {
  val swaggerUI      = new Swagger()
  val serviceVersion = flag[String]("service.version", "NA", "the version of service")

  @Provides
  def swagger: Swagger = {

    val info = new Info()
      .contact(new Contact().name("Elsa").email("saieva.bi01g@g2.nctu.edu.tw"))
      .description("**CRUD for product management sample** - A service that serves as product management.")
      .version(serviceVersion())
      .title("Product Management API")

    swaggerUI
      .info(info)
      .addSecurityDefinition(
        "sampleBasic", {
          val d = new BasicAuthDefinition()
          d.setType("basic")
          d
        }
      )

    swaggerUI
  }
}
