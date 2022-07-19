# README file
## Oracle Analytics Coding Challenge
#### the aim is to provide the following requirements. 
* Initialise a vending machine with a given float.
* Register the coins that has been deposited by the user.
* Return the correct coins to the user once the selected product has been vended.
* Provide interactive commandline test-harness to play with the code.

## How to Run the application?
* clone the project https://github.com/bopagec/oracle-vending-machine.git
* mvn clean install
* mvn spring-boot:run ( or run the VendingMachineApplication java main method). This will prompt a `shell:>` for commands inputs

## How to Test on Shell command?
* create a vending machine with custom float of 50
> create --float 50
* list items in the vending machine 
> list
* select an item from the list with it's corresponding item id (ie: id as 4)
> select --id 4
* vend an item with the item id with arrays of corresponding coin denomination id
* in below example customer selected item id 4 and inserted ONE_POUND ONE_POUND FIFTY_PENCE and TWENTY_PENCE (with their ids)
> vend --id 4 --coins 0 0 1 2
* track changes
> track
* see database in h2 in-memory database
   - url`http://localhost:8080/h2`
   - database `jdbc:h2:mem:testdb`
   - username `sa`
   - password (leave blank)
   - see ```TRACKER``` table to track changes
   
## How to test on Postman?
* create a vending machine with custom float of 50
> http://localhost:8080/vending-machine/create/50 (GET request)
* list items in the vending machine
> http://localhost:8080/vending-machine/list (GET request)
* select an item form the list with it's corresponding item id (ie: itemId 7)
> http://localhost:8080/vending-machine/select/7 (GET request)
* vend an item with the `VendRequest`
> http://localhost:8080/vending-machine/vend (POST request)
```json
{
    "itemId": 7,
    "coins":[
        "ONE_POUND", "FIFTY_PENCE", "ONE_POUND", "ONE_POUND", "TEN_PENCE", "TEN_PENCE", "FIFTY_PENCE"
    ]
}
```
* track coin changes
> http://localhost:8080/vending-machine/track (GET erquest)

## Technologies used
* Spring boot 2.6.5
* Java 11
* Spring data jpa (Hibernate)
* Spring web
* Spring Shell Methods 2.1.0-M5 (for test-harness)
* H2 in-memory Database
* Lombok
* JUnit Jupiter
* Maven

## Solution approach
* for solution has been built with spring boot REST API. 
This is because it can be easily integrated with any mobile devices that can be attached to the machine.

## Note
* Please make sure Spring Shell Dependencies of the correct version is installed in order to make the test-harness work.

## Assumptions
* VendingMachine supports only for UK money denominations.
* Only 1 vending machine is installed. We must rerun the application if we want to install a new vending machine
* Customer should insert arrays of coins to vend an item. If not enough money is inserted coins will be ejected and customer should reinsert correctly.
* Vending Machine denominations cash float and min coin count can be changed from the property file.
* When the Vending Machine is created it will break down denominations equally.


