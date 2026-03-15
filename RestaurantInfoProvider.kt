package com.sarang.torang.di.restauarnt_info_di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarang.torang.LocalRestaurantInfoImageLoader
import com.sarang.torang.RestaurantInfo
import com.sarang.torang.RestaurantInfoImageLoader
import com.sarang.torang.RestaurantInfoViewModel
import com.sarang.torang.compose.restaurantinfo.RestaurantInfo
import com.sryang.library.compose.workflow.BestPracticeViewModel

@Composable
fun restaurantInfo(viewModel : RestaurantInfoViewModel = hiltViewModel(),
                   restaurantImageLoader : RestaurantInfoImageLoader = {}): RestaurantInfo =
    {
        val uiState = viewModel.uiState
        LaunchedEffect(it.restaurantId) { viewModel.fetchRestaurantInfo1(it.restaurantId) }
        RequestLocationBox { currentLocation, onRequestLocation ->
            PermissionBox(BestPracticeViewModel(), onRequestLocation) { isGranted, request ->
                LaunchedEffect(currentLocation) {
                    currentLocation?.let { viewModel.setCurrentLocation(it.first, it.second) }
                }
                CompositionLocalProvider(LocalRestaurantInfoImageLoader provides restaurantInfoImageLoader) {
                    RestaurantInfo(
                        uiState = uiState,
                        isLocationPermissionGranted = isGranted,
                        onLocation = it.onLocation,
                        onRequestPermission = { request() },
                        topPhotoRowHeight = 350.dp
                    )
                }
            }
        }
    }