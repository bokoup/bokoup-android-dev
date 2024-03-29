package com.bokoup.customerapp.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.bokoup.customerapp.R
import com.bokoup.customerapp.dom.model.ScanResult


@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun ScanContent(
    viewModel: ScanViewModel = hiltViewModel(),
    padding: PaddingValues,
    boxColor: Color = Color.Green,
    navigateToApprove: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    cameraController.bindToLifecycle(
        lifecycleOwner
    )

    val permissions =
        listOf(Manifest.permission.CAMERA)

    var hasPermissions by remember {
        mutableStateOf(
            permissions.associateWith {
                (ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED)
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { granted ->
            hasPermissions = granted
        }
    )

    val scanResult: ScanResult? by viewModel.scanResult.collectAsState()

    LaunchedEffect(scanResult) {
        if (scanResult != null) {
            if (scanResult is ScanResult.BokoupUrl) {
                val scanResult = scanResult as ScanResult.BokoupUrl
                navigateToApprove(scanResult.url)
            }
        }

    }

    if (hasPermissions[Manifest.permission.CAMERA] != true) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                text = stringResource(R.string.grant_permission_camera_instructions),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = { launcher.launch(permissions.toTypedArray()) }) {
                Text(text = stringResource(R.string.grant_permission))
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.BottomCenter
        ) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    previewView.controller = cameraController
                    previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

                    cameraController.cameraSelector = selector
                    cameraController.imageAnalysisBackpressureStrategy =
                        STRATEGY_KEEP_ONLY_LATEST

                    try {
                        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(
                            context
                        ),
                            MlKitAnalyzer(
                                listOf(viewModel.scanner),
                                COORDINATE_SYSTEM_VIEW_REFERENCED,
                                ContextCompat.getMainExecutor(context)
                            ) { result ->
                                val codes = result.getValue(viewModel.scanner)
                                if (codes != null && codes.size > 0) {
                                    viewModel.getScanResult(codes[0])
                                } else {
                                    viewModel.getScanResult(null)
                                }
                            })

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                },
                modifier = Modifier.fillMaxSize(),
            )
            if (scanResult != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPath(
                        path = Path().apply {
                            val cornerPoints = scanResult!!.barcode.cornerPoints!!
                            val startPoint =
                                Pair(cornerPoints[0].x.toFloat(), cornerPoints[0].y.toFloat())
                            cornerPoints.forEachIndexed { i, point ->
                                if (i == 0) {
                                    moveTo(startPoint.first, startPoint.second)
                                } else {
                                    lineTo(point.x.toFloat(), point.y.toFloat())
                                }
                            }
                            lineTo(startPoint.first, startPoint.second)
                        },
                        color = boxColor,
                        style = Fill,
                        alpha = 0.5f

                    )

                }
            }
            if (scanResult is ScanResult.Other) {
                val scanResult = scanResult as ScanResult.Other
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .height(64.dp), horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { viewModel.copyToClipboard(scanResult.value) }) {
                        Text(
                            text = "${scanResult.value.slice(0..16)}...",
                        )
                    }
                }
            }

        }

    }
}

