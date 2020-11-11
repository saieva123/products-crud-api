package storeServices.models

final case class BsonProduct(
    productId: String,
    title: String,
    showName: String,
    desc: Option[String],
    tags: List[String],
    published: Boolean,
    images: Option[String]
)
