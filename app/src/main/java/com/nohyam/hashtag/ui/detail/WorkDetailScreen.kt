package com.nohyam.hashtag.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nohyam.hashtag.ui.sample.sampleWorks
import com.nohyam.hashtag.ui.theme.HashTagTheme

@Composable
fun WorkDetailScreen(
    id: Int,
    mediaType: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val work = sampleWorks.find { it.id == id && it.mediaType == mediaType }
    // 표시 전용 더미 선택 상태: 첫 키워드를 선택한 것으로 가정합니다.
    val selected = work?.keywordVotes?.keys?.take(1).orEmpty()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBackClick) { Text("‹ 뒤로 가기") }
                Text("작품 상세", style = MaterialTheme.typography.titleLarge)
            }
            if (work == null) {
                Text("작품을 찾을 수 없어요.", modifier = Modifier.padding(24.dp))
                return@Column
            }
            val rankedKeywords = work.keywordVotes.toList().sortedByDescending { it.second }

            LazyColumn(
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "info") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(220.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) { Text("포스터 준비 중") }
                        }
                        Text(work.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("${work.type} · ${work.year}년 ${if (mediaType == "movie") "개봉" else "방영"}")
                        Text("작품 정보와 선택 인원은 더미 데이터입니다.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                item(key = "synopsis") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("줄거리", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(work.synopsis, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                item(key = "heading") {
                    Text("작품 키워드 · 선택 인원순", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(rankedKeywords, key = { it.first }) { (keyword, count) ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (keyword in selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("#$keyword", style = MaterialTheme.typography.titleMedium)
                            Text("${count}명 선택${if (keyword in selected) " · ✓ 내가 선택함" else ""}")
                        }
                    }
                }
                item(key = "actions") {
                    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                        Text("키워드 선택,관리")
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true, name = "영화 상세")
@Composable
private fun MovieDetailPreview() {
    HashTagTheme(dynamicColor = false) { WorkDetailScreen(id = 1, mediaType = "movie") }
}

@Preview(showBackground = true, name = "드라마 상세 · 다크")
@Composable
private fun DramaDetailPreview() {
    HashTagTheme(darkTheme = true, dynamicColor = false) { WorkDetailScreen(id = 4, mediaType = "tv") }
}
