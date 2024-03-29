package com.bokoup.customerapp.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.bokoup.customerapp.nav.NavGraph
import com.bokoup.customerapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            App(
                calculateWindowSizeClass(this).widthSizeClass
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
fun DefaultPreview() {
    AppTheme {
        NavGraph(
            navController = rememberNavController(),
            openDrawer = {}
        )
    }
}