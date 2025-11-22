package com.sarang.torang.di.restauarnt_info_di

import android.Manifest
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun RequestLocationBox(
    tag                 : String            = "__RequestLocationBox",
    contents : @Composable (Pair<Double, Double>?, ()->Unit)->Unit = { _, _->}
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationInfo by remember { mutableStateOf("") }
    var currentLocation : Pair<Double, Double>? by remember { mutableStateOf(null) }

    Box(Modifier.fillMaxSize()) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@Box
        }
    }

    contents.invoke(currentLocation, { scope.launch(Dispatchers.IO) {
        val result = locationClient.lastLocation.await()
        locationInfo = if (result == null)
            "No last known location. Try fetching the current location first"
        else
            "Current location is lat : ${result.latitude} long : ${result.longitude} fetched at ${System.currentTimeMillis()}"
        currentLocation = Pair(result.latitude, result.longitude)
    }})
}