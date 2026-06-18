package com.example.data

import android.graphics.Bitmap
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import android.util.Base64

class Repository(
    private val projectDao: ProjectDao,
    private val brandKitDao: BrandKitDao,
    private val paymentDao: PaymentDao
) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()
    val brandKitFlow: Flow<BrandKit?> = brandKitDao.getBrandKitFlow()
    val allPayments: Flow<List<Payment>> = paymentDao.getAllPayments()

    suspend fun getProjectById(id: Long): Project? = projectDao.getProjectById(id)
    suspend fun insertProject(project: Project): Long = projectDao.insertProject(project)
    suspend fun deleteProjectById(id: Long) = projectDao.deleteProjectById(id)

    suspend fun getBrandKit(): BrandKit {
        return brandKitDao.getBrandKit() ?: BrandKit().also {
            brandKitDao.insertBrandKit(it)
        }
    }

    suspend fun saveBrandKit(brandKit: BrandKit) {
        brandKitDao.insertBrandKit(brandKit)
    }

    suspend fun savePayment(payment: Payment) {
        paymentDao.insertPayment(payment)
    }

    // Convert Bitmap to Base64 for feeding into multimodal prompt
    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    // Call Gemini API to generate the video advertising assets
    suspend fun generateAdCampaign(
        productName: String,
        productDesc: String,
        targetAudience: String,
        customPrompt: String,
        stylePreset: String,
        bitmap: Bitmap?,
        brandKit: BrandKit
    ): Project = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasApiKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        var analyzedCategory = "Electronics"
        var analyzedColors = "${brandKit.primaryColor}, ${brandKit.secondaryColor}, #111827"
        var analyzedPositioning = "Premium high-fidelity option for core daily users."
        var analyzedStyle = "Cinematic Minimalist"

        var headline = "Unleash Superior Audio Quality"
        var hook = "Meet the sound of your dreams."
        var voiceover = "Listen closely. The world fades away. All that remains is premium, high-fidelity acoustics. Crafted for your active lifestyle, designed for you."
        var cta = brandKit.defaultCtaText
        var hashtags = "#premium #marketing #trending"
        var benefits = "Noise Cancellation, 30h battery, HD calling."
        var painPoints = "Tangled cords, ambient noise interruptions, low battery anxiety."
        var solutions = "Direct wireless touch-control, powerful active suppression, smart quick charging."
        var marketingAngle = "Elevating standard routines to high-luxury symphonies."

        var s1Desc = "Cinematic camera zoom on the clean contours of $productName."
        var s2Desc = "Dynamic light sweep emphasizing premium color details: ${brandKit.primaryColor} highlighting sleek metallic lines."
        var s3Desc = "Side-by-side transition splitting into benefit icons for active sound gating."
        var s4Desc = "Splashes of fine particles symbolizing pure high-resonance, with happy user silhouettes."
        var s5Desc = "Modern clean logo card fading in, displaying CTA and $productName branding."

        var s1Overlay = "The Future of Sound"
        var s2Overlay = "Engineered for Clarity"
        var s3Overlay = "Zero Noise. Pure Audio."
        var s4Overlay = "Loved by +10,000 Professionals"
        var s5Overlay = "Elevate Your Sound Today"

        // Indonesia market entries
        var tiktokCap = "Audio ini bikin hanyut! Headphone tanpa batas untuk dukung fokus harianmu. Klik keranjang kuning sekarang! ⚡🛒"
        var tiktokHooks = "Capek denger suara berisik pas lagi fokus kerja? Ini solusinya!"
        var tiktokHash = "#AdCraftAI #audiohp #headsetviral #localindonesia #trendingtech"
        var shopeeTitle = "PROMO GILA - $productName Headset Wireless ANC Premium V5.3 Garansi Resmi"
        var tokomelTitle = "$productName Wireless Earbuds Active Noise Cancelling High-End Original"
        var idProdDesc = "Nikmati kejernihan suara tingkat tinggi bersama $productName. Dibekali fitur Active Noise Cancelling peredam bising terbaik, baterai super awet 30 jam, dan warna trendi yang modern. Sangat cocok untuk anak muda!"
        var fbAds = "🔥 Capek dengan earphone murahan yang sering rusak? Upgrade ke premium hari ini dengan garansi servis penuh!"
        var igCap = "Menyempurnakan setiap ketukan musik favoritmu. Didesain ergonomis dan dinamis. ✨"
        var waMessage = "Halo Kak! Ada kejutan menarik buat kamu pecinta audio berkualitas tinggi. Dapatkan penawaran $productName dengan diskon 20% khusus hari ini! Pesan di sini: wa.me/62812345"

        if (hasApiKey) {
            try {
                val fullSystemInstruction = """
                    You are an expert AI Advertising Video Generator called AdCraft AI.
                    You analyze products and generate high-grade video scripts, scene storyboards, and Indonesian local marketing copy.
                    
                    You MUST return the output containing exactly the following tags, followed by their generated contents. DO NOT omit any tag. Use English for core aspects, and follow the language request for Indonesian aspects.
                    
                    [CATEGORY] - Identify specific category (e.g., Electronics, Fashion, Beauty, Food, etc.)
                    [COLORS] - Recommend 3 dominant colors as comma-separated Hex values, starting with ${brandKit.primaryColor} and ${brandKit.secondaryColor}
                    [POSITIONING] - 1-sentence product market positioning
                    [STYLE] - Style (e.g. Cinematic, Luxury, Minimalist, Energetic, Viral TikTok)
                    [HEADLINE] - Ad headline (under 6 words)
                    [HOOK] - Engaging attention grabber hook
                    [VOICEOVER] - Complete voiceover script for a 30s video
                    [CTA] - Compelling CTA including context on "${brandKit.defaultCtaText}"
                    [HASHTAGS] - Top 5 generic marketing hashtags
                    [BENEFITS] - Top 3 benefits/features
                    [PAIN_POINTS] - Main user problems solved
                    [SOLUTIONS] - How this product acts as a reliable solution
                    [ANGLE] - Core emotional marketing angle
                    [SCENE_1_DESC] - Camera/motion prompt for opening scene (Scene 1)
                    [SCENE_1_OVERLAY] - Big text overlay for Scene 1
                    [SCENE_2_DESC] - Camera/motion prompt for product showcase scene (Scene 2)
                    [SCENE_2_OVERLAY] - Big text overlay for Scene 2
                    [SCENE_3_DESC] - Camera/motion prompt for benefits/features scene (Scene 3)
                    [SCENE_3_OVERLAY] - Big text overlay for Scene 3
                    [SCENE_4_DESC] - Camera/motion prompt for social proof scene (Scene 4)
                    [SCENE_4_OVERLAY] - Big text overlay for Scene 4
                    [SCENE_5_DESC] - Camera/motion prompt for strong finish Call-to-action scene (Scene 5)
                    [SCENE_5_OVERLAY] - Big text overlay for Scene 5
                    
                    Indonesian Marketing Localizations:
                    [ID_TIKTOK_CAPTION] - Catchy TikTok caption in Indonesian, including emojis and keranjang kuning mention
                    [ID_TIKTOK_HOOKS] - 3 high-converting hook taglines in Indonesian
                    [ID_TIKTOK_HASHTAGS] - Popular local TikTok tags (e.g., #racuntiktok)
                    [ID_SHOPEE_TITLE] - Shopee title in Indonesian matching SEO best practices (under 80 chars)
                    [ID_TOKOPEDIA_TITLE] - Tokopedia SEO-friendly title in Indonesian
                    [ID_PRODUCT_DESC] - Long engaging description in Indonesian for marketplaces
                    [ID_FB_COPY] - Direct response Facebook Ad Copy in Indonesian
                    [ID_IG_CAPTION] - Lifestyle focused Instagram caption in Indonesian with emojis
                    [ID_WA_MESSAGE] - Direct sales WhatsApp marketing message in Indonesian with professional greetings
                """.trimIndent()

                val promptStringBuilder = StringBuilder()
                promptStringBuilder.append("Please analyze and generate advertising video components for this product:\n")
                promptStringBuilder.append("Product Name: $productName\n")
                promptStringBuilder.append("Product Description: $productDesc\n")
                promptStringBuilder.append("Target Audience: $targetAudience\n")
                promptStringBuilder.append("Custom Prompt instructions: $customPrompt\n")
                promptStringBuilder.append("Aesthetic Preset: $stylePreset\n")
                promptStringBuilder.append("Apply brand colors Primary: ${brandKit.primaryColor}, Secondary: ${brandKit.secondaryColor}\n")

                val promptText = promptStringBuilder.toString()

                val partsList = mutableListOf<GeminiPart>()
                partsList.add(GeminiPart(text = promptText))

                if (bitmap != null) {
                    try {
                        val base64Data = bitmap.toBase64()
                        partsList.add(GeminiPart(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data)))
                    } catch (e: Exception) {
                        Log.e("Repository", "Failed to encode bitmap to base64", e)
                    }
                }

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = partsList)),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = fullSystemInstruction))),
                    generationConfig = GenerationConfig(temperature = 0.7f)
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!rawText.isNullOrEmpty()) {
                    // Start parsing with robust tag extractor
                    fun getTagContent(text: String, tag: String): String {
                        val regex = Regex("""\[$tag]\s*(.*?)(?=\[[A-Z0-9_]+]|$)""", RegexOption.DOT_MATCHES_ALL)
                        val match = regex.find(text)
                        return match?.groups?.get(1)?.value?.trim() ?: ""
                    }

                    val cat = getTagContent(rawText, "CATEGORY")
                    if (cat.isNotEmpty()) analyzedCategory = cat

                    val col = getTagContent(rawText, "COLORS")
                    if (col.isNotEmpty()) analyzedColors = col

                    val pos = getTagContent(rawText, "POSITIONING")
                    if (pos.isNotEmpty()) analyzedPositioning = pos

                    val sty = getTagContent(rawText, "STYLE")
                    if (sty.isNotEmpty()) analyzedStyle = sty

                    val hld = getTagContent(rawText, "HEADLINE")
                    if (hld.isNotEmpty()) headline = hld

                    val hk = getTagContent(rawText, "HOOK")
                    if (hk.isNotEmpty()) hook = hk

                    val vo = getTagContent(rawText, "VOICEOVER")
                    if (vo.isNotEmpty()) voiceover = vo

                    val ct = getTagContent(rawText, "CTA")
                    if (ct.isNotEmpty()) cta = ct

                    val hs = getTagContent(rawText, "HASHTAGS")
                    if (hs.isNotEmpty()) hashtags = hs

                    val bn = getTagContent(rawText, "BENEFITS")
                    if (bn.isNotEmpty()) benefits = bn

                    val pp = getTagContent(rawText, "PAIN_POINTS")
                    if (pp.isNotEmpty()) painPoints = pp

                    val sl = getTagContent(rawText, "SOLUTIONS")
                    if (sl.isNotEmpty()) solutions = sl

                    val ang = getTagContent(rawText, "ANGLE")
                    if (ang.isNotEmpty()) marketingAngle = ang

                    val s1 = getTagContent(rawText, "SCENE_1_DESC")
                    if (s1.isNotEmpty()) s1Desc = s1
                    val s1o = getTagContent(rawText, "SCENE_1_OVERLAY")
                    if (s1o.isNotEmpty()) s1Overlay = s1o

                    val s2 = getTagContent(rawText, "SCENE_2_DESC")
                    if (s2.isNotEmpty()) s2Desc = s2
                    val s2o = getTagContent(rawText, "SCENE_2_OVERLAY")
                    if (s2o.isNotEmpty()) s2Overlay = s2o

                    val s3 = getTagContent(rawText, "SCENE_3_DESC")
                    if (s3.isNotEmpty()) s3Desc = s3
                    val s3o = getTagContent(rawText, "SCENE_3_OVERLAY")
                    if (s3o.isNotEmpty()) s3Overlay = s3o

                    val s4 = getTagContent(rawText, "SCENE_4_DESC")
                    if (s4.isNotEmpty()) s4Desc = s4
                    val s4o = getTagContent(rawText, "SCENE_4_OVERLAY")
                    if (s4o.isNotEmpty()) s4Overlay = s4o

                    val s5 = getTagContent(rawText, "SCENE_5_DESC")
                    if (s5.isNotEmpty()) s5Desc = s5
                    val s5o = getTagContent(rawText, "SCENE_5_OVERLAY")
                    if (s5o.isNotEmpty()) s5Overlay = s5o

                    // Indonesian markers
                    val tkCap = getTagContent(rawText, "ID_TIKTOK_CAPTION")
                    if (tkCap.isNotEmpty()) tiktokCap = tkCap
                    val tkHk = getTagContent(rawText, "ID_TIKTOK_HOOKS")
                    if (tkHk.isNotEmpty()) tiktokHooks = tkHk
                    val tkHs = getTagContent(rawText, "ID_TIKTOK_HASHTAGS")
                    if (tkHs.isNotEmpty()) tiktokHash = tkHs

                    val shTitle = getTagContent(rawText, "ID_SHOPEE_TITLE")
                    if (shTitle.isNotEmpty()) shopeeTitle = shTitle
                    val tokTitle = getTagContent(rawText, "ID_TOKOPEDIA_TITLE")
                    if (tokTitle.isNotEmpty()) tokomelTitle = tokTitle

                    val prdDesc = getTagContent(rawText, "ID_PRODUCT_DESC")
                    if (prdDesc.isNotEmpty()) idProdDesc = prdDesc

                    val fbCopy = getTagContent(rawText, "ID_FB_COPY")
                    if (fbCopy.isNotEmpty()) fbAds = fbCopy

                    val igCaptionText = getTagContent(rawText, "ID_IG_CAPTION")
                    if (igCaptionText.isNotEmpty()) igCap = igCaptionText

                    val waText = getTagContent(rawText, "ID_WA_MESSAGE")
                    if (waText.isNotEmpty()) waMessage = waText
                }
            } catch (e: Exception) {
                Log.e("Repository", "Gemini API error. Falling back to local generation.", e)
            }
        } else {
            // Apply high fidelity customized local content if no key is supplied yet
            analyzedCategory = when {
                productName.contains("shirt", true) || productName.contains("baju", true) || stylePreset.contains("Fashion", true) -> "Fashion & Clothing"
                productName.contains("cream", true) || productName.contains("beauty", true) || stylePreset.contains("Beauty", true) -> "Beauty & Cosmetics"
                productName.contains("food", true) || productName.contains("kopi", true) || stylePreset.contains("Food", true) -> "Food & Beverage"
                else -> "Luxury Electronics"
            }
            analyzedStyle = stylePreset
            headline = "Upgrade Your Routine Now"
            hook = "Stop scrolling. This will change everything!"
            voiceover = "Ever felt like something is missing? Meet $productName. Engineered precisely for $targetAudience, it delivers uncompromised high performance every single day. See the unmatched craftsmanship."
            cta = brandKit.defaultCtaText
            hashtags = "#marketingviral #upgrade #musthave"
            benefits = "Ergonomic fit, high efficiency, beautiful finish."
            painPoints = "Standard options lack style, comfort, and direct utility."
            solutions = "Bespoke engineering, designed for premium performance."
            marketingAngle = "Empowering users with sophisticated capabilities."

            s1Desc = "Elegant camera pan showcasing the visual accents of $productName."
            s2Desc = "Macro shots of tactile materials under warm glowing spotlights."
            s3Desc = "Animated title cards sweeping across, mapping top reviews."
            s4Desc = "Dynamic transition overlaying positive feedbacks in rapid sequence."
            s5Desc = "Closing visual sequence with product branding, styled logo, and direct action prompts."

            s1Overlay = "Crafted to Perfection"
            s2Overlay = "A Touch of Excellence"
            s3Overlay = "Designed for $targetAudience"
            s4Overlay = "★★★★★ Rated 4.9/5 Stars"
            s5Overlay = "Claim Yours Special Offer"

            // Localized custom Indonesian equivalents
            tiktokCap = "Racun baru check! 🔥 Buat kalian $targetAudience wajib punya $productName biar makin trendi & produktif. Cobain sekarang dan klik keranjang kuning! 🛒✨"
            tiktokHooks = "1. Gak usah ragu, ini produk impianmu!\n2. Capek pakai barang biasa aja? Ini upgrade aslimu\n3. Terbongkar! Rahasia tampil eksklusif harian"
            shopeeTitle = "REKOMENDASI AYAH - $productName Jaminan Kualitas Premium Ekstra Hemat"
            tokomelTitle = "Distributor Resmi $productName Eksklusif Berkualitas Tinggi"
            idProdDesc = "$productName hadir sebagai solusi terbaik bagi kamu yang mendambakan kualitas tinggi di atas segalanya. Cocok digunakan sehari-hari dengan ketahanan bahan premium luar biasa."
        }

        return@withContext Project(
            name = productName,
            description = productDesc,
            targetAudience = targetAudience,
            customPrompt = customPrompt,
            templateId = when(stylePreset) {
                "Viral TikTok" -> "viral_tiktok"
                "Luxury Brand" -> "luxury_brand"
                "Electronics" -> "electronics"
                else -> "viral_tiktok"
            },
            analyzedCategory = analyzedCategory,
            analyzedColors = analyzedColors,
            analyzedPositioning = analyzedPositioning,
            analyzedStyle = analyzedStyle,
            headline = headline,
            hook = hook,
            voiceoverText = voiceover,
            ctaText = cta,
            hashtags = hashtags,
            benefits = benefits,
            painPoints = painPoints,
            solutions = solutions,
            marketingAngle = marketingAngle,
            scene1Description = s1Desc,
            scene1Overlay = s1Overlay,
            scene2Description = s2Desc,
            scene2Overlay = s2Overlay,
            scene3Description = s3Desc,
            scene3Overlay = s3Overlay,
            scene4Description = s4Desc,
            scene4Overlay = s4Overlay,
            scene5Description = s5Desc,
            scene5Overlay = s5Overlay,
            selectedMusicTrack = when(stylePreset) {
                "Luxury Brand" -> "Luxury Symphony"
                "Viral TikTok" -> "Epic Beat"
                else -> "Modern Uplifting Piano"
            },
            idTikTokCaption = tiktokCap,
            idTikTokHooks = tiktokHooks,
            idTikTokHashtags = tiktokHash,
            idShopeeTitle = shopeeTitle,
            idTokopediaTitle = tokomelTitle,
            idProductDescription = idProdDesc,
            idFacebookAdCopy = fbAds,
            idInstagramCaption = igCap,
            idWhatsAppMessage = waMessage
        )
    }
}
