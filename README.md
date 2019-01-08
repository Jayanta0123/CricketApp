# CricketApp
This is a simple Cricket-application, which has the following features
1. Display the real-time list of International, first-class and regional cricket matches which are going to take place in near future.
2. A total of 125/126 matches will be displayed.
3. Hardly 10 to 20 matches will have a status of matchStarted as "true", rest all will have matchStarted as "false".
4. For the matches where the matchStarted is true, user will be able to view their real-time score summary - like scores of both the teams including all the innings played in the match.
5. The winner information though the user has to find out from the score-summary, it doesn't come in the original JSON response.


# Softwares
This is entirely a spring-boot application. So as an user, you will need the following softwares installed in your machine
1. Java 1.8
2. Maven (for application build)

# Steps to be followed to Use the App
1. Download/Clone the project in your local machine. If you download, unzip and go the root directory; if you do a git-clone, clone and go to the project directory
2. Run the following statements
3. mvn clean install
4. java -jar target/CricketApp-0.0.1-SNAPSHOT.jar (you can use tab key to get the jar file)
5. Open http://localhost:5280/ in your browser (the application  will run on port 5280)
