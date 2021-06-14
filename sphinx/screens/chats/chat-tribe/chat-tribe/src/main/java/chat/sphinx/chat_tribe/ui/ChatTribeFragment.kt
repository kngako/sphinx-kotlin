package chat.sphinx.chat_tribe.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import app.cash.exhaustive.Exhaustive
import by.kirich1409.viewbindingdelegate.viewBinding
import chat.sphinx.chat_common.databinding.LayoutChatFooterBinding
import chat.sphinx.chat_common.databinding.LayoutChatHeaderBinding
import chat.sphinx.chat_common.ui.ChatFragment
import chat.sphinx.chat_common.ui.viewstate.header.ChatHeaderFooterViewState
import chat.sphinx.chat_tribe.R
import chat.sphinx.chat_tribe.databinding.FragmentChatTribeBinding
import chat.sphinx.chat_tribe.databinding.LayoutPodcastPlayerFooterBinding
import chat.sphinx.chat_tribe.navigation.TribeChatNavigator
import chat.sphinx.concept_image_loader.ImageLoader
import chat.sphinx.podcast_player.objects.Podcast
import dagger.hilt.android.AndroidEntryPoint
import io.matthewnelson.android_feature_screens.util.goneIfFalse
import io.matthewnelson.concept_views.viewstate.collect
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
internal class ChatTribeFragment: ChatFragment<
        FragmentChatTribeBinding,
        ChatTribeFragmentArgs,
        ChatTribeViewModel,
        >(R.layout.fragment_chat_tribe)
{
    private val podcastPlayerBinding: LayoutPodcastPlayerFooterBinding by viewBinding(LayoutPodcastPlayerFooterBinding::bind, R.id.include_podcast_player_footer)

    override val binding: FragmentChatTribeBinding by viewBinding(FragmentChatTribeBinding::bind)

    override val footerBinding: LayoutChatFooterBinding by viewBinding(LayoutChatFooterBinding::bind, R.id.include_chat_tribe_footer)
    override val headerBinding: LayoutChatHeaderBinding by viewBinding(LayoutChatHeaderBinding::bind, R.id.include_chat_tribe_header)

    override val recyclerView: RecyclerView
        get() = binding.recyclerViewMessages

    override val viewModel: ChatTribeViewModel by viewModels()

    @Inject
    override lateinit var chatNavigator: TribeChatNavigator

    @Inject
    protected lateinit var imageLoaderInj: ImageLoader<ImageView>
    override val imageLoader: ImageLoader<ImageView>
        get() = imageLoaderInj


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onStopSupervisor.scope.launch(viewModel.mainImmediate) {
            viewModel.loadTribeAndPodcastData().collect { podcast ->
                configurePodcastPlayer(podcast)
                addPodcastOnClickListeners(podcast)
            }
        }
    }

    private suspend fun configurePodcastPlayer(podcast: Podcast) {
        podcastPlayerBinding.apply {
            if (root.isGone) {
                scrollToBottom(callback = {
                    root.goneIfFalse(true)
                })
            }

            textViewPlayPauseButton.text = getString(
                if (podcast.isPlaying) R.string.material_icon_name_pause_button else R.string.material_icon_name_play_button
            )

            val currentEpisode = podcast.getCurrentEpisode()
            textViewEpisodeTitle.text = currentEpisode.title

            val progress: Int = withContext(viewModel.io) {
                try {
                    podcast.getPlayingProgress()
                } catch (e: ArithmeticException) {
                    0
                }
            }

            progressBar.layoutParams.width =
                progressBarContainer.measuredWidth * (progress / 100)
            progressBar.requestLayout()
        }
    }

    private fun addPodcastOnClickListeners(podcast: Podcast) {
        podcastPlayerBinding.apply {
            val currentEpisode = podcast.getCurrentEpisode()

            textViewEpisodeTitle.setOnClickListener {
                viewModel.goToPodcastPlayerScreen(podcast)
            }

            textViewPlayPauseButton.setOnClickListener {
                if (currentEpisode.playing) {
                    viewModel.pauseEpisode(currentEpisode)
                } else {
                    viewModel.playEpisode(currentEpisode, podcast.currentTime)
                }
            }

            textViewForward30Button.setOnClickListener {
                viewModel.seekTo(podcast.currentTime + 30)
            }

            textViewBoostPodcastButton.setOnClickListener {
                //TODO: Boost podcast episode
            }
        }
    }

    override fun subscribeToViewStateFlow() {
        super.subscribeToViewStateFlow()
        onStopSupervisor.scope.launch(viewModel.mainImmediate) {
            viewModel.podcastViewStateContainer.collect { viewState ->
                @Exhaustive
                when (viewState) {
                    is PodcastViewState.Idle -> {}
                    is PodcastViewState.MediaStateUpdate -> {
                        configurePodcastPlayer(viewState.podcast)
                    }
                }
            }
        }
    }
}
