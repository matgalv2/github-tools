# GitHub Tools
Simple REST application for listing user's repositories 

### There are 2 branches each representing different approach:
* master - approach utilizing nonblocking WebClient (reactive / immutable structures)
* feign_client - approach utilizing FeignClient (imperative / mutable structures - memory aware)


### Technology stack:
* Java 21
* Spring Boot 3
* WireMock
* Mockito



## API
### Endpoints
    /repos/{username} 
        Path parameters: 
            1. "username" - name of the user from GitHub
        Some of the responses:
            200 - if there is such a user with provided username (body: array[Repository])
            403 - if the requests rate limit was exceeded (60/h unauthorized user)
            404 - if user with provided username was not found
            503 - if the github api is unavailable
            


### Types
    Repository:
        - ownerLogin (string)
        - repositoryName (string)
        - branches (array[Branch])
        - errors (array[Error])

    Branch:
        - name (string)
        - lastCommit (string)

    Error:
        - status (integer)
        - message (string)
