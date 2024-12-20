package com.example.zyl_241213_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import android.content.SharedPreferences
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import org.json.JSONObject
// 颜色圆形框显示
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.PlayArrow
import android.media.MediaPlayer
import androidx.compose.foundation.lazy.LazyColumn

class EchoActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val USERNAME_KEY = "username_key"
    private val PREFS_NAME = "TreeHole_Login_user_Prefs"
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 获取用户名
        val username = intent.getStringExtra("username") ?:
            sharedPreferences.getString(USERNAME_KEY, "默认用户名") ?: "默认用户"

        mediaPlayer = MediaPlayer.create(this, R.raw.shudong_de_shengyin_xu_jia_ying)
        setContent {
            EchoScreen(username)
        }
    }

    override fun onDestroy() { // 用户退出时暂停播放音乐
        super.onDestroy()
        pauseMusic() // 在退出时暂停音乐
        mediaPlayer.release() // 释放 MediaPlayer 资源
    }

    override fun onBackPressed() { // 用户按下返回键时暂停音乐
        pauseMusic() // 在返回时暂停音乐
        super.onBackPressed() // 调用父类的方法
    }

    private fun playMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            println("开始播放音乐")
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            println("暂停播放音乐")
        }
    }
    @Composable
    fun EchoScreen(username: String) {
        val context = LocalContext.current
        val moodsCount = remember { getMoodStatistics(username) } // 获取心情统计数据
        val isPlaying = remember { mutableStateOf(false) }

        // Finding the mood with the maximum count
        val (maxMood, _) = moodsCount.entries.maxByOrNull { it.value }?.let { it.key to it.value } ?: "平和" to 0

        // Get corresponding color for the most common mood
        val moodColor = getColorForMood(maxMood)
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Color(0xFFB2EAB3),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "   回   声",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007A6D),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = {
                                isPlaying.value = !isPlaying.value
                                if (isPlaying.value) {
                                    playMusic()
                                } else {
                                    pauseMusic()
                                }
                            },
                            modifier = Modifier
                                .size(48.dp) // 设置按钮的大小
                                .wrapContentSize(Alignment.Center) // 确保内容居中
                        ) {
                            if (isPlaying.value) {
                                PauseIcon() // 显示暂停图标
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play Music",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp) // 调整播放图标大小
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                BottomNavigation(
                    backgroundColor = Color(0xFFE1F5E5)
                ) {
                    val items = listOf("首页", "树洞", "回声", "我的")
                    items.forEach { item ->
                        val selected = item == "回声" // 设置当前页面selected = true
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
                            selected = selected,
                            onClick = {
                                // 停止音乐
                                pauseMusic()
                                // 释放资源
                                mediaPlayer.release()
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFE1F5E5)), // 设置整体背景
                ) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        // 年轮标题部分
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp) // 间距
                                .padding(horizontal = 16.dp) // 添加左右边距
                                .background(Color(0xFFF2FCF2))
                                .wrapContentSize(Alignment.Center) // 确保内容居中
                        ) {
                            Text(
                                text = "岁 月 年 轮",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007A6D),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    item {
                        // 环形图部分
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp) // 间距
                                .padding(horizontal = 16.dp) // 添加左右边距
                                .background(Color(0xFFF2FCF2)) // 设置背景颜色
                                .padding(16.dp) // 添加内边距
                                .wrapContentSize(Alignment.Center) // 确保内容居中
                        ) {
                            DrawMoodChart(moodsCount)
                        }
                    }

                    item {
                        // 心情统计列表部分
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp) // 间距
                                .padding(horizontal = 16.dp) // 添加左右边距
                                .background(Color(0xFFF2FCF2)) // 设置背景颜色
                                .padding(16.dp)
                                .wrapContentSize(Alignment.Center) // 确保内容居中
                        ) {
                            MoodStatisticsList(moodsCount)
                        }
                    }

                    item {
                        // 显示数量最多的心情及其颜色
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(Color(0xFFF2FCF2), shape = MaterialTheme.shapes.medium) // 添加形状
                                .wrapContentSize(Alignment.Center), // 确保内容在 Column 中居中
                            horizontalAlignment = Alignment.CenterHorizontally, // 水平居中
                            verticalArrangement = Arrangement.Center // 垂直居中
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "你的小树的主色是：",
                                fontSize = 18.sp,
                                color = Color(0xFF007A6D), // 使用主题颜色
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp) // 增加底部间距
                            )
                            Text(
                                text = "${maxMood} ❀", // 添加装饰符号
                                fontSize = 28.sp,
                                color = moodColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp) // 增加上下间距
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

        )
    }
    @Composable
    fun MoodStatisticsList(moodsCount: Map<String, Int>) {
        // 使用一个Column作为外层容器
        Column {
            // 使用一个Row来实现每行显示两个心情
            moodsCount.toList().chunked(2).forEach { moodPair ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    moodPair.forEach { (mood, count) ->
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(getColorForMood(mood), shape = CircleShape) // 设置为圆形
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "$mood: $count", fontSize = 16.sp, textAlign = TextAlign.Start)

                        Spacer(modifier = Modifier.width(50.dp)) // 每个心情之间增加间距
                    }
                }
            }
        }
    }

    @Composable
    fun DrawMoodChart(moodsCount: Map<String, Int>) {
        Canvas(modifier = Modifier.size(200.dp)) {
            // 过滤掉值为 0 的心情
            val filteredMoods = moodsCount.filter { it.value > 0 }
            val total = filteredMoods.values.sum()

            val outlineWidth = 10f // 轮廓的宽度
            val segmentCount = filteredMoods.size

            // 如果没有心情数据，则不绘制任何内容
            if (segmentCount == 0) return@Canvas

            var startAngle = 0f

            filteredMoods.forEach { (mood, count) ->
                val sweepAngle = (count.toFloat() / total) * 360f

                // 计算间隔的角度
                val gapAngle = 4f
                val adjustedSweepAngle = sweepAngle - gapAngle // 每个段的角度减去间隔

                // 绘制段的轮廓
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = adjustedSweepAngle,
                    useCenter = false,
                    topLeft = Offset(size.width / 2 - 150f, size.height / 2 - 150f),
                    size = Size(300f, 300f),
                    style = Stroke(width = outlineWidth)
                )

                // 绘制实际的段
                drawArc(
                    color = getColorForMood(mood),
                    startAngle = startAngle,
                    sweepAngle = adjustedSweepAngle,
                    useCenter = false,
                    topLeft = Offset(size.width / 2 - 150f, size.height / 2 - 150f),
                    size = Size(300f - outlineWidth, 300f - outlineWidth),
                    style = Stroke(width = 80f) // 调整段的粗细
                )

                // 更新起始角度
                startAngle += sweepAngle
            }
        }
    }

    private fun getColorForMood(mood: String): Color {
        return when (mood) {
            "忧郁" -> Color(0xFF2F8BFA) // 忧郁蓝
            "伤心" -> Color(0xFFABDCE3) // 伤心青
            "焦虑" -> Color(0xFF5EAD68) // 焦虑绿
            "愤怒" -> Color(0xFFFF5C5C) // 愤怒红
            "开心" -> Color(0xFFFFEE4C) // 开心黄
            "活力" -> Color(0xFFFF8B4D) // 活力橙
            "幸福" -> Color(0xFFFA88C7) // 幸福粉
            "平和" -> Color(0xFF888888) // 平和灰
            else -> Color(0xFFe0e0e0) // default
        }
    }

    private fun getMoodStatistics(username: String): Map<String, Int> {
        // 实际读取用户的记录，并统计心情数量
        val moodTypes = arrayOf("忧郁", "伤心", "焦虑", "愤怒", "开心", "活力", "幸福", "平和")
        val moodCount = mutableMapOf<String, Int>()

        moodTypes.forEach { mood -> moodCount[mood] = 0 } // 初始化计数

        val dir = filesDir
        val files = dir.listFiles() ?: return moodCount // 返回默认统计

        files.forEach { file ->
            try {
                val fileContent = file.readText()
                val record = JSONObject(fileContent)
                val owner = record.getString("owner")
                if (owner == username) {
                    val mood = record.getString("mood")
                    moodCount[mood] = moodCount.getValue(mood) + 1 // 增加心情计数
                }
            } catch (e: Exception) {
                // 处理异常
            }
        }

        return moodCount
    }
}

@Composable
fun PauseIcon() {
    Canvas(modifier = Modifier.size(48.dp)) { // 图标大小
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 计算矩形的宽度，减少相对宽度
        val rectWidth = canvasWidth / 6 // 调整宽度
        val space = canvasWidth / 10 // 矩形之间的间隙

        // 计算Y轴的起始位置，以便居中
        val startY = (canvasHeight - (canvasHeight * 0.65f)) / 2

        // 绘制左侧矩形
        drawRect(
            color = Color.White,
            topLeft = Offset(0f, startY), // Y轴起始位置调整
            size = Size(rectWidth, canvasHeight * 0.65f) // 左侧矩形
        )

        // 绘制右侧矩形
        drawRect(
            color = Color.White,
            topLeft = Offset(rectWidth + space, startY), // Y轴起始位置调整
            size = Size(rectWidth, canvasHeight * 0.65f) // 右侧矩形
        )
    }
}



