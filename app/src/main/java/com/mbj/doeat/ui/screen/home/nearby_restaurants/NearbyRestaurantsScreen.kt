package com.mbj.doeat.ui.screen.home.nearby_restaurants

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.mbj.doeat.R
import com.mbj.doeat.ui.component.Image
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.mbj.doeat.ui.screen.home.nearby_restaurants.viewmodel.NearByRestaurantsViewModel

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterialApi::class)
@Composable
fun NearbyRestaurantsScreen(
    name: String,
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
) {
    val viewModel = NearByRestaurantsViewModel()
    val context = LocalContext.current
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val myLocationInfo by viewModel.location.collectAsState()
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition(myLocationInfo, 11.0)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        if (permissionsMap.isEmpty()) {
            Log.d("NearbyRestaurantsScreen", "권한 요청이 취소되었습니다.")
        } else {
            val areGranted = permissionsMap.values.all { it }
            if (areGranted) {
                Log.d("NearbyRestaurantsScreen", "권한이 동의되었습니다.")
                myLocation(fusedLocationClient, viewModel, cameraPositionState)
            } else {
                Log.d("NearbyRestaurantsScreen", "권한이 거부되었습니다.")
                /**
                 * 권한 거부 상태 UI TODO
                 */
            }
        }
    }

    handleLocationPermission(
        permissions = permissions,
        cameraPositionState = cameraPositionState,
        context = context,
        fusedLocationClient = fusedLocationClient,
        viewModel = viewModel
    )

    val bottomSheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )

    val drawableResId = R.drawable.my_location_icon
    val imagePainter = painterResource(id = drawableResId)

    BottomSheetScaffold(
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            ) {
                MyBottomSheetContent()
            }
        },
        sheetPeekHeight = 200.dp,
        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState),
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            NaverMap(cameraPositionState = cameraPositionState) {
                Marker(
                    state = MarkerState(position = LatLng(37.532600, 127.024612)),
                    captionText = "서울"
                )
            }
            Image(
                painter = imagePainter,
                contentDescription = "내 위치",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 10.dp, y = (-210).dp),
                onClick = {
                    checkAndRequestPermissions(
                        context = context,
                        permissions = permissions,
                        launcher = launcher,
                        onPermissionGranted = {
                            myLocation(
                                fusedLocationClient = fusedLocationClient,
                                nearByRestaurantsViewModel = viewModel,
                                cameraPositionState = cameraPositionState
                            )
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
fun myLocation(
    fusedLocationClient: FusedLocationProviderClient,
    nearByRestaurantsViewModel: NearByRestaurantsViewModel,
    cameraPositionState: CameraPositionState,
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            nearByRestaurantsViewModel.updateLocation(LatLng(it.latitude, it.longitude))
            cameraPositionState.move(
                CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude))
                    .animate(CameraAnimation.Easing)
            )
        }
        fusedLocationClient.lastLocation.addOnCanceledListener {

        }
    } catch (e: SecurityException) {
        Log.d("NearbyRestaurantsScreen", e.stackTraceToString())
    }
}

@Composable
fun MyBottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        /**
         * 바텀 시트 내용 추가 TODO
         */
    }
}

private fun checkAndRequestPermissions(
    context: Context,
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    onPermissionGranted: () -> Unit
) {
    if (permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }) {
        onPermissionGranted()
        Log.d("NearbyRestaurantsScreen", "권한이 이미 존재합니다.")
    } else {
        launcher.launch(permissions)
    }
}

fun handleLocationPermission(
    permissions: Array<String>,
    cameraPositionState: CameraPositionState,
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: NearByRestaurantsViewModel,
) {
    if (permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }) {
        myLocation(fusedLocationClient, viewModel, cameraPositionState)
    } else {
        /**
         * 권한 거부 상태 UI TODO
         */
    }
}
