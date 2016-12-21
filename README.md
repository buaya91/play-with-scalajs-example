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
Client should run the game as if it's local, after receiving 1st gamestate from server
running at the same rate as server side.
