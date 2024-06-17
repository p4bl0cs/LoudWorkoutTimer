package com.plr.loudworkouttimer

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.gson.Gson

class TimerRepo(private val context: Context) {

    fun getTimerInfoList() : SnapshotStateList<TimerInfo> {
        val gson = Gson()
        val fileManager = FileManager(context)

        val timerInfoListJson = fileManager.getData("col", "[]")
        return gson.fromJson(timerInfoListJson, Array<TimerInfo>::class.java).toList().toMutableStateList()
    }

    fun addOrUpdateTimerInfo(timerInfo: TimerInfo) : TimerInfo? {
        try {
            if (!existNoDuplicates(timerInfo.name.trim())) {
                if (timerInfo.id == null || timerInfo.id!! <= 0) {
                    return null
                }
            }

            val result = TimerInfo(timerInfo.name, timerInfo.initialSeconds, timerInfo.initialSets, timerInfo.breakSeconds, null)

            if (timerInfo.id == null || timerInfo.id!! == 0) {
                result.id = getTimerInfoMaxID() + 1

                addTimerInfo(result)
            }
            else {
                result.id = timerInfo.id
                updateTimerInfo(result)
            }

            return result
        }
        catch (e: Exception) {
            return null
        }
    }

    fun deleteTimerInfo(id: Int) : Boolean {
        try {
            val timerInfoList = getTimerInfoList()
            val existingTimerInfo = timerInfoList.find { it.id == id }

            if (existingTimerInfo == null) {
                throw Exception("No record.")
            }

            timerInfoList.remove(existingTimerInfo)

            saveTimerList(timerInfoList)

            return true
        }
        catch (e: Exception) {
            return false
        }
    }

    fun existNoDuplicates(name: String) : Boolean {
        val timerInfoList = getTimerInfoList()

        if (!timerInfoList.any()) {
            return true
        }

        return timerInfoList.find { it.name.lowercase() == name.lowercase() } == null
    }

    private fun getTimerInfoMaxID() : Int {
        val timerInfoList = getTimerInfoList()

        if (timerInfoList.any()) {
            return timerInfoList.maxBy { it.id!! }.id!!.toInt()
        }

        return 1
    }

    private fun addTimerInfo(timerInfo: TimerInfo) : Boolean{
        val timerInfoList = getTimerInfoList()

        if (timerInfoList.any {it.name.lowercase() == timerInfo.name.lowercase()}) {
            throw Exception("Duplicated name.")
        }

        timerInfoList.add(timerInfo)
        saveTimerList(timerInfoList)

        return true
    }

    private fun updateTimerInfo(timerInfo: TimerInfo) : Boolean {
        val timerInfoList = getTimerInfoList()

        val existingTimerInfo = timerInfoList.find { it.id == timerInfo.id }

        if (existingTimerInfo == null) {
            throw Exception("No record.")
        }

        existingTimerInfo.name = timerInfo.name
        existingTimerInfo.initialSeconds = timerInfo.initialSeconds
        existingTimerInfo.initialSets = timerInfo.initialSets
        existingTimerInfo.breakSeconds = timerInfo.breakSeconds

        saveTimerList(timerInfoList)

        return true
    }

    private fun saveTimerList(timerInfoList: List<TimerInfo>) : Boolean {
        val gson = Gson()
        val json = gson.toJson(timerInfoList)

        val fileManager = FileManager(context)
        fileManager.saveData("col", json)

        return true
    }
}