package com.example.cloud.retrofit

sealed class ApiState {
    class Success(val data: Any) : ApiState()
    class Failure(val message: String) : ApiState()
    object Loading : ApiState()
}