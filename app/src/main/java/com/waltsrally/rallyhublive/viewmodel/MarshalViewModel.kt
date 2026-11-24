package com.waltsrally.rallyhublive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waltsrally.rallyhublive.data.api.RetrofitClient
import com.waltsrally.rallyhublive.data.models.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MarshalViewModel : ViewModel() {

    private val _sections = MutableStateFlow<List<RhlSection>>(emptyList())
    val sections: StateFlow<List<RhlSection>> = _sections

    private val _selectedSection = MutableStateFlow<RhlSection?>(null)
    val selectedSection: StateFlow<RhlSection?> = _selectedSection

    private val _cars = MutableStateFlow<List<CarEntry>>(emptyList())
    val cars: StateFlow<List<CarEntry>> = _cars

    // Cars that have started (startTime != null) — drives FinishMarshalScreen
    val startedCars: StateFlow<List<CarEntry>> = _cars
        .map { list -> list.filter { it.startTime != null } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Countdown for the finish-screen auto-poll (counts 10 → 0)
    private val _pollCountdown = MutableStateFlow(10)
    val pollCountdown: StateFlow<Int> = _pollCountdown

    private var pollJob: Job? = null

    private val _results = MutableStateFlow<List<StageResult>>(emptyList())
    val results: StateFlow<List<StageResult>> = _results

    private val _board = MutableStateFlow<List<BoardEntry>>(emptyList())
    val board: StateFlow<List<BoardEntry>> = _board

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _flashedCar = MutableStateFlow<String?>(null)
    val flashedCar: StateFlow<String?> = _flashedCar

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    fun init() {
        loadSections()
    }

    fun loadSections() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val resp = RetrofitClient.api.getSections()
                if (resp.ok == true) {
                    _sections.value = resp.sections ?: emptyList()
                    if (_selectedSection.value == null) {
                        _selectedSection.value = resp.sections.firstOrNull()
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectSection(section: RhlSection) {
        _selectedSection.value = section
        loadAllCars()
    }

    fun loadAllCars() {
        val sid = _selectedSection.value?.id ?: return
        viewModelScope.launch {
            _loading.value = true
            try {
                val resp = RetrofitClient.api.getAllCars(sectionId = sid)
                if (resp.ok == true) _cars.value = resp.cars ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun recordActualStart(carNumber: String) {
        val sid = _selectedSection.value?.id ?: return
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.recordStart(
                    body  = RecordStartRequest(sectionId = sid, carNumber = carNumber)
                )
                if (resp.ok == true) {
                    // Flash the cell, then refresh
                    _flashedCar.value = carNumber
                    delay(600)
                    _flashedCar.value = null
                    loadAllCars()
                } else {
                    _error.value = resp.error ?: "Failed"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun setAdminStartTime(carNumber: String, timeString: String) {
        val sid = _selectedSection.value?.id ?: return
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.setAdminStartTime(
                    body  = SetAdminStartRequest(sectionId = sid, carNumber = carNumber, startTime = timeString)
                )
                if (resp.ok == true) {
                    _toastMessage.value = "✅ Car #$carNumber set to ${resp.startTime?.substring(11, 19) ?: timeString}"
                    loadAllCars()
                } else {
                    _error.value = resp.error ?: "Failed"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // ── Auto-poll for finish screen ────────────────────────────────────────────

    fun startFinishPolling() {
        if (pollJob?.isActive == true) return
        pollJob = viewModelScope.launch {
            while (true) {
                for (i in 10 downTo 1) {
                    _pollCountdown.value = i
                    delay(1000)
                }
                _pollCountdown.value = 0
                loadAllCars()
                delay(100) // brief pause before resetting
                _pollCountdown.value = 10
            }
        }
    }

    fun stopFinishPolling() {
        pollJob?.cancel()
        pollJob = null
        _pollCountdown.value = 10
    }

    fun recordFinishTime(carNumber: String) {
        val sid = _selectedSection.value?.id ?: return
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.recordTime(
                    body  = RecordTimeRequest(sectionId = sid, carNumber = carNumber)
                )
                if (resp.ok == true) {
                    _flashedCar.value = carNumber
                    delay(600)
                    _flashedCar.value = null
                    loadAllCars()
                } else {
                    _error.value = resp.error ?: "Failed"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun recordDNF(carNumber: String) {
        val sid = _selectedSection.value?.id ?: return
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.recordDnf(
                    body  = RecordDNFRequest(sectionId = sid, carNumber = carNumber)
                )
                if (resp.ok == true) {
                    _flashedCar.value = carNumber
                    delay(600)
                    _flashedCar.value = null
                    loadAllCars()
                } else {
                    _error.value = resp.error ?: "DNF failed"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadResults() {
        val sid = _selectedSection.value?.id ?: return
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.getResults(sectionId = sid)
                if (resp.ok == true) _results.value = resp.results.orEmpty()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadBoard() {
        val sid = _selectedSection.value?.id ?: return
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.getBoard(sectionId = sid)
                if (resp.ok == true) _board.value = resp.board.orEmpty()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearToast() { _toastMessage.value = null }
    fun clearError() { _error.value = null }
}
