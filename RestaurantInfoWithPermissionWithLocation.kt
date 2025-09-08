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
import com.sarang.torang.RestaurantInfo
import com.sarang.torang.di.restaurant_info.RestaurantInfoWithPermission
import com.sryang.library.compose.workflow.BestPracticeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
fun RestaurantInfoWithPermissionWithLocation(
    tag                 : String            = "__RestaurantInfoWithPermissionWithLocation",
    restaurantId        : Int,
    onLocation          : () -> Unit        = { Log.w(tag, "onLocation doesn't set") },
    onWeb               : (String) -> Unit  = { Log.w(tag, "onWeb doesn't set") },
    onCall              : (String) -> Unit  = { Log.w(tag, "onCall doesn't set") },
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationInfo by remember { mutableStateOf("") }
    var currentLatitude : Double? by remember { mutableStateOf(null) }
    var currentLongitude : Double? by remember { mutableStateOf(null) }

    RestaurantInfoWithPermission(viewModel = BestPracticeViewModel(),
        currentLatitude = currentLatitude,
        currentLongitude = currentLongitude,
        restaurantId = restaurantId ,
        onLocation = onLocation,
        onWeb = onWeb,
        onCall = onCall,
        onRequestLocation = { scope.launch(Dispatchers.IO) {
            val result = locationClient.lastLocation.await()
            locationInfo = if (result == null) { "No last known location. Try fetching the current location first" }
            else { "Current location is lat : ${result.latitude} long : ${result.longitude} fetched at ${System.currentTimeMillis()}" }
            Log.d(tag, locationInfo)
            currentLatitude = result.latitude
            currentLongitude = result.longitude
        } }
    )
}

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
fun restaurantInfo(): RestaurantInfo =
    { restaurantId, onLocation, onWeb, onCall ->
    RestaurantInfoWithPermissionWithLocation(restaurantId = restaurantId, onLocation = onLocation, onWeb = onWeb, onCall = onCall)
}