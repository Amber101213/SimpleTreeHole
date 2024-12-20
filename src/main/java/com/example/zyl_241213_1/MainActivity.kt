package com.example.zyl_241213_1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import android.content.SharedPreferences
import android.content.Context
class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Check login status
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // 自动登录并跳转到 TreeHoleActivity
            startActivity(Intent(this, TreeHoleActivity::class.java))
            finish() // 结束当前活动
        } else {
            setContent {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF90EE90)), // 浅绿色背景
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp) // 整体内边距
            ) {
                Text(
                    text = "TreeHole",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold // 加粗
                )

                Spacer(modifier = Modifier.height(30.dp)) // 增加间距

                Text(
                    text = "log in ...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold // 加粗
                )

                Spacer(modifier = Modifier.height(16.dp)) // 增加间距

                // 点击事件，跳转到登录或注册界面
                Button(onClick = {
                    startActivity(Intent(this@MainActivity, LoginRegisterActivity::class.java))
                }) {
                    Text(text = "登 录")
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainScreen() {
        MainScreen()
    }
}
