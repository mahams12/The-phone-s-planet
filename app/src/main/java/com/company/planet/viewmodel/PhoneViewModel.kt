package com.company.planet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.planet.data.Phone
import com.company.planet.data.PhoneRepository
import com.company.planet.data.PhoneTotals
import com.company.planet.data.computeAll
import com.company.planet.data.computePhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen { DASHBOARD, ADD, INVENTORY }

enum class StatusFilter { ALL, STOCK, SOLD }

data class PhoneUiState(
    val phones: List<Phone> = emptyList(),
    val filteredPhones: List<Phone> = emptyList(),
    val totals: PhoneTotals = PhoneTotals(),
    val companies: List<String> = emptyList(),
    val search: String = "",
    val companyFilter: String = "all",
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val isLoading: Boolean = true,
    val currentScreen: AppScreen = AppScreen.DASHBOARD,
    val editingPhone: Phone? = null,
    val previewPhone: Phone? = null,
    val highlightId: String? = null,
    val toastMessage: String? = null,
    val errorMessage: String? = null
)

class PhoneViewModel(
    private val repository: PhoneRepository = PhoneRepository()
) : ViewModel() {

    private val phones = MutableStateFlow<List<Phone>>(emptyList())
    private val search = MutableStateFlow("")
    private val companyFilter = MutableStateFlow("all")
    private val statusFilter = MutableStateFlow(StatusFilter.ALL)
    private val isLoading = MutableStateFlow(true)
    private val currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    private val editingPhone = MutableStateFlow<Phone?>(null)
    private val previewPhone = MutableStateFlow<Phone?>(null)
    private val highlightId = MutableStateFlow<String?>(null)
    private val toastMessage = MutableStateFlow<String?>(null)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PhoneUiState> = combine(
        phones,
        combine(search, companyFilter, statusFilter) { s, c, st -> Triple(s, c, st) },
        combine(isLoading, currentScreen) { loading, screen -> loading to screen },
        combine(editingPhone, previewPhone, highlightId) { editing, preview, highlight ->
            Triple(editing, preview, highlight)
        },
        combine(toastMessage, errorMessage) { toast, error -> toast to error }
    ) { phoneList, filters, loadingScreen, modals, messages ->
        val (searchQuery, company, status) = filters
        val (loading, screen) = loadingScreen
        val (editing, preview, highlight) = modals
        val (toast, error) = messages

        val filtered = filterPhones(phoneList, searchQuery, company, status)
        val companies = phoneList.map { it.company }.filter { it.isNotBlank() }.distinct().sorted()

        PhoneUiState(
            phones = phoneList,
            filteredPhones = filtered,
            totals = computeAll(phoneList),
            companies = companies,
            search = searchQuery,
            companyFilter = company,
            statusFilter = status,
            isLoading = loading,
            currentScreen = screen,
            editingPhone = editing,
            previewPhone = preview,
            highlightId = highlight,
            toastMessage = toast,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PhoneUiState()
    )

    init {
        viewModelScope.launch {
            repository.observePhones().collect { list ->
                phones.value = list
                isLoading.value = false
            }
        }
    }

    fun navigateTo(screen: AppScreen, keepForm: Boolean = false) {
        if (screen == AppScreen.ADD && !keepForm) {
            editingPhone.value = null
        }
        currentScreen.value = screen
    }

    fun setSearch(query: String) {
        search.value = query
    }

    fun setCompanyFilter(company: String) {
        companyFilter.value = company
    }

    fun setStatusFilter(filter: StatusFilter) {
        statusFilter.value = filter
    }

    fun openAddScreen() {
        editingPhone.value = null
        currentScreen.value = AppScreen.ADD
    }

    fun openEditForm(phone: Phone) {
        editingPhone.value = phone
        previewPhone.value = null
        currentScreen.value = AppScreen.ADD
    }

    fun resetForm() {
        editingPhone.value = null
    }

    fun openPreview(phone: Phone) {
        previewPhone.value = phone
    }

    fun closePreview() {
        previewPhone.value = null
    }

    fun clearHighlight() {
        highlightId.value = null
    }

    fun clearToast() {
        toastMessage.value = null
    }

    fun clearError() {
        errorMessage.value = null
    }

    fun savePhone(phone: Phone) {
        viewModelScope.launch {
            try {
                val isEdit = phone.id.isNotBlank() && phones.value.any { it.id == phone.id }
                val id = phone.id.ifBlank { UUID.randomUUID().toString() }
                val existing = phones.value.find { it.id == id }
                val toSave = phone.copy(
                    id = id,
                    createdAt = existing?.createdAt ?: System.currentTimeMillis()
                )
                repository.savePhone(toSave)
                editingPhone.value = null
                highlightId.value = id
                toastMessage.value = if (isEdit) "Phone updated" else "Phone added to inventory"
                currentScreen.value = AppScreen.INVENTORY
            } catch (e: Exception) {
                errorMessage.value = "Failed to save: ${e.localizedMessage}"
            }
        }
    }

    fun deletePhone(phone: Phone) {
        viewModelScope.launch {
            try {
                repository.deletePhone(phone.id)
                closePreview()
                toastMessage.value = "Phone deleted"
            } catch (e: Exception) {
                errorMessage.value = "Failed to delete: ${e.localizedMessage}"
            }
        }
    }

    private fun filterPhones(
        list: List<Phone>,
        searchQuery: String,
        company: String,
        status: StatusFilter
    ): List<Phone> {
        val s = searchQuery.trim().lowercase()
        return list.filter { phone ->
            if (company != "all" && phone.company != company) return@filter false
            val c = computePhone(phone)
            when (status) {
                StatusFilter.SOLD -> if (!c.sold) return@filter false
                StatusFilter.STOCK -> if (c.sold) return@filter false
                StatusFilter.ALL -> Unit
            }
            if (s.isNotEmpty()) {
                val hay = listOf(
                    phone.company,
                    phone.model,
                    phone.detail,
                    phone.imei1,
                    phone.imei2,
                    phone.colour,
                    phone.storage
                ).joinToString(" ").lowercase()
                if (!hay.contains(s)) return@filter false
            }
            true
        }
    }
}
