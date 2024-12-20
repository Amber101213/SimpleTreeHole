package com.example.zyl_241213_1


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
// 内容窗口滑动
import androidx.compose.foundation.verticalScroll // 确保使用 verticalScroll
import androidx.compose.foundation.rememberScrollState
// 内容字体大小
import androidx.compose.ui.text.TextStyle
// 当前用户名
import android.content.SharedPreferences
import android.content.Context

class ConfideActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val USERNAME_KEY = "username_key"
    private val PREFS_NAME = "TreeHole_Login_user_Prefs"
    private var title by mutableStateOf(TextFieldValue())
    private var content by mutableStateOf(TextFieldValue())
    private var selectedMood by mutableStateOf("心情选择") // 心情选择栏默认显示
    private var dropdownExpanded by mutableStateOf(false)
    private var showSuccessDialog by mutableStateOf(false) // 控制弹窗显示
    // 设置内容输入最大字符数
    private val MAX_CONTENT_LENGTH = 1000 // 最大字符数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setContent {
            ConfideScreen()
        }
    }

    @Composable
    fun ConfideScreen() {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Color(0xFFB2EAB3),
                    content = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "倾   诉",
                                color = Color(0xFF007A6D),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        ) { paddingValues -> // 使用 paddingValues
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // 处理内边距
                    .padding(16.dp)
                    .background(Color(0xFFE8F5E9)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // 定义标题超出字数的提示变量
                var showTitleError by remember { mutableStateOf(false) }

                // 输入标题
                OutlinedTextField(
                    value = title,
                    onValueChange = { newText ->
                        if (newText.text.length <= 35) { // 限制为35字以内
                            title = newText
                            showTitleError = false // 清除错误提示
                        } else {
                            showTitleError = true // 超过字数，设置错误提示为 true
                        }
                    },
                    label = { Text("请输入标题") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 18.sp), // 设置字体大小为 18sp
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color(0xFF3F51B5),
                        unfocusedLabelColor = Color.Gray
                    )
                )

                // 标题字数统计
                Text(
                    text = "${title.text.length}/35",
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                    modifier = Modifier.align(Alignment.End) // 右对齐
                )

                // 错误提示
                if (showTitleError) {
                    Text(
                        text = "标题不能超过35个字！",
                        style = TextStyle(fontSize = 12.sp, color = Color.Red),
                        modifier = Modifier.align(Alignment.End) // 右对齐
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // 添加滚动功能
                        .padding(16.dp)
                ) {
                    // 心情选择器
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // 确保 Row 宽度与页面同宽
                            .padding(bottom = 8.dp) // Row 下方添加间距
                            .clickable { dropdownExpanded = true } // 使 Row 可点击
                            .background(Color.White) // 设置背景颜色
                            .padding(16.dp), // 内边距
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                    ) {
                        Text(selectedMood, fontSize = 18.sp) // 显示当前选中的心情
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).padding(start = 4.dp) // Icon 左侧添加间距
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        val moods = arrayOf("忧郁", "伤心", "焦虑", "愤怒", "开心", "活力", "幸福", "平和")
                        moods.forEach { mood ->
                            DropdownMenuItem(onClick = {
                                selectedMood = mood
                                dropdownExpanded = false
                            }) {
                                Text(mood)
                            }
                        }
                    }

                    // 内容输入框
                    var showContentError by remember { mutableStateOf(false) } // 控制内容超出字数的提示

                    OutlinedTextField(
                        value = content,
                        onValueChange = { newText ->
                            if (newText.text.length <= MAX_CONTENT_LENGTH) {
                                content = newText
                                showContentError = false // 清除错误提示
                            } else {
                                showContentError = true // 超出字数，设置错误提示为 true
                                Toast.makeText(this@ConfideActivity, "内容不得超过${MAX_CONTENT_LENGTH}个字符", Toast.LENGTH_SHORT).show()
                            }
                        },
                        label = { Text("请输入内容") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedLabelColor = Color(0xFF3F51B5),
                            unfocusedLabelColor = Color.Gray
                        ),
                        maxLines = 20,
                        textStyle = TextStyle(fontSize = 18.sp) // 设置字体大小为 18sp
                    )
                    // 内容字数统计
                    Text(
                        text = "${content.text.length}/$MAX_CONTENT_LENGTH",
                        style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                        modifier = Modifier.align(Alignment.End) // 右对齐
                    )
                    // 超出字数提示
                    if (showContentError) {
                        Text(
                            text = "内容不能超过${MAX_CONTENT_LENGTH}个字！",
                            style = TextStyle(fontSize = 12.sp, color = Color.Red),
                            modifier = Modifier.align(Alignment.End) // 右对齐
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // 按钮行
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp), // 增加底部内边距以使按钮更大
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 取消按钮
                        Button(
                            onClick = { finish() },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp), // 设置圆角弧度
                            modifier = Modifier.size(100.dp, 45.dp) // 设置按钮大小
                        ) {
                            Text("取 消",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        // 发布按钮
                        Button(
                            onClick = {
                                // 检查是否选择了心情、标题和内容
                                when {
                                    title.text.isEmpty() -> {
                                        Toast.makeText(this@ConfideActivity, "请先输入标题", Toast.LENGTH_SHORT).show()
                                    }
                                    selectedMood == "心情选择" -> {
                                        Toast.makeText(this@ConfideActivity, "请先选择心情", Toast.LENGTH_SHORT).show()
                                    }
                                    content.text.isEmpty() -> {
                                        Toast.makeText(this@ConfideActivity, "请先输入内容", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        publishMood()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp), // 设置圆角弧度
                            modifier = Modifier.size(100.dp, 45.dp) // 设置按钮大小
                        ) {
                            Text("发 布",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            // 弹窗
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false }, // reset
                    title = {
                        Text(
                            text = "发布成功！",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4DB6AC), // 树洞主题色
                            fontSize = 20.sp
                        )
                    },
                    text = {
                        Text(
                            text = "树洞已经听到你的声音啦！",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { showSuccessDialog = false },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("确定", color = Color.White)
                        }
                    },
                    backgroundColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }

    private fun publishMood() {
        val currentTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        // 从 SharedPreferences 获取用户名
        val username = intent.getStringExtra("username") ?:
            sharedPreferences.getString(USERNAME_KEY, "默认用户名") ?: "默认用户"
        val jsonObject = JSONObject().apply {
            put("owner", username) // 添加用户名
            put("time", System.currentTimeMillis())
            put("title", title.text)
            put("content", content.text)
            put("mood", selectedMood)
        }

        val file = File(filesDir, "$currentTime.json")
        file.writeText(jsonObject.toString())

        //Toast.makeText(this, "内容已保存至 ${file.name}", Toast.LENGTH_SHORT).show()
        showSuccessDialog = true  // sign，显示成功弹窗
        // 重置输入框
        title = TextFieldValue("")
        content = TextFieldValue("")
        selectedMood = "心情选择" // 重置默认心情
    }

}