package it.maicol07.spraypaintkt.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.maicol07.spraypaintkt.sample.data.models.Book
import it.maicol07.spraypaintkt.sample.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() = AppTheme {
    var isRefreshing by remember { mutableStateOf(false) }
    val books = remember { mutableStateListOf<Book>() }
    val coroutineScope = rememberCoroutineScope()
    val refresh = suspend {
        isRefreshing = true
        val b = Book.includes("author", "publisher", "reviews", "reviews.reader").all()
        books.removeAll { true }
        books.addAll(b.data)
        isRefreshing = false
    }
    LaunchedEffect(Unit) {
        refresh()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        refresh()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
    ) {
        Column(Modifier.padding(it)) {
            var selectedBook by remember { mutableStateOf<Book?>(null) }
            if (isRefreshing) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(books) { book ->
                    BookCard(book) {
                        selectedBook = book
                    }
                }
            }

            if (selectedBook != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedBook = null }
                ) {
                    BookDetail(selectedBook!!)
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(book.author.name)
            Spacer(modifier = Modifier.height(8.dp))
            Text(book.publisher.name, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun BookDetail(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = book.title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(book.author.name)
        Spacer(modifier = Modifier.height(8.dp))
        Text(book.publisher.name, style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Reviews",
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn {
            items(book.reviews) { review ->
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(review.reader.name) },
                    supportingContent = { Text("By ${review.reader.name} on ${review.created}") },
                    leadingContent = { Icon(Icons.Outlined.Star, contentDescription = "Review") }
                )
            }
        }
    }
}
