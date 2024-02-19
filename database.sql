CREATE TABLE Employees (
    EmployeeID INT PRIMARY KEY,
    Name VARCHAR(255),
    Wage FLOAT,
    Role VARCHAR(255)
);

CREATE TABLE MenuItems (
    MenuItemID INT PRIMARY KEY,
    Name VARCHAR(255),
    Price FLOAT
);

CREATE TABLE IngredientsInventory (
    IngredientID INT PRIMARY KEY,
    Name VARCHAR(255),
    Stock FLOAT,
    MaxStock FLOAT,
    Units VARCHAR(255)
);

CREATE TABLE Transactions (
    TransactionID INT PRIMARY KEY,
    EmployeeID INT,
    Total FLOAT,
    Date DATE,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
);

CREATE TABLE TransactionEntry (
    MenuItemID INT,
    TransactionID INT,
    FOREIGN KEY (MenuItemID) REFERENCES MenuItems(MenuItemID),
    FOREIGN KEY (TransactionID) REFERENCES Transactions(TransactionID)
);

CREATE TABLE MenuItemIngredients (
    MenuItemID INT,
    IngredientID INT,
    Quantity FLOAT,
    Units VARCHAR(255),
    FOREIGN KEY (MenuItemID) REFERENCES MenuItems(MenuItemID),
    FOREIGN KEY (IngredientID) REFERENCES IngredientsInventory(IngredientID)
);
