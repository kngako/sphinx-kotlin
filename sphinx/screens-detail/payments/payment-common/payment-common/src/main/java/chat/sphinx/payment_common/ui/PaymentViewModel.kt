package chat.sphinx.payment_common.ui

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavArgs
import chat.sphinx.concept_repository_contact.ContactRepository
import chat.sphinx.concept_repository_message.model.SendPayment
import chat.sphinx.payment_common.navigation.PaymentNavigator
import chat.sphinx.payment_common.ui.viewstate.AmountViewState
import chat.sphinx.wrapper_common.dashboard.ChatId
import chat.sphinx.wrapper_common.dashboard.ContactId
import chat.sphinx.wrapper_contact.Contact
import io.matthewnelson.android_feature_viewmodel.SideEffectViewModel
import io.matthewnelson.concept_coroutines.CoroutineDispatchers
import io.matthewnelson.concept_views.viewstate.ViewState
import io.matthewnelson.concept_views.viewstate.ViewStateContainer
import kotlinx.coroutines.flow.*

abstract class PaymentViewModel<ARGS: NavArgs, VS: ViewState<VS>>(
    dispatchers: CoroutineDispatchers,
    val navigator: PaymentNavigator,
    private val contactRepository: ContactRepository,
    initialViewState: VS
): SideEffectViewModel<
        PaymentSideEffectFragment,
        PaymentSideEffect,
        VS
        >(dispatchers, initialViewState)
{
    private val sendPaymentBuilder = SendPayment.Builder()

    protected abstract val args: ARGS

    protected abstract val chatId: ChatId?
    protected abstract val contactId: ContactId?

    val amountViewStateContainer: ViewStateContainer<AmountViewState> by lazy {
        ViewStateContainer(AmountViewState.Idle)
    }

    protected val contactSharedFlow: SharedFlow<Contact?> = flow {
        contactId?.let { contactId ->
            emitAll(contactRepository.getContactById(contactId))
        } ?: run {
            emit(null)
        }
    }.distinctUntilChanged().shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1,
    )

    abstract fun updateAmount(amountString: String)

}
