package com.thryan.secondclass.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thryan.secondclass.AppDataStore
import com.thryan.secondclass.SCRepository
import com.thryan.secondclass.core.data.ActivityClass
import com.thryan.secondclass.core.data.ScoreDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val scRepository: SCRepository,
    private val appDataStore: AppDataStore
) : ViewModel() {
    private val _userState = MutableStateFlow(
        UserState(
            user = com.thryan.secondclass.core.data.User("", "", 1),
            loading = true,
            radarScore = emptyList(),
            scoreInfo = com.thryan.secondclass.core.data.ScoreInfo(0.0, 0, 0, 0),
            dynamic = appDataStore.getDynamic(false)
        )
    )
    val userState: StateFlow<UserState> = _userState.asStateFlow()


    private suspend fun update(block: suspend UserState.() -> UserState) {
        val newState: UserState
        userState.value.apply { newState = block() }
        _userState.emit(newState)
    }

    init {
        viewModelScope.launch {
            val user = scRepository.getUser()
            if (scRepository.radarScores != null) {
                update {
                    copy(
                        user = user,
                        scoreInfo = scRepository.getScoreInfo(),
                        radarScore = scRepository.radarScores!!,
                        loading = false
                    )
                }
            } else {
                val activityClass = async { scRepository.getActivityClass().rows }
                val scoreInfo = async { scRepository.getScoreInfo() }
                val scoreDetails = async { scRepository.getScoreDetails().activity }
                val radarScores = buildRadarScore(activityClass.await(), scoreDetails.await())
                scRepository.radarScores = radarScores
                update {
                    copy(
                        user = user,
                        scoreInfo = scoreInfo.await(),
                        radarScore = radarScores,
                        loading = false
                    )
                }
            }
        }
    }

    fun send(intent: UserIntent) = viewModelScope.launch { onHandle(intent) }

    private suspend fun onHandle(intent: UserIntent) {
        when (intent) {
            is UserIntent.ChangeDynamic -> {
                update { copy(dynamic = intent.checked) }
                appDataStore.putDynamic(intent.checked)
            }

            UserIntent.Dialog -> {

            }
        }
    }

    fun buildRadarScore(
        activityClass: List<ActivityClass>,
        scoreDetails: List<ScoreDetail>
    ): List<RadarScore> {
        val radarScore = mutableListOf<RadarScore>()
        scoreDetails.forEach { scoreDetail ->
            val minScore = activityClass.filter { it.classifyName == scoreDetail.name }
            if (minScore.isNotEmpty()) radarScore.add(
                RadarScore(
                    scoreDetail.name,
                    scoreDetail.value,
                    minScore.first().minIntegralSchool
                )
            )
        }
        return radarScore
    }
}