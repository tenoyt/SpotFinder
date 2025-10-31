package com.example.spotfinder.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.spotfinder.model.Location

/**
 * Database helper class for managing the locations database
 * Handles creation, version management, and CRUD operations for location data
 */
class LocationDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "SpotFinder.db"
        private const val DATABASE_VERSION = 1

        // Table and column names
        const val TABLE_LOCATIONS = "locations"
        const val COLUMN_ID = "id"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }

    /**
     * Called when the database is created for the first time
     * Creates the locations table and populates it with 100 GTA locations
     */
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_LOCATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ADDRESS TEXT NOT NULL,
                $COLUMN_LATITUDE REAL NOT NULL,
                $COLUMN_LONGITUDE REAL NOT NULL
            )
        """.trimIndent()

        db?.execSQL(createTableQuery)

        // Populate database with 100 GTA locations
        db?.let { populateInitialData(it) }
    }

    /**
     * Called when the database needs to be upgraded
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATIONS")
        onCreate(db)
    }

    /**
     * Populates the database with 100 locations across the Greater Toronto Area
     */
    private fun populateInitialData(db: SQLiteDatabase) {
        val locations = listOf(
            // Downtown Toronto (15 locations)
            Location(0, "Union Station, Toronto", 43.6452, -79.3806),
            Location(0, "CN Tower, Toronto", 43.6426, -79.3871),
            Location(0, "Rogers Centre, Toronto", 43.6414, -79.3894),
            Location(0, "Ripley's Aquarium, Toronto", 43.6424, -79.3860),
            Location(0, "Toronto Eaton Centre, Toronto", 43.6544, -79.3807),
            Location(0, "St. Lawrence Market, Toronto", 43.6487, -79.3716),
            Location(0, "City Hall, Toronto", 43.6534, -79.3839),
            Location(0, "Harbourfront Centre, Toronto", 43.6388, -79.3817),
            Location(0, "Distillery District, Toronto", 43.6503, -79.3598),
            Location(0, "Yonge-Dundas Square, Toronto", 43.6561, -79.3802),
            Location(0, "Art Gallery of Ontario, Toronto", 43.6536, -79.3925),
            Location(0, "Royal Ontario Museum, Toronto", 43.6677, -79.3948),
            Location(0, "Casa Loma, Toronto", 43.6780, -79.4094),
            Location(0, "Scotiabank Arena, Toronto", 43.6435, -79.3791),
            Location(0, "Financial District, Toronto", 43.6489, -79.3817),

            // Scarborough (15 locations)
            Location(0, "Scarborough Town Centre", 43.7764, -79.2577),
            Location(0, "Scarborough Bluffs Park", 43.7118, -79.2363),
            Location(0, "Toronto Zoo", 43.8206, -79.1847),
            Location(0, "Rouge National Urban Park", 43.8233, -79.1450),
            Location(0, "Scarborough GO Station", 43.7742, -79.2574),
            Location(0, "Centennial College Progress Campus", 43.7855, -79.2269),
            Location(0, "Thomson Memorial Park", 43.7672, -79.2322),
            Location(0, "Guild Park and Gardens", 43.7484, -79.1944),
            Location(0, "Agincourt Mall", 43.7866, -79.2811),
            Location(0, "Cedarbrae Mall", 43.7617, -79.2228),
            Location(0, "Scarborough Civic Centre", 43.7733, -79.2577),
            Location(0, "L'Amoreaux Park", 43.8038, -79.3067),
            Location(0, "Morningside Park", 43.7861, -79.1941),
            Location(0, "Port Union Waterfront Park", 43.7831, -79.1317),
            Location(0, "Scarborough Village", 43.7447, -79.2148),

            // Mississauga (15 locations)
            Location(0, "Square One Shopping Centre", 43.5933, -79.6424),
            Location(0, "Mississauga Celebration Square", 43.5933, -79.6441),
            Location(0, "Port Credit", 43.5501, -79.5837),
            Location(0, "Lakeside Park, Mississauga", 43.5503, -79.5843),
            Location(0, "Kariya Park", 43.5886, -79.6401),
            Location(0, "Erin Mills Town Centre", 43.5517, -79.7395),
            Location(0, "Mississauga Valley Park", 43.5698, -79.6079),
            Location(0, "Credit Valley Hospital", 43.5826, -79.6666),
            Location(0, "Clarkson GO Station", 43.5168, -79.6387),
            Location(0, "Streetsville Memorial Park", 43.5848, -79.7180),
            Location(0, "Meadowvale Town Centre", 43.5965, -79.7506),
            Location(0, "Heartland Town Centre", 43.6383, -79.7373),
            Location(0, "Living Arts Centre", 43.5900, -79.6441),
            Location(0, "Mississauga City Hall", 43.5893, -79.6441),
            Location(0, "Burnhamthorpe Library", 43.5914, -79.6451),

            // Brampton (10 locations)
            Location(0, "Brampton City Hall", 43.6834, -79.7614),
            Location(0, "Bramalea City Centre", 43.7309, -79.7619),
            Location(0, "Gage Park, Brampton", 43.6833, -79.7537),
            Location(0, "Chinguacousy Park", 43.7064, -79.7394),
            Location(0, "Heart Lake Conservation Area", 43.6956, -79.8053),
            Location(0, "Professor's Lake", 43.6988, -79.7728),
            Location(0, "Brampton GO Station", 43.6831, -79.7595),
            Location(0, "Shoppers World Brampton", 43.6993, -79.7370),
            Location(0, "Trinity Common Mall", 43.6658, -79.7789),
            Location(0, "Brampton Gateway Terminal", 43.6841, -79.7599),

            // Markham (10 locations)
            Location(0, "Markham Civic Centre", 43.8561, -79.3370),
            Location(0, "Pacific Mall, Markham", 43.8267, -79.3028),
            Location(0, "Markville Shopping Centre", 43.8607, -79.3316),
            Location(0, "Main Street Markham", 43.8753, -79.2620),
            Location(0, "Toogood Pond Park", 43.8689, -79.3324),
            Location(0, "Aaniin Community Centre", 43.8556, -79.3355),
            Location(0, "Unionville Main Street", 43.8478, -79.3101),
            Location(0, "Milliken Mills Park", 43.8308, -79.2964),
            Location(0, "Rouge Valley Conservation Centre", 43.8442, -79.2428),
            Location(0, "Markham Museum", 43.9006, -79.3633),

            // Ajax (8 locations)
            Location(0, "Ajax GO Station", 43.8508, -79.0364),
            Location(0, "Ajax Town Hall", 43.8537, -79.0365),
            Location(0, "Ajax Waterfront Park", 43.8692, -79.0183),
            Location(0, "Greenwood Conservation Area", 43.8508, -79.0530),
            Location(0, "Paradise Beach", 43.8619, -79.0138),
            Location(0, "Ajax Community Centre", 43.8508, -79.0368),
            Location(0, "Rotary Park Ajax", 43.8647, -79.0200),
            Location(0, "Ajax Public Library", 43.8502, -79.0370),

            // Pickering (7 locations)
            Location(0, "Pickering Town Centre", 43.8384, -79.0893),
            Location(0, "Pickering GO Station", 43.8358, -79.0893),
            Location(0, "Pickering Civic Complex", 43.8337, -79.0893),
            Location(0, "Frenchman's Bay", 43.8356, -79.0747),
            Location(0, "Pickering Recreation Complex", 43.8364, -79.0683),
            Location(0, "Beachfront Park Pickering", 43.8247, -79.0891),
            Location(0, "Rougemount Park", 43.8142, -79.1336),

            // Oshawa (10 locations)
            Location(0, "Oshawa Centre", 43.8971, -78.8658),
            Location(0, "Oshawa City Hall", 43.8971, -78.8658),
            Location(0, "Parkwood Estate", 43.9169, -78.8342),
            Location(0, "Oshawa GO Station", 43.8680, -78.8474),
            Location(0, "Lakeview Park, Oshawa", 43.8657, -78.8419),
            Location(0, "Canadian Automotive Museum", 43.8947, -78.8687),
            Location(0, "Oshawa Valley Botanical Gardens", 43.9350, -78.8650),
            Location(0, "Tribute Communities Centre", 43.8994, -78.8629),
            Location(0, "Durham College", 43.9447, -78.8967),
            Location(0, "Oshawa Public Libraries", 43.8971, -78.8658),

            // North York (10 locations)
            Location(0, "Yorkdale Shopping Centre", 43.7253, -79.4522),
            Location(0, "York University", 43.7735, -79.5019),
            Location(0, "North York Centre", 43.7677, -79.4163),
            Location(0, "Mel Lastman Square", 43.7677, -79.4150),
            Location(0, "Black Creek Pioneer Village", 43.7676, -79.5179),
            Location(0, "Edwards Gardens", 43.7279, -79.3583),
            Location(0, "Fairview Mall", 43.7785, -79.3449),
            Location(0, "Toronto Botanical Garden", 43.7279, -79.3625),
            Location(0, "Earl Bales Park", 43.7548, -79.4313),
            Location(0, "Downsview Park", 43.7408, -79.4774)
        )

        // Insert all locations into database
        locations.forEach { location ->
            val values = ContentValues().apply {
                put(COLUMN_ADDRESS, location.address)
                put(COLUMN_LATITUDE, location.latitude)
                put(COLUMN_LONGITUDE, location.longitude)
            }
            db.insert(TABLE_LOCATIONS, null, values)
        }
    }

    /**
     * Adds a new location to the database
     * @param location The location object to add
     * @return The row ID of the newly inserted row, or -1 if an error occurred
     */
    fun addLocation(location: Location): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ADDRESS, location.address)
            put(COLUMN_LATITUDE, location.latitude)
            put(COLUMN_LONGITUDE, location.longitude)
        }
        return db.insert(TABLE_LOCATIONS, null, values)
    }

    /**
     * Retrieves all locations from the database
     * @return List of all locations
     */
    fun getAllLocations(): List<Location> {
        val locations = mutableListOf<Location>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_LOCATIONS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ADDRESS ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val location = Location(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    address = getString(getColumnIndexOrThrow(COLUMN_ADDRESS)),
                    latitude = getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE)),
                    longitude = getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                )
                locations.add(location)
            }
        }
        cursor.close()
        return locations
    }

    /**
     * Searches for locations by address (partial match)
     * @param searchQuery The search string to match against addresses
     * @return List of matching locations
     */
    fun searchLocationsByAddress(searchQuery: String): List<Location> {
        val locations = mutableListOf<Location>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_LOCATIONS,
            null,
            "$COLUMN_ADDRESS LIKE ?",
            arrayOf("%$searchQuery%"),
            null,
            null,
            "$COLUMN_ADDRESS ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val location = Location(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    address = getString(getColumnIndexOrThrow(COLUMN_ADDRESS)),
                    latitude = getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE)),
                    longitude = getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                )
                locations.add(location)
            }
        }
        cursor.close()
        return locations
    }

    /**
     * Updates an existing location in the database
     * @param location The location object with updated information
     * @return The number of rows affected
     */
    fun updateLocation(location: Location): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ADDRESS, location.address)
            put(COLUMN_LATITUDE, location.latitude)
            put(COLUMN_LONGITUDE, location.longitude)
        }
        return db.update(
            TABLE_LOCATIONS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(location.id.toString())
        )
    }

    /**
     * Deletes a location from the database
     * @param id The ID of the location to delete
     * @return The number of rows affected
     */
    fun deleteLocation(id: Int): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_LOCATIONS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * Retrieves a single location by ID
     * @param id The ID of the location to retrieve
     * @return The location object, or null if not found
     */
    fun getLocationById(id: Int): Location? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_LOCATIONS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var location: Location? = null
        if (cursor.moveToFirst()) {
            location = Location(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
            )
        }
        cursor.close()
        return location
    }
}