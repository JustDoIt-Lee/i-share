import androidx.navigation.NavController

fun NavController.navigateSingleTopTo(route: String) {
    if (currentDestination?.route != route) {
        this.navigate(route) {
            launchSingleTop = true
        }
    }
}
