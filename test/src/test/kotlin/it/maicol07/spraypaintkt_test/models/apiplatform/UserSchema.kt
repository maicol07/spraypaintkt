package it.maicol07.spraypaintkt_test.models.apiplatform

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import kotlin.String

@ResourceSchema(
    resourceType = "User",
    endpoint = "api/users",
)
public interface UserSchema {
    @Attr
    public var nickname: String?

    @Attr
    public var firstName: String?

    @Attr
    public var lastName: String?

    @Attr
    public var picture: String?

    @Attr
    public var emailVerifiedAt: String?

    @Attr(mutable = false)
    public val createdAt: String?

    @Attr(mutable = false)
    public val updatedAt: String?

    @Attr(mutable = false)
    public val deletedAt: String?

    @Attr
    public var name: String?
}
