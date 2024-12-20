package com.example.zyl_241213_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import kotlinx.coroutines.launch
import android.content.Intent
import android.content.Context
// 字体大小
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// 密码可见
import androidx.compose.ui.text.input.VisualTransformation
// 绘制可见/不可见图标
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
// 保存当前用户名
import android.content.SharedPreferences

class LoginRegisterActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private val USERNAME_KEY = "username_key"
    private val PREFS_NAME = "TreeHole_Login_user_Prefs"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(this)
        // 定义 SharedPreferences 实例
        // 存储文件的名称；私有文件
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 检查登录状态
        val username = sharedPreferences.getString(USERNAME_KEY, null)
        if (username != null) {
            // 如果用户已登录，跳转到 TreeHoleActivity
            startActivity(Intent(this, TreeHoleActivity::class.java).apply {
                putExtra("username", username)
            })
            finish() // 结束当前活动
            return
        }

        setContent {
            MaterialTheme {
                LoginRegisterScreen(database) // 直接传入数据库实例
            }
        }
    }

    @Composable
    fun LoginRegisterScreen(database: AppDatabase) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoginMode by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var passwordVisible by remember { mutableStateOf(false) } // 控制密码可见性
        val scope = rememberCoroutineScope()
        val keyboardController = LocalSoftwareKeyboardController.current
        val passwordFocusRequester = remember { FocusRequester() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部的退出按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFA5D6A7))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val intent = Intent(this@LoginRegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(
                        text = "退出",
                        color = Color.White,
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (isLoginMode) "登录" else "注册",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 用户名输入框
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("请输入您的用户名") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .onKeyEvent {
                        if (it.key == Key.Enter) {
                            keyboardController?.hide()
                            passwordFocusRequester.requestFocus()
                            true
                        } else {
                            false
                        }
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 密码输入框
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("请输入密码") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        if (passwordVisible) {
                            VisibilityIcon()  // 绘制眼睛图标
                        } else {
                            VisibilityOffIcon()  // 绘制闭眼图标
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .focusRequester(passwordFocusRequester)
                    .onKeyEvent {
                        if (it.key == Key.Enter) {
                            keyboardController?.hide()
                            scope.launch {
                                handleButtonClick(isLoginMode, username, password, database) { error ->
                                    errorMessage = error
                                }
                            }
                            true
                        } else {
                            false
                        }
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 错误信息显示
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    scope.launch {
                        handleButtonClick(isLoginMode, username, password, database) { errorMessage = it }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(
                    text = if (isLoginMode) "登  录" else "注  册",
                    color = Color.White,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { isLoginMode = !isLoginMode }) {
                Text(text = if (isLoginMode) "注册账户" else "已有账号? 去登录")
            }
        }
    }

    private suspend fun handleButtonClick(
        isLoginMode: Boolean, username: String, password: String,
        database: AppDatabase, setErrorMessage: (String?) -> Unit
    ) {
        if (isLoginMode) {
            val user = database.userDao().login(username)
            if (user != null && user.password == password) {
                // 保存用户名
                // 使用 SharedPreferences.Editor 来保存数据
                with(sharedPreferences.edit()) {
                    putString(USERNAME_KEY, username)
                    apply()
                }
                val intent = Intent(this@LoginRegisterActivity, TreeHoleActivity::class.java)
                intent.putExtra("username", username) // 将用户名传递到下一个Activity
                startActivity(intent)
                finish()
            } else {
                setErrorMessage("用户名或密码错误")
            }
        } else {
            val existingUser = database.userDao().login(username)
            if (existingUser != null) {
                setErrorMessage("用户名已存在")
            } else {
                database.userDao().register(User(username, password))
                setErrorMessage("注册成功，请登录")
            }
        }
    }
    // 绘制可见（睁眼）图标
    @Composable
    fun VisibilityIcon() {
        Canvas(modifier = Modifier.size(24.dp)) {
            // 画眼睛外轮廓
            drawOval(
                color = Color.Black,
                topLeft = Offset(0f, size.height * 0.2f),
                size = Size(size.width, size.height * 0.6f)
            )
            // 画眼睛内部轮廓
            drawOval(
                color = Color.White,
                topLeft = Offset(size.width * 0.1f, size.height * 0.3f),
                size = Size(size.width * 0.8f, size.height * 0.4f)
            )
            // 画眼睛瞳孔
            drawCircle(
                color = Color.Black,
                radius = size.width * 0.1f,
                center = Offset(size.width / 2, size.height / 2)
            )
        }
    }
    // 绘制可见（闭眼）图标
    @Composable
    fun VisibilityOffIcon() {
        Canvas(modifier = Modifier.size(24.dp)) {
            // 绘制眼睛的外形（闭眼的弯曲部分）
            drawArc(
                color = Color.Black,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(0f, (size.height * 0.5f) - (size.height * 0.3f)),
                size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.6f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f) // 设置线宽
            )

            // 绘制眼睛的内部轮廓
            drawOval(
                color = Color.White,
                topLeft = Offset(size.width * 0.1f, size.height * 0.3f),
                size = Size(size.width * 0.8f, size.height * 0.4f)
            )

            // 画“闭眼”的横线
            drawLine(
                color = Color.White,
                start = Offset(size.width * 0.2f, size.height * 0.5f),
                end = Offset(size.width * 0.8f, size.height * 0.5f),
                strokeWidth = 4f
            )
        }
    }



}
