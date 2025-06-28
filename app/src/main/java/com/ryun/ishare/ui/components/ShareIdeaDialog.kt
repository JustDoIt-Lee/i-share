package com.ryun.ishare.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ryun.ishare.ui.state.ShareUIState
import com.ryun.ishare.ui.theme.poorstoryFont
import com.ryun.ishare.viewmodel.IdeaListViewModel

@Composable
fun ShareIdeaDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFFFBFB),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // 제목
                Text(
                    text = "아이디어 공유",
                    fontSize = 17.sp,
                    fontFamily = poorstoryFont,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 내용
                Text(
                    text = "이 아이디어를 공유하시겠습니까?",
                    fontSize = 13.sp,
                    fontFamily = poorstoryFont,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 버튼 2개
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFD7FF),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "공유",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "취소",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShareDialogIfNeeded(
    ideaId: String,
    shareUIState: ShareUIState,
    viewModel: IdeaListViewModel
) {
    if (shareUIState.shareDialogIdeaId == ideaId) {
        ShareIdeaDialog(
            onConfirm = {
                viewModel.incrementShareCount(ideaId)
                viewModel.setPendingShareIdeaId(ideaId)
                viewModel.setShareDialogIdeaId(null)
            },
            onDismiss = {
                viewModel.setShareDialogIdeaId(null)
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 200)
@Composable
fun PreviewShareIdeaDialog() {
    MaterialTheme {
        ShareIdeaDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}