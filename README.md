# Simple Proxy of TFL Bus arrivals
## Description
This project takes the full structure returned from the TFL StopPoint endpoint and simplifies and sorts the reponse into arrival order. 
I built this for use on low power ESP devices to remove the overhead of dealing with the larger json objects and sorting. 

**This is still very WIP**

# Aims
- Provide the bare minimum of data required to recreate TFL bus stop screens
- Sort the results into arrival order
- Have the ability to change the stop being returned (my unique use case)
- Ability to rename stops and destinations to match smaller screen real-estat

## Endpoints
*Note: A basic authentication system has been added that requires the "API-Key" header to be populated and match the server confirguration value.*
- allArrivals - Returns all upcoming arrivals for the currently configured stop in arrival order
- arrivals/{stop_id} - Returns the upcming arrivals for a specific stop in arrival order
- changeCurrentStop/{stop_id} - Changes the current stored stop (used for devices without external interfaces)

## Functionality
- A default stop is set via the main configuration and will be reverted to on server restart.
- If the current stop is changed it will automatically revert to the default after a configurable interval.
- The names of both destinations and stops can be adjusted via the configuration.

## Planned Changes
- [ ] Abaility to store current stop per unique identifier
- [ ] Caching of recently retrieved TFL data
- [ ] Ability to add mapping to helper functions (stop/destination descriptions)
