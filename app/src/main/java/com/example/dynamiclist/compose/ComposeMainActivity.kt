package com.example.dynamiclist.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.dynamiclist.DynamicListViewModel
import com.example.dynamiclist.ListDataModel
import com.example.dynamiclist.R
import com.example.dynamiclist.compose.ui.theme.DynamicListTheme
import com.google.accompanist.drawablepainter.rememberDrawablePainter

class ComposeMainActivity : ComponentActivity() {

    private val viewModel: DynamicListViewModel by viewModels<DynamicListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (viewModel.isBottomSheetVisible.value) {
                BottomSheetUI(viewModel)
            }
            DynamicListTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn {
                        item {
                            ImageSliderWithDots(listOfImages = viewModel.listOfImages)
                            SearchableRecyclerView(viewModel)
                        }
                    }

                    GenericFloatingActionButton(
                        onClick = {
                            viewModel.isBottomSheetVisible.value = true
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageSliderWithDots(
    listOfImages: ArrayList<Int>,
) {
    val pagerState = rememberPagerState(initialPage = 0)  {
        listOfImages.size
    }
    val dotsCount = listOfImages.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            Card(shape = RoundedCornerShape(8.dp),modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp)) {
                Image(
                    painter = painterResource(id = listOfImages[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            (0 until dotsCount).forEach { index ->
                val isSelected = pagerState.currentPage == index
                Image(
                    painter = rememberDrawablePainter(
                        drawable = ContextCompat.getDrawable(
                            LocalContext.current,
                            if (isSelected) R.drawable.selected_dot
                            else R.drawable.unselected_dot
                        )
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchableRecyclerView(viewModel: DynamicListViewModel) {

    var query by remember { mutableStateOf("") }
    val filteredList = remember(query) {
        viewModel.filterList(query)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 45.dp, top = 5.dp, end = 45.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(text = "Search") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            },
            textStyle = TextStyle(fontSize = 18.sp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
                    .heightIn(0.dp, 670.dp)
            ) {
                items(filteredList) { item ->
                    ListItem(item)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }

}

@Composable
fun ListItem(
    item: ListDataModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 15.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    ),
                    maxLines = 1,
                    modifier = Modifier.padding(top = 5.dp)
                )

                Text(
                    text = item.desc,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = Color.Black
                    ),
                    maxLines = 1,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}

@Composable
fun GenericFloatingActionButton(onClick: () -> Unit, modifier: Modifier) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.Blue,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = modifier
    ) {
        Icon(Icons.Default.MoreVert, contentDescription = "FAB")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetUI(viewModel: DynamicListViewModel) {

    val bottomSheetState =
        rememberModalBottomSheetState(true)

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = {
            viewModel.isBottomSheetVisible.value = false
        },
        dragHandle = {  },
        containerColor = Color.White,
    ) {
        val itemCount = viewModel.listOfItems.size
        val characterStats = viewModel.calculateCharacterFrequency(viewModel.listOfItems.map { it.title })

        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
            ) {
                Text(
                    text = "List 1 ($itemCount items)",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = characterStats.take(3).joinToString("\n") { "${it.first} = ${it.second}" },
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }
    }
}