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

    // Estado del mapa con Google Maps
    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isLocationPermissionGranted by remember { mutableStateOf(false) }
    var showStadiumList by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

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

    LaunchedEffect(userLocation, uiState.stadiums) {
        if (userLocation != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(userLocation!!.latitude, userLocation!!.longitude),
                12f
            )
        } else if (uiState.stadiums.isNotEmpty()) {
            val first = uiState.stadiums.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(first.latitude, first.longitude),
                10f
            )
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
                title = { Text("Mapa de Sedes") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showStadiumList = !showStadiumList }) {
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
                                    viewModel.selectStadium(stadium)
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(stadium.latitude, stadium.longitude),
                                                14f
                                            )
                                        )
                                    }
                                    true
                                }
                            )
                        }
                    }

                    // ============ BOTÓN PARA MOSTRAR LISTA ============
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
                                        ListItem(
                                            headlineContent = { Text(stadium.name) },
                                            supportingContent = {
                                                Text("${stadium.city}, ${stadium.country}")
                                            },
                                            leadingContent = {
                                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.selectStadium(stadium)
                                                    coroutineScope.launch {
                                                        cameraPositionState.animate(
                                                            CameraUpdateFactory.newLatLngZoom(
                                                                LatLng(stadium.latitude, stadium.longitude),
                                                                14f
                                                            )
                                                        )
                                                    }
                                                    showStadiumList = false
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ============ DETALLE DEL ESTADIO SELECCIONADO ============
                    uiState.selectedStadium?.let { stadium ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = stadium.name,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                text = "${stadium.city}, ${stadium.country}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(onClick = { viewModel.clearSelectedStadium() }) {
                                            Icon(
                                                Icons.Default.ArrowBack,
                                                contentDescription = "Cerrar"
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Capacidad: ${stadium.capacity} espectadores",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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