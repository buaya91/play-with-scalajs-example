# Classic snake game in Scala-js

# TODO

- [x] Add speed up
- [x] Server reconcilation
- [ ] Client prediction
- [ ] Add AI
- [ ] Refactor frontend with react
- [ ] Evaluate webrtc

## Server Reconcilation
Server will maintain n frame of game state as buffer, when receive 
client input which happen before the latest frame sent out, server will
check if there is corresponding frameNo in buffer, 
 - If yes, take all frame after the frameNo inclusively, apply the input until we get the next frame to sent, 
   return latest gameState together with frameNo
 - If no, compute latest gameState using last frame and return together with frameNo

### Implication
Every frame will take longer time to process
Input that is too slow will be dropped

## Client Prediction
Client will have a self-paced routine, which to be run once client receive
the first gamestate from server. 

- every prediction will be stored in a buffer
- every game state from server will be stored in another buffer
- on each prediction frame, we will check if latest frame received is same as we predicted
  - if yes, we ditch all frame buffer before that, and continue
  - if no, things get ugly, 
    - if player is predicted wrongly, make it back to correct place
    - if opponent is predicted wrongly, place them back

### Server side high level over view

### Client side high level over view
Input => Server State update || User input
Output => Game Command update || Graphic render

Internal => 
- Identified Game Input - aggregated by server assigned ID + User Input
- Client prediction - self-adjusted timer + latest game state + game logic

One time for whole lifetime =>
- Assigned ID

One time of each frame =>
- User input

Buffer =>
- Unacknowledged Predicted frameNo
- Server state in future (highly unlikely but let's do it for the sake)

