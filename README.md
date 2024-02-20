# project-2-315


Command to Login to Database:
    psql -h csce-315-db.engr.tamu.edu -U csce315_903_04_user -d csce315_903_04_db 

Command to view Tables in Database:
    \dt

Command to view attributes in table:
    \d Table_Name

Command to view rows in table:
    SELECT * FROM Table_Name

Command to Create Tables for Database:
    \i path/to file/ (e.g. ../project-2-315/database.sql);

Command to upload csv file to database:
    \copy Table_Name FROM csv file name DELIMTER ',' CSV HEADER;

Command to drop all tables:
     DROP TABLE Employees, MenuItemIngredients, Transactions, TransactionEntry, MenuItems, IngredientsInventory CASCADE;

