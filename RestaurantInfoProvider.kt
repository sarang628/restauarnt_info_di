package com.sarang.torang.di.restauarnt_info_di

import androidx.annotation.RequiresPermission
import com.sarang.torang.RestaurantInfo
import com.sarang.torang.RestaurantInfoScreen
import com.sryang.library.compose.workflow.BestPracticeViewModel

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
fun restaurantInfo(): RestaurantInfo =
    { restaurantId, onLocation, onWeb, onCall ->
        RequestLocationBox { currentLatitude, currentLongitude, onRequestLocation ->
            PermissionBox(BestPracticeViewModel(), onRequestLocation) { isGranted, request ->
                RestaurantInfoScreen(
                    currentLongitude = currentLongitude,
                    currentLatitude = currentLatitude,
                    restaurantId = restaurantId,
                    isLocationPermissionGranted = isGranted,
                    onLocation = onLocation,
                    onWeb = onWeb,
                    onCall = onCall,
                    onRequestPermission = { request() }
                )
            }
        }
    }