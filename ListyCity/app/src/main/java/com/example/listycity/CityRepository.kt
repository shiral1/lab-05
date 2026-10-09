package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CityRepository {
    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")
    private val _cities = mutableStateListOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Toronto", "ON")
    )

    val cities: List<City>
        get() = _cities

    init {
        // Listener watches the cities colleciton and updates _cities whenever Firestore changes
        // When Firestore changes:
        // - Clear the local list
        // - Read the city documents
        // - Add them to _cities
        // - Compose then updates the UI

        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _cities.clear()

            snapshot?.documents?.forEach { document ->
                val city = document.toObject(City::class.java)
                if (city != null) {
                    _cities.add(city)
                }
            }
        }
    }

    fun addCity(city: City) {
        // Write to Firestore, which then will trigger the snapshot listener,
        // rebuilding _cities and updating the Compose UI
        citiesRef.document(city.name).set(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        // Use Firestore to find the document being updated
       citiesRef.document(oldCity.name).set(updatedCity)
    }
}