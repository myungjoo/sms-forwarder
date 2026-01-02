package com.example.smsforwarder.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smsforwarder.R
import com.example.smsforwarder.data.Rule
import com.example.smsforwarder.data.RuleRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var repository: RuleRepository
    private lateinit var adapter: RulesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = RuleRepository(this)

        setupRecyclerView()
        setupFab()
        checkPermissions()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RulesAdapter(
            repository.getRules(),
            onDeleteClick = { rule ->
                repository.removeRule(rule.id)
                refreshList()
            },
            onToggleClick = { rule, isEnabled ->
                rule.isEnabled = isEnabled
                repository.updateRule(rule)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun refreshList() {
        adapter.updateRules(repository.getRules())
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showAddRuleDialog()
        }
    }

    private fun showAddRuleDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_rule, null)
        val etIncoming = view.findViewById<EditText>(R.id.etIncoming)
        val etForward = view.findViewById<EditText>(R.id.etForward)

        AlertDialog.Builder(this)
            .setTitle("Add Forwarding Rule")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val incoming = etIncoming.text.toString()
                val forward = etForward.text.toString()
                if (incoming.isNotEmpty() && forward.isNotEmpty()) {
                    repository.addRule(Rule(incomingNumber = incoming, forwardToNumber = forward))
                    refreshList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS
        )

        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), 100)
        }

        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
            AlertDialog.Builder(this)
                .setTitle("Notification Access Required")
                .setMessage("To forward RCS/Chatting+ messages, please grant Notification Access to this app.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
