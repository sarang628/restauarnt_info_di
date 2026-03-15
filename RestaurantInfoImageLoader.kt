package com.sarang.torang.di.restauarnt_info_di

import com.sarang.torang.RestaurantInfoImageLoader
import com.sarang.torang.di.image.TorangAsyncImageData
import com.sarang.torang.di.image.provideTorangAsyncImage

val restaurantInfoImageLoader : RestaurantInfoImageLoader = {
    provideTorangAsyncImage().invoke(
        TorangAsyncImageData(modifier        = it.modifier,
            model           = it.url,
            progressSize    = it.progressSize,
            errorIconSize   = it.errorIconSize,
            contentScale    = it.contentScale)
) }