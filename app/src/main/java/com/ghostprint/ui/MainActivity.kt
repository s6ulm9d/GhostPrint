package com.ghostprint.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.net.NetworkInterface
import java.net.InetAddress

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val logs = remember { mutableStateListOf<String>() }
                    var textState by remember { mutableStateOf(TextFieldValue("")) }
                    var lastKeyUpTime by remember { mutableStateOf(0L) }
                    var keyDownTime by remember { mutableStateOf(0L) }

                    val listState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()
                    val focusRequester = remember { FocusRequester() }

                    // Auto-scroll when logs change
                    LaunchedEffect(logs.size) {
                        if (logs.isNotEmpty()) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(logs.size - 1)
                            }
                        }
                    }

                    // Request focus so keyboard pops up
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Foreground TextField for demo typing
                            OutlinedTextField(
                                value = textState,
                                onValueChange = { newValue ->
                                    if (newValue.text.length > textState.text.length) {
                                        val newChar = newValue.text.last()
                                        val ascii = newChar.code
                                        val now = System.currentTimeMillis()
                                        val log = "Char: '$newChar' | ASCII: $ascii | Time: $now"
                                        logs.add(log)
                                        Log.d("CharLogger", log)
                                    }
                                    textState = newValue
                                },
                                label = { Text("Type here", color = Color.White) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .onKeyEvent { event: KeyEvent ->
                                        val now = System.currentTimeMillis()
                                        when (event.type) {
                                            KeyEventType.KeyDown -> {
                                                keyDownTime = now
                                            }
                                            KeyEventType.KeyUp -> {
                                                val holdDuration = now - keyDownTime
                                                val deltaBetweenKeys =
                                                    if (lastKeyUpTime != 0L) now - lastKeyUpTime else 0L
                                                lastKeyUpTime = now

                                                val keyCode = event.nativeKeyEvent.keyCode
                                                val log =
                                                    "HW KeyCode: $keyCode | Hold=${holdDuration}ms | ΔPrev=${deltaBetweenKeys}ms"
                                                logs.add(log)
                                                Log.d("KeyLogger", log)
                                            }
                                            else -> {}
                                        }
                                        false
                                    },
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.Gray,
                                    cursorColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Display local IP addresses
                            val ips = remember { mutableStateListOf<String>() }
                            LaunchedEffect(Unit) {
                                ips.clear()
                                ips.addAll(getLocalIpAddresses())
                            }
                            Text("IPs: ${ips.joinToString()}", color = Color.White)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Log list
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .background(Color.DarkGray)
                                    .padding(8.dp)
                            ) {
                                items(logs) { log ->
                                    Text(
                                        log,
                                        color = Color.Red,
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getLocalIpAddresses(): List<String> {
        return try {
            val list = mutableListOf<String>()
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf: NetworkInterface = interfaces.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr: InetAddress = addrs.nextElement()
                    val host = addr.hostAddress ?: continue
                    if (!addr.isLoopbackAddress && !host.contains(":")) {
                        list.add(host)
                    }
                }
            }
            list
        } catch (e: Exception) {
            listOf("unknown")
        }
    }
}