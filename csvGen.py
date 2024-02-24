import csv
from itemDict import *


def write_to_csv(data, filename, fieldnames):
    with open(filename, mode='w', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(data)


menu_items_fieldnames = ['MenuItemID', 'Name', 'Price']

ingredients_fieldnames = ['IngredientID', 'Name', 'Stock', 'MaxStock', 'Units']

# Convert MenuItemName and IngredientName to IDs in menu_item_ingredients
menu_item_ingredients_with_ids = [
    {
        "MenuItemID": menu_item_id_lookup[entry["MenuItemName"]],
        "IngredientID": ingredient_id_lookup[entry["IngredientName"]],
        "Quantity": entry["Quantity"]
    } for entry in menu_item_ingredients if "MenuItemName" in entry and "IngredientName" in entry  # This checks if the keys exist
]

# Fieldnames for MenuItemIngredients
menu_item_ingredients_fieldnames = [
    'MenuItemID', 'IngredientID', 'Quantity']

write_to_csv(menu_items, 'MenuItems.csv', menu_items_fieldnames)
write_to_csv(ingredients_inventory, 'ingredients.csv', ingredients_fieldnames)
write_to_csv(menu_item_ingredients_with_ids, 'MenuItemIngredients.csv',
             menu_item_ingredients_fieldnames)
