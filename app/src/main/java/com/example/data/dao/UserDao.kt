package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfile): Long

    @Update
    suspend fun updateUser(user: UserProfile)

    @Delete
    suspend fun deleteUser(user: UserProfile)

    @Query("SELECT COUNT(*) FROM user_profiles")
    suspend fun getUserCount(): Int
}
