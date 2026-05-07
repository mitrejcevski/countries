//package nl.jovmit.countries.ui.screen
//
//import kotlinx.coroutines.awaitAll
//import kotlinx.coroutines.coroutineScope
//
//class TestCoroutine {
//
//}
//
//// launch - fire & forget
//// async - await - wait for result
//
//Dispatchers
//
//Main - UI
//Default - compute
//IO - api / network (blocking calls: writting to database)
//
//// Runs on thread, but not thread
//
//Scope:
//
//Global
//Activity / Frgament
//ViewModel
//Custom
//SuperviendScope
//
//coroutineScope.launch {
//    fetchData()
//}
//
//val defferedResult = coroutineScope.async {
//    fetchData()
//}
//val data = defferedResult.await()
//
//// multiple api
//val defferedData = coroutineScope.async {
//    fetchData() // 1s
//}
//val defferedInfo = coroutineScope.async {
//    fetchInfo() // 1.5 s
//}
//val data = awaitAll(defferedResult.await(), defferedInfo.await()) // 1.5
//
//// cancel
//
//coroutineScope