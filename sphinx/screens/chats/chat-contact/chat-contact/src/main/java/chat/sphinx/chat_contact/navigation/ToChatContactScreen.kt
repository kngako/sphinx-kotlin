package chat.sphinx.chat_contact.navigation

import androidx.navigation.NavController
import chat.sphinx.chat_contact.R
import chat.sphinx.wrapper_chat.Chat
import chat.sphinx.wrapper_contact.Contact
import io.matthewnelson.android_feature_navigation.DefaultNavOptions
import io.matthewnelson.concept_navigation.NavigationRequest

class ToChatContactScreen(
    val chat: Chat?,
    val contact: Contact
): NavigationRequest<NavController>() {
    override fun navigate(controller: NavController) {
        controller.navigate(
            R.id.chat_contact_nav_graph,
            null,
            DefaultNavOptions.defaultAnimsBuilt
        )
    }
}
