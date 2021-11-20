package dvp.app.assistant.services.translator.model

import com.google.gson.annotations.SerializedName

data class Sentence (
    val trans: String,
    val orig: String,
    val backend: Long,
//    @SerializedName("model_specification")
//    val modelSpecification: List<Spell>,
//    @SerializedName("translation_engine_debug_info")
//    val translationEngineDebugInfo: List<TranslationEngineDebugInfo>
)