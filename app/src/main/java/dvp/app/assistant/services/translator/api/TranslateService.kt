package dvp.app.assistant.services.translator.api

import dvp.app.assistant.services.translator.model.Translate
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface TranslateService {

    @GET("translate_a/single")
    fun listSentence(
        @Query("client") client: String = "gtx",
        @Query("sl") source: String = "ja",
        @Query("tl") destination: String = "vi",
        @Query("dt") dt: String = "t",
        @Query("dj") dj: Int = 1,
        @Query("q") query: String,
    ): Call<Translate>
}

