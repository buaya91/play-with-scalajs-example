## single client flow perspective

- accept conn 
    - generate connId 
    - if rec JoinGame 
        - send AssignedID 
        - start streaming 
    - if disconn 
        - leave game

## Server flow perspective

- init 
    - expect connection
        - conn received
            - save conn to broadcast list
            - start streaming
        - expect req
            - update state

1. hold all connections for broadcast
2. hold all req since last frame
3. timer
4. game logic
