package com.example.smsforwarder.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RuleRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sms_forwarder_rules", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val rulesKey = "rules_list"

    fun getRules(): List<Rule> {
        val json = prefs.getString(rulesKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Rule>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addRule(rule: Rule) {
        val currentRules = getRules().toMutableList()
        currentRules.add(rule)
        saveRules(currentRules)
    }

    fun removeRule(ruleId: String) {
        val currentRules = getRules().toMutableList()
        currentRules.removeAll { it.id == ruleId }
        saveRules(currentRules)
    }
    
    fun updateRule(rule: Rule) {
        val currentRules = getRules().toMutableList()
        val index = currentRules.indexOfFirst { it.id == rule.id }
        if (index != -1) {
            currentRules[index] = rule
            saveRules(currentRules)
        }
    }

    private fun saveRules(rules: List<Rule>) {
        val json = gson.toJson(rules)
        prefs.edit().putString(rulesKey, json).apply()
    }
}
