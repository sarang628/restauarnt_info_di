package com.sarang.torang.di.restauarnt_info_di

import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
fun RequestLocationBox(
    tag                 : String            = "__RequestLocationBox",
    contents : @Composable (Double?, Double?, ()->Unit)->Unit = {_,_,_->}
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationInfo by remember { mutableStateOf("") }
    var currentLatitude : Double? by remember { mutableStateOf(null) }
    var currentLongitude : Double? by remember { mutableStateOf(null) }

    contents.invoke(currentLongitude, currentLongitude, { scope.launch(Dispatchers.IO) {
        val result = locationClient.lastLocation.await()
        locationInfo = if (result == null) { "No last known location. Try fetching the current location first" }
        else { "Current location is lat : ${result.latitude} long : ${result.longitude} fetched at ${System.currentTimeMillis()}" }
        Log.d(tag, locationInfo)
        currentLatitude = result.latitude
        currentLongitude = result.longitude
    }})
}