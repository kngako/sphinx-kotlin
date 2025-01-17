package chat.sphinx.concept_network_query_contact.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetTribeMembersResponse(
    val contacts: List<ContactDto>
)
