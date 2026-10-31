package com.example.recipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class Recipe(val name: String, val category: String, val ingredients: List<String>, val steps: String)
private val recipes = listOf(
    Recipe("西红柿炒鸡蛋", "菜", listOf("西红柿", "鸡蛋"), "1. 西红柿切块，鸡蛋打散。\n2. 炒熟鸡蛋盛出。\n3. 炒软西红柿后倒回鸡蛋，调味翻匀。"),
    Recipe("南瓜蒸蛋", "菜", listOf("南瓜", "鸡蛋"), "1. 南瓜切薄片蒸熟。\n2. 鸡蛋加温水搅匀。\n3. 倒入碗中蒸至凝固。"),
    Recipe("冬瓜炒虾仁", "菜", listOf("冬瓜", "虾"), "1. 虾去壳去虾线，冬瓜切片。\n2. 先炒虾仁，再下冬瓜。\n3. 加少量水焖熟调味。"),
    Recipe("清炒太湖菜", "菜", listOf("太湖菜"), "1. 洗净切段。\n2. 热锅下蒜末。\n3. 大火快炒至熟，加盐。"),
    Recipe("豆角炒肉丝", "菜", listOf("豆角", "后腿肉"), "1. 肉切丝腌制，豆角切段。\n2. 豆角先充分炒熟。\n3. 加肉丝炒至全熟调味。"),
    Recipe("香煎鸡胸肉", "菜", listOf("鸡胸肉"), "1. 鸡胸肉切薄片腌制。\n2. 平底锅小火煎至两面金黄。\n3. 确保中心完全熟透。"),
    Recipe("火腿炒鸡蛋", "菜", listOf("火腿", "鸡蛋"), "1. 火腿切丁，鸡蛋打散。\n2. 炒熟鸡蛋。\n3. 加火腿翻炒至热透。"),
    Recipe("冬瓜虾汤", "汤", listOf("冬瓜", "虾"), "1. 冬瓜切片，虾清理干净。\n2. 水开下冬瓜煮软。\n3. 加虾煮熟后调味。"),
    Recipe("西红柿蛋汤", "汤", listOf("西红柿", "鸡蛋"), "1. 西红柿切块煮开。\n2. 缓缓倒入蛋液。\n3. 蛋花凝固后调味。"),
    Recipe("南瓜汤", "汤", listOf("南瓜"), "1. 南瓜去皮切块。\n2. 加水煮软。\n3. 压成泥并调味。")
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { RecipeApp() } }
}
@Composable private fun RecipeApp() {
    val options = listOf("后腿肉", "鸡胸肉", "鸡蛋", "西红柿", "鱼尾", "太湖菜", "南瓜", "冬瓜", "虾", "蔬菜肠", "豆角", "火腿")
    val selected = remember { mutableStateListOf<String>().apply { addAll(options) } }
    val menu = remember { mutableStateListOf<Recipe>() }
    var filter by remember { mutableStateOf(true) }
    val available = recipes.filter { !filter || it.ingredients.all(selected::contains) }
    MaterialTheme { Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("今天吃什么 🍳", style = MaterialTheme.typography.headlineMedium)
        Text("两人份 · 三菜一汤 · 离线菜谱")
        Spacer(Modifier.height(8.dp))
        Text("冰箱现有食材（点击切换）")
        options.chunked(3).forEach { row -> Row { row.forEach { name -> FilterChip(selected = name in selected, onClick = { if (name in selected) selected.remove(name) else selected.add(name) }, label = { Text(name) }, modifier = Modifier.padding(end = 5.dp)) } } }
        Row { Text("仅显示食材齐全的菜谱", modifier = Modifier.weight(1f)); Switch(checked = filter, onCheckedChange = { filter = it }) }
        Button(onClick = { menu.clear(); menu.addAll(available.filter { it.category == "菜" }.shuffled().take(3)); menu.addAll(available.filter { it.category == "汤" }.shuffled().take(1)) }) { Text("🎲 随机生成菜单") }
        Text("今日菜单（点击候选菜谱可手动替换或加入）", style = MaterialTheme.typography.titleMedium)
        if (menu.isEmpty()) Text("还没有选菜，点击随机生成或从下方挑选")
        menu.forEach { recipe -> Text("• ${recipe.name}") }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Text("候选菜谱 ${available.size} 道", style = MaterialTheme.typography.titleMedium)
        LazyColumn { items(available) { recipe ->
            var expanded by remember { mutableStateOf(false) }
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Column(Modifier.padding(12.dp)) {
                Text("${recipe.name} · ${recipe.category}", style = MaterialTheme.typography.titleMedium)
                Text("食材：${recipe.ingredients.joinToString("、")}")
                Row { TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "收起做法" else "查看做法") }; TextButton(onClick = {
                    val categoryMax = if (recipe.category == "汤") 1 else 3
                    val categoryItems = menu.filter { it.category == recipe.category }
                    if (recipe !in menu) { if (categoryItems.size >= categoryMax) menu.remove(categoryItems.first()); menu.add(recipe) }
                }) { Text("选这道") } }
                if (expanded) Text(recipe.steps)
            } }
        } }
    } }
}
