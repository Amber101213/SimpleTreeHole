
package com.example.zyl_241213_1
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

// mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.TextStyle

import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius



const val MAX_COMMENT_LENGTH = 100 // 定义评论的最大字数

class RecordDetailActivity : ComponentActivity() {

    private lateinit var record: Record
    private lateinit var username: String
    private var comments by mutableStateOf<List<Comment>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 从 intent 中获取 Record 对象
        record = intent.getParcelableExtra("record")
            ?: throw IllegalArgumentException("Record must be provided")
        // 从 intent 中获取 username
        username = intent.getStringExtra("username")
            ?: throw IllegalArgumentException("Username must be provided")

        comments = loadComments() // 加载评论并更新 `comments`

        setContent {
            RecordDetail(record)
        }
    }

    @Composable
    fun RecordDetail(record: Record) {
        var commentContent by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) } // 控制发布评论弹窗显示的状态
        var showCommentError by remember { mutableStateOf(false) } // 控制评论超出字数的错误提示

        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Color(0xFFB2EAB3),
                    contentColor = Color(0xFF007A6D)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = { finish() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "记 录 详 情          ",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007A6D),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFE1F5E5)),
                    horizontalAlignment = Alignment.Start
                ) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))

                        // 标题部分
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(Color(0xB2FFFFFF), shape = MaterialTheme.shapes.medium)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "标题: ${record.title}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // 时间部分
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "时间: ${record.time}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF555555)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp)) // 添加间距

                        // 心情部分
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "心情: ${record.mood}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF555555)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // 内容部分
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(Color(0xB2FFFFFF), shape = MaterialTheme.shapes.medium)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = record.content,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF00796B),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        // 评论区
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)
                                .background(Color(0xFFC1EAE4), shape = MaterialTheme.shapes.medium)
                                .padding(4.dp)
                        ) {
                            Text(
                                text = " 评论区",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF545454)
                            )
                        }
                    }

                    // 渲染评论
                    itemsIndexed(comments.filter {
                        it.time_of_record == record.time && it.owner == record.owner
                    }.toList()) { index, comment ->
                        Spacer(modifier = Modifier.height(16.dp))
                        // 评论部分
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(Color.White, shape = MaterialTheme.shapes.medium)
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "评论时间: ${comment.time}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF555555)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = comment.content,
                                    fontSize = 16.sp,
                                    color = Color(0xFF004D40),
                                    softWrap = true // 确保文本可以换行
                                )
                            }
                        }
                    }

                    // 发表评论区
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        // 输入评论的文本框
                        Box(modifier = Modifier.heightIn(max = 120.dp)) { // 限制最大高度
                            TextField(
                                value = commentContent,
                                onValueChange = { newText ->
                                    if (newText.length <= MAX_COMMENT_LENGTH) {
                                        commentContent = newText
                                        showCommentError = false // 清空错误提示
                                    } else {
                                        showCommentError = true // 超出字符限制
                                        Toast.makeText(this@RecordDetailActivity, "评论不得超过${MAX_COMMENT_LENGTH}个字符", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                label = { Text("在这里留下你的评论...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .heightIn(min = 60.dp), // 设置最小高度
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedLabelColor = Color(0xFF009688),
                                    unfocusedLabelColor = Color.Gray,
                                    // 设置文本框背景颜色
                                    backgroundColor = Color(0xFFE0F2F1)
                                ),
                                maxLines = 5, // 允许最多显示 5 行
                                textStyle = TextStyle(fontSize = 16.sp)
                            )

                            // 添加评论字数统计
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "${commentContent.length}/$MAX_COMMENT_LENGTH",
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .align(Alignment.CenterEnd) // 这里让文本在盒子中右对齐
                                )
                            }
                        }
// 错误提示               // 错误提示
                        if (showCommentError) {
                            Box(modifier = Modifier.fillMaxWidth()) { // 使用Box作为容器
                                Text(
                                    text = "评论不能超过${MAX_COMMENT_LENGTH}个字！",
                                    style = TextStyle(fontSize = 12.sp, color = Color.Red),
                                    modifier = Modifier.align(Alignment.CenterEnd) // 在Box中对齐
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // 发布评论按钮
                        Button(
                            onClick = {
                                if (commentContent.isBlank()) {
                                    Toast.makeText(this@RecordDetailActivity, "评论不能为空！", Toast.LENGTH_SHORT).show()
                                } else {
                                    showDialog = true // 显示对话框
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF7DC483),
                                contentColor = Color(0xFFFFFFFF)
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            TreeHoleIcon(modifier = Modifier.size(24.dp))
                            Text(
                                text = "发布评论",
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        )
        // 确认对话框
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                backgroundColor = Color(0xFFC1EAE4), // 设置对话框背景色
                title = {
                    Text("确认发表",
                        color = Color(0xFF007A6D),
                        style = TextStyle(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("你确定要发表这条评论吗？",
                        color = Color(0xFF555555),
                        style = TextStyle(fontSize = 16.sp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val currentTime = SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()
                            ).format(Date())
                            val commentObj = Comment(
                                time = currentTime,
                                owner = username,
                                time_of_record = record.time,
                                content = commentContent
                            )
                            comments = saveCommentToJson(commentObj) // 更新 comments
                            commentContent = "" // 清空评论框
                            showDialog = false // 关闭对话框
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF7DC483),
                            contentColor = Color.White // 白色文字
                        )
                    ) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Gray, // 灰色
                            contentColor = Color.White // 白色文字
                        )
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }

    private fun saveCommentToJson(comment: Comment): List<Comment> {
        val commentsFile = File(filesDir, "comments.json")
        val commentsList = if (commentsFile.exists()) {
            val commentsJson = commentsFile.readText()
            Gson().fromJson(commentsJson, Array<Comment>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        commentsList.add(comment) // 添加新评论到列表

        // 创建和转换为 JSON
        val json = Gson().toJson(commentsList)

        try {
            FileWriter(commentsFile).use { writer ->
                writer.write(json)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return commentsList // 返回更新后的评论列表
    }

    private fun loadComments(): List<Comment> {
        val commentsList = mutableListOf<Comment>()
        val commentsFile = File(filesDir, "comments.json")
        if (commentsFile.exists()) {
            try {
                val commentsJson = commentsFile.readText()
                val commentsArray = Gson().fromJson(commentsJson, Array<Comment>::class.java)
                commentsList.addAll(commentsArray)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return commentsList.toList() // 返回不可变列表
    }

}
@Composable
fun TreeHoleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val width = size.width
        val height = size.height
        // 绘制外部树干
        drawRoundRect(
            color = Color(0xFF795548), // 棕色
            topLeft = Offset(0f, height * 0.2f),
            size = Size(width, height * 0.5f),
            cornerRadius = CornerRadius(8f, 8f)
        )
        // 绘制洞口
        drawOval(
            color = Color(0xFF4E342E), // 深棕色
            topLeft = Offset(width * 0.2f, height * 0.1f),
            size = Size(width * 0.6f, height * 0.3f)
        )
        // 绘制草地
        drawRoundRect(
            color = Color(0xFF8BC34A), // 绿色
            topLeft = Offset(0f, height * 0.6f),
            size = Size(width, height * 0.2f),
            cornerRadius = CornerRadius(0f, 0f)
        )
    }
}