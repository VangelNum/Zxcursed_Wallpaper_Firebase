package com.zxcursed.wallpaper.feature_watch_photo.presentation

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.zxcursed.wallpaper.feature_favourite.domain.model.FavouritePhotosEntity
import com.zxcursed.wallpaper.feature_favourite.presentation.ViewModelForFavourite


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WatchPhotoScreen(
    viewModelWatchPhotoViewModel: WatchPhotoViewModel,
    url: String?,
    scaffoldState: BottomSheetScaffoldState,
    viewModelForFavourite: ViewModelForFavourite = hiltViewModel()
) {

    val items = listOf(
        BottomSheet.Favourite,
        BottomSheet.Share,
        BottomSheet.Download,
    )

    val favourites = viewModelForFavourite.allFavouritePhotos.value

    val photoInFavourite = remember {
        mutableStateOf(false)
    }

    photoInFavourite.value = favourites.data.toString().contains(url!!)
    val context = LocalContext.current

    BottomSheetScaffold(
        scaffoldState = scaffoldState, sheetContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Spacer(modifier = Modifier.weight(3f))
                Divider(
                    modifier = Modifier
                        .height(4.dp)
                        .weight(1f)
                )
                Spacer(modifier = Modifier.weight(3f))
            }
            items.forEachIndexed { index, current ->
                ListItem(
                    text = {
                        if (index == 0) {
                            if (photoInFavourite.value) {
                                Text(text = stringResource(id = BottomSheet.FavouriteDelete.name))
                            } else {
                                Text(text = stringResource(id = current.name))
                            }
                        } else {
                            Text(text = stringResource(id = current.name))
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = current.icon),
                            contentDescription = "icon",
                            tint = if (photoInFavourite.value && index == 0) Color.Red else Color.White
                        )
                    },
                    modifier = Modifier.clickable {
                        when (index) {
                            0 -> {
                                if (!photoInFavourite.value) {
                                    viewModelForFavourite.addFavouritePhoto(
                                        FavouritePhotosEntity(
                                            url
                                        )
                                    )
                                } else {
                                    viewModelForFavourite.deleteFavouritePhoto(url)
                                }
                            }

                            1 -> {
                                share(url, context = context)
                            }

                            2 -> {
                                download(url, context = context)
                            }
                        }
                    }
                )
            }
        }, sheetElevation = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        sheetPeekHeight = 36.dp,
        drawerElevation = 0.dp
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, bottom = 46.dp, top = 10.dp),
            shape = RoundedCornerShape(15.dp),
        ) {
            SubcomposeAsyncImage(
                model = url,
                contentDescription = "photo",
                contentScale = ContentScale.Crop,
            ) {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    viewModelWatchPhotoViewModel.triggerStates(0F, 0F)
                } else {
                    val height = painter.intrinsicSize.height
                    val width = painter.intrinsicSize.width
                    viewModelWatchPhotoViewModel.triggerStates(height, width)
                    SubcomposeAsyncImageContent()
                }
            }
        }
    }


}

private fun download(url: String?, context: Context) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("Downloading")
        request.setMimeType("image/*")
        request.setTitle("File")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "photo.png"
        )
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        manager!!.enqueue(request)
    } catch (e: Exception) {
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
    }
}


private fun share(url: String?, context: Context) {
    try {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
    }
}
