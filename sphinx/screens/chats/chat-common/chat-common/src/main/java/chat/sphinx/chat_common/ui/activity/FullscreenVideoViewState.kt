package chat.sphinx.chat_common.ui.activity

import io.matthewnelson.concept_views.viewstate.ViewState

internal sealed  class FullscreenVideoViewState : ViewState<FullscreenVideoViewState>() {
    object Idle : FullscreenVideoViewState()

    class VideoMessage(
        val name: String
    ): FullscreenVideoViewState()

    class MetaDataLoaded(
        val duration: Int,
        val videoWidth: Int,
        val videoHeight: Int,
    ): FullscreenVideoViewState()

    class CurrentTimeUpdate(
        val currentTime: Int
    ): FullscreenVideoViewState()

    class PausePlayback(
        val currentTime: Int
    ): FullscreenVideoViewState()

    class ContinuePlayback(
        val currentTime: Int
    ): FullscreenVideoViewState()
}