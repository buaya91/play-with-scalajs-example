
# Classic snake game in Scala-js


### Flow

1. Fetch data from firebase
2. Create snake for current user
3. Fire event to notify other user
4. Play on

## Next to do
1. Test and fix collision
2. Implement init from cloud
3. Implement and fix communication
    1. Get once from cloud
    2. write whenever events happen
    3. React from cloud changes

## Events

Global | Local
------ | ------
Snake Added | Collision
Apple Eaten |
Direction Changed |
Snake Removed |


## Event handling design choices

Treat events from server and events to server separately
this will

1. Events generated internally, to be broadcast to server
2. Events received from server
