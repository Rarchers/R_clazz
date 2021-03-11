package com.example.r_class.model.daos

import androidx.room.*
import com.example.r_class.model.entity.User

@Dao
interface UserDao {
    @Query("select * from user")
    fun queryAll() : List<User>

    @Query("select * from user where identityNumber = :identityNumber")
    fun queryWithIdentityNumber(identityNumber : String) : User?

    @Query("select * from user where phone = :phone")
    fun queryWithPhone(phone : String) : User?

    @Update
    fun updateInfo(vararg user: User)

    @Delete
    fun deleteUser(vararg user: User)

    @Insert
    fun insertUser(vararg user: User)

}