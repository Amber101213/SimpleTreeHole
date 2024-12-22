package com.example.zyl_241213_1

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

// show record detail
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.clickable

// 当前用户名
import android.content.SharedPreferences
import android.content.Context



class ShowRecordActivity : ComponentActivity(){
    private lateinit var sharedPreferences: SharedPreferences
    private val USERNAME_KEY = "username_key"
    private val PREFS_NAME = "TreeHole_Login_user_Prefs"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        backgroundColor = Color(0xFFB2EAB3),
                        content = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "树   洞",
                                    color = Color(0xFF007A6D),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                },
                bottomBar = { BottomBar() },
                backgroundColor = Color(0xFFE1F5E5) // 设置背景颜色
            ) { paddingValues ->
                MainContent(paddingValues)
            }
        }
    }
    override fun onBackPressed() { // 用户按下返回键时暂停音乐
        super.onBackPressed() // 原先的实现，实际上会退出当前 Activity
        // 修改为跳转到 TreeHoleActivity
        val intent = Intent(this, TreeHoleActivity::class.java)
        val username = intent.getStringExtra("username") ?:
        sharedPreferences.getString(USERNAME_KEY, "默认用户名") ?: "默认用户"
        intent.putExtra("username", username)
        startActivity(intent)
        finish() // 结束当前 Activity 以避免返回
    }

    @Composable
    fun BottomBar() {
        val context = LocalContext.current
        // 提取用户名，提供默认值确保用户名非空
        val username = intent.getStringExtra("username") ?:
            sharedPreferences.getString(USERNAME_KEY, "默认用户名") ?: "默认用户"
        BottomNavigation(
            backgroundColor = Color(0xFFE1F5E5)
        ) {
            val items = listOf("首页", "树洞", "回声", "我的")
            items.forEach { item ->
                val selected = item == "树洞"
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
                                // 启动 ShowRecordActivity
                                val intent = Intent(context, TreeHoleActivity::class.java)
                                intent.putExtra("username", username) // 将用户名传递到下一个Activity
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
    }

    @Composable
    fun MainContent(paddingValues: PaddingValues) {
        val context = LocalContext.current
        val username = intent.getStringExtra("username") ?:
            sharedPreferences.getString(USERNAME_KEY, "默认用户名") ?: "默认用户"
        // 读取记录返回的列表
        val recordList = readConfideData(username)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(recordList) { index, record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp) // 间距
                        .clickable {
                            val intent = Intent(context, RecordDetailActivity::class.java)
                            intent.putExtra("username", username) // 将用户名传递到下一个Activity
                            intent.putExtra("record", record)
                            context.startActivity(intent)
                        },
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = record.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = record.mood,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                        Text(
                            text = record.time,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

    private fun readConfideData(username: String): List<Record> {
        val dir = filesDir
        val files = dir.listFiles() ?: return listOf() // 返回空列表
        val recordList = mutableListOf<Record>()

        files.forEach { file ->
            try {
                val fileContent = file.readText()
                val record = JSONObject(fileContent)
                val owner = record.getString("owner")
                // 只添加当前用户的记录到返回的recordList
                if (owner == username) {
                    val title = record.getString("title")
                    val mood = record.getString("mood")
                    val time = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date(record.getLong("time")))
                    val content = record.getString("content")
                    recordList.add(Record(owner, title, mood, time, content)) // 添加记录
                }
            } catch (e: Exception) {
                // 处理异常
            }
        }
        return recordList
    }

    @Composable
    fun CustomTreeIcon() {
        Canvas(modifier = Modifier.size(24.dp)) {
            drawPath(
                path = Path().apply {
                    moveTo(size.width / 2, size.height * 0.1f)
                    lineTo(size.width, size.height * 0.80f)
                    lineTo(0f, size.height * 0.80f)
                    close()
                },
                color = Color(0xFF4DB6AC)
            )
            drawRect(
                color = Color(139, 69, 19),
                topLeft = Offset(size.width / 2 - 5, size.height * 0.80f),
                size = Size(10f, size.height * 0.2f)
            )
        }
    }

    @Composable
    fun EchoIconStyle() {
        Canvas(modifier = Modifier.size(24.dp)) {
            val width = size.width
            val height = size.height
            val barWidth = width * 0.15f
            val gap = width * 0.1f

            drawRect(color = Color.Black, topLeft = Offset(0f, height * 0.2f), size = Size(barWidth, height * 0.6f))
            drawRect(color = Color.Black, topLeft = Offset(gap + barWidth, height * 0.3f), size = Size(barWidth, height * 0.4f))
            drawRect(color = Color.Black, topLeft = Offset((gap + barWidth) * 2, height * 0.4f), size = Size(barWidth, height * 0.2f))
            drawRect(color = Color.Black, topLeft = Offset(gap * 2 + barWidth * 3, height * 0.3f), size = Size(barWidth, height * 0.4f))
            drawRect(color = Color.Black, topLeft = Offset(gap * 3 + barWidth * 4, height * 0.2f), size = Size(barWidth, height * 0.6f))
        }
    }
}