package com.sarang.torang.di.restauarnt_info_di

import androidx.annotation.RequiresPermission
import androidx.compose.ui.unit.dp
import com.sarang.torang.RestaurantInfoScreen
import com.sarang.torang.compose.restaurantinfo.RestaurantInfo
import com.sryang.library.compose.workflow.BestPracticeViewModel

@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
fun restaurantInfoScreen(): RestaurantInfo =
    {
        RequestLocationBox { currentLocation, onRequestLocation ->
            PermissionBox(BestPracticeViewModel(), onRequestLocation) { isGranted, request ->
                RestaurantInfoScreen(
                    topPhotoRowHeight = 500.dp,
                    currentLocation = currentLocation,
                    restaurantId = it.restaurantId,
                    isLocationPermissionGranted = isGranted,
                    onLocation = it.onLocation,
                    onRequestPermission = { request() }
                )
            }
        }
    }