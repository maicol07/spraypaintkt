package it.maicol07.spraypaintkt.tests

import it.maicol07.spraypaintkt.tests.models.User
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * NOTE: Since we don't have a fake API to test against, we won't use save methods in the tests.
 * Instead, we'll compare the payload sent to the server with the expected one.
 */
class SaveTest : BaseTest() {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun save() = runTest {
        val response = client.find<User>("1")
        val user = response.data

        assertEquals("""{
  "data": {
    "type": "users",
    "id": "1",
    "attributes": {
      "username": "Toby",
      "displayName": "Toby",
      "avatarUrl": "https://discuss.flarum.org/assets/avatars/lwnpBO5jpgtAtK3l.png",
      "slug": "Toby",
      "joinTime": "2014-11-19T23:47:00+00:00",
      "discussionCount": 69,
      "commentCount": 1249,
      "canEdit": false,
      "canEditCredentials": false,
      "canEditGroups": false,
      "canDelete": false,
      "canSuspend": false,
      "canModerateExports": false,
      "usernameHistory": null,
      "bio": "Flarum founder and ex-lead developer. http://twitter.com/tobyzerner",
      "canViewBio": true,
      "canEditBio": false,
      "blocksPd": false,
      "cannotBeDirectMessaged": false,
      "points": 0,
      "canHaveVotingNotifications": true,
      "ignored": false,
      "canBeIgnored": true,
      "bestAnswerCount": 0,
      "canSpamblock": false,
      "canViewWarnings": false,
      "canManageWarnings": false,
      "canDeleteWarnings": false,
      "visibleWarningCount": 0
    },
    "relationships": {
      "groups": {
        "data": [
          {
            "type": "groups",
            "id": "11"
          },
          {
            "type": "groups",
            "id": "12"
          }
        ]
      },
      "ranks": {
        "data": []
      }
    }
  }
}""", user.toJsonApiString {
            prettyPrint = true
            prettyPrintIndent = "  "
        })

        user.username = "Toby2"
        user.displayName = "Toby2"

        assertEquals("""{
  "data": {
    "type": "users",
    "id": "1",
    "attributes": {
      "username": "Toby2",
      "displayName": "Toby2",
      "avatarUrl": "https://discuss.flarum.org/assets/avatars/lwnpBO5jpgtAtK3l.png",
      "slug": "Toby",
      "joinTime": "2014-11-19T23:47:00+00:00",
      "discussionCount": 69,
      "commentCount": 1249,
      "canEdit": false,
      "canEditCredentials": false,
      "canEditGroups": false,
      "canDelete": false,
      "canSuspend": false,
      "canModerateExports": false,
      "usernameHistory": null,
      "bio": "Flarum founder and ex-lead developer. http://twitter.com/tobyzerner",
      "canViewBio": true,
      "canEditBio": false,
      "blocksPd": false,
      "cannotBeDirectMessaged": false,
      "points": 0,
      "canHaveVotingNotifications": true,
      "ignored": false,
      "canBeIgnored": true,
      "bestAnswerCount": 0,
      "canSpamblock": false,
      "canViewWarnings": false,
      "canManageWarnings": false,
      "canDeleteWarnings": false,
      "visibleWarningCount": 0
    },
    "relationships": {
      "groups": {
        "data": [
          {
            "type": "groups",
            "id": "11"
          },
          {
            "type": "groups",
            "id": "12"
          }
        ]
      },
      "ranks": {
        "data": []
      }
    }
  }
}""", user.toJsonApiString {
            prettyPrint = true
            prettyPrintIndent = "  "
        })
    }
}