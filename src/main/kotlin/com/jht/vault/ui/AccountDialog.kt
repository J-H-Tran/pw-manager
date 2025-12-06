package com.jht.vault.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog

@Composable
fun AddAccountDialog(
    onAdd: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var accountName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column {
                TextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Account Name") }
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") }
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") }
                )
                Button(onClick = { password = generatePassword() }) {
                    Text("Generate Password")
                }
                error?.let { Text(it, color = MaterialTheme.colors.error) }
                Row {
                    Button(onClick = {
                        if (accountName.isBlank() || username.isBlank() || password.isBlank()) {
                            error = "All fields are required."
                        } else {
                            onAdd(accountName, username, password)
                            onDismiss()
                        }
                    }) { Text("Add") }
                    Button(onClick = onDismiss) { Text("Cancel") }
                }
            }
        }
    }
}

fun generatePassword(length: Int = 16): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}