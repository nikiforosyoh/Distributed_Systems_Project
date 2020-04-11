# Distributed_Systems_Project
A music streaming on demand system based on the Publisher - Broker - Consumer archetype with the use of consistent hashing written with Java 8


*ΟΔΗΓΙΕΣ*

1. Ορίζουμε το πλήθος των Brokers στην κλάση Node στην μεταβλητή N.
2. Σηκώνουμε τους Brokers με τα στοιχεία που υπαρχουν στο brokers.txt 
(τα βαζουμε ως παραμέτρους στον κατασκευαστή στη main).
3. Σηκώνουμε τους Publishers βάζοντας τα γράμματα a,j και k,z αντίστοιχα στον κατασκευαστή στη main και αλλάζοντας κάθε φορά το serverPort. 
(προσοχή να μην ειναι ίδιο με κάποιο απο το brokers.txt) 
4. Σηκώνουμε τους Consumers.
5. Σαν είσοδο δίνουμε πρώτα το όνομα του καλλιτέχνη και μετά τον τίτλο του τραγουδιού.
    (δεν εχει σημασία αν δώσουμε πεζά η κεφαλαία)
    (προσέχουμε να μην βάλουμε κενό στο τέλος του ονόματος του καλλιτέχνη και του τίτλου)
