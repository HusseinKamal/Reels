package com.hussein.reels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.hussein.reels.ui.reel.ShortViewCompose
import com.hussein.reels.ui.theme.ReelsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReelsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ShortViewCompose(
                        activity = this,
                        videoItemsUrl = videoUrls,
                        clickItemPosition = 0
                    )
                }
            }
        }
    }
}
val videoUrls = listOf(
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
)