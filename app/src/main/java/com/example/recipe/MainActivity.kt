package com.example.recipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

private val appColors = lightColorScheme(
    primary = Color(0xFFE85D2A), onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDCCB), secondary = Color(0xFF527A4B),
    background = Color(0xFFFFFBF7), surface = Color(0xFFFFFBF7),
    surfaceVariant = Color(0xFFF6EEE8), onSurface = Color(0xFF28201C)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = appColors) { RecipeApp() } }
    }
}

@Composable private fun RecipeApp() {
    val options = listOf("后腿肉", "鸡胸肉", "鸡蛋", "西红柿", "鱼尾", "太湖菜", "南瓜", "冬瓜", "虾", "蔬菜肠", "豆角", "火腿")
    val selected = remember { mutableStateListOf<String>().apply { addAll(options) } }
    val menu = remember { mutableStateListOf<Recipe>() }
    var filter by remember { mutableStateOf(true) }
    var tab by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    var expandedRecipe by remember { mutableStateOf<String?>(null) }
    val available = recipes.filter { !filter || it.ingredients.all(selected::contains) }
    fun randomizeMenu() {
        val dishes = available.filter { it.category == "菜" }
        val soups = available.filter { it.category == "汤" }
        if (dishes.size < 3 || soups.isEmpty()) message = "当前可选 ${dishes.size} 道菜、${soups.size} 道汤。请补充食材，或在冰箱页关闭严格匹配。"
        else { menu.clear(); menu.addAll(dishes.shuffled().take(3)); menu.add(soups.random()); message = null }
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = {
        NavigationBar(containerColor = Color.White) {
            listOf("⌂" to "首页", "▦" to "菜谱", "♧" to "冰箱").forEachIndexed { index, item ->
                NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Text(item.first, style = MaterialTheme.typography.titleLarge) }, label = { Text(item.second) })
            }
        }
    }) { padding ->
        when (tab) {
            0 -> HomeScreen(Modifier.padding(padding), menu, selected, message, ::randomizeMenu, { tab = 2 }, { tab = 1 }, menu::remove)
            1 -> RecipeListScreen(Modifier.padding(padding), available, expandedRecipe, { expandedRecipe = if (expandedRecipe == it.name) null else it.name }) { recipe ->
                val max = if (recipe.category == "汤") 1 else 3
                val sameCategory = menu.filter { it.category == recipe.category }
                if (recipe !in menu) { if (sameCategory.size >= max) menu.remove(sameCategory.first()); menu.add(recipe) }
                message = null
            }
            else -> PantryScreen(Modifier.padding(padding), options, selected, filter, { filter = it }) { name -> if (name in selected) selected.remove(name) else selected.add(name) }
        }
    }
}

@Composable private fun HomeScreen(modifier: Modifier, menu: List<Recipe>, selected: List<String>, message: String?, onRandomize: () -> Unit, onOpenPantry: () -> Unit, onOpenRecipes: () -> Unit, onRemove: (Recipe) -> Unit) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("今天吃什么", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("把晚饭交给一点小惊喜", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.primaryContainer) { Text("2 人份", Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontWeight = FontWeight.SemiBold) }
            }
        }
        item {
            Card(shape = RoundedCornerShape(28.dp), elevation = CardDefaults.cardElevation(5.dp)) {
                Box(Modifier.fillMaxWidth().height(238.dp)) {
                    Image(painterResource(R.drawable.home_hero), "三菜一汤的温馨家常晚餐", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC2B1710)))))
                    Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Text("今晚，认真吃顿饭", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("三菜一汤 · 营养搭配 · 不再纠结", color = Color.White.copy(alpha = .88f))
                        Spacer(Modifier.height(12.dp)); Button(onClick = onRandomize, shape = RoundedCornerShape(16.dp)) { Text("帮我搭配  ✦", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
        message?.let { item { AssistChip(onClick = onOpenPantry, label = { Text(it) }, leadingIcon = { Text("!") }) } }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) { Text("我家冰箱", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); TextButton(onClick = onOpenPantry) { Text("管理食材") } }
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                selected.take(8).forEach { SuggestionChip(onClick = onOpenPantry, label = { Text(it) }) }
                if (selected.size > 8) SuggestionChip(onClick = onOpenPantry, label = { Text("+${selected.size - 8}") })
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("今日菜单", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(if (menu.isEmpty()) "还没决定？一键生成三菜一汤" else "已经为你配好 ${menu.size} 道", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                TextButton(onClick = onOpenRecipes) { Text("自由搭配") }
            }
        }
        if (menu.isEmpty()) item {
            OutlinedCard(shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(22.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { Text("🍲", style = MaterialTheme.typography.displaySmall); Text("你的晚餐菜单还空着", fontWeight = FontWeight.Bold); Text("点击上面的“帮我搭配”，马上得到灵感", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        } else items(menu, key = { it.name }) { recipe -> MenuCard(recipe) { onRemove(recipe) } }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable private fun MenuCard(recipe: Recipe, onRemove: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(16.dp), color = if (recipe.category == "汤") Color(0xFFE4F1DE) else Color(0xFFFFE7D8)) { Text(recipe.category, Modifier.padding(14.dp), fontWeight = FontWeight.Bold) }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) { Text(recipe.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(recipe.ingredients.joinToString(" · "), maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            TextButton(onClick = onRemove) { Text("移除") }
        }
    }
}

@Composable private fun RecipeListScreen(modifier: Modifier, recipeList: List<Recipe>, expandedRecipe: String?, onExpand: (Recipe) -> Unit, onSelect: (Recipe) -> Unit) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("全部菜谱", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("找到喜欢的菜，加入今晚菜单", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (recipeList.isEmpty()) item { Text("没有符合当前食材条件的菜谱，请到冰箱页调整食材。") }
        items(recipeList, key = { it.name }) { recipe ->
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Text(recipe.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) { Text(recipe.category, Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) } }
                    Text("食材 · ${recipe.ingredients.joinToString("、")}", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                    if (expandedRecipe == recipe.name) Text(recipe.steps, modifier = Modifier.padding(top = 12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { TextButton(onClick = { onExpand(recipe) }) { Text(if (expandedRecipe == recipe.name) "收起做法" else "查看做法") }; Button(onClick = { onSelect(recipe) }, shape = RoundedCornerShape(14.dp)) { Text("加入菜单") } }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable private fun PantryScreen(modifier: Modifier, options: List<String>, selected: List<String>, filter: Boolean, onFilterChange: (Boolean) -> Unit, onToggle: (String) -> Unit) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { Text("我家冰箱", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("点选家里现有的食材，推荐会更准确", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item { Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("严格食材匹配", fontWeight = FontWeight.Bold); Text("只推荐食材齐全的菜", color = MaterialTheme.colorScheme.onSurfaceVariant) }; Switch(checked = filter, onCheckedChange = onFilterChange) } } }
        item {
            Text("现有食材 ${selected.size} 种", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { options.forEach { name -> FilterChip(selected = name in selected, onClick = { onToggle(name) }, label = { Text(name) }) } }
        }
    }
}
