package dvp.app.assistant.services.translator.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Apis {
    private const val BASE_URL = "https://translate.googleapis.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun translate(): TranslateService = service()

    private inline fun <reified T> service(): T {
        return retrofit.create(T::class.java)
    }

}
