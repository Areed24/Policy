# PolicyService

### To build the project and run all corresponding test cases:
```
./gradlew clean build
```

### Description:
The policy service manages the policy lifecycle from creation to completion. A policy is created in this service by listening to events from the Customer Application Service. The policy's state transitions are driven via RESTful endpoints from user input, in lieu of an event stream.
