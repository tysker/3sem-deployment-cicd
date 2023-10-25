## Deployment CI/CD pipeline (Docker & GitHub Actions)


<img src="image.png" height="468" width="468">


### Part 1 - GitHub workflow pipeline

1. Fork this repository to your own GitHub account and clone it to your local machine
2. Create a folder called .github/workflows
3. Create a new file called workflow.yml
4. Copy the following code into the workflow.yml file

```yaml
name: API JAVALIN WORKFLOW
on:
  push:
    branches: [ main ] // This is the branch that will trigger the workflow on a push
  pull_request:
    branches: [ main ] // This is the branch that will trigger the workflow on a pull request
jobs:
  build:
    runs-on: ubuntu-latest // This is the operating system that will run the workflow
    steps:
      -
        name: Checkout
        uses: actions/checkout@v3 // This is a GitHub action that checks out your repository
      -
        name: Set up JDK 17
        uses: actions/setup-java@v3 // This is a GitHub action that sets up the JDK
        with:
          java-version: '17'
          distribution: 'temurin' // This is the JDK distribution
      -
        name: Build with Maven 
        run: mvn --batch-mode --update-snapshots package // This is the command to build the project
        
```

5. Push the changes to GitHub and go to the actions tab to see the workflow in action

***

### Part 2 - Docker

1. Sign up for a Dockerhub account
2. Create a new repository on Dockerhub
3. Create a new file called Dockerfile in the root of the project
4. Copy the following code into the Dockerfile

```dockerfile
FROM eclipse-temurin:17-alpine
# This is the jar file that you want to run
COPY target/app.jar /app.jar
# This is the port that your javalin application will listen on
EXPOSE 7070
# This is the command that will be run when the container starts
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

5.  Add the following to the workflow.yml file

```yaml
      -
        name: Login to Docker Hub
        uses: docker/login-action@v2 // This is a GitHub action that logs you into Dockerhub
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }} 
          password: ${{ secrets.DOCKERHUB_TOKEN }}
      -
        name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2 // This is a GitHub action that sets up Docker Buildx
      -
        name: Build and push
        uses: docker/build-push-action@v4 // This is a GitHub action that builds and pushes your Docker image
        with:
          context: .
          file: ./Dockerfile // This is the path to your Dockerfile
          push: true 
          tags: ${{ secrets.DOCKERHUB_USERNAME }}/<NAME-OF-DOCKERHUB-REPOSITORY>:<DOCKER-TAG> // This is the name of your Dockerhub repository and the tag you want to give your image
```

6. Add secrets to your GitHub repository

GO TO (GitHub Secret): Settings -> Secrets and variables -> actions -> New repository secret

- key -> DOCKERHUB_USERNAME : value -> your dockerhub username
- key -> DOCKERHUB_TOKEN : value -> your dockerhub token

GO TO (Dockerhub token) : https://hub.docker.com/settings/security -> New Access Token

***

### Part 3 - Deployment with docker compose

1. Clone the ```droplet``` branch on your droplet

**How to clone a specific repository branch**

```bash
    git clone -b <branch_name> <remote_repo>
```

2. Fill out the docker-compose file with the right image name and tag
3. Fill out the db/init.sql file with the right database name
4. Fill out the .env file with the right properties


### How to run the application

1. Run the following command to start the application

```bash
    docker-compose up -d
```

**The first time you may want to run it without the -d flag to see the logs**


### Access the application

```
    <YOUR_DROPLET_IP>:<JAVALIN_PORT>/api/v1/<ENDPOINT>
```







