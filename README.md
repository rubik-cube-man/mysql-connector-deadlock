# MySQL Java Deadlock Test Code

Sample code to replica an issue we're currently seeing in the mysql-connector-j library.

The issue means that closing a connection causes a deadlock whilst active queries are running.


## Running this code

First run:

`docker compose up -d`

(Inside the root directory of the project)

Next run the main class: [MySQLDeadlockTest](src/main/java/com/rubikcubeman/mysql/MySQLDeadlockTest.java)

This may output a bunch of errors (This is normal, I didn't exactly put much effort into this code).
However, if the program fails to exit - it means that it's caught itself in the deadlock. This happens most times.
You can take a thread dump of the program, and you should see the issue.