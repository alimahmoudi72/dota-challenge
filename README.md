# Bayes Java Dota Challlenge

This is the [task](TASK.md).

## Description


* The Builder pattern is used to build each combat entry extracted from the log file because this pattern separates the construction of a complex object from its representation (the same construction process can create different representations). So I added `@Builder`, `@NoArgsConstructor`, and `@AllArgsConstructor` to `CombatLogEntryEntity.java` class.
  

* The `MatchEntity.java` class features the `addCombatLogEntry` utility method which is used to synchronize both sides of the bidirectional association to persist new MatchEntity.


* It is possible to move `rest.model` classes to another package and use these classes as DTOs to transport values between layers and prevent unnecessary data transformation especially in controller layer.


* Four custom Exceptions `InputIsNotParsableException`, `InternalServerErrorException`, `MatchNotFoundException`, and `NoResultException` with proper  HttpStatus and message and also an appender `ERROR_FILE` config (logback-development.xml) were added to handle different exceptions and log them appropriately.


* `Utils.readFile` method is added as a utility method to read the log file content. 


* Built-in functions in JPA repository are used to fetch combat entries. Data manipulation and desired output production is done in service layer. 


* Simplicity, Readability, and Performance (in cases such as MatchEntity persistence) are considered generally.