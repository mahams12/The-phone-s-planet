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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

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
    val showForm: Boolean = false,
    val editingPhone: Phone? = null,
    val previewPhone: Phone? = null,
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
    private val showForm = MutableStateFlow(false)
    private val editingPhone = MutableStateFlow<Phone?>(null)
    private val previewPhone = MutableStateFlow<Phone?>(null)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PhoneUiState> = combine(
        phones,
        combine(search, companyFilter, statusFilter) { s, c, st -> Triple(s, c, st) },
        combine(isLoading, showForm) { loading, form -> loading to form },
        combine(editingPhone, previewPhone, errorMessage) { editing, preview, error ->
            Triple(editing, preview, error)
        }
    ) { phoneList, filters, loadingForm, modals ->
        val (searchQuery, company, status) = filters
        val (loading, formVisible) = loadingForm
        val (editing, preview, error) = modals

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
            showForm = formVisible,
            editingPhone = editing,
            previewPhone = preview,
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

    fun setSearch(query: String) {
        search.value = query
    }

    fun setCompanyFilter(company: String) {
        companyFilter.value = company
    }

    fun setStatusFilter(filter: StatusFilter) {
        statusFilter.value = filter
    }

    fun openAddForm() {
        editingPhone.value = null
        showForm.value = true
        previewPhone.value = null
    }

    fun openEditForm(phone: Phone) {
        editingPhone.value = phone
        showForm.value = true
        previewPhone.value = null
    }

    fun closeForm() {
        showForm.value = false
        editingPhone.value = null
    }

    fun openPreview(phone: Phone) {
        previewPhone.value = phone
    }

    fun closePreview() {
        previewPhone.value = null
    }

    fun clearError() {
        errorMessage.value = null
    }

    fun savePhone(phone: Phone) {
        viewModelScope.launch {
            try {
                val id = phone.id.ifBlank { UUID.randomUUID().toString() }
                val existing = phones.value.find { it.id == id }
                val toSave = phone.copy(
                    id = id,
                    createdAt = existing?.createdAt ?: System.currentTimeMillis()
                )
                repository.savePhone(toSave)
                closeForm()
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
