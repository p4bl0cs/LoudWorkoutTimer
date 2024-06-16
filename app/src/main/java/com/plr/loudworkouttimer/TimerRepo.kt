package com.plr.loudworkouttimer

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.gson.Gson

class TimerRepo(private val context: Context) {

    fun GetTimerInfoList() : SnapshotStateList<TimerInfo> {
        val gson = Gson()
        val fileManager = FileManager(context)

        val timerInfoListJson = fileManager.getData("col", "[]")
        return gson.fromJson(timerInfoListJson, Array<TimerInfo>::class.java).toList().toMutableStateList()
    }

    fun AddOrUpdateTimerInfo(timerInfo: TimerInfo) : TimerInfo? {
        try {
            if (!ExistNoDuplicates(timerInfo.name.trim())) {
                if (timerInfo.id == null || timerInfo.id!! <= 0) {
                    return null
                }
            }

            val result = TimerInfo(timerInfo.name, timerInfo.initialSeconds, timerInfo.initialSets, timerInfo.breakSeconds, null)

            if (timerInfo.id == null || timerInfo.id!! == 0) {
                result.id = GetTimerInfoMaxID() + 1

                AddTimerInfo(result)
            }
            else {
                result.id = timerInfo.id
                UpdateTimerInfo(result)
            }

            return result
        }
        catch (e: Exception) {
            return null
        }
    }

    fun DeleteTimerInfo(id: Int) : Boolean {
        try {
            val timerInfoList = GetTimerInfoList()
            val existingTimerInfo = timerInfoList.find { it.id == id }

            if (existingTimerInfo == null) {
                throw Exception("No record.")
            }

            timerInfoList.remove(existingTimerInfo)

            SaveTimerList(timerInfoList)

            return true
        }
        catch (e: Exception) {
            return false
        }
    }

    fun ExistNoDuplicates(name: String) : Boolean {
        val timerInfoList = GetTimerInfoList()

        if (!timerInfoList.any()) {
            return true
        }

        return timerInfoList.find { it.name.lowercase() == name.lowercase() } == null
    }

    private fun GetTimerInfoMaxID() : Int {
        val timerInfoList = GetTimerInfoList()

        if (timerInfoList.any()) {
            return timerInfoList.maxBy { it.id!! }.id!!.toInt()
        }

        return 1
    }

    private fun AddTimerInfo(timerInfo: TimerInfo) : Boolean{
        val timerInfoList = GetTimerInfoList()

        if (timerInfoList.any {it.name.lowercase() == timerInfo.name.lowercase()}) {
            throw Exception("Duplicated name.")
        }

        timerInfoList.add(timerInfo)
        SaveTimerList(timerInfoList)

        return true
    }

    private fun UpdateTimerInfo(timerInfo: TimerInfo) : Boolean {
        val timerInfoList = GetTimerInfoList()

        val existingTimerInfo = timerInfoList.find { it.id == timerInfo.id }

        if (existingTimerInfo == null) {
            throw Exception("No record.")
        }

        existingTimerInfo.name = timerInfo.name
        existingTimerInfo.initialSeconds = timerInfo.initialSeconds
        existingTimerInfo.initialSets = timerInfo.initialSets
        existingTimerInfo.breakSeconds = timerInfo.breakSeconds

        SaveTimerList(timerInfoList)

        return true
    }

    private fun SaveTimerList(timerInfoList: List<TimerInfo>) : Boolean {
        val gson = Gson()
        val json = gson.toJson(timerInfoList)

        val fileManager = FileManager(context)
        fileManager.saveData("col", json)

        return true
    }
}