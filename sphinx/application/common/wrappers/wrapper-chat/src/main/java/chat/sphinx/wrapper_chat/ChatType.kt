package chat.sphinx.wrapper_chat


@Suppress("NOTHING_TO_INLINE")
inline fun ChatType.isContact(): Boolean =
    this is ChatType.Contact

@Suppress("NOTHING_TO_INLINE")
inline fun ChatType.isGroup(): Boolean =
    this is ChatType.Group

@Suppress("NOTHING_TO_INLINE")
inline fun ChatType.isTribe(): Boolean =
    this is ChatType.Tribe



/**
 * Converts the integer value returned over the wire to an object.
 *
 * @throws [IllegalArgumentException] if the integer is not supported
 * */
@Suppress("NOTHING_TO_INLINE")
@Throws(IllegalArgumentException::class)
inline fun Int.toChatType(): ChatType =
    when (this) {
        ChatType.CONTACT -> {
            ChatType.Contact
        }
        ChatType.GROUP -> {
            ChatType.Group
        }
        ChatType.TRIBE -> {
            ChatType.Tribe
        }
        else -> {
            throw IllegalArgumentException(
                "ChatType for integer '$this' not supported"
            )
        }
    }


/**
 * Comes off the wire as:
 *  - 0 (Contact)
 *  - 1 (Group)
 *  - 2 (Tribe)
 *
 * https://github.com/stakwork/sphinx-relay/blob/7f8fd308101b5c279f6aac070533519160aa4a9f/src/constants.ts#L74
 * */
sealed class ChatType {

    companion object {
        const val CONTACT = 0
        const val GROUP = 1
        const val TRIBE = 2
    }

    abstract val value: Int

    object Contact: ChatType() {
        override val value: Int
            get() = CONTACT
    }

    object Group: ChatType() {
        override val value: Int
            get() = GROUP
    }

    object Tribe: ChatType() {
        override val value: Int
            get() = TRIBE
    }
}