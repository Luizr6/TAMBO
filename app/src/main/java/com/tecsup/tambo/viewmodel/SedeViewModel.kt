package com.tecsup.tambo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.tecsup.tambo.data.AppDatabase
import com.tecsup.tambo.data.SedeEntity
import com.tecsup.tambo.data.SedeRepository
import kotlinx.coroutines.launch

class SedeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SedeRepository(AppDatabase.getInstance(application).sedeDao())

    val sedes: LiveData<List<SedeEntity>> = repository.sedes

    fun insert(sede: SedeEntity) = viewModelScope.launch { repository.insert(sede) }

    fun update(sede: SedeEntity) = viewModelScope.launch { repository.update(sede) }

    fun delete(sede: SedeEntity) = viewModelScope.launch { repository.delete(sede) }
}
