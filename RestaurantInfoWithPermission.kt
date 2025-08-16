package com.sarang.torang.di.restaurant_info

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.sarang.torang.RestaurantInfo
import com.sarang.torang.RestaurantInfo_
import com.sryang.library.compose.workflow.BestPracticeViewModel
import com.sryang.library.compose.workflow.MoveSystemSettingDialog
import com.sryang.library.compose.workflow.PermissonWorkFlow.CheckRationale
import com.sryang.library.compose.workflow.PermissonWorkFlow.DeniedPermission
import com.sryang.library.compose.workflow.PermissonWorkFlow.GrantedPermission
import com.sryang.library.compose.workflow.PermissonWorkFlow.InitialPermissionCheck
import com.sryang.library.compose.workflow.PermissonWorkFlow.RecognizeToUser
import com.sryang.library.compose.workflow.PermissonWorkFlow.RequestPermission
import com.sryang.library.compose.workflow.PermissonWorkFlow.ShowRationale
import com.sryang.library.compose.workflow.PermissonWorkFlow.SuggestSystemSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RestaurantInfoWithPermission(
    tag: String = "__RestaurantInfoWithPermission",
    viewModel: BestPracticeViewModel,
    onRequestLocation : ()->Unit = {},
    currentLatitude : Double? = null,
    currentLongitude : Double? = null,
    restaurantId : Int,
    onLocation: () -> Unit = { Log.w(tag, "onLocation doesn't set") },
    onWeb: (String) -> Unit = { Log.w(tag, "onWeb doesn't set") },
    onCall: (String) -> Unit = { Log.w(tag, "onCall doesn't set") },
){
    var timeDiff : Long by remember { mutableLongStateOf(0L) } // 2번 권한 거부 시 시스템 이동 다이얼로그를 띄우는 조건
    val requestPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION, { viewModel.permissionResult(it, System.currentTimeMillis() - timeDiff) })
    val state = viewModel.state

    when (state) {
        InitialPermissionCheck  /* 1. 최초 */ -> { viewModel.initialPermissionCheck(requestPermission.status.isGranted) }
        RecognizeToUser         /* 2. UX에 권한을 필요로 하는 정보 인지 시키기 */-> { SimpleAlertDialog( onNo = viewModel::noInRecognizeUser, onYes = viewModel::yesInRecognizeUser , text = "음식점과 내 위치 거리 계신을 위해 위치 정보에 접근이 필요 합니다.") }
        CheckRationale          /* 3. 다이얼로그에서 사용자 거절 */ -> { viewModel.checkRational(requestPermission.status.shouldShowRationale) }
        DeniedPermission        /* 5. 권한 거부 */-> {  }
        GrantedPermission       /* 6. 사용자가 권한을 허가했다면, 자원 접근 가능 */-> { onRequestLocation.invoke() }
        RequestPermission       /* 7. 런타임 권한 요청하기 */ -> { LaunchedEffect(state == RequestPermission) { requestPermission.launchPermissionRequest(); timeDiff = System.currentTimeMillis() } }
        SuggestSystemSetting    /* 8. 권한 거부 상태에서 요청 시 */ -> { MoveSystemSettingDialog(onMove = viewModel::onMoveInSystemDialog, onDeny = viewModel::onNoInSystemDialog) }
        ShowRationale           /* 9. rationale을 표시 */ -> { SimpleAlertDialog(onNo = viewModel::noRationale, onYes = viewModel::yesRationale, text ="음식점과 내 위치 거리 계신을 위해 위치 정보에 접근이 필요 합니다. 2회 거부 시 시스템 설정에서만 권한 부여가 가능합니다.") }
    }

    Box{
        RestaurantInfo_(
            currentLongitude = currentLongitude,
            currentLatitude = currentLatitude,
            restaurantId = restaurantId,
            isLocationPermissionGranted = requestPermission.status.isGranted,
            onLocation = onLocation,
            onWeb = onWeb,
            onCall = onCall,
            onRequestPermission = { viewModel.request() }
        )
    }
}

@Composable
fun RestaurantInfoWithPermissionWithLocation(
    tag : String = "__RestaurantInfoWithPermissionWithLocation",
    restaurantId : Int,
    onLocation: () -> Unit = { Log.w(tag, "onLocation doesn't set") },
    onWeb: (String) -> Unit = { Log.w(tag, "onWeb doesn't set") },
    onCall: (String) -> Unit = { Log.w(tag, "onCall doesn't set") },) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationInfo by remember { mutableStateOf("") }
    var currentLatitude : Double? by remember { mutableStateOf(null) }
    var currentLongitude : Double? by remember { mutableStateOf(null) }

    RestaurantInfoWithPermission(viewModel = BestPracticeViewModel(), currentLatitude = currentLatitude, currentLongitude = currentLongitude, restaurantId = restaurantId ,onRequestLocation = {
        scope.launch(Dispatchers.IO) {
            val result = locationClient.lastLocation.await()
            locationInfo = if (result == null) { "No last known location. Try fetching the current location first" }
            else { "Current location is \n" + "lat : ${result.latitude}\n long : ${result.longitude}\n" + "fetched at ${System.currentTimeMillis()}" }
            currentLatitude = result.latitude
            currentLongitude = result.longitude
        }
    })
}

val restaurantInfo: RestaurantInfo = { restaurantId, onLocation, onWeb, onCall ->
    RestaurantInfoWithPermissionWithLocation(restaurantId = restaurantId, onLocation = onLocation, onWeb = onWeb, onCall = onCall)
}