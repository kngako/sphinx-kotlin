package chat.sphinx.chat_common.ui.viewstate.messageholder

import chat.sphinx.concept_link_preview.model.*
import chat.sphinx.wrapper_chat.ChatType
import chat.sphinx.wrapper_common.PhotoUrl
import chat.sphinx.wrapper_common.lightning.*
import chat.sphinx.wrapper_common.tribe.TribeJoinLink
import chat.sphinx.wrapper_contact.ContactAlias
import chat.sphinx.wrapper_message.MessageType
import chat.sphinx.wrapper_message_media.MessageMedia

internal sealed class LayoutState private constructor() {

    data class MessageStatusHeader(
        val senderName: String?,
        val showSent: Boolean,
        val showSendingIcon: Boolean,
        val showBoltIcon: Boolean,
        val showFailedContainer: Boolean,
        val showLockIcon: Boolean,
        val timestamp: String,
    ): LayoutState() {
        val showReceived: Boolean
            get() = !showSent
    }


    data class GroupActionIndicator(
        val actionType: MessageType.GroupAction,
        val chatType: ChatType?,
        val isAdminView: Boolean,
        val subjectName: String?,
    ): LayoutState()


    data class DeletedMessage(
        val gravityStart: Boolean,
        val timestamp: String,
    ): LayoutState()

    sealed class Bubble private constructor(): LayoutState() {

        sealed class ContainerFirst private constructor(): Bubble() {

            data class ReplyMessage(
                val showSent: Boolean,
                val sender: String,
                val text: String,
                val url: String?,
                val media: MessageMedia?,
            ): ContainerFirst() {
                val showReceived: Boolean
                    get() = !showSent
            }

        }

        sealed class ContainerSecond private constructor(): Bubble() {

            data class PaidMessageSentStatus(
                val amount: Sat,
                val purchaseType: MessageType.Purchase?,
            ): ContainerSecond() {
                val amountText: String
                    get() = amount.asFormattedString(appendUnit = true)
            }

            data class DirectPayment(
                val showSent: Boolean,
                val amount: Sat
            ): ContainerSecond() {
                val showReceived: Boolean
                    get() = !showSent

                val unitLabel: String
                    get() = amount.unit
            }

            data class ImageAttachment(
                val url: String,
                val media: MessageMedia?,
            ): ContainerSecond()

            data class PodcastBoost(
                val amount: Sat,
            ): ContainerSecond()

            data class CallInvite(
                val videoButtonVisible: Boolean
            ): ContainerSecond()

            data class BotResponse(
                val html: String
            ): ContainerSecond()

            // FileAttachment
            // AudioAttachment
            // VideoAttachment

            // Invoice
        }

        sealed class ContainerThird private constructor(): Bubble() {

            data class UnsupportedMessageType(
                val messageType: MessageType,
                val gravityStart: Boolean,
            ): ContainerThird()

            data class Message(
                val text: String
            ): ContainerThird()

            sealed class LinkPreview private constructor(): ContainerThird() {

                data class ContactPreview(
                    val alias: ContactAlias?,
                    val photoUrl: PhotoUrl?,
                    val showBanner: Boolean,

                    // Used only to anchor data for click listeners
                    val lightningNodeDescriptor: LightningNodeDescriptor
                ): LinkPreview()

                data class HttpUrlPreview(
                    val title: HtmlPreviewTitle?,
                    val domainHost: HtmlPreviewDomainHost,
                    val description: PreviewDescription?,
                    val imageUrl: PreviewImageUrl?,
                    val favIconUrl: HtmlPreviewFavIconUrl?,

                    // Used only to anchor data for click listeners
                    val url: String,
                ): LinkPreview()

                data class TribeLinkPreview(
                    val name: TribePreviewName,
                    val description: PreviewDescription?,
                    val imageUrl: PreviewImageUrl?,
                    val showBanner: Boolean,

                    // Used only to anchor data for click listeners
                    val joinLink: TribeJoinLink,
                ) : LinkPreview()
            }

        }

        sealed class ContainerFourth private constructor(): Bubble() {

            data class Boost(
                private val totalAmount: Sat,
                val senderPics: Set<BoostReactionImageHolder>
            ): ContainerFourth() {
                val amountText: String
                    get() = totalAmount.asFormattedString()

                val amountUnitLabel: String
                    get() = totalAmount.unit

                // will be gone if null is returned
                val numberUniqueBoosters: Int?
                    get() = if (senderPics.size > 1) {
                        senderPics.size
                    } else {
                        null
                    }
            }

            data class PaidMessageDetails(
                val amount: Sat,
                val purchaseType: MessageType.Purchase?,
                val isShowingReceivedMessage: Boolean,
                val showPaymentAcceptedIcon: Boolean,
                val showPaymentProgressWheel: Boolean,
                val showSendPaymentIcon: Boolean,
                val showPaymentReceivedIcon: Boolean,
            ): ContainerFourth() {
                val amountText: String
                    get() = amount.asFormattedString(appendUnit = true)
            }
        }
    }
}

// TODO: TEMPORARY!!! until Initial holder can be refactored...

@JvmInline
value class SenderPhotoUrl(val value: String): BoostReactionImageHolder

@JvmInline
value class SenderInitials(val value: String): BoostReactionImageHolder

sealed interface BoostReactionImageHolder
