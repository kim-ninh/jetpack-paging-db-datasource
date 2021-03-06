/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package paging.android.example.com.pagingsample

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Database Access Object for the Cheese database.
 */
@Dao
interface CheeseDao {
    /**
     * Room knows how to return a LivePagedListProvider, from which we can get a LiveData and serve
     * it back to UI via ViewModel.
     */
    @Query("SELECT * FROM Cheese ORDER BY name COLLATE NOCASE ASC")
    fun allCheesesByName(): DataSource.Factory<Int, Cheese>

    @Query("SELECT * FROM Cheese WHERE name > :lastName ORDER BY name COLLATE NOCASE ASC LIMIT :total")
    fun allCheesesByName(total: Int, lastName: String): List<Cheese>

    @Query("SELECT * FROM Cheese ORDER BY name COLLATE NOCASE ASC LIMIT :total")
    fun allCheesesByName(total: Int): List<Cheese>

    @Query("SELECT * FROM Cheese")
    fun allCheeses(): LiveData<List<Cheese>>

    @Query("SELECT * FROM Cheese WHERE id > :lastId ORDER BY id ASC LIMIT :total")
    fun allCheesesByIdAfter(total: Int, lastId: Int): List<Cheese>

    @Query("SELECT * FROM (SELECT * FROM Cheese WHERE id < :firstId ORDER BY id DESC LIMIT :total) ORDER BY id ASC")
    fun allCheesesByIdBefore(total: Int, firstId: Int): List<Cheese>

    @Query("SELECT * FROM Cheese ORDER BY id ASC LIMIT :total")
    fun allCheesesById(total: Int): List<Cheese>

    @Query("SELECT COUNT(*) FROM CHEESE WHERE id < :firstId")
    fun totalBeforeId(firstId: Int): Int

    @Query("SELECT COUNT(*) FROM cheese")
    fun countTotal(): Int

    @Query("SELECT * FROM Cheese ORDER BY name ASC LIMIT :size OFFSET :offset")
    fun allCheesesByName(offset: Int, size: Int): List<Cheese>

    @Insert
    fun insert(cheeses: List<Cheese>)

    @Insert
    fun insert(cheese: Cheese)

    @Delete
    fun delete(cheese: Cheese)
}