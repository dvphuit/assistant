package dvp.app.assistant.services.translator.model

import com.google.gson.annotations.SerializedName

data class TranslationEngineDebugInfo (
    @SerializedName("model_tracking")
    val modelTracking: ModelTracking
)