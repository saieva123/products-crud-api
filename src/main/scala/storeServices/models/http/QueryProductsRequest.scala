package storeServices.models.http

import com.twitter.finatra.http.annotations.QueryParam
import com.twitter.finatra.validation.constraints.NotEmpty

final case class QueryProductsRequest(
    @QueryParam @NotEmpty productId: Option[String],
    @QueryParam @NotEmpty title: Option[String],
    @QueryParam @NotEmpty tag: Option[String],
    @QueryParam published: Option[Boolean]
)
