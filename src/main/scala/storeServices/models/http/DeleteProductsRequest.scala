package storeServices.models.http

import com.twitter.finatra.http.annotations.RouteParam
import com.twitter.finatra.validation.constraints.{NotEmpty, UUID}

final case class DeleteProductsRequest(
    @RouteParam @NotEmpty @UUID productId: String
)
