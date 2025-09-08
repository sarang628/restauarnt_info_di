package com.sarang.torang.di.restauarnt_info_di

import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarang.torang.RestaurantInfo
import com.sarang.torang.RestaurantInfoScreen
import com.sarang.torang.RestaurantInfoViewModel
import com.sryang.library.compose.workflow.BestPracticeViewModel

@Composable
@RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
fun restaurantInfo(viewModel : RestaurantInfoViewModel = hiltViewModel()): RestaurantInfo =
    { restaurantId, onLocation, onWeb, onCall ->
        val uiState = viewModel.uiState
        LaunchedEffect(restaurantId) { viewModel.fetchRestaurantInfo1(restaurantId) }
        RequestLocationBox { currentLocation, onRequestLocation ->
            PermissionBox(BestPracticeViewModel(), onRequestLocation) { isGranted, request ->
                LaunchedEffect(currentLocation) {
                    currentLocation?.let { viewModel.setCurrentLocation(it.first, it.second) }
                }
                RestaurantInfo(
                    uiState = uiState,
                    isLocationPermissionGranted = isGranted,
                    onLocation = onLocation,
                    onWeb = onWeb,
                    onCall = onCall,
                    onRequestPermission = { request() }
                )
            }
        }
    }