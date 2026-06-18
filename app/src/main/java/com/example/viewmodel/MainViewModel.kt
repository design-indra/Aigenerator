package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState {
    object Idle : UiState
    object Analyzing : UiState
    object Generating : UiState
    data class Success(val project: Project) : UiState
    data class Error(val message: String) : UiState
}

sealed interface ExportState {
    object Idle : ExportState
    data class Rendering(val progress: Float) : ExportState
    data class Success(val format: String, val resolution: String) : ExportState
}

class MainViewModel(
    application: Application,
    private val repository: Repository
) : AndroidViewModel(application) {

    // Global UI state
    val projects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val brandKit: StateFlow<BrandKit> = repository.brandKitFlow
        .map { it ?: BrandKit() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BrandKit())

    val payments: StateFlow<List<Payment>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active screen navigation
    val currentTab = MutableStateFlow("CREATE") // CREATE, PROJECTS, TEMPLATES, BRANDKIT, SETTINGS
    
    // Active project view
    val activeProject = MutableStateFlow<Project?>(null)

    // Form inputs
    val productName = MutableStateFlow("Wireless Earbuds X1")
    val productDesc = MutableStateFlow("Premium sound quality with advanced active noise cancellation and 30-hour battery life.")
    val targetAudience = MutableStateFlow("Young Professionals & Commuters")
    val customPrompt = MutableStateFlow("Create a high-energy modern advertisement with cinematic elements.")
    val stylePreset = MutableStateFlow("Viral TikTok") // Viral TikTok, Luxury Brand, Cinematic, Minimalist, Emotional
    val selectedVoice = MutableStateFlow("Male") // Male, Female
    val selectedLanguage = MutableStateFlow("English") // Indonesian, English, Spanish, French
    val selectedMusicStyle = MutableStateFlow("Energetic") // Energetic, Luxury, Emotional, Modern, Viral TikTok
    val selectedAspectRatio = MutableStateFlow("9:16") // 9:16, 1:1, 16:9
    val pickedBitmap = MutableStateFlow<Bitmap?>(null)
    val localImagePath = MutableStateFlow("")

    // Generation states
    val uiState = MutableStateFlow<UiState>(UiState.Idle)
    val exportState = MutableStateFlow<ExportState>(ExportState.Idle)

    // Brand Kit Editor fields
    val brandKitPrimaryColor = MutableStateFlow("#FF3E6C")
    val brandKitSecondaryColor = MutableStateFlow("#0DF5E3")
    val brandKitCta = MutableStateFlow("Shop Now!")
    val brandKitPreset = MutableStateFlow("Modern Glossy")

    init {
        // Load Brand Kit defaults on start
        viewModelScope.launch {
            val bk = repository.getBrandKit()
            brandKitPrimaryColor.value = bk.primaryColor
            brandKitSecondaryColor.value = bk.secondaryColor
            brandKitCta.value = bk.defaultCtaText
            brandKitPreset.value = bk.brandStylePreset
        }
    }

    fun selectTab(tab: String) {
        currentTab.value = tab
        if (tab == "CREATE" && uiState.value is UiState.Success) {
            // keep state or clear
        } else {
            activeProject.value = null
        }
    }

    fun selectProject(project: Project) {
        activeProject.value = project
        currentTab.value = "PROJECTS"
    }

    fun applyTemplatePreset(name: String, desc: String, audience: String, style: String, voiceLang: String = "English") {
        productName.value = name
        productDesc.value = desc
        targetAudience.value = audience
        stylePreset.value = style
        selectedLanguage.value = voiceLang
        currentTab.value = "CREATE"
    }

    fun updateBrandKit() {
        viewModelScope.launch {
            val current = repository.getBrandKit()
            val updated = current.copy(
                primaryColor = brandKitPrimaryColor.value,
                secondaryColor = brandKitSecondaryColor.value,
                defaultCtaText = brandKitCta.value,
                brandStylePreset = brandKitPreset.value
            )
            repository.saveBrandKit(updated)
        }
    }

    fun triggerGeneration() {
        viewModelScope.launch {
            uiState.value = UiState.Analyzing
            kotlinx.coroutines.delay(2000) // Simulate Computer Vision Image Scan

            uiState.value = UiState.Generating
            kotlinx.coroutines.delay(2500) // Simulate Storyboard and script writing

            try {
                val currentBrand = repository.getBrandKit()
                val generatedProject = repository.generateAdCampaign(
                    productName = productName.value,
                    productDesc = productDesc.value,
                    targetAudience = targetAudience.value,
                    customPrompt = customPrompt.value,
                    stylePreset = stylePreset.value,
                    bitmap = pickedBitmap.value,
                    brandKit = currentBrand
                )
                
                // Keep the chosen path or base reference
                generatedProject.imagePath = localImagePath.value
                
                val newId = repository.insertProject(generatedProject)
                val finalProject = generatedProject.copy(id = newId)
                
                uiState.value = UiState.Success(finalProject)
                activeProject.value = finalProject
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message ?: "Failed to generate video campaign")
            }
        }
    }

    fun triggerExport(resolution: String) {
        viewModelScope.launch {
            exportState.value = ExportState.Rendering(0f)
            for (p in 1..10) {
                kotlinx.coroutines.delay(250)
                exportState.value = ExportState.Rendering(p * 10f)
            }
            exportState.value = ExportState.Success(
                format = if (selectedAspectRatio.value == "9:16") "9:16 TikTok Reels" else if (selectedAspectRatio.value == "1:1") "1:1 Square" else "16:9 Landscape MP4",
                resolution = resolution
            )
        }
    }

    fun clearExportState() {
        exportState.value = ExportState.Idle
    }

    fun simulateStripePayment(planName: String, price: String) {
        viewModelScope.launch {
            val simulatedTx = Payment(
                paymentId = "ch_stripe_" + (100000..999999).random(),
                amount = price,
                status = "COMPLETED",
                planName = planName
            )
            repository.savePayment(simulatedTx)
        }
    }

    fun resetState() {
        uiState.value = UiState.Idle
        activeProject.value = null
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProjectById(project.id)
            if (activeProject.value?.id == project.id) {
                activeProject.value = null
            }
        }
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
