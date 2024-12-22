package com.example.zyl_241213_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Size

// "+" jump to ConfideActivity.kt
import androidx.compose.ui.platform.LocalContext
import android.content.Intent

// 当前用户名
import android.content.SharedPreferences
import android.content.Context

import android.os.Handler
import android.os.Looper
import android.widget.Toast

class TreeHoleActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val USERNAME_KEY = "username_key"
    private val PREFS_NAME = "TreeHole_Login_user_Prefs"
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // 提取用户名，提供默认值确保用户名非空
        val username = intent.getStringExtra("username") ?:
            sharedPreferences.getString(USERNAME_KEY, "默认用户名") ?: "默认用户"
        setContent {
            TreeHoleScreen(username)
        }
    }
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()  // 向上调用父类
            finishAffinity() // 关闭当前活动及其所有父活动，退出应用
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}



@Composable
fun TreeHoleScreen(username: String) {
    // 用于onClick跳转
    val context = LocalContext.current // 移动到这里
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color(0xFFB2EAB3),
                content = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "首   页",
                            color = Color(0xFF007A6D),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFFE1F5E5)
            ) {
                // 添加导航项
                val items = listOf("首页", "树洞", "回声", "我的")
                items.forEach { item ->
                    val selected = item == "首页"
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
                                    // 启动 TreeHoleActivity
                                    val intent = Intent(context, TreeHoleActivity::class.java)
                                    intent.putExtra("username", username)
                                    context.startActivity(intent)
                                }
                                "树洞" -> {
                                    // 启动 ShowRecordActivity
                                    val intent = Intent(context, ShowRecordActivity::class.java)
                                    intent.putExtra("username", username) // 将用户名传递到下一个Activity
                                    context.startActivity(intent)
                                }
                                "回声" -> {
                                    val intent = Intent(context, EchoActivity::class.java)
                                    intent.putExtra("username", username)
                                    context.startActivity(intent)
                                }
                                "我的" -> {
                                    // 启动 ProfileActivity
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
        floatingActionButton = {
            // 正确获取新活动的上下文
            // 不可卸载onClick内，onClick参数不接受可组合函数
            // val context = LocalContext.current
            FloatingActionButton(
                onClick = {
                    // context移动到TreeHoleScreen()下第一行
                    val intent = Intent(context, ConfideActivity::class.java)
                    intent.putExtra("username", username) // 将用户名传递到下一个Activity
                    context.startActivity(intent) // 在这里调用导航
                },
                backgroundColor = Color(0xFFB2EAB3)
            ) {
                Text("+", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(Color(0xFFE8F5E9)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "欢迎回来，$username!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "这里是心灵的", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "树洞", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4DB6AC))
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "你想对你的小树说些什么呢？", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}




@Composable
fun CustomTreeIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        // 绘制树的顶部
        drawPath(
            path = Path().apply {
                moveTo(size.width / 2, size.height * 0.1f) // 顶部尖端
                lineTo(size.width, size.height * 0.80f) // 右下角
                lineTo(0f, size.height * 0.80f) // 左下角
                close() // 关闭形状
            },
            color = Color(0xFF4DB6AC) // 树的颜色
        )
        // 绘制树干
        drawRect(
            color = Color(139, 69, 19), // 棕色树干
            topLeft = Offset(size.width / 2 - 5, size.height * 0.80f), // 树干位置
            size = Size(10f, size.height * 0.2f) // 树干大小
        )
    }
}
@Composable
fun EchoIconStyle() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val width = size.width
        val height = size.height

        // 绘制回声条纹
        val barWidth = width * 0.15f
        val gap = width * 0.1f

        drawRect(
            color = Color.Black,
            topLeft = Offset(0f, height * 0.2f),
            size = Size(barWidth, height * 0.6f)
        )
        drawRect(
            color = Color.Black,
            topLeft = Offset(gap + barWidth, height * 0.3f),
            size = Size(barWidth, height * 0.4f)
        )
        drawRect(
            color = Color.Black,
            topLeft = Offset((gap + barWidth) * 2, height * 0.4f),
            size = Size(barWidth, height * 0.2f)
        )
        drawRect(
            color = Color.Black,
            topLeft = Offset(gap * 2 + barWidth * 3, height * 0.3f),
            size = Size(barWidth, height * 0.4f)
        )
        drawRect(
            color = Color.Black,
            topLeft = Offset(gap * 3 + barWidth * 4, height * 0.2f),
            size = Size(barWidth, height * 0.6f)
        )
    }
}
