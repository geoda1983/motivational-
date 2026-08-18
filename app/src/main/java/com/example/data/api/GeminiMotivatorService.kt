package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.MotivationContent
import com.example.data.offline.OfflineInspirations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiMotivatorService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateInspirationForGoal(
        goalTitle: String,
        category: String,
        whyItMatters: String,
        isIdleSlackingNudge: Boolean = false
    ): MotivationContent = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiMotivator", "No API key configured, using high-fidelity offline motivator library.")
            return@withContext if (isIdleSlackingNudge) {
                OfflineInspirations.getRandomIdleNudge(goalTitle)
            } else {
                OfflineInspirations.getInspirationForGoal(goalTitle, category)
            }
        }

        val prompt = if (isIdleSlackingNudge) {
            """
            The user is currently idle or slacking off and needs an immediate, punchy anti-procrastination wake-up call to get back to their stated goal:
            Goal: "$goalTitle"
            Category: "$category"
            Why this goal matters to them: "$whyItMatters"

            Please provide a JSON response with these exact keys:
            {
              "quote": "A powerful quote about overcoming hesitation or seizing this exact moment",
              "author": "The real historical, modern, or philosophical figure who said it",
              "story": "A short, vivid 2-3 sentence true story of someone who conquered lethargy/resistance or pushed through friction",
              "takeaway": "One clear psychological reframe on why slacking off right now hurts their future self",
              "actionNudge": "A single micro-action they can do in under 3 minutes to restart momentum"
            }
            Only return valid JSON without markdown wrapping.
            """.trimIndent()
        } else {
            """
            The user is pursuing this personal goal and needs inspiring motivation, deep historical/real-world context, and a short story:
            Goal: "$goalTitle"
            Category: "$category"
            Why it matters: "$whyItMatters"

            Search knowledge of history, sports, science, arts, and philosophy to find a deeply relevant quote and inspiring true micro-story directly matching this goal.
            
            Please provide a JSON response with these exact keys:
            {
              "quote": "A punchy, memorable, and contextually perfect quote directly relevant to '$goalTitle'",
              "author": "The renowned historical, modern, or philosophical figure who said it",
              "story": "A vivid 3-4 sentence true micro-story illustrating how this figure or an innovator overcame obstacles directly analogous to this goal",
              "takeaway": "A profound, actionable insight connecting the story directly to the user's goal",
              "actionNudge": "A specific, immediate challenge or step the user should execute today"
            }
            Only return valid JSON without markdown wrapping.
            """.trimIndent()
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("GeminiMotivator", "Gemini API error: ${response.code} ${response.message}")
                return@withContext OfflineInspirations.getInspirationForGoal(goalTitle, category)
            }

            val responseBody = response.body?.string() ?: ""
            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (text.isNotBlank()) {
                val parsed = JSONObject(text.trim())
                MotivationContent(
                    quote = parsed.optString("quote", "Action conquers doubt."),
                    author = parsed.optString("author", "AI Motivator"),
                    story = parsed.optString("story", "Great achievements are built day by day through focused effort."),
                    takeaway = parsed.optString("takeaway", "Your daily habits compound into your ultimate destiny."),
                    actionNudge = parsed.optString("actionNudge", "Take one focused step toward your goal now."),
                    category = category,
                    isIdleAlert = isIdleSlackingNudge
                )
            } else {
                OfflineInspirations.getInspirationForGoal(goalTitle, category)
            }
        } catch (e: Exception) {
            Log.e("GeminiMotivator", "Error calling Gemini API, fallback to offline inspirations", e)
            OfflineInspirations.getInspirationForGoal(goalTitle, category)
        }
    }

    suspend fun generateMissionPassedDebrief(goalTitle: String, category: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "You stared down the friction, refused to quit, and conquered '$goalTitle'. Massive RESPECT earned!"
        }

        try {
            val prompt = """
            The user has just completed and achieved their major objective: "$goalTitle" (Category: $category).
            Write an electrifying, legendary 2-sentence celebratory debrief acknowledging their grit, discipline, and earned RESPECT. Keep the tone bold, celebratory, and empowering.
            """.trimIndent()

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val rootJson = JSONObject(responseBody)
                val text = rootJson.optJSONArray("candidates")?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
                if (!text.isNullOrBlank()) {
                    return@withContext text.trim().removePrefix("\"").removeSuffix("\"")
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiMotivator", "Failed to generate debrief", e)
        }
        "You stared down the friction, refused to quit, and conquered '$goalTitle'. Massive RESPECT earned!"
    }
}
