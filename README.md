# beauty_salon_CRM

## Description

Java server-client app to manage a beauty salon.
<br>
<br>
Killer-feature: two-factor authentication (server can send email to person via Jakarta Mail Api).
<br>
<br>
Other features:
* Access levels (admin, master, client).
* Client possibile to book a visit.
* Masters can manage their skills and client booking.
* Admins can create pdf reports based on salon events and manage all the user accounts.
* Passwords and secret keys are stored in encrypted form.

### Used tools

#### Maven-based architecture

#### Server
* [Dotenv Java](https://mvnrepository.com/artifact/io.github.cdimascio/dotenv-java)
* [Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok)
* [Log4j](https://mvnrepository.com/artifact/log4j/log4j)
* [Gson](https://mvnrepository.com/artifact/com.google.code.gson/gson) (extension: [Gson Javatime Serialisers](https://mvnrepository.com/artifact/com.fatboyindustrial.gson-javatime-serialisers/gson-javatime-serialisers))
* [Hibernate](https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core)
* [PostgreSQL JDBC Driver](https://mvnrepository.com/artifact/org.postgresql/postgresql)
* [JBCrypt](https://mvnrepository.com/artifact/org.mindrot/jbcrypt)
* [Jakarta Mail](https://mvnrepository.com/artifact/jakarta.mail/jakarta.mail-api)
* [JUnit](https://mvnrepository.com/artifact/junit/junit)

#### Client
* [JavaFX FXML](https://mvnrepository.com/artifact/org.openjfx/javafx-fxml)
* [Dotenv Java](https://mvnrepository.com/artifact/io.github.cdimascio/dotenv-java)
* [Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok)
* [Gson](https://mvnrepository.com/artifact/com.google.code.gson/gson) (extension: [Gson Javatime Serialisers](https://mvnrepository.com/artifact/com.fatboyindustrial.gson-javatime-serialisers/gson-javatime-serialisers))
* [IText Core](https://mvnrepository.com/artifact/com.itextpdf/itextpdf)

_License: MIT_
