package com.sarang.torang.di.restaurant_info

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SimpleAlertDialog(onYes: () -> Unit = {}, onNo: () -> Unit = {}, text: String = "") {
    AlertDialog(
        onDismissRequest = { onNo },
        dismissButton = { TextButton(onNo) { Text("No") } },
        confirmButton = { TextButton(onYes) { Text("Yes") } },
        text = {
            Text(text)
        })
}