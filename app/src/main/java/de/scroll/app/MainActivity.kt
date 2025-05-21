package de.scroll.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.wa.automate.core.utils.AppPreferences
import de.scroll.app.ui.home.HomeScreen
import de.scroll.app.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppPreferences.init(applicationContext)
        setContent {
            MyApplicationTheme {
                HomeScreen()
            }
        }
    }


}

