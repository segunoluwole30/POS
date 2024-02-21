import numpy as np
import random
import datetime

import csv

# Dictionary to hold menu item prices
menu_item_prices = {}

# Read the MenuItems CSV file
with open('menuitems.csv', 'r') as csvfile:  # Ensure this matches your CSV file name/path
    reader = csv.DictReader(csvfile)
    for row in reader:
        menu_item_id = int(row['MenuItemID'])
        price = float(row['Price'])
        menu_item_prices[menu_item_id] = price

# Time ranges (in 24-hour format)
time_ranges = {
    'breakfast': (8, 11),  # 8 AM to 11 AM
    'lunch': (11, 14),  # 11 AM to 2 PM
    'late lunch': (14, 17),  # 2 PM to 5 PM
    'dinner': (17, 20)  # 5 PM to 8 PM
}

# Probabilities for each time range
time_probabilities = {
    'breakfast': 0.1,  # 10% of orders
    'lunch': 0.45,  # 45% of orders
    'late lunch': 0.225,  # 27.5% of orders
    'dinner': 0.225  # 27.5% of orders
}


# Function to choose a time range based on defined probabilities
def choose_time_range():
    ranges = list(time_ranges.keys())
    probabilities = [time_probabilities[r] for r in ranges]
    chosen_range = np.random.choice(ranges, p=probabilities)
    return time_ranges[chosen_range]

# Function to generate a random time within the chosen time range


def generate_random_time_within_range(time_range):
    start_hour, end_hour = time_range
    hour = random.randint(start_hour, end_hour - 1)
    minute = random.randint(0, 59)
    second = random.randint(0, 59)
    return hour, minute, second


# Configuration
# Current date
current_date = datetime.datetime.now()

# Start date set to January 1st of the previous year
start_date = datetime.datetime(current_date.year - 1, 1, 1)

# Calculate the number of days from the start date to today
num_days = (current_date - start_date).days + \
    1  # +1 to include today in the range

total_sales_target = 1_000_000  # Total sales target for the year
employee_ids = [5747, 6930, 2033, 4074, 2057, 9067, 4829, 3867, 6045, 7834]
menu_item_ids = list(range(1, 28))  # MenuItemID values from 1 to 27
min_items_per_transaction = 1
max_items_per_transaction = 5

# Peak days configuration
peak_days = [datetime.datetime(
    start_date.year, 1, 15), datetime.datetime(start_date.year, 8, 30)]
peak_day_sales = 10000  # Increased target sales for peak days
regular_days_sales_target = (
    total_sales_target - (peak_day_sales * len(peak_days))) / (num_days - len(peak_days))


def batch_insert_transactions(transactions):
    transaction_values = ', '.join(transactions)
    return f"INSERT INTO Transactions (TransactionID, EmployeeID, Total, Date) VALUES {transaction_values};"


def batch_insert_entries(entries):
    entry_values = ', '.join(entries)
    return f"INSERT INTO TransactionEntry (TransactionID, MenuItemID) VALUES {entry_values};"


# Open a file to write the SQL statements
with open('transactions.sql', 'w') as file:
    transaction_id = 1
    for day in range(num_days):
        transactions = []
        entries = []
        current_day_date = start_date + datetime.timedelta(days=day)
        daily_sales_target = peak_day_sales if current_day_date in peak_days else regular_days_sales_target
        daily_sales = 0

        while daily_sales < daily_sales_target:
            employee_id = random.choice(employee_ids)
            num_items = random.randint(
                min_items_per_transaction, max_items_per_transaction)
            items_selected = random.choices(
                menu_item_ids, k=num_items)  # select menu items

            # Calculate total based on selected menu items
            total = sum([menu_item_prices[item] for item in items_selected])
            # Choose a time range and generate a random time within that range
            chosen_time_range = choose_time_range()
            random_hour, random_minute, random_second = generate_random_time_within_range(
                chosen_time_range)

            transaction_time = current_day_date.replace(
                hour=random_hour, minute=random_minute, second=random_second).strftime('%Y-%m-%d %H:%M:%S')

            transactions.append(
                f"({transaction_id}, {employee_id}, {total:.2f}, '{transaction_time}')")
            for item in items_selected:
                entries.append(f"({transaction_id}, {item})")

            daily_sales += total
            transaction_id += 1

        if transactions:
            file.write(batch_insert_transactions(transactions) + '\n')
        if entries:
            file.write(batch_insert_entries(entries) + '\n')

print("Optimized transactions have been generated.")
