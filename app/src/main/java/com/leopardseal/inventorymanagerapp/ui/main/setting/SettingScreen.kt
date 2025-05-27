package com.leopardseal.inventorymanagerapp.ui.main.setting

@Composable
fun SettingScreen(
    viewModel = SettingViewModel = hiltViewModel(),
    onUnauthorized : () -> Unit
){
    val versionResource by viewModel.versionResponse.collectAsState()

    when(versionResource){
        is Resource.Success ->{
            Text("Server version : ${versionResource.value}")
        }
        is Resource.Loading, Resource.Init -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Failure -> {
            val error = orgsResource as Resource.Failure
            LaunchedEffect(error) {
                if (error.isNetworkError) {
                    Toast.makeText(context, "Please check your internet and try again", Toast.LENGTH_LONG).show()
                } else if (error.errorCode == HttpStatus.SC_UNAUTHORIZED) {
                    onUnauthorized()
                } else {
                    Toast.makeText(context, "An error occurred, please try again later", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    

}