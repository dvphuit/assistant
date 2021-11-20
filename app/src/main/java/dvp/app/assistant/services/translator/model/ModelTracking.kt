package dvp.app.assistant.services.translator.model

import com.google.gson.annotations.SerializedName

data class ModelTracking (
    @SerializedName("checkpoint_md5")
    val checkpointMd5: String,
    @SerializedName("launch_doc")
    val launchDoc: String
)