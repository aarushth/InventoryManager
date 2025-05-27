package com.leopardseal.inventorymanagerapp.ui.main.org

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository : SettingRepository
) : ViewModel(){

    private val _versionResponse = MutableStateFlow<Resource<String>>(Resource.Init)
    val versionResponse : StateFlow<Resource<String>>
        get() = _versionResponse

    init{
        _versionResponse.value = Resource.Loading
        getVersion()
    }

    fun getVersion(){
        _versionResponse.value = repository.getVersion()
    }
}