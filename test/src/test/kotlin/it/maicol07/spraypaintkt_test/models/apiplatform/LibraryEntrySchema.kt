package it.maicol07.spraypaintkt_test.models.apiplatform

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.Relation
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import kotlin.Any
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.collections.List

@ResourceSchema(
    resourceType = "LibraryEntry",
    endpoint = "api/library_entries",
)
public interface LibraryEntrySchema {
    @Attr
    public var gameId: Int?

    @Attr
    public var status: LibraryEntryStatus?

    @Attr
    public var completionStatus: LibraryEntryCompletionStatus?

    @Attr
    public var owned: Boolean?

    @Attr
    public var platformsIds: List<Any?>?

    @Attr
    public var startDate: String?

    @Attr
    public var endDate: String?

    @Attr
    public var playedTime: Int?

    @Attr
    public var rating: Float?

    @Attr
    public var ratingDetails: List<Any?>?

    @Attr
    public var review: String?

    @Attr(mutable = false)
    public val createdAt: String?

    @Attr(mutable = false)
    public val updatedAt: String?

    @Attr
    public var editionsIds: List<Any?>?

    @Attr
    public var userId: Any?

    @Relation
    public val user: UserSchema?
}

public enum class LibraryEntryStatus {
    PLAYING,
    COMPLETED,
    PAUSED,
    ABANDONED,
    BACKLOG,
}

public enum class LibraryEntryCompletionStatus {
    MAIN_STORY,
    MAIN_PLUS_SIDES,
    FULL_100,
}
