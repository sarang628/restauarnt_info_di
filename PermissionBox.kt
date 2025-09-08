package com.sarang.torang.di.restauarnt_info_di

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.sarang.torang.di.restaurant_info.SimpleAlertDialog
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


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionBox(
    viewModel               : BestPracticeViewModel,
    onRequestLocation       : ()->Unit                                = {},
    content                 : @Composable (Boolean, ()->Unit) -> Unit = {_,_ ->}
){
    var timeDiff : Long by remember { mutableLongStateOf(0L) } // 2번 권한 거부 시 시스템 이동 다이얼로그를 띄우는 조건
    val requestPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION, { viewModel.permissionResult(it, System.currentTimeMillis() - timeDiff) })
    val state = viewModel.state

    when (state) {
        InitialPermissionCheck  /* 1. 최초 */ -> { viewModel.initialPermissionCheck(requestPermission.status.isGranted) }
        RecognizeToUser         /* 2. UX에 권한을 필요로 하는 정보 인지 시키기 */-> {
            SimpleAlertDialog(
                onNo = viewModel::noInRecognizeUser,
                onYes = viewModel::yesInRecognizeUser,
                text = "음식점과 내 위치 거리 계신을 위해 위치 정보에 접근이 필요 합니다."
            )
        }
        CheckRationale          /* 3. 다이얼로그에서 사용자 거절 */ -> { viewModel.checkRational(requestPermission.status.shouldShowRationale) }
        DeniedPermission        /* 5. 권한 거부 */-> {  }
        GrantedPermission       /* 6. 사용자가 권한을 허가했다면, 자원 접근 가능 */-> { onRequestLocation.invoke() }
        RequestPermission       /* 7. 런타임 권한 요청하기 */ -> { LaunchedEffect(state == RequestPermission) { requestPermission.launchPermissionRequest(); timeDiff = System.currentTimeMillis() } }
        SuggestSystemSetting    /* 8. 권한 거부 상태에서 요청 시 */ -> { MoveSystemSettingDialog(onMove = viewModel::onMoveInSystemDialog, onDeny = viewModel::onNoInSystemDialog) }
        ShowRationale           /* 9. rationale을 표시 */ -> {
            SimpleAlertDialog(
                onNo = viewModel::noRationale,
                onYes = viewModel::yesRationale,
                text = "음식점과 내 위치 거리 계신을 위해 위치 정보에 접근이 필요 합니다. 2회 거부 시 시스템 설정에서만 권한 부여가 가능합니다."
            )
        }
    }

    content.invoke(requestPermission.status.isGranted, { viewModel.request() })
}