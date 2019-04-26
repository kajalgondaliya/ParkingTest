# Parking System

This application displays the information about the open parking lots available in the user's area

## Users of application

Anyone who wants to see the nearest parking location for their vehicles

### API Used

Google Places API is used to get the parking locations and data for a specific area or place.

### Functionalities:

User starts the application on his device and is shown the splash screen.
After waiting for 1-2 seconds users will see the home screen where three options are located as
	1)Map
	2)Parking history and
	3)Disable fictitious location.

		1) Map
		The parking lots available around a radius of 5 miles of the user's current location is displayed.
		If the user wants to scroll to the full list of parking lots, he/she can click on 'Show list' text displayed at the bottom of the screen and can redirect to that particular parking lot by clicking on the item of the list.
		If the user wants to search parking lots of any area, he/she can search area from search bar displayed on top of the screen.
		When the user enters in any parking lot, parking is allocated to that user and the information of parking such as name, address, parking start time, end time are stored in the database.

		2)Parking history
		In this, if the user has parked in any of the parking lots, then its history will be displayed.

		3)Disable fictitious location
		For optimizing battery doze mode, if the user doesn't continuously want to request updated location, he/she can disable fictitious location. By disabling this, all the parking lots will be available with respect to the user's last latitude and longitude available in the application.

### Other used class:
The location Service class is used to get the user's current location.

### Unit test class:
LocationServiceTest : This class Covers unit test method for getting nearby parking lots and entering in parking lot


