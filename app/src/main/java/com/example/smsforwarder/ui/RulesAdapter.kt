package com.example.smsforwarder.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smsforwarder.R
import com.example.smsforwarder.data.Rule

class RulesAdapter(
    private var rules: List<Rule>,
    private val onDeleteClick: (Rule) -> Unit,
    private val onToggleClick: (Rule, Boolean) -> Unit
) : RecyclerView.Adapter<RulesAdapter.RuleViewHolder>() {

    class RuleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIncoming: TextView = view.findViewById(R.id.tvIncoming)
        val tvForward: TextView = view.findViewById(R.id.tvForward)
        val switchEnabled: Switch = view.findViewById(R.id.switchEnabled)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rule, parent, false)
        return RuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        val rule = rules[position]
        holder.tvIncoming.text = "From: ${rule.incomingNumber}"
        holder.tvForward.text = "To: ${rule.forwardToNumber}"
        holder.switchEnabled.isChecked = rule.isEnabled

        holder.btnDelete.setOnClickListener { onDeleteClick(rule) }
        holder.switchEnabled.setOnCheckedChangeListener { _, isChecked ->
            onToggleClick(rule, isChecked)
        }
    }

    override fun getItemCount() = rules.size

    fun updateRules(newRules: List<Rule>) {
        rules = newRules
        notifyDataSetChanged()
    }
}
