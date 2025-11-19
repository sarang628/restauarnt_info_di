package com.sarang.torang.di.restauarnt_info_di

import androidx.annotation.RequiresPermission
import com.sarang.torang.RestaurantInfo
import com.sarang.torang.RestaurantInfoScreen
import com.sryang.library.compose.workflow.BestPracticeViewModel

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
fun restaurantInfoScreen(): RestaurantInfo =
    {
        RequestLocationBox { currentLocation, onRequestLocation ->
            PermissionBox(BestPracticeViewModel(), onRequestLocation) { isGranted, request ->
                RestaurantInfoScreen(
                    currentLocation = currentLocation,
                    restaurantId = it.restaurantId,
                    isLocationPermissionGranted = isGranted,
                    onLocation = it.onLocation,
                    onWeb = it.onWeb,
                    onCall = it.onCall,
                    onRequestPermission = { request() }
                )
            }
        }
    }