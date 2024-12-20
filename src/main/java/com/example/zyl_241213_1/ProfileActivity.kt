package com.example.zyl_241213_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person


class ProfileActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val USERNAME_KEY = "username_key"
    private val PREFS_NAME = "TreeHole_Login_user_Prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 获取用户名
        val username = intent.getStringExtra("username") ?: sharedPreferences.getString(USERNAME_KEY, "默认用户名") ?: "默认用户"

        setContent {
            ProfileScreen(username)
        }
    }

    @Composable
    fun ProfileScreen(username: String) {
        val context = LocalContext.current
        val showLogoutDialog = remember { mutableStateOf(false) } // 控制弹窗的状态

        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Color(0xFFB2EAB3),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "个 人 中 心",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007A6D),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            bottomBar = {
                BottomNavigation(
                    backgroundColor = Color(0xFFE1F5E5)
                ) {
                    // 添加导航项
                    val items = listOf("首页", "树洞", "回声", "我的")
                    items.forEach { item ->
                        val selected = item == "我的"
                        BottomNavigationItem(
                            icon = {
                                when (item) {
                                    "首页" -> Icon(imageVector = Icons.Filled.Home, contentDescription = null)
                                    "树洞" -> CustomTreeIcon()
                                    "回声" -> EchoIconStyle()
                                    "我的" -> Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                                }
                            },
                            label = { Text(item) },
                            selected = selected, // 设置当前页面selected = true
                            onClick = {
                                when (item) {
                                    "首页" -> {
                                        val intent = Intent(context, TreeHoleActivity::class.java)
                                        intent.putExtra("username", username)
                                        context.startActivity(intent)
                                    }
                                    "树洞" -> {
                                        val intent = Intent(context, ShowRecordActivity::class.java)
                                        intent.putExtra("username", username)
                                        context.startActivity(intent)
                                    }
                                    "回声" -> {
                                        val intent = Intent(context, EchoActivity::class.java)
                                        intent.putExtra("username", username)
                                        context.startActivity(intent)
                                    }
                                    "我的" -> {
                                        val intent = Intent(context, ProfileActivity::class.java)
                                        intent.putExtra("username", username)
                                        context.startActivity(intent)
                                    }
                                }
                            },
                            selectedContentColor = Color(0xFF007A6D), // 选中时的内容颜色
                            unselectedContentColor = Color.Gray, // 未选中时的内容颜色
                            alwaysShowLabel = true,
                            modifier = Modifier.background(
                                if (selected) Color(0xFFB2EAB3) else Color(0xFFE1F5E5)
                            )
                        )
                    }
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .background(Color(0xFFE1F5E5)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "欢迎回来，$username!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007A6D)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "这里是你的个人中心",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 退出登录按钮
                    Button(
                        onClick = { showLogoutDialog.value = true },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4DB6AC))
                    ) {
                        Text(text = "退出登录", color = Color.White)
                    }
                }
            }
        )

        // 退出登录确认对话框
        if (showLogoutDialog.value) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog.value = false },
                title = {
                    Text(
                        text = "确认退出登录？",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007A6D),
                        fontSize = 20.sp
                    )
                },
                text = {
                    Text(
                        text = "你真的确定要退出登录吗？",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // 清除登录状态
                            with(sharedPreferences.edit()) {
                                remove(USERNAME_KEY)
                                apply() // 应用更改
                            }
                            // 跳转到 MainActivity
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                            finish() // 结束当前Activity
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4DB6AC)) // 主题色
                    ) {
                        Text("确认", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showLogoutDialog.value = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray) // 设置取消按钮颜色
                    ) {
                        Text("取消", color = Color.Black)
                    }
                },
                backgroundColor = Color.White, // 对话框背景颜色
                contentColor = Color.Black, // 对话框内容颜色
                shape = MaterialTheme.shapes.medium // 设置圆角
            )
        }

    }
}
