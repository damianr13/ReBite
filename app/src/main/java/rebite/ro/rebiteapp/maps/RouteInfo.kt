package rebite.ro.rebiteapp.maps

import com.google.maps.model.DirectionsResult
import com.google.maps.model.Duration

class RouteInfo(var duration: Duration) {
    constructor(directionsResult: DirectionsResult) : this(directionsResult.routes[0].legs[0].duration)
}

interface RouteListener {
    fun onRouteRetrieved(routeInfo: RouteInfo)
}