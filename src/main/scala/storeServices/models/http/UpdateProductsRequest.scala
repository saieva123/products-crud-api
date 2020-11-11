package storeServices.models.http

import com.twitter.finatra.http.annotations.RouteParam
import com.twitter.finatra.validation.constraints.{NotEmpty, UUID}

final case class UpdateProductsRequest(
    @RouteParam @NotEmpty @UUID productId: String,
    @NotEmpty title: Option[String],
    @NotEmpty showName: Option[String],
    desc: Option[String],
    tags: Option[List[String]],
    published: Option[Boolean],
    images: Option[String]
)
