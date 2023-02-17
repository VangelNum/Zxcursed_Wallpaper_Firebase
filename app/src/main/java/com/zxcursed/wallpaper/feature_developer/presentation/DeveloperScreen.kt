package com.zxcursed.wallpaper.feature_developer.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.zxcursed.wallpaper.R


@Composable
fun DeveloperScreen(viewModel: DeveloperViewModel = hiltViewModel()) {

    val resource = viewModel.allUsersPhotosForDeveloper.value
    UsersImages(viewModel, allUsersPhotos = resource)

}


@Composable
fun UsersImages(
    viewModel: DeveloperViewModel,
    allUsersPhotos: DeveloperState,
) {


    if (allUsersPhotos.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    if (allUsersPhotos.error.isNotBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = allUsersPhotos.error)
        }
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(allUsersPhotos.data) { collectPhotos ->
            if (collectPhotos.url.isNotEmpty()) {
                collectPhotos.url.forEach { onePhoto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        SubcomposeAsyncImage(
                            model = onePhoto,
                            contentDescription = "photos",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(),
                            contentScale = ContentScale.Crop,
                            error = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_error_24),
                                    contentDescription = "error"
                                )
                            }
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    viewModel.updateUserPhotosDev(onePhoto, collectPhotos)
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_upload_24),
                                        contentDescription = "upload",
                                        modifier = Modifier.size(30.dp),
                                        tint = Color.Red
                                    )
                                }
                                IconButton(onClick = {
                                    viewModel.deleteUserPhotosDev(onePhoto, collectPhotos)
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                                        contentDescription = "delete",
                                        modifier = Modifier.size(30.dp),
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = stringResource(id = R.string.link) +" "+ onePhoto)
                    Text(text = stringResource(id = R.string.email) + " " + collectPhotos.email)
                    Text(text = stringResource(id = R.string.uid) + " " + collectPhotos.userId)
                    Text(text = stringResource(id = R.string.score) + " " + collectPhotos.score)

                }
            }


        }
    }
}