package chat.sphinx.dashboard.ui.adapter

import android.content.Context
import chat.sphinx.dashboard.R
import chat.sphinx.dashboard.ui.adapter.DashboardChat.Active
import chat.sphinx.dashboard.ui.adapter.DashboardChat.Inactive
import chat.sphinx.wrapper_chat.Chat
import chat.sphinx.wrapper_chat.isConversation
import chat.sphinx.wrapper_common.*
import chat.sphinx.wrapper_common.dashboard.ContactId
import chat.sphinx.wrapper_common.invite.InviteStatus
import chat.sphinx.wrapper_common.lightning.Sat
import chat.sphinx.wrapper_contact.Contact
import chat.sphinx.wrapper_message.*
import chat.sphinx.wrapper_message_media.MediaType
import kotlinx.coroutines.flow.Flow
import chat.sphinx.wrapper_invite.Invite as InviteWrapper

/**
 * [DashboardChat]s are separated into 2 categories:
 *  - [Active]: An active chat
 *  - [Inactive]: A contact without a conversation yet, or an Invite
 * */
sealed class DashboardChat {

    abstract val chatName: String?
    abstract val photoUrl: PhotoUrl?
    abstract val sortBy: Long

    abstract val unseenMessageFlow: Flow<Long?>?

    abstract fun getDisplayTime(today00: DateTime): String

    abstract fun getMessageText(context: Context): String

    abstract fun hasUnseenMessages(): Boolean

    abstract fun isEncrypted(): Boolean

    sealed class Active: DashboardChat() {

        abstract val chat: Chat
        abstract val message: Message?

        override val sortBy: Long
            get() = message?.date?.time ?: chat.createdAt.time

        override fun getDisplayTime(today00: DateTime): String {
            return message?.date?.chatTimeFormat(today00) ?: ""
        }

        fun isMessageSenderSelf(message: Message): Boolean =
            message.sender == chat.contactIds.firstOrNull()

        abstract fun getMessageSender(message: Message, context: Context, withColon: Boolean = true): String

        override fun hasUnseenMessages(): Boolean {
            val ownerId: ContactId? = chat.contactIds.firstOrNull()
            val isLastMessageOutgoing = message?.sender == ownerId
            val lastMessageSeen = message?.seen?.isTrue() ?: true
            val chatSeen = chat.seen.isTrue()
            return !lastMessageSeen && !chatSeen && !isLastMessageOutgoing
        }

        override fun isEncrypted(): Boolean {
            return true
        }

        @ExperimentalStdlibApi
        override fun getMessageText(context: Context): String {
            val message: Message? = message
            return when {
                message == null -> {
                    ""
                }
                message.messageDecryptionError -> {
                    context.getString(R.string.decryption_error)
                }
                message.status.isDeleted() -> {
                    context.getString(R.string.last_message_description_message_deleted)
                }
                message.type.isMessage() -> {
                    message.messageContentDecrypted?.value?.let { decrypted ->
                        when {
                            message.giphyData != null -> {
                                String.format(context.getString(R.string.last_message_description_gif_shared), getMessageSender(message, context))
                            }
                            message.podBoost != null -> {
                                val amount: Long = message.podBoost?.amount?.value ?: message.amount.value
                                val unit: String = if (amount > 1) "sats" else "sat"
                                String.format(context.getString(R.string.last_message_description_boost), amount, unit)
                            }
                            // TODO: check for clip::
                            else -> {
                                "${getMessageSender(message, context)}$decrypted"
                            }
                        }
                    } ?: "${getMessageSender(message, context)}..."
                }
                message.type.isInvoice() -> {
                    val amount: String = if (message.amount.value > 1) {
                        "${message.amount.value} sats"
                    } else {
                        "${message.amount.value} sat"
                    }

                    if (isMessageSenderSelf(message)) {
                        String.format(context.getString(R.string.last_message_description_invoice_sent), amount)
                    } else {
                        String.format(context.getString(R.string.last_message_description_invoice_receive), amount)
                    }

                }
                message.type.isPayment() || message.type.isDirectPayment() -> {
                    val amount: String = if (message.amount.value > 1) {
                        "${message.amount.value} sats"
                    } else {
                        "${message.amount.value} sat"
                    }

                    if (isMessageSenderSelf(message)) {
                        String.format(context.getString(R.string.last_message_description_payment_sent), amount)
                    } else {
                        String.format(context.getString(R.string.last_message_description_payment_received), amount)
                    }
                }
                message.type.isAttachment() -> {
                    message.messageMedia?.let { media ->
                        when (val type = media.mediaType) {
                            is MediaType.Audio -> {
                                R.string.last_message_description_an_audio_clip
                            }
                            is MediaType.Image -> {
                                if (type.isGif) {
                                    R.string.last_message_description_a_gif
                                } else {
                                    R.string.last_message_description_an_image
                                }
                            }
                            is MediaType.Pdf -> {
                                R.string.last_message_description_a_pdf
                            }
                            is MediaType.Text -> {
                                R.string.last_message_description_a_paid_message
                            }
                            is MediaType.Unknown -> {
                                R.string.last_message_description_an_attachment
                            }
                            is MediaType.Video -> {
                                R.string.last_message_description_a_video
                            }
                            else -> {
                                null
                            }
                        }?.let { stringId ->
                            val sentString = context.getString(R.string.last_message_description_sent)
                            val element = context.getString(stringId)

                            "${getMessageSender(message, context,false)} $sentString $element"
                        }
                    } ?: ""
                }
                message.type.isGroupJoin() -> {
                    String.format(context.getString(R.string.last_message_description_has_join_tribe), getMessageSender(message, context, false))
                }
                message.type.isGroupLeave() -> {
                    String.format(context.getString(R.string.last_message_description_just_left_tribe), getMessageSender(message, context, false))
                }
                message.type.isBoost() -> {
                    val amount: Long = message.podBoost?.amount?.value ?: message.amount.value
                    val unit: String = if (amount > 1) "sats" else "sat"
                    String.format(context.getString(R.string.last_message_description_boost), amount, unit)
                }
                else -> {
                    ""
                }
            }
        }

        class Conversation(
            override val chat: Chat,
            override val message: Message?,
            val contact: Contact,
            override val unseenMessageFlow: Flow<Long?>,
        ): Active() {

            init {
                require(chat.type.isConversation()) {
                    """
                    DashboardChat.Conversation is strictly for
                    Contacts. Use DashboardChat.GroupOrTribe.
                """.trimIndent()
                }
            }

            override val chatName: String?
                get() = contact.alias?.value

            override val photoUrl: PhotoUrl?
                get() = chat.photoUrl ?: contact.photoUrl

            override fun getMessageSender(message: Message, context: Context, withColon: Boolean): String {
                if (isMessageSenderSelf(message)) {
                    return context.getString(R.string.last_message_description_you) + if (withColon) ": " else ""
                }

                return contact.alias?.let { alias ->
                    alias.value + if (withColon) ": " else ""
                } ?: ""
            }

        }

        class GroupOrTribe(
            override val chat: Chat,
            override val message: Message?,
            override val unseenMessageFlow: Flow<Long?>,
        ): Active() {

            override val chatName: String?
                get() = chat.name?.value

            override val photoUrl: PhotoUrl?
                get() = chat.photoUrl

            override fun getMessageSender(message: Message, context: Context, withColon: Boolean): String {
                if (isMessageSenderSelf(message)) {
                    return context.getString(R.string.last_message_description_you) + if (withColon) ": " else ""
                }

                return message.senderAlias?.let { alias ->
                    alias.value + if (withColon) ": " else ""
                } ?: ""
            }

        }
    }

    /**
     * Inactive chats are for newly added contacts that are awaiting
     * messages to be sent (the Chat has not been created yet)
     * */
    sealed class Inactive: DashboardChat() {

        override fun getDisplayTime(today00: DateTime): String {
            return ""
        }

        class Conversation(
            val contact: Contact
        ): Inactive() {

            override val chatName: String?
                get() = contact.alias?.value

            override val photoUrl: PhotoUrl?
                get() = contact.photoUrl

            override val sortBy: Long
                get() = contact.createdAt.time

            override val unseenMessageFlow: Flow<Long?>?
                get() = null

            override fun getMessageText(context: Context): String {
                return ""
            }

            override fun hasUnseenMessages(): Boolean {
                return false
            }

            override fun isEncrypted(): Boolean {
                return !(contact.rsaPublicKey?.value?.isEmpty() ?: true)
            }

        }

        class Invite(
            val contact: Contact,
            val invite: InviteWrapper?
        ): Inactive() {

            override val chatName: String?
                get() =  "Invite: ${contact.alias?.value ?: "Unknown"}"

            override val photoUrl: PhotoUrl?
                get() = contact.photoUrl

            override val sortBy: Long
                get() = Long.MAX_VALUE

            override val unseenMessageFlow: Flow<Long?>?
                get() = null

            fun getChatName(context: Context): String {
                val contactAlias = contact.alias?.value ?: context.getString(R.string.unknown)
                return String.format(context.getString(R.string.last_message_description_invite), contactAlias)
            }

            override fun getMessageText(context: Context): String {

                return when (invite?.status) {
                    is InviteStatus.Pending -> {
                        String.format(context.getString(R.string.last_message_description_looking_available_node), (contact.alias?.value ?: "Unknown"))
                    }
                    is InviteStatus.Ready, InviteStatus.Delivered -> {
                        context.getString(R.string.last_message_description_invite_ready)
                    }
                    is InviteStatus.InProgress -> {
                        val chatName = getChatName(context) ?: context.getString(R.string.unknown)
                        String.format(context.getString(R.string.last_message_description_invite_signing_on), chatName)
                    }
                    is InviteStatus.PaymentPending -> {
                        context.getString(R.string.last_message_description_invite_tap_to_pay)
                    }
                    is InviteStatus.ProcessingPayment -> {
                        context.getString(R.string.last_message_description_invite_payment_sent)
                    }
                    is InviteStatus.Complete -> {
                        context.getString(R.string.last_message_description_invite_signup_complete)
                    }
                    is InviteStatus.Expired -> {
                        context.getString(R.string.last_message_description_invite_expired)
                    }

                    null,
                    is InviteStatus.Unknown -> {
                        ""
                    }
                }
            }

            fun getInviteIconAndColor(): Pair<Int, Int>? {

                return when (invite?.status) {
                    is InviteStatus.Pending -> {
                        Pair(R.string.material_icon_name_invite_pending, R.color.sphinxOrange)
                    }
                    is InviteStatus.Ready, InviteStatus.Delivered -> {
                        Pair(R.string.material_icon_name_invite_ready, R.color.primaryGreen)
                    }
                    is InviteStatus.InProgress -> {
                        Pair(R.string.material_icon_name_invite_in_progress, R.color.primaryBlue)
                    }
                    is InviteStatus.PaymentPending -> {
                        Pair(R.string.material_icon_name_invite_payment_pending, R.color.secondaryText)
                    }
                    is InviteStatus.ProcessingPayment -> {
                        Pair(R.string.material_icon_name_invite_payment_sent, R.color.secondaryText)
                    }
                    is InviteStatus.Complete -> {
                        Pair(R.string.material_icon_name_invite_complete, R.color.primaryGreen)
                    }
                    is InviteStatus.Expired -> {
                        Pair(R.string.material_icon_name_invite_expired, R.color.primaryRed)
                    }

                    null,
                    is InviteStatus.Unknown -> {
                        null
                    }
                }
            }

            fun getInvitePrice(): Sat? {
                return invite?.price
            }

            override fun hasUnseenMessages(): Boolean {
                return false
            }

            override fun isEncrypted(): Boolean {
                return false
            }
        }
    }
}
