package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.BrandKit
import com.example.data.Payment
import com.example.data.Project
import com.example.ui.theme.*
import com.example.viewmodel.ExportState
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()
    
    val context = LocalContext.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack),
        containerColor = SpaceBlack,
        topBar = {
            TopHeaderBar(
                onLogoClick = { viewModel.selectTab("CREATE") }
            )
        },
        bottomBar = {
            BottomNavBar(
                activeTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1E102F), SpaceBlack),
                        center = Offset(0.5f, 0.2f),
                        radius = 1200f
                    )
                )
        ) {
            // Screen router
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "ScreenTransition"
            ) { tab ->
                when {
                    activeProject != null -> {
                        ProjectDetailsScreen(
                            project = activeProject!!,
                            viewModel = viewModel,
                            onBack = {
                                viewModel.activeProject.value = null
                            }
                        )
                    }
                    tab == "CREATE" -> {
                        CreateAdScreen(viewModel = viewModel)
                    }
                    tab == "PROJECTS" -> {
                        ProjectsListScreen(viewModel = viewModel)
                    }
                    tab == "TEMPLATES" -> {
                        TemplatesScreen(viewModel = viewModel)
                    }
                    tab == "BRANDKIT" -> {
                        BrandKitScreen(viewModel = viewModel)
                    }
                    tab == "SETTINGS" -> {
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeaderBar(
    onLogoClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clickable { onLogoClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glowing custom logo outline
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(AdPink, AdCyan)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "AdCraft Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AdCraft AI",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "PROFESSIONAL AD ENGINE",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 8.sp,
                        color = AdCyan,
                        letterSpacing = 1.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF231435), shape = RoundedCornerShape(20.dp))
                    .border(1.dp, AdPink.copy(alpha = 0.4f), shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Premium Badge",
                        tint = AdGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PRO LIFETIME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = CardBackground.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, BorderDark.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                Triple("CREATE", Icons.Default.AutoAwesome, "Create"),
                Triple("PROJECTS", Icons.Default.MovieFilter, "My Videos"),
                Triple("TEMPLATES", Icons.Default.GridView, "Templates"),
                Triple("BRANDKIT", Icons.Default.Palette, "Brand Kit"),
                Triple("SETTINGS", Icons.Default.Settings, "Settings")
            )

            items.forEach { (tabId, icon, label) ->
                val isActive = activeTab == tabId
                val tint by animateColorAsState(
                    targetValue = if (isActive) AdPink else TextGray,
                    label = "NavColor"
                )
                val scale by animateFloatAsState(
                    targetValue = if (isActive) 1.1f else 1.0f,
                    label = "NavScale"
                )

                Column(
                    modifier = Modifier
                        .testTag("nav_btn_$tabId")
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tabId) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = tint,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(scale)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = tint
                    )
                }
            }
        }
    }
}

