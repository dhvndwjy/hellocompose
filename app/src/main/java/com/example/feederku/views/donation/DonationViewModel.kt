package com.example.feederku.views.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.feederku.views.donation.data.DonationRepository
import com.example.feederku.views.donation.data.FirebaseDonationRepository
import com.example.feederku.views.donation.model.DonationCampaign
import com.example.feederku.views.donation.model.Donor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DonationUiState(
    val campaigns: List<DonationCampaign> = emptyList(),
    val filteredCampaigns: List<DonationCampaign> = emptyList(),
    val selectedCampaign: DonationCampaign? = null,
    val recentDonors: List<Donor> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DonationViewModel(
    private val repository: DonationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DonationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeCampaigns()
    }

    private fun observeCampaigns() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getCampaigns()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { campaigns ->
                    _uiState.update { state ->
                        state.copy(
                            campaigns = campaigns,
                            filteredCampaigns = applyFilters(campaigns, state.searchQuery, state.selectedCategory),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredCampaigns = applyFilters(state.campaigns, query, state.selectedCategory)
            )
        }
    }

    fun onCategorySelect(category: String) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredCampaigns = applyFilters(state.campaigns, state.searchQuery, category)
            )
        }
    }

    private fun applyFilters(campaigns: List<DonationCampaign>, query: String, category: String): List<DonationCampaign> {
        return campaigns.filter {
            (category == "All" || it.category == category) &&
            (it.title.contains(query, ignoreCase = true) || it.organizationName.contains(query, ignoreCase = true))
        }
    }

    fun selectCampaign(campaignId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val campaign = repository.getCampaignById(campaignId)
            _uiState.update { it.copy(selectedCampaign = campaign, isLoading = false) }
            
            if (campaign != null) {
                repository.getRecentDonors(campaignId).collect { donors ->
                    _uiState.update { it.copy(recentDonors = donors) }
                }
            }
        }
    }

    fun submitDonation(campaignId: String, amount: Long, donorName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.submitDonation(campaignId, amount, donorName)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.update { it.copy(errorMessage = "Failed to submit donation") }
            }
        }
    }

    fun registerCampaign(campaign: DonationCampaign, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.registerCampaign(campaign)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.update { it.copy(errorMessage = "Failed to register campaign") }
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                DonationViewModel(FirebaseDonationRepository())
            }
        }
    }
}
