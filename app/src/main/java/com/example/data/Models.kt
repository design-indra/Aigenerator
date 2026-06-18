package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "projects")
@JsonClass(generateAdapter = true)
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val targetAudience: String,
    val customPrompt: String = "",
    val templateId: String = "viral_tiktok",
    val aspectRatio: String = "9:16",
    val voiceType: String = "Male",
    val voiceLanguage: String = "English",
    val musicStyle: String = "Energetic",
    var imagePath: String = "", // URI of local picked image
    val timestamp: Long = System.currentTimeMillis(),
    
    // AI Analysis Output
    val analyzedCategory: String = "",
    val analyzedColors: String = "",
    val analyzedPositioning: String = "",
    val analyzedStyle: String = "",
    
    // AI Copywriting Outputs
    val headline: String = "",
    val hook: String = "",
    val voiceoverText: String = "",
    val ctaText: String = "",
    val hashtags: String = "",
    val benefits: String = "",
    val painPoints: String = "",
    val solutions: String = "",
    val marketingAngle: String = "",
    
    // Storyboard Scenes 1-5 (Action / Prompt)
    val scene1Description: String = "",
    val scene2Description: String = "",
    val scene3Description: String = "",
    val scene4Description: String = "",
    val scene5Description: String = "",
    
    // Animated Overlay Texts for Scenes 1-5
    val scene1Overlay: String = "",
    val scene2Overlay: String = "",
    val scene3Overlay: String = "",
    val scene4Overlay: String = "",
    val scene5Overlay: String = "",

    // Audio selections
    val selectedMusicTrack: String = "",
    
    // Indonesia Specific Marketing Features
    val idTikTokCaption: String = "",
    val idTikTokHooks: String = "",
    val idTikTokHashtags: String = "",
    val idShopeeTitle: String = "",
    val idTokopediaTitle: String = "",
    val idProductDescription: String = "",
    val idFacebookAdCopy: String = "",
    val idInstagramCaption: String = "",
    val idWhatsAppMessage: String = ""
)

@Entity(tableName = "brand_kit")
@JsonClass(generateAdapter = true)
data class BrandKit(
    @PrimaryKey val id: Int = 1,
    val logoUri: String = "",
    val primaryColor: String = "#FF3E6C",
    val secondaryColor: String = "#0DF5E3",
    val brandFont: String = "Space Grotesk",
    val defaultCtaText: String = "Shop Now!",
    val brandStylePreset: String = "Modern Glossy"
)

@Entity(tableName = "payments")
@JsonClass(generateAdapter = true)
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val paymentId: String,
    val amount: String,
    val date: Long = System.currentTimeMillis(),
    val status: String,
    val planName: String
)