// STAGE 1: Creating configuration page
@Composable
fun CreateAdScreen(viewModel: MainViewModel) {
    val productName by viewModel.productName.collectAsStateWithLifecycle()
    val productDesc by viewModel.productDesc.collectAsStateWithLifecycle()
    val targetAudience by viewModel.targetAudience.collectAsStateWithLifecycle()
    val customPrompt by viewModel.customPrompt.collectAsStateWithLifecycle()
    val stylePreset by viewModel.stylePreset.collectAsStateWithLifecycle()
    val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val selectedMusicStyle by viewModel.selectedMusicStyle.collectAsStateWithLifecycle()
    val selectedAspectRatio by viewModel.selectedAspectRatio.collectAsStateWithLifecycle()
    val localImagePath by viewModel.localImagePath.collectAsStateWithLifecycle()
    val pickedBitmap by viewModel.pickedBitmap.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Pick Image from device activity launcher
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.localImagePath.value = it.toString()
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.pickedBitmap.value = bitmap
                Toast.makeText(context, "Product Image Mock Loaded!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // High fidelity presets for quick start if the user has no image
    val mockProducts = listOf(
        Triple("Wireless Over-Ear Headphones", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500", "Sleek metallic sound driver with cushion padding"),
        Triple("Anti-Aging Repair Serum", "https://images.unsplash.com/photo-1556228453-efd6c1ff04f6?w=500", "Frosted glass amber dropper with glowing liquid organic extracts"),
        Triple("Retro Designer Sneaker", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500", "Sporty athletic runner styling in bright vibrant red highlights"),
        Triple("Premium Roast Coffee Beans", "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500", "Golden foil burlap seal with rich roasting aroma espresso bean pack")
    )

    if (uiState is UiState.Analyzing || uiState is UiState.Generating) {
        LoadingAnimationScreen(uiState = uiState, onCancel = { viewModel.resetState() })
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Create High-Converting Ad",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Empower products with Computer Vision & Generative AI scripts instantly.",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            // SECTION 1: Product Photo Upload
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "PRODUCT IMAGE UPLOAD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdCyan,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    border = BorderStroke(
                                        1.5.dp,
                                        brush = Brush.linearGradient(listOf(AdPink, AdCyan))
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(Color(0xFF0F0B1E))
                                .clickable {
                                    pickerLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (pickedBitmap != null) {
                                Image(
                                    bitmap = pickedBitmap!!.asImageBitmap(),
                                    contentDescription = "Uploaded preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.BottomEnd
                                ) {
                                    Text(
                                        text = "Change Photo",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .background(
                                                Color.Black.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = "Upload Icon",
                                        tint = AdPink,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Drag & Drop or Click to Upload Image",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "PNG, JPG or WEBP (Max 20MB)",
                                        color = TextGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Or tap a high-fidelity preset to try instantly:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(mockProducts) { (title, url, teaser) ->
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(75.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            1.dp,
                                            if (productName == title) AdPink else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(CardBackground)
                                        .clickable {
                                            viewModel.productName.value = title
                                            viewModel.productDesc.value = teaser
                                            // Assign a simulated resource
                                            viewModel.localImagePath.value = url
                                            // Make a mock bitmap
                                            viewModel.pickedBitmap.value = Bitmap.createBitmap(
                                                150,
                                                150,
                                                Bitmap.Config.ARGB_8888
                                            )
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Preset loaded: $title",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    listOf(Color.Transparent, Color.Black)
                                                )
                                            )
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(
                                            imageVector = when(title) {
                                                "Wireless Over-Ear Headphones" -> Icons.Default.Headphones
                                                "Anti-Aging Repair Serum" -> Icons.Default.Science
                                                "Retro Designer Sneaker" -> Icons.Default.SportsEsports
                                                else -> Icons.Default.LocalCafe
                                            },
                                            contentDescription = null,
                                            tint = AdCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = title,
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: Form Inputs
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "PRODUCT INFORMATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdCyan,
                            letterSpacing = 1.sp
                        )

                        // Name
                        OutlinedTextField(
                            value = productName,
                            onValueChange = { viewModel.productName.value = it },
                            label = { Text("Product Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("prod_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AdPink,
                                unfocusedBorderColor = BorderDark,
                                focusedLabelColor = AdPink,
                                unfocusedLabelColor = TextGray
                            )
                        )

                        // Description
                        OutlinedTextField(
                            value = productDesc,
                            onValueChange = { viewModel.productDesc.value = it },
                            label = { Text("Product Details / Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AdPink,
                                unfocusedBorderColor = BorderDark,
                                focusedLabelColor = AdPink,
                                unfocusedLabelColor = TextGray
                            )
                        )

                        // Target Audience
                        OutlinedTextField(
                            value = targetAudience,
                            onValueChange = { viewModel.targetAudience.value = it },
                            label = { Text("Target Audience") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AdPink,
                                unfocusedBorderColor = BorderDark,
                                focusedLabelColor = AdPink,
                                unfocusedLabelColor = TextGray
                            )
                        )

                        // Custom instructions prompt
                        OutlinedTextField(
                            value = customPrompt,
                            onValueChange = { viewModel.customPrompt.value = it },
                            label = { Text("Custom Prompt (Optional)") },
                            placeholder = { Text("Describe the advertisement style you want...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AdPink,
                                unfocusedBorderColor = BorderDark,
                                focusedLabelColor = AdPink,
                                unfocusedLabelColor = TextGray
                            )
                        )
                    }
                }
            }

            // SECTION 3: Style & Template Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "AI ADVERTISING STYLING PRESET",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdCyan,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val styles = listOf(
                            "Viral TikTok" to Icons.Default.MusicNote,
                            "Luxury Brand" to Icons.Default.Diamond,
                            "Cinematic" to Icons.Default.Movie,
                            "Emotional" to Icons.Default.FilterVintage,
                            "Minimalist" to Icons.Default.Spa,
                            "Direct Response" to Icons.Default.OfflineBolt
                        )

                        styles.chunked(2).forEach { rowStyles ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowStyles.forEach { (style, icon) ->
                                    val isSelected = stylePreset == style
                                    val bdr = if (isSelected) BorderStroke(1.5.dp, AdPink) else BorderStroke(1.dp, BorderDark)
                                    val bg = if (isSelected) Color(0xFF28132D) else CardBackground

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(bg, shape = RoundedCornerShape(8.dp))
                                            .border(bdr, shape = RoundedCornerShape(8.dp))
                                            .clickable { viewModel.stylePreset.value = style }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) AdPink else TextGray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = style,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else TextGray,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 4: Voicing & Music Configs
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "AI VOICE & MEDIA INTEGRATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdCyan,
                            letterSpacing = 1.sp
                        )

                        // Voice and languages Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Voice profile", fontSize = 11.sp, color = TextGray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row {
                                    listOf("Male", "Female").forEach { voice ->
                                        val selected = selectedVoice == voice
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (selected) AdPink else CardBackground,
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .clickable { viewModel.selectedVoice.value = voice }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = voice,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) Color.White else TextGray
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1.2f)) {
                                Text("Locales Language", fontSize = 11.sp, color = TextGray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row {
                                    listOf("English", "Indonesian").forEach { lang ->
                                        val selected = selectedLanguage == lang
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (selected) AdPink else CardBackground,
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .clickable { viewModel.selectedLanguage.value = lang }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (lang == "English") "EN" else "ID",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) Color.White else TextGray
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                }
                            }
                        }

                        // Aspect ratio selector
                        Column {
                            Text("Render Video Frame Aspect Ratio", fontSize = 11.sp, color = TextGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val ratios = listOf(
                                    Triple("9:16", Icons.Default.StayCurrentPortrait, "TikTok/Shorts"),
                                    Triple("1:1", Icons.Default.Square, "Instagram Feed"),
                                    Triple("16:9", Icons.Default.Tv, "YouTube Landscape")
                                )
                                ratios.forEach { (ratio, icon, label) ->
                                    val selected = selectedAspectRatio == ratio
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(
                                                1.dp,
                                                if (selected) AdPink else BorderDark,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { viewModel.selectedAspectRatio.value = ratio },
                                        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFF2E1734) else CardBackground)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = ratio,
                                                tint = if (selected) AdPink else TextGray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(ratio, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(label, fontSize = 8.sp, color = TextGray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // CORE TRIGGER: Compile and create elements!
            item {
                Button(
                    onClick = {
                        if (productName.isBlank()) {
                            Toast.makeText(context, "Please configure Product Name", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.triggerGeneration()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AdPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Generate",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GENERATE AI VIDEO CAMPAIGN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

// Global Glowing Loading and computer vision analysis screen
@Composable
fun LoadingAnimationScreen(
    uiState: UiState,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarGradient")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarRotation"
    )

    val subtitle = if (uiState is UiState.Analyzing) "Phase 1/2: Analyzing product images and styles..." else "Phase 2/2: Writing scripts and 5-scene storyboards..."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack.copy(alpha = 0.95f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .drawBehind {
                        // Drawing futuristic pulse circles
                        drawCircle(
                            color = AdPink.copy(alpha = 0.15f),
                            radius = size.width / 1.5f + (angle % 31f) * 1.5f,
                            style = Stroke(width = 4f)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Spinning gradient radar circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .rotate(angle)
                        .background(
                            brush = Brush.sweepGradient(listOf(AdPink, AdCyan, Color.Transparent)),
                            shape = CircleShape
                        )
                )
                // Hollow center
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(SpaceBlack, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState is UiState.Analyzing) Icons.Default.Visibility else Icons.Default.AutoAwesome,
                        contentDescription = "AI Scanner",
                        tint = AdPink,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = if (uiState is UiState.Analyzing) "COMPUTER VISION SCAN ACTIVE" else "DRAFTING VIRAL CONVERSION COPY",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = AdCyan,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                color = AdPink,
                trackColor = BorderDark,
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(40.dp))
            OutlinedButton(
                onClick = onCancel,
                border = BorderStroke(1.dp, BorderDark),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Cancel Generator", fontSize = 12.sp)
            }
        }
    }
}

// STAGE 2 & 5: GORGEOUS HIGH-FIDELITY PREVIEW VIDEO PLAYER & CAMPAIGN DETAILS
@Composable
fun ProjectDetailsScreen(
    project: Project,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var activeSceneIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableStateOf(0f) }

    val exportState by viewModel.exportState.collectAsStateWithLifecycle()
    val brandKit by viewModel.brandKit.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val clipManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    // Smooth camera visual transitions (pans, zoom offsets)
    val animatedProgress by animateFloatAsState(
        targetValue = if (isPlaying) 1.25f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CameraMotionEffect"
    )

    // Dynamic scene play animation timeline loop
    LaunchedEffect(isPlaying, activeSceneIndex) {
        if (isPlaying) {
            while (true) {
                delay(300)
                videoProgress += 0.05f
                if (videoProgress >= 1.0f) {
                    videoProgress = 0f
                    activeSceneIndex = (activeSceneIndex + 1) % 5
                }
            }
        }
    }

    val activeSceneDescription = when(activeSceneIndex) {
        0 -> project.scene1Description
        1 -> project.scene2Description
        2 -> project.scene3Description
        3 -> project.scene4Description
        else -> project.scene5Description
    }

    val activeSceneOverlayText = when(activeSceneIndex) {
        0 -> project.scene1Overlay
        1 -> project.scene2Overlay
        2 -> project.scene3Overlay
        3 -> project.scene4Overlay
        else -> project.scene5Overlay
    }

    // Export popups
    if (exportState !is ExportState.Idle) {
        AlertDialog(
            onDismissRequest = { viewModel.clearExportState() },
            containerColor = DeepNavy,
            title = {
                Text(
                    text = if (exportState is ExportState.Rendering) "PREPARING RENDER EXPORT..." else "EXPORT COMPLETED! ✓",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (exportState is ExportState.Rendering) {
                        val progress = (exportState as ExportState.Rendering).progress
                        CircularProgressIndicator(
                            color = AdPink,
                            modifier = Modifier.size(60.dp),
                            progress = { progress / 100f }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Encoding high-fidelity dynamic visual camera flows, text graphics overlay, synthesizer voices & Selected music into final MP4...",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${progress.toInt()}% COMPLETE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdCyan
                        )
                    } else {
                        val success = exportState as ExportState.Success
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = AdCyan,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your high-definition marketing advertisement has been compiles successfully and written to local device storage gallery directory at:",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "DCIM/AdCraft_Generations/${project.name.replace(" ", "_")}_${success.resolution}.mp4",
                                color = AdPink,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (exportState is ExportState.Success) {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = AdPink),
                        onClick = { viewModel.clearExportState() }
                    ) {
                        Text("Close Output")
                    }
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Back toolbar button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBack,
                    border = BorderStroke(1.dp, BorderDark),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row {
                    IconButton(
                        onClick = {
                            viewModel.deleteProject(project)
                            onBack()
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red.copy(alpha = 0.8f))
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Delete", modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        // Header Metadata
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.analyzedCategory,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = AdCyan,
                        letterSpacing = 1.0.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(4.dp).background(TextGray, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = project.analyzedStyle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // INTERACTIVE RENDER GRAPHICS PREVIEW CONTAINER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preview_player_card"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, AdPink),
                colors = CardDefaults.cardColors(containerColor = SpaceBlack)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Headline Tag
                    Text(
                        text = "SCENE ${activeSceneIndex + 1} OF 5: ${
                            when(activeSceneIndex) {
                                0 -> "HOOK INTRO"
                                1 -> "PRODUCT HIGHLIGHT"
                                2 -> "BENEFITS DEEPDIVE"
                                3 -> "SOCIAL SIGNALS"
                                else -> "BRAND CALL-TO-ACTION"
                            }
                        }",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        color = AdCyan,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Resizable Interactive Canvas Frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(
                                if (project.aspectRatio == "9:16") 0.58f else if (project.aspectRatio == "1:1") 1f else 1.77f
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0C071C))
                            .border(1.dp, BorderDark, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Product Base image mock with camera matrix translation effects
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(animatedProgress),
                            contentAlignment = Alignment.Center
                        ) {
                            if (project.imagePath.startsWith("https://")) {
                                AsyncImage(
                                    model = project.imagePath,
                                    contentDescription = "Dynamic preview background",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Default abstract visual background
                                Image(
                                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                                    contentDescription = "Mock item",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .alpha(0.3f),
                                    colorFilter = ColorFilter.tint(AdPink)
                                )
                            }

                            // Dynamic overlays for scenes that makes it feel extremely active and alive
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.Black.copy(alpha = 0.2f),
                                                Color.Black.copy(alpha = 0.65f)
                                            )
                                        )
                                    )
                            )

                            // Specific Scene Overlay Graphics
                            when (activeSceneIndex) {
                                1 -> { // Showcase light sweep
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.White.copy(alpha = 0.2f),
                                                        Color.Transparent
                                                    ),
                                                    start = Offset(0f, 0f),
                                                    end = Offset(animatedProgress * 600f, animatedProgress * 600f)
                                                )
                                            )
                                    )
                                }
                                2 -> { // Features checklist bubbles
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.TopStart
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            project.benefits.split(",").forEach { term ->
                                                Row(
                                                    modifier = Modifier
                                                        .background(
                                                            Color.Black.copy(alpha = 0.6f),
                                                            shape = RoundedCornerShape(20.dp)
                                                        )
                                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = AdCyan,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(term.trim(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                                3 -> { // Trust signals
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFFFFB800).copy(alpha = 0.9f),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = "Stars", tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("4.9 Trust Index", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                                4 -> { // Final CTA with Brand Kit Colors! Simple dynamic rendering of chosen branding options
                                    val bkColorParsed = try {
                                        Color(android.graphics.Color.parseColor(brandKit.primaryColor))
                                    } catch (e: Exception) {
                                        AdPink
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.82f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Campaign,
                                                contentDescription = "Brandlogo",
                                                tint = bkColorParsed,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = project.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(bkColorParsed, shape = RoundedCornerShape(20.dp))
                                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                                    .border(2.dp, Color.White, shape = RoundedCornerShape(20.dp))
                                            ) {
                                                Text(
                                                    text = brandKit.defaultCtaText.uppercase(),
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Sliding Big Overlay Title Text
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 20.dp)
                                .background(CardBackground.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = activeSceneOverlayText.uppercase(),
                                fontSize = if (project.aspectRatio == "9:16") 18.sp else 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp,
                                style = TextStyle(
                                    shadow = androidx.compose.ui.graphics.Shadow(
                                        color = Color.Black,
                                        offset = Offset(2f, 2f),
                                        blurRadius = 4f
                                    )
                                )
                            )
                        }

                        // Subtitle narration Voiceover overlay at the bottom
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Voice playing",
                                        tint = AdCyan,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "[AI VOICE GENERATOR: ${project.voiceType} (${project.voiceLanguage})]",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdCyan
                                    )
                                }
                                Text(
                                    text = "\"${project.voiceoverText}\"",
                                    color = Color.White,
                                    fontSize = if (project.aspectRatio == "9:16") 11.sp else 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Player controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { isPlaying = !isPlaying }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = AdPink,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Progress slider
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .background(BorderDark, shape = CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(videoProgress)
                                    .background(AdPink, shape = CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "0:${String.format("%02d", (videoProgress * 30).toInt())} / 0:30",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Scene selection pills for direct timeline scrub
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (0..4).forEach { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .background(
                                        if (activeSceneIndex == i) AdPink else CardBackground,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        activeSceneIndex = i
                                        videoProgress = 0f
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SCENE ${i + 1}",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeSceneIndex == i) Color.White else TextGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // SCENE STORYBOARD DETAILS ACCORDION
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VideoCall, contentDescription = "Camera", tint = AdCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("STORYBOARD CAMERA DIRECTIONS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "The AI video model is configured with the following active camera prompt details:",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SpaceBlack, shape = RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = activeSceneDescription,
                            color = AdCyan,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // GENERATED COPYWRITING METRICS OR ASSETS (HEADLINE, ANGLE, BENEFITS etc.)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Article, contentDescription = "Copywriting", tint = AdCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI CONVERSION COPYWRITING", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Headline
                    Column {
                        Text("Marketing Hook & Headline", fontSize = 11.sp, color = TextGray)
                        Text(project.headline, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    // Positioning
                    Column {
                        Text("Product Positioning Angle", fontSize = 11.sp, color = TextGray)
                        Text(project.analyzedPositioning, fontSize = 12.sp, color = Color.White)
                    }
                    // Benefits & Painpoints
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Core Benefits Solved", fontSize = 11.sp, color = TextGray)
                            Text(project.benefits, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdCyan)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("User Pain Points", fontSize = 11.sp, color = TextGray)
                            Text(project.painPoints, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // SPECIAL INDONESIAN LOCALIZATION MARKETS PANEL (HIGH PRIORITY FEATURE)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, AdCyan.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C171F))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Translate, contentDescription = "Indonesia", tint = AdCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("INDONESIA MARKET CAMPAIGN", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdCyan)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("RI SEAMLESS", color = Color.Red, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "AI localized conversion copies formatted specifically for Shopee, Tokopedia, TikTok Local Shop and Whatsapp direct selling formats:",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val localCampaigns = listOf(
                        Triple("TikTok Caption & Hooks", project.idTikTokCaption + "\n\nHOOKS:\n" + project.idTikTokHooks, Icons.Default.MusicNote),
                        Triple("Shopee Marketplace SEO Title", project.idShopeeTitle, Icons.Default.ShoppingBag),
                        Triple("Tokopedia Marketplace SEO Title", project.idTokopediaTitle, Icons.Default.Storefront),
                        Triple("Rincian Deskripsi Produk", project.idProductDescription, Icons.Default.Description),
                        Triple("WhatsApp Broadcast Sales Copy", project.idWhatsAppMessage, Icons.Default.Message)
                    )

                    localCampaigns.forEach { (formatTitle, contentBody, icon) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(SpaceBlack, shape = RoundedCornerShape(10.dp))
                                .border(1.dp, BorderDark, shape = RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = icon, contentDescription = null, tint = AdPink, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(formatTitle, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = {
                                        clipManager.setText(AnnotatedString(contentBody))
                                        Toast.makeText(context, "Copied $formatTitle!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = AdCyan, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = contentBody,
                                color = TextGray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // MP4 EXPORT ACTIONS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("COMPILE & EXPORT VIDEO", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select output render formats. Higher scales require more operations.", fontSize = 10.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("1080P", "2K", "4K").forEach { scaleQuality ->
                            Button(
                                onClick = { viewModel.triggerExport(scaleQuality) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (scaleQuality == "4K") AdPink else CardBackground),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(scaleQuality, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// STAGE 3: Projects list view
@Composable
fun ProjectsListScreen(viewModel: MainViewModel) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "My Generated Projects",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = "Stored local SQLite collections generated on this device.",
            fontSize = 12.sp,
            color = TextGray
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = "Empty",
                        tint = AdPink,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("No AI ads generated yet!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = "Head back to the Create tab to configure and generate your first marketing video ad details.",
                        color = TextGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(projects) { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectProject(project) },
                        colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass),
                        border = BorderStroke(1.dp, BorderDark),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0D061A)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (project.imagePath.startsWith("https://")) {
                                    AsyncImage(
                                        model = project.imagePath,
                                        contentDescription = "Project image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = "Scene",
                                        tint = AdPink,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(AdCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = project.analyzedCategory.uppercase(),
                                            color = AdCyan,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = project.aspectRatio,
                                        color = TextGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = project.name,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "\"${project.headline}\"",
                                    color = TextGray,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "View",
                                tint = TextGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// STAGE 4: Preset Templates list
@Composable
fun TemplatesScreen(viewModel: MainViewModel) {
    val items = listOf(
        TemplateData("Viral TikTok Trend", "Fast paced zoom-ins with bold colored subtitles and high resonance. Ideal for daily reels.", "Teens & Young Lifestyle", "Viral TikTok", "Indonesian"),
        TemplateData("Luxury Cinematic Brand", "Slow macro camera glides, golden outlines, and professional orchestra music accents.", "Premium Tech, Fashion", "Luxury Brand", "English"),
        TemplateData("Electronics Launch promo", "Glowing laser light outlines, particle sweeps, and fast specifications titles grid.", "Techies & General Buyers", "Cinematic", "English"),
        TemplateData("Beauty Cosmetic Cream", "Soft radial gradient filters, smooth fade transitions, and warm skincare testimonials.", "Beauty / Skincare Care", "Cinematic", "Indonesian"),
        TemplateData("Delicious Food & Brew", "Vivid contrast color pops, quick zoom-pans, and bold pricing stickers overlays.", "Gourmet Food Lovers", "Minimalist", "Indonesian"),
        TemplateData("Fashion & Clothing showcase", "Asymmetrical layouts, high contrast cuts, and pulsing music with reviews scrolling bar.", "Fashion Seekers", "Luxury Brand", "English")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "SaaS Video Templates",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = "Select a pre-configured scenario template to try AI generation instantly.",
            fontSize = 12.sp,
            color = TextGray
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            items(items) { temp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(temp.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .background(AdPink.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(temp.style, color = AdPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(temp.description, color = TextGray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Audience: ${temp.audience}",
                                color = AdCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = AdPink),
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    viewModel.applyTemplatePreset(
                                        name = temp.title.replace("Trend", "Product").replace("Brand", "Item"),
                                        desc = "A premium product built using ${temp.title} parameters.",
                                        audience = temp.audience,
                                        style = temp.style,
                                        voiceLang = temp.language
                                    )
                                }
                            ) {
                                Text("Load Template", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class TemplateData(
    val title: String,
    val description: String,
    val audience: String,
    val style: String,
    val language: String
)

// STAGE 5: Brand kit configurations page
@Composable
fun BrandKitScreen(viewModel: MainViewModel) {
    val primaryColor by viewModel.brandKitPrimaryColor.collectAsStateWithLifecycle()
    val secondaryColor by viewModel.brandKitSecondaryColor.collectAsStateWithLifecycle()
    val defaultCta by viewModel.brandKitCta.collectAsStateWithLifecycle()
    val brandPreset by viewModel.brandKitPreset.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val colorsGroup = listOf(
        "#FF3E6C" to "#0DF5E3", // Magenta and Cyan
        "#7C3AED" to "#F43F5E", // Purple and Red
        "#10B981" to "#3B82F6", // Emerald and Blue
        "#F59E0B" to "#EC4899"  // Amber and Pink
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "My SaaS Brand Kit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Save your logo, colors, and fonts. AI applies this brand kit automatically to the closing Scene 5 of generated videos.",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "BRAND IDENTITY SETTINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdCyan,
                        letterSpacing = 1.sp
                    )

                    // Pick brand color palettes
                    Column {
                        Text("Recommended Brand Theme Color Palettes", fontSize = 12.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            colorsGroup.forEach { (pColor, sColor) ->
                                val isSelected = primaryColor == pColor && secondaryColor == sColor
                                val borderStroke = if (isSelected) BorderStroke(2.dp, Color.White) else BorderStroke(1.dp, BorderDark)

                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .background(CardBackground, shape = RoundedCornerShape(8.dp))
                                        .border(borderStroke, shape = RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.brandKitPrimaryColor.value = pColor
                                            viewModel.brandKitSecondaryColor.value = sColor
                                        }
                                        .padding(4.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                                .background(
                                                    Color(android.graphics.Color.parseColor(pColor)),
                                                    RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                                                )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                                .background(
                                                    Color(android.graphics.Color.parseColor(sColor)),
                                                    RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Custom input fields
                    OutlinedTextField(
                        value = primaryColor,
                        onValueChange = { viewModel.brandKitPrimaryColor.value = it },
                        label = { Text("Custom Hex Color Primary") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AdPink,
                            unfocusedBorderColor = BorderDark
                        )
                    )

                    OutlinedTextField(
                        value = secondaryColor,
                        onValueChange = { viewModel.brandKitSecondaryColor.value = it },
                        label = { Text("Custom Hex Color Secondary") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AdPink,
                            unfocusedBorderColor = BorderDark
                        )
                    )

                    OutlinedTextField(
                        value = defaultCta,
                        onValueChange = { viewModel.brandKitCta.value = it },
                        label = { Text("Default Calls to Action (CTA) Text") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AdPink,
                            unfocusedBorderColor = BorderDark
                        )
                    )

                    OutlinedTextField(
                        value = brandPreset,
                        onValueChange = { viewModel.brandKitPreset.value = it },
                        label = { Text("Brand Layout Style Preset") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AdPink,
                            unfocusedBorderColor = BorderDark
                        )
                    )

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = AdPink),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            viewModel.updateBrandKit()
                            Toast.makeText(context, "Brand Kit saved successfully!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Save Brand Kit Settings", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// STAGE 6: Settings and Payments simulated logs
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "AdCraft AI System Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manage configurations, integration adapters, and simulated credits.",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }

        // Adapter Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "INTEGRATION ADAPTER ARCHITECTURE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdCyan,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "AdCraft AI encapsulates text, video, and audio models behind a strict provider adapter architecture, allowing swapping on the fly.",
                        fontSize = 12.sp,
                        color = Color.White
                    )

                    val providers = listOf(
                        "Text Generation AI" to "Gemini 3.5 Flash",
                        "Image-To-Video AI" to "Veo 3.1 Fast Generator (Preview)",
                        "Voice Synthesis TTS" to "Prebuilt Kore Engine",
                        "Soundtrack Composer" to "Lyria Audio Synthesis Model"
                    )

                    providers.forEach { (topic, details) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SpaceBlack, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(topic, fontSize = 11.sp, color = TextGray)
                            Text(details, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdPink)
                        }
                    }
                }
            }
        }

        // Simulated credit checkouts Stripe Pricing Plans
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, AdPink.copy(alpha = 0.4f), shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "SIMULATED STRIPE CODES PRICING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val plans = listOf(
                        Triple("Enterprise Global Plan", "$199 / month", "Unlimited 4K renders & 12 localized languages"),
                        Triple("Creators Starter Kit", "$29 / month", "50 HD exports & core adapters active")
                    )

                    plans.forEach { (plan, price, specs) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(SpaceBlack, shape = RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(plan, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(specs, color = TextGray, fontSize = 10.sp)
                                }
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = AdPink),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = {
                                        viewModel.simulateStripePayment(plan, price)
                                        Toast.makeText(context, "Simulated $plan payment verified!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("Pay $price", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Payment logs history
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundGlass)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "TRANSACTION LOGS HISTORY (ROOM DB)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (payments.isEmpty()) {
                        Text("No transactions logged yet on this device.", fontSize = 12.sp, color = TextGray)
                    } else {
                        payments.forEach { rowLog ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(SpaceBlack, RoundedCornerShape(6.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(rowLog.planName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(rowLog.paymentId, color = TextGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                }
                                Text(rowLog.amount, color = AdCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
