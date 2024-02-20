menu_items = [
    {"MenuItemID": 1, "Name": "Bacon Cheeseburger", "Price": 8.29},
    {"MenuItemID": 2, "Name": "Classic Hamburger", "Price": 6.89},
    {"MenuItemID": 3, "Name": "Double Stack Burger", "Price": 9.99},
    {"MenuItemID": 5, "Name": "Gig Em Patty Melt", "Price": 7.59},
    {"MenuItemID": 6, "Name": "Cheeseburger", "Price": 6.89},
    {"MenuItemID": 7, "Name": "Black Bean Burger", "Price": 8.38},
    {"MenuItemID": 9, "Name": "Rev's Grilled Chicken Sandwich", "Price": 8.39},
    {"MenuItemID": 10, "Name": "Spicy Chicken Sandwich", "Price": 8.39},
    {"MenuItemID": 11, "Name": "Aggie Chicken Club", "Price": 8.39},
    {"MenuItemID": 12, "Name": "2 Corn Dog Value Meal", "Price": 4.99},
    {"MenuItemID": 13, "Name": "2 Hot Dog Value Meal", "Price": 4.99},
    {"MenuItemID": 14, "Name": "3 Tender Entree", "Price": 4.99},
    {"MenuItemID": 15, "Name": "3 Chicken Tender Combo", "Price": 7.99},
    {"MenuItemID": 16, "Name": "French Fries", "Price": 1.99},
    {"MenuItemID": 17, "Name": "Aggie Shakes", "Price": 4.49},
    {"MenuItemID": 18, "Name": "Cookie Ice Cream Sundae", "Price": 4.69},
    {"MenuItemID": 19, "Name": "Double Scoop Ice Cream", "Price": 3.29},
    {"MenuItemID": 20, "Name": "Root Beer Float", "Price": 5.49},
    {"MenuItemID": 21, "Name": "Fountain Drink", "Price": 1.99},
    {"MenuItemID": 22, "Name": "Bottled Water", "Price": 2.19},
]

ingredients_inventory = [
    {"IngredientID": 1, "Name": "Beef Patties",
        "Stock": 100, "MaxStock": 200, "Units": "Pieces"},
    {"IngredientID": 2, "Name": "Bacon", "Stock": 50,
        "MaxStock": 100, "Units": "Slices"},
    {"IngredientID": 3, "Name": "Cheddar Cheese",
        "Stock": 40, "MaxStock": 80, "Units": "Slices"},
    {"IngredientID": 4, "Name": "Lettuce",
        "Stock": 20, "MaxStock": 40, "Units": "Heads"},
    {"IngredientID": 5, "Name": "Tomato", "Stock": 30,
        "MaxStock": 60, "Units": "Slices"},
    {"IngredientID": 6, "Name": "Onion", "Stock": 25,
        "MaxStock": 50, "Units": "Slices"},
    {"IngredientID": 7, "Name": "Pickles", "Stock": 15,
        "MaxStock": 30, "Units": "Slices"},
    {"IngredientID": 8, "Name": "Burger Buns",
        "Stock": 100, "MaxStock": 200, "Units": "Pieces"},
    {"IngredientID": 9, "Name": "Ketchup", "Stock": 10,
        "MaxStock": 20, "Units": "Bottles"},
    {"IngredientID": 10, "Name": "Mustard", "Stock": 10,
        "MaxStock": 20, "Units": "Bottles"},
    {"IngredientID": 11, "Name": "Rye Bread",
        "Stock": 50, "MaxStock": 100, "Units": "Slices"},
    {"IngredientID": 12, "Name": "Swiss Cheese",
        "Stock": 40, "MaxStock": 80, "Units": "Slices"},
    {"IngredientID": 13, "Name": "Black Beans",
        "Stock": 10, "MaxStock": 20, "Units": "Cans"},
    {"IngredientID": 14, "Name": "Avocado",
        "Stock": 20, "MaxStock": 40, "Units": "Pieces"},
    {"IngredientID": 15, "Name": "Vegan Mayo",
        "Stock": 5, "MaxStock": 10, "Units": "Jars"},
    {"IngredientID": 16, "Name": "Chicken Breast",
        "Stock": 60, "MaxStock": 120, "Units": "Pieces"},
    {"IngredientID": 17, "Name": "Spicy Mayo",
        "Stock": 5, "MaxStock": 10, "Units": "Jars"},
    {"IngredientID": 18, "Name": "Cornmeal",
        "Stock": 10, "MaxStock": 20, "Units": "Bags"},
    {"IngredientID": 19, "Name": "Hot Dogs",
        "Stock": 50, "MaxStock": 100, "Units": "Pieces"},
    {"IngredientID": 20, "Name": "Hot Dog Buns",
        "Stock": 50, "MaxStock": 100, "Units": "Pieces"},
    {"IngredientID": 21, "Name": "Breading Mix",
        "Stock": 10, "MaxStock": 20, "Units": "Bags"},
    {"IngredientID": 22, "Name": "Cooking Oil",
        "Stock": 20, "MaxStock": 40, "Units": "Liters"},
    {"IngredientID": 23, "Name": "Potatoes",
        "Stock": 30, "MaxStock": 60, "Units": "Pounds"},
    {"IngredientID": 24, "Name": "Ice Cream",
        "Stock": 50, "MaxStock": 100, "Units": "Pints"},
    {"IngredientID": 25, "Name": "Milk", "Stock": 20,
        "MaxStock": 40, "Units": "Gallons"},
    {"IngredientID": 26, "Name": "Flavor Syrup",
        "Stock": 10, "MaxStock": 20, "Units": "Bottles"},
    {"IngredientID": 27, "Name": "Chocolate Chip Cookies",
        "Stock": 30, "MaxStock": 60, "Units": "Packets"},
    {"IngredientID": 28, "Name": "Chocolate Syrup",
        "Stock": 5, "MaxStock": 10, "Units": "Bottles"},
    {"IngredientID": 29, "Name": "Whipped Cream",
        "Stock": 10, "MaxStock": 20, "Units": "Cans"},
    {"IngredientID": 30, "Name": "Root Beer",
        "Stock": 20, "MaxStock": 40, "Units": "Bottles"},
    {"IngredientID": 31, "Name": "Carbonated Water",
        "Stock": 50, "MaxStock": 100, "Units": "Liters"},
    {"IngredientID": 32, "Name": "Flavor Syrup for Drinks",
        "Stock": 10, "MaxStock": 20, "Units": "Bottles"},
    {"IngredientID": 33, "Name": "Water", "Stock": 100,
        "MaxStock": 200, "Units": "Bottles"}
]


menu_item_ingredients = [
    # Ingredients for Bacon Cheeseburger
    {"MenuItemName": "Bacon Cheeseburger",
        "IngredientName": "Beef Patties", "Quantity": 1, "Units": "Pieces"},
    {"MenuItemName": "Bacon Cheeseburger",
        "IngredientName": "Bacon", "Quantity": 2, "Units": "Slices"},
    {"MenuItemName": "Bacon Cheeseburger",
        "IngredientName": "Cheddar Cheese", "Quantity": 1, "Units": "Slices"},
    # Add more ingredients for Bacon Cheeseburger...

    # Ingredients for Classic Hamburger
    {"MenuItemName": "Classic Hamburger", "IngredientName": "Beef Patties",
        "Quantity": 1, "Units": "Pieces"},
    {"MenuItemName": "Classic Hamburger",
        "IngredientName": "Lettuce", "Quantity": 1, "Units": "Heads"},
    {"MenuItemName": "Classic Hamburger",
        "IngredientName": "Tomato", "Quantity": 1, "Units": "Slice"},
    # Add more ingredients for Classic Hamburger...

    # Ingredients for Double Stack Burger
    {"MenuItemName": "Double Stack Burger",
        "IngredientName": "Beef Patties", "Quantity": 2, "Units": "Pieces"},
    {"MenuItemName": "Double Stack Burger",
        "IngredientName": "Cheddar Cheese", "Quantity": 2, "Units": "Slices"},
    # Add more ingredients for Double Stack Burger...

    # Continue adding entries for each menu item and their ingredients...
]

# Lookup dictionary to convert Item Names to ID's for storage in database
menu_item_id_lookup = {item['Name']: item['MenuItemID'] for item in menu_items}
ingredient_id_lookup = {
    ingredient['Name']: ingredient['IngredientID'] for ingredient in ingredients_inventory}
