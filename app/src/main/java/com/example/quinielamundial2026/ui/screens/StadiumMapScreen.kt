package com.example.quinielamundial2026.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.navigation.Screen
import com.example.quinielamundial2026.ui.components.LoadingSpinner
import com.example.quinielamundial2026.ui.viewmodels.StadiumMapViewModel
import com.example.quinielamundial2026.ui.viewmodels.ViewModelFactory
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StadiumMapScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val factory = ViewModelFactory(QuinielaApplication.instance.container)
    val viewModel: StadiumMapViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isLocationPermissionGranted by remember { mutableStateOf(false) }
    var showStadiumList by remember { mutableStateOf(false) }
    var selectedStadiumId by remember { mutableStateOf<Int?>(null) }
    var isZoomedToStadium by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun goToGeneralView() {
        isZoomedToStadium = false
        selectedStadiumId = null
        if (uiState.stadiums.isNotEmpty()) {
            val avgLat = uiState.stadiums.map { it.latitude }.average()
            val avgLng = uiState.stadiums.map { it.longitude }.average()
            coroutineScope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(avgLat, avgLng),
                        6f
                    )
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        isLocationPermissionGranted = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(isLocationPermissionGranted) {
        if (isLocationPermissionGranted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    userLocation = location
                }
            } catch (e: Exception) {}
        }
    }

    // ============ ZOOM PARA VER TODOS LOS ESTADIOS ============
    LaunchedEffect(uiState.stadiums) {
        if (uiState.stadiums.isNotEmpty() && !isZoomedToStadium) {
            val avgLat = uiState.stadiums.map { it.latitude }.average()
            val avgLng = uiState.stadiums.map { it.longitude }.average()

            val minLat = uiState.stadiums.minOf { it.latitude }
            val maxLat = uiState.stadiums.maxOf { it.latitude }
            val minLng = uiState.stadiums.minOf { it.longitude }
            val maxLng = uiState.stadiums.maxOf { it.longitude }

            val latDiff = maxLat - minLat
            val lngDiff = maxLng - minLng
            val maxDiff = maxOf(latDiff, lngDiff)

            val zoomLevel = when {
                maxDiff < 2.0 -> 12f
                maxDiff < 5.0 -> 10f
                maxDiff < 10.0 -> 8f
                else -> 6f
            }

            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(avgLat, avgLng),
                zoomLevel
            )
        }
    }

    // ============ CENTRAR EN ESTADIO SELECCIONADO DESDE LA LISTA ============
    LaunchedEffect(selectedStadiumId, uiState.stadiums) {
        if (selectedStadiumId != null) {
            val stadium = uiState.stadiums.find { it.id == selectedStadiumId }
            stadium?.let {
                isZoomedToStadium = true
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            14f
                        )
                    )
                }
            }
        }
    }

    if (!isLocationPermissionGranted) {
        LaunchedEffect(Unit) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isZoomedToStadium && selectedStadiumId != null) {
                            val stadium = uiState.stadiums.find { it.id == selectedStadiumId }
                            stadium?.name ?: "Mapa de Sedes"
                        } else {
                            "Mapa de Sedes"
                        }
                    )
                },
                navigationIcon = {
                    // ============ BOTÓN DE RETROCESO - SIEMPRE NAVEGA ATRÁS ============
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // ============ BOTÓN PARA VOLVER A VISTA GENERAL ============
                    if (isZoomedToStadium) {
                        IconButton(
                            onClick = {
                                goToGeneralView()
                            }
                        ) {
                            Icon(
                                Icons.Default.Public,
                                contentDescription = "Ver todos los estadios"
                            )
                        }
                    }

                    // ============ BOTÓN PARA MOSTRAR LISTA ============
                    IconButton(onClick = {
                        showStadiumList = !showStadiumList
                    }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Lista de sedes")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isLocationPermissionGranted && userLocation != null) {
                FloatingActionButton(
                    onClick = {
                        userLocation?.let {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(it.latitude, it.longitude),
                                        14f
                                    )
                                )
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Mi ubicación")
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingSpinner()
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${uiState.error}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadStadiums() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            uiState.stadiums.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay estadios disponibles",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Próximamente se cargarán las sedes",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // ============ MAPA CON GOOGLE MAPS ============
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = isLocationPermissionGranted,
                            mapType = MapType.NORMAL
                        )
                    ) {
                        // ============ MARCADORES DE ESTADIOS ============
                        uiState.stadiums.forEach { stadium ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(stadium.latitude, stadium.longitude)
                                ),
                                title = stadium.name,
                                snippet = "${stadium.city}, ${stadium.country}",
                                onClick = {
                                    navController.navigate(
                                        Screen.StadiumDetail.passStadiumId(stadium.id)
                                    )
                                    true
                                }
                            )
                        }
                    }

                    // ============ LISTA DE ESTADIOS ============
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    ) {
                        if (showStadiumList) {
                            Card(
                                modifier = Modifier
                                    .width(280.dp)
                                    .height(400.dp)
                                    .padding(top = 56.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(uiState.stadiums) { stadium ->
                                        val isSelected = selectedStadiumId == stadium.id

                                        ListItem(
                                            headlineContent = {
                                                Text(
                                                    stadium.name,
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                            },
                                            supportingContent = {
                                                Text("${stadium.city}, ${stadium.country}")
                                            },
                                            leadingContent = {
                                                Icon(
                                                    Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = if (isSelected) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    }
                                                )
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    // Centrar el mapa en el estadio
                                                    selectedStadiumId = stadium.id
                                                    showStadiumList = false
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ============ INDICADOR DE ESTADIO SELECCIONADO ============
                    if (isZoomedToStadium && selectedStadiumId != null) {
                        val selectedStadium = uiState.stadiums.find { it.id == selectedStadiumId }
                        if (selectedStadium != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${selectedStadium.name} - ${selectedStadium.city}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}