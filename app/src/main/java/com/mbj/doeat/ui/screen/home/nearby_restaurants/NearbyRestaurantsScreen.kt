package com.mbj.doeat.ui.screen.home.nearby_restaurants

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.data.remote.model.SearchResult
import com.mbj.doeat.ui.component.LongRectangleButtonWithParams
import com.mbj.doeat.ui.component.MainAppBar
import com.mbj.doeat.ui.component.RoundedLine
import com.mbj.doeat.ui.component.ToastMessage
import com.mbj.doeat.ui.graph.DetailScreen
import com.mbj.doeat.ui.model.SearchWidgetState
import com.mbj.doeat.ui.screen.home.nearby_restaurants.viewmodel.NearByRestaurantsViewModel
import com.mbj.doeat.ui.theme.Yellow700
import com.mbj.doeat.ui.theme.randomColors
import com.mbj.doeat.util.MapConverter.formatLatLng
import com.mbj.doeat.util.MapConverter.removeHtmlTags
import com.mbj.doeat.util.NavigationUtils
import com.mbj.doeat.util.UrlUtils

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterialApi::class)
@Composable
fun NearbyRestaurantsScreen(
    name: String,
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
) {
    val viewModel: NearByRestaurantsViewModel = hiltViewModel()

    val context = LocalContext.current
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val myLocationInfoState by viewModel.location.collectAsState()
    val searchWidgetState by viewModel.searchWidgetState.collectAsState()
    val searchTextState by viewModel.searchTextState.collectAsState()
    val searchResultState by viewModel.searchResult.collectAsState(initial = SearchResult(emptyList()))
    val searchResultCollapseState by viewModel.searchResultCollapse.collectAsState(initial = false)
    val searchResultCollapseCountState by viewModel.searchResultCollapseCount.collectAsState()
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition(myLocationInfoState, 11.0)
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
                updateMyLocation(fusedLocationClient, viewModel, cameraPositionState)
            } else {
                Log.d("NearbyRestaurantsScreen", "권한이 거부되었습니다.")
                viewModel.setLocationPermissionDenied()
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
                    .height(500.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .offset(y = 15.dp)
            ) {
                MyBottomSheetContent(viewModel, cameraPositionState, navController)
            }
        },
        sheetPeekHeight = 150.dp,
        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState),
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = Color.Transparent,
        topBar = {
            MainAppBar(
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                backgroundColor = Yellow700,
                contentColor = Color.Black,
                defaultAppBarText = "맛집 검색",
                searchAppBarText = "지역을 입력해주세요.",
                onTextChange = {
                    viewModel.updateSearchText(newValue = it)
                },
                onCloseClicked = {
                    viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                },
                onSearchClicked = { searchWord ->
                    viewModel.getFamousRestaurant(searchWord)
                    viewModel.toggleSearchResultCollapsed()
                },
                onSearchTriggered = {
                    viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                }
            )
        }
    ) {
        CollapseBottomSheetIfRequired(bottomSheetState, searchResultCollapseState, searchResultCollapseCountState)
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            NaverMap(cameraPositionState = cameraPositionState) {
                if (searchResultState.items.isNotEmpty()) {
                    searchResultState.items.forEachIndexed { index, searchItem ->
                        val iconTintColor = randomColors[index % randomColors.size]
                        Marker(
                            state = MarkerState(position = formatLatLng(searchItem.mapy, searchItem.mapx)),
                            captionText = removeHtmlTags(searchItem.title),
                            iconTintColor = iconTintColor
                        )
                    }
                    cameraPositionState.move(
                        CameraUpdate.scrollAndZoomTo(formatLatLng(searchResultState.items.first().mapy, searchResultState.items.first().mapx), 18.0)
                            .animate(CameraAnimation.Easing)
                    )
                }
                if(myLocationInfoState != LatLng(37.532600, 127.024612)) {
                    Marker(
                        state = MarkerState(position = myLocationInfoState),
                        captionText = "내 위치",
                    )
                }
            }
            Image(
                painter = imagePainter,
                contentDescription = "내 위치",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 10.dp, y = (-160).dp),
                onClick = {
                    checkAndRequestPermissions(
                        context = context,
                        permissions = permissions,
                        launcher = launcher,
                        onPermissionGranted = {
                            updateMyLocation(
                                fusedLocationClient = fusedLocationClient,
                                viewModel = viewModel,
                                cameraPositionState = cameraPositionState
                            )
                        }
                    )
                }
            )
            ToastMessage(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
                    .offset(y = (-190).dp),
                showMessage = viewModel.isLocationPermissionDenied.collectAsState(initial = false).value,
                clickCount = viewModel.isLocationPermissionDeniedCount.collectAsState().value,
                message = "위치 권한이 거부되었습니다.\n허용 후 다시 시도해주세요."
            )
            ToastMessage(
                modifier = Modifier.padding(16.dp),
                showMessage = viewModel.isSearchInvalid.collectAsState(initial = false).value,
                clickCount = viewModel.isSearchInvalidCount.collectAsState().value,
                message = "올바른 지역을 입력해주세요."
            )
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
fun updateMyLocation(
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: NearByRestaurantsViewModel,
    cameraPositionState: CameraPositionState,
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            viewModel.updateLocation(LatLng(it.latitude, it.longitude))
            cameraPositionState.move(
                CameraUpdate.scrollAndZoomTo(LatLng(it.latitude, it.longitude), 18.0)
                    .animate(CameraAnimation.Easing)
            )
        }
    } catch (e: SecurityException) {
        Log.d("NearbyRestaurantsScreen", e.stackTraceToString())
    }
}

@Composable
fun MyBottomSheetContent(viewModel: NearByRestaurantsViewModel, cameraPositionState: CameraPositionState, navController: NavHostController) {
    val searchResult by viewModel.searchResult.collectAsState(initial = SearchResult(emptyList()))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        RoundedLine(
            strokeWidth = 2.dp,
            cornerRadius = 22.dp,
            modifier = Modifier
                .width(30.dp)
                .height(2.dp)
                .background(Color.Black)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "맛집 정보",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn {
            items(
                items = searchResult.items,
                key = { searchItem -> searchItem.hashCode() }
            ) { searchItem ->
                MyBottomSheetContentItem(searchItem = searchItem, cameraPositionState = cameraPositionState, navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MyBottomSheetContentItem(searchItem: SearchItem,cameraPositionState: CameraPositionState, navController: NavHostController) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(modifier = Modifier.clickable {
        cameraPositionState.move(
            CameraUpdate.scrollAndZoomTo(formatLatLng(searchItem.mapy, searchItem.mapx), 18.0)
                .animate(CameraAnimation.Easing)
        )
    }) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Text(text = removeHtmlTags(searchItem.title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = searchItem.category,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Text(text = searchItem.roadAddress,
                    fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LongRectangleButtonWithParams(text = "상세보기",
            height = 40.dp,
            useFillMaxWidth = true,
            padding = PaddingValues(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
            backgroundColor = Yellow700,
            contentColor = Color.Black
        ) {
            val encodedLink = UrlUtils.encodeUrl(searchItem.link)
            NavigationUtils.navigate(
                navController, DetailScreen.Detail.navigateWithArg(
                    searchItem.copy(link = encodedLink)
                )
            )
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            thickness = 1.dp
        )
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
        updateMyLocation(fusedLocationClient, viewModel, cameraPositionState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollapseBottomSheetIfRequired(
    bottomSheetState: BottomSheetState,
    shouldCollapse: Boolean,
    eventCount: Int
) {
    LaunchedEffect(eventCount) {
        if (shouldCollapse) {
            bottomSheetState.collapse()
        }
    }
}
