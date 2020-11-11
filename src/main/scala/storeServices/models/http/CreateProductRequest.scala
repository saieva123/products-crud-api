package storeServices.models.http

import com.twitter.finatra.validation.constraints.NotEmpty

final case class CreateProductRequest(
    @NotEmpty title: String,
    @NotEmpty showName: String,
    desc: Option[String],
    tags: List[String],
    published: Boolean,
    images: Option[String]
)
