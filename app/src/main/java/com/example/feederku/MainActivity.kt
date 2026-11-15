package com.example.feederku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.feederku.ui.theme.FeederkuTheme
import com.example.feederku.navigation.AppNavHost
import org.osmdroid.config.Configuration
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. SET USER AGENT FIRST - This is mandatory to avoid 403 Forbidden
        val userAgent = "FeederKu-Android-Application-v1.0"
        Configuration.getInstance().userAgentValue = userAgent
        
        // 2. Set cache paths to internal storage to avoid permission issues
        val osmConfig = Configuration.getInstance()
        val basePath = File(cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        val tileCache = File(basePath.absolutePath, "tile")
        osmConfig.osmdroidTileCache = tileCache
        
        // 3. Load configuration
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        enableEdgeToEdge()
        setContent {
            FeederkuTheme {
                AppNavHost()
            }
        }
    }
}
