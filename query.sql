--Query to get Ingredients associated with a specific menu item
SELECT mi.Name AS MenuItemName, ii.Name AS IngredientName
FROM MenuItemIngredients mii
JOIN MenuItems mi ON mii.MenuItemID = mi.MenuItemID
JOIN IngredientsInventory ii ON mii.IngredientID = ii.IngredientID
WHERE mi.Name = 'Double Stack Burger';

SELECT
    EXTRACT(WEEK FROM Date) AS WeekNumber,
    COUNT(*) AS OrderCount
FROM Transactions
GROUP BY WeekNumber
ORDER BY WeekNumber;
