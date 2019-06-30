package rebite.ro.rebiteapp.users

import com.google.firebase.firestore.PropertyName
import rebite.ro.rebiteapp.users.restaurants.SimpleLocation

class RestaurantUserCreatorRequest {
    @PropertyName("email")
    public lateinit var email: String

    @PropertyName("display_name")
    public lateinit var display_name: String

    @PropertyName("description")
    public lateinit var description: String

    @PropertyName("image")
    public lateinit var image: String

    @PropertyName("location")
    public lateinit var location: SimpleLocation

    @PropertyName("address")
    public lateinit var address: String
}