package com.example.dailynutri.data

/**
 * Single Source of Truth untuk data User.
 * Hapus file UserData.kt lainnya!
 */
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)