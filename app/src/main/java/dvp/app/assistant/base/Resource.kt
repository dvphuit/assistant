package dvp.app.assistant.base

sealed class Resource<T>() {
    class Empty<T>() : Resource<T>()
    class Loading<T>(data: T? = null) : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val throwable: Throwable, val data: T?) : Resource<T>()
}

sealed class ViewState {
    // Represents different states for quotes
    object Empty : ViewState()
    object Loading : ViewState()
    data class Success<T>(val data: T) : ViewState()
    data class Error(val exception: Throwable?) : ViewState()
}