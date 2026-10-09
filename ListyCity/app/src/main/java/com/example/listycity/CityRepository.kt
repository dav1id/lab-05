package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CityRepository {
    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities") // name of the table

    // Explicitly calling the constructor
    init {
        citiesRef.addSnapshotListener{ snapshot, error ->
            if (error != null){
                return@addSnapshotListener
            }

            _cities.clear()


            /*
                Documents is a list of the number of rows in Firestone db -> Uses the .forEach
                lambda function, like in Java, to loop through all the documents and perform ->

                document.toObject() turns each document into a cities object
            */
            snapshot?.documents?.forEach{ document ->
                val city = document.toObject(City::class.java)
                if (city != null){
                    _cities.add(city)
                }
            }
        }
    }

    private val _cities = mutableStateListOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Toronto", "ON")
    )

    val cities: List<City>
        get() = _cities

    fun addCity(city: City) {
        _cities.add(city)

        /*
            Adds a new document (row) to the cities collection. This calls the
            snapshotListener which then updates every city. Clears the cities
        */
        citiesRef.document(city.name).set(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        val index = _cities.indexOf(oldCity)
        if (index != -1) {
            _cities[index] = updatedCity
        }

        // Changes the contents of the document (the row)
        citiesRef.document(oldCity.name).set(updatedCity);
    }

    fun deleteCity(cityName: String){
        citiesRef.document(cityName).delete()
    }
}