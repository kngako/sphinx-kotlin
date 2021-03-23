package chat.sphinx.feature_network_query_chat

import app.cash.exhaustive.Exhaustive
import chat.sphinx.concept_network_client.NetworkClient
import chat.sphinx.concept_network_query_chat.model.ChatDto
import chat.sphinx.concept_network_query_chat.NetworkQueryChat
import chat.sphinx.concept_relay.RelayDataHandler
import chat.sphinx.concept_relay.retrieveRelayUrlAndJavaWebToken
import chat.sphinx.feature_network_query_chat.model.GetChatsRelayResponse
import chat.sphinx.kotlin_response.ResponseError
import chat.sphinx.kotlin_response.KotlinResponse
import chat.sphinx.kotlin_response.LoadResponse
import chat.sphinx.network_relay_call.RelayCall
import chat.sphinx.wrapper_relay.JavaWebToken
import chat.sphinx.wrapper_relay.RelayUrl
import com.squareup.moshi.Moshi
import io.matthewnelson.concept_coroutines.CoroutineDispatchers
import kotlinx.coroutines.flow.*

class NetworkQueryChatImpl(
    private val dispatchers: CoroutineDispatchers,
    private val moshi: Moshi,
    private val networkClient: NetworkClient,
    private val relayDataHandler: RelayDataHandler
): NetworkQueryChat() {

    companion object {
        private const val ENDPOINT_CHAT = "/chat"
        private const val ENDPOINT_CHATS = "/chats"
        private const val ENDPOINT_GROUP = "/group"
        private const val ENDPOINT_KICK = "/kick"
        private const val ENDPOINT_MEMBER = "/member"
        private const val ENDPOINT_TRIBE = "/tribe"
    }

    ///////////
    /// GET ///
    ///////////
    override fun getChats(): Flow<LoadResponse<List<ChatDto>, ResponseError>> = flow {
        relayDataHandler.retrieveRelayUrlAndJavaWebToken().let { response ->
            @Exhaustive
            when (response) {
                is KotlinResponse.Error -> {
                    emit(response)
                }
                is KotlinResponse.Success -> {
                    emitAll(
                        getChats(response.value.first, response.value.second)
                    )
                }
            }
        }
    }

    override fun getChats(
        javaWebToken: JavaWebToken,
        relayUrl: RelayUrl
    ): Flow<LoadResponse<List<ChatDto>, ResponseError>> =
        RelayCall.Get.execute(
            dispatchers = dispatchers,
            jwt = javaWebToken,
            moshi = moshi,
            adapterClass = GetChatsRelayResponse::class.java,
            networkClient = networkClient,
            url = relayUrl.value + ENDPOINT_CHATS
        )

    ///////////
    /// PUT ///
    ///////////
//    app.put('/chats/:id', chats.updateChat)
//    app.put('/chat/:id', chats.addGroupMembers)
//    app.put('/kick/:chat_id/:contact_id', chats.kickChatMember)
//    app.put('/member/:contactId/:status/:messageId', chatTribes.approveOrRejectMember)
//    app.put('/group/:id', chatTribes.editTribe)

    ////////////
    /// POST ///
    ////////////
//    app.post('/group', chats.createGroupChat)
//    app.post('/chats/:chat_id/:mute_unmute', chats.mute)
//    app.post('/tribe', chatTribes.joinTribe)

    //////////////
    /// DELETE ///
    //////////////
//    app.delete('/chat/:id', chats.deleteChat)
}