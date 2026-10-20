package com.example.data.repository

import com.example.data.local.ParchiDao
import com.example.data.local.ParchiEntity
import kotlinx.coroutines.flow.Flow

class ParchiRepository(private val parchiDao: ParchiDao) {
  val allParchis: Flow<List<ParchiEntity>> = parchiDao.getAllParchis()

  suspend fun insertParchi(parchi: ParchiEntity): Long {
    return parchiDao.insertParchi(parchi)
  }

  suspend fun getParchiById(id: Long): ParchiEntity? {
    return parchiDao.getParchiById(id)
  }

  suspend fun updateParchi(parchi: ParchiEntity) {
    parchiDao.updateParchi(parchi)
  }

  suspend fun deleteParchi(parchi: ParchiEntity) {
    parchiDao.deleteParchi(parchi)
  }

  suspend fun deleteById(id: Long) {
    parchiDao.deleteById(id)
  }
}
