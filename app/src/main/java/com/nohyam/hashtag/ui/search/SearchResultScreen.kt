package com.nohyam.hashtag.ui.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nohyam.hashtag.ui.theme.HashTagTheme

// 화면 확인용 가상 작품과 키워드 선택 인원입니다.
private data class SampleWork(
    val id: Int,
    val title: String,
    val type: String,
    val year: Int,
    val keywordVotes: Map<String, Int>
)

private val sampleWorks = listOf(
    SampleWork(1, "마지막 단서", "영화", 2024, mapOf("반전" to 1284, "몰입감" to 936, "스릴러" to 815)),
    SampleWork(2, "기억의 밤을 걷다", "드라마", 2023, mapOf("반전" to 752, "몰입감" to 684, "여운이남는" to 310)),
    SampleWork(3, "내일의 우리", "영화", 2025, mapOf("시간여행" to 940, "반전" to 521, "몰입감" to 708)),
    SampleWork(4, "작은 계절", "드라마", 2024, mapOf("힐링" to 1203, "가족애" to 824, "감동" to 659)),
    SampleWork(5, "우리의 여름", "영화", 2022, mapOf("힐링" to 734, "영상미" to 692, "여운이남는" to 481)),
    SampleWork(6, "단서 없는 방", "드라마", 2025, mapOf("반전" to 891, "몰입감" to 1024, "스릴러" to 932)),
    SampleWork(7, "다시 만난 우리", "드라마", 2021, mapOf("가족애" to 452, "감동" to 563, "코미디" to 287)),
    SampleWork(8, "시간의 단서", "영화", 2023, mapOf("시간여행" to 602, "반전" to 479, "몰입감" to 523))
)

/** API 없이 화면 상태를 확인하기 위한 더미 상태입니다. */
enum class SearchResultState { Content, Loading, Error }

/** 제목 또는 #으로 구분한 키워드를 더미 데이터에서 검색하는 독립 화면. */
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    initialQuery: String = "",
    state: SearchResultState = SearchResultState.Content
) {
    var query by rememberSaveable(initialQuery) { mutableStateOf(initialQuery) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchText = query.trim()
    val isKeywordSearch = searchText.startsWith("#")
    val keywords = if (isKeywordSearch) {
        searchText.split('#')
            .map { keyword -> keyword.filterNot(Char::isWhitespace) }
            .filter(String::isNotBlank)
            .distinct()
    } else {
        emptyList()
    }
    val results = when {
        searchText.isBlank() -> emptyList()
        isKeywordSearch && keywords.isEmpty() -> emptyList()
        isKeywordSearch -> sampleWorks.filter { work ->
            keywords.all { keyword -> keyword in work.keywordVotes }
        }.sortedWith(
            compareByDescending<SampleWork> { work ->
                keywords.minOf { work.keywordVotes.getValue(it) }
            }.thenByDescending { work ->
                keywords.sumOf { work.keywordVotes.getValue(it) }
            }
        )

        else -> sampleWorks.filter { it.title.contains(searchText, ignoreCase = true) }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                label = { Text("제목 또는 #키워드 검색") },
                placeholder = { Text("예: 우리의 또는 #반전 #몰입감") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isKeywordSearch) {
                    item(key = "keywords") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                keywords.forEachIndexed { index, keyword ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (index > 0) {
                                            Text(
                                                "AND",
                                                modifier = Modifier.padding(end = 8.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        KeywordChip(keyword = keyword, onRemove = {
                                            query = keywords.filterNot { it == keyword }
                                                .joinToString(" ") { "#$it" }
                                        })
                                    }
                                }
                            }
                            Text(
                                text = if (keywords.size > 1) "선택한 키워드를 모두 포함하는 작품" else "키워드와 어울리는 작품",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item(key = "summary") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isKeywordSearch || searchText.isBlank()) "검색 결과" else "‘$searchText’ 제목 검색 결과",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (state) {
                                SearchResultState.Content -> "작품 ${results.size}개 · 더미 데이터"
                                SearchResultState.Loading -> "검색 중 · 더미 상태"
                                SearchResultState.Error -> "검색 오류 · 더미 상태"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (state != SearchResultState.Content) {
                    item(key = "status") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = if (state == SearchResultState.Error) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerLow
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (state == SearchResultState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.semantics {
                                            contentDescription = "검색 결과 로딩 중"
                                        }
                                    )
                                }
                                Text(
                                    text = if (state == SearchResultState.Loading) {
                                        "작품을 찾고 있어요. 잠시만 기다려 주세요."
                                    } else {
                                        "검색 결과를 불러오지 못했어요.\n잠시 후 다시 시도해 주세요."
                                    },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    return@LazyColumn
                }
                if (results.isEmpty()) {
                    item(key = "empty") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Text(
                                text = when {
                                    searchText.isBlank() -> "찾고 싶은 작품의 제목이나 #키워드를 입력해 주세요."
                                    isKeywordSearch && keywords.isEmpty() -> "# 뒤에 키워드를 입력해 주세요."
                                    else -> "일치하는 작품이 없어요.\n다른 검색어를 입력하거나 키워드를 줄여 보세요."
                                },
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                items(results, key = { it.id }) { work ->
                    WorkResultCard(work = work, keywords = keywords)
                }
            }
        }
    }
}

@Composable
private fun KeywordChip(keyword: String, onRemove: () -> Unit) {
    val iconColor = MaterialTheme.colorScheme.onSecondaryContainer
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = iconColor
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "#$keyword",
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelLarge
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.semantics { contentDescription = "$keyword 키워드 삭제" }
            ) {
                Canvas(modifier = Modifier.size(16.dp)) {
                    val inset = 3.dp.toPx()
                    val stroke = 1.8.dp.toPx()
                    drawLine(
                        iconColor,
                        Offset(inset, inset),
                        Offset(size.width - inset, size.height - inset),
                        stroke,
                        StrokeCap.Round
                    )
                    drawLine(
                        iconColor,
                        Offset(size.width - inset, inset),
                        Offset(inset, size.height - inset),
                        stroke,
                        StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkResultCard(work: SampleWork, keywords: List<String>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "포스터\n준비 중",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    work.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${work.type} · ${work.year}년 ${if (work.type == "영화") "개봉" else "방영"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                keywords.forEach { keyword ->
                    Text(
                        text = "#$keyword · ${work.keywordVotes.getValue(keyword)}명 선택",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "제목 검색")
@Composable
private fun TitleSearchPreview() {
    HashTagTheme(dynamicColor = false) { SearchResultScreen(initialQuery = "우리") }
}

@Preview(showBackground = true, name = "키워드 AND 검색")
@Composable
private fun KeywordSearchPreview() {
    HashTagTheme(dynamicColor = false) { SearchResultScreen(initialQuery = "#반전 #몰입감") }
}

@Preview(showBackground = true, name = "키워드 검색 · 다크")
@Composable
private fun DarkKeywordSearchPreview() {
    HashTagTheme(
        darkTheme = true,
        dynamicColor = false
    ) { SearchResultScreen(initialQuery = "#힐링") }
}

@Preview(showBackground = true, name = "검색 결과 없음")
@Composable
private fun EmptySearchPreview() {
    HashTagTheme(dynamicColor = false) { SearchResultScreen(initialQuery = "#없는키워드") }
}

@Preview(showBackground = true, name = "검색 중")
@Composable
private fun LoadingSearchPreview() {
    HashTagTheme(dynamicColor = false) {
        SearchResultScreen(initialQuery = "#반전 #몰입감", state = SearchResultState.Loading)
    }
}

@Preview(showBackground = true, name = "검색 오류")
@Composable
private fun ErrorSearchPreview() {
    HashTagTheme(dynamicColor = false) {
        SearchResultScreen(initialQuery = "우리", state = SearchResultState.Error)
    }
}
