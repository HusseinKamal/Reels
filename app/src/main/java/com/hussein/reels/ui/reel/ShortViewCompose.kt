@file:Suppress("DEPRECATION")

package com.hussein.reels.ui.reel
import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ShortViewCompose(
    activity: Activity,
    videoItemsUrl:List<String>,
    clickItemPosition:Int = 0,
    videoHeader:@Composable () -> Unit = {},
    videoBottom:@Composable () -> Unit = {}
) {
    activity.immersive(darkMode = true)
    val pagerState: PagerState = run {
        remember {
            PagerState(clickItemPosition, 0, videoItemsUrl.size - 1)
        }
    }
    val initialLayout= remember {
        mutableStateOf(true)
    }
    val pauseIconVisibleState = remember {
        mutableStateOf(false)
    }
    Pager(
        state = pagerState,
        orientation = Orientation.Vertical,
        offscreenLimit = 1
    ) {
        pauseIconVisibleState.value=false
        SingleVideoItemContent(videoItemsUrl[page],
            pagerState,
            page,
            initialLayout,
            pauseIconVisibleState,
            videoHeader,
            videoBottom)
    }

    LaunchedEffect(clickItemPosition){
        delay(300)
        initialLayout.value=false
    }

}

@Composable
private fun SingleVideoItemContent(
    videoUrl: String,
    pagerState: PagerState,
    pager: Int,
    initialLayout: MutableState<Boolean>,
    pauseIconVisibleState: MutableState<Boolean>,
    VideoHeader: @Composable() () -> Unit,
    VideoBottom: @Composable() () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()){
        VideoPlayer(videoUrl,pagerState,pager,pauseIconVisibleState)
        VideoHeader.invoke()
        Box(modifier = Modifier.align(Alignment.BottomStart)){
            VideoBottom.invoke()
        }
        if (initialLayout.value) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black))
        }
    }
}

@OptIn(UnstableApi::class)
@SuppressLint("OpaqueUnitKey")
@Composable
fun VideoPlayer(
    videoUrl: String,
    pagerState: PagerState,
    pager: Int,
    pauseIconVisibleState: MutableState<Boolean>,
) {
    val context = LocalContext.current
    val scope= rememberCoroutineScope()

    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context)
            .build()
            .apply {
                val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(context, context.packageName)
                )
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))

                this.prepare(source)
            }
    }
    if (pager == pagerState.currentPage) {
        exoPlayer.playWhenReady = true
        exoPlayer.play()
    } else {
        exoPlayer.pause()
    }
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

    DisposableEffect(
        Box(modifier = Modifier.fillMaxSize()){
            AndroidView(factory = {
                PlayerView(context).apply {
                    hideController()
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            },modifier = Modifier.noRippleClickable {
                pauseIconVisibleState.value=true
                exoPlayer.pause()
                scope.launch {
                    delay(500)
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        pauseIconVisibleState.value=false
                        exoPlayer.play()
                    }
                }
            })
            if (pauseIconVisibleState.value)
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp))
        }
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}










