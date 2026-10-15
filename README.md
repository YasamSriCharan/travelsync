# TravelSync

TravelSync is a Java console application for simple travel discovery, trip planning, service allocation, budget selection, and booking management. It provides a menu-driven experience using a built-in set of Indian travel destinations and activities.

## Features

- Browse destination descriptions, estimated daily costs, and available seats
- Search destinations by name or keyword
- Create a trip-cost estimate for a selected destination and duration
- Allocate travel-service seats
- Select activities within a budget
- View travel insights and a destination recommendation
- Create and view bookings with reservation codes

## Requirements

- JDK 8 or later (JDK 17+ recommended)
- A terminal with `javac` and `java` available on `PATH`

## Run the application

From the project root, compile the source files and start the console app:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\Main.java src\InputHelper.java src\TravelData.java src\BookingManager.java src\TravelSyncEngine.java
java -cp out Main
```

Choose an option from the displayed menu:

| Option | Action |
| --- | --- |
| 1 | View destination information |
| 2 | Search destinations |
| 3 | Plan a trip and estimate its cost |
| 4 | Allocate travel-service seats |
| 5 | Optimise activities for a budget |
| 6 | View travel insights and recommendations |
| 7 | Create a booking |
| 8 | View bookings |
| 0 | Exit |

## Project structure

```text
src/
|- Main.java              # Application entry point and menu
|- TravelSyncEngine.java  # Coordinates application features
|- TravelData.java        # Built-in destinations and activities
|- BookingManager.java    # In-memory booking management
\- InputHelper.java       # Validated console input helpers
```

## Notes

- All data is stored in memory for the duration of the program; bookings are not persisted after exit.
- No external libraries, database, or network connection are required.

