import random
import datetime

# Configuration
start_date = datetime.datetime(2023, 1, 1)
num_days = 365  # One year
total_sales_target = 1_000_000  # Total sales target for the year
employee_ids = [5747, 6930, 2033, 4074, 2057, 9067, 4829, 3867, 6045, 7834]
menu_item_ids = list(range(1, 28))  # MenuItemID values from 1 to 27
min_items_per_transaction = 1
max_items_per_transaction = 5
min_price_per_item = 2.00
max_price_per_item = 20.00

# Peak days configuration
peak_days = [datetime.datetime(2023, 1, 15), datetime.datetime(2023, 8, 30)]
peak_day_sales = 50000  # Target sales for each peak day
regular_days_sales_target = (
    total_sales_target - (peak_day_sales * len(peak_days))) / (num_days - len(peak_days))


def generate_transaction_sql(transaction_id, employee_id, total, date_time):
    return f"INSERT INTO Transactions (TransactionID, EmployeeID, Total, Date) VALUES ({transaction_id}, {employee_id}, {total}, '{date_time}');"


def generate_transaction_entry_sql(transaction_id, menu_item_id):
    return f"INSERT INTO TransactionEntry (TransactionID, MenuItemID) VALUES ({transaction_id}, {menu_item_id});"


# Open a file to write the SQL statements
with open('transactions_with_peak_days.sql', 'w') as file:
    transaction_id = 1  # Initialize transaction ID counter
    for day in range(num_days):
        current_date = start_date + datetime.timedelta(days=day)
        daily_sales_target = peak_day_sales if current_date in peak_days else regular_days_sales_target
        daily_sales = 0

        while daily_sales < daily_sales_target:
            employee_id = random.choice(employee_ids)
            num_items = random.randint(
                min_items_per_transaction, max_items_per_transaction)
            total = 0

            for _ in range(num_items):
                menu_item_id = random.choice(menu_item_ids)
                item_price = round(random.uniform(
                    min_price_per_item, max_price_per_item), 2)
                total += item_price

            transaction_time = current_date + \
                datetime.timedelta(hours=random.randint(
                    8, 20), minutes=random.randint(0, 59))

            # Write the transaction SQL statement with transaction ID
            transaction_sql = generate_transaction_sql(
                transaction_id, employee_id, total, transaction_time)
            file.write(transaction_sql + '\n')

            for _ in range(num_items):
                menu_item_id = random.choice(menu_item_ids)

                # Write transaction entry SQL statement with the same transaction ID
                entry_sql = generate_transaction_entry_sql(
                    transaction_id, menu_item_id)
                file.write(entry_sql + '\n')

            daily_sales += total
            transaction_id += 1  # Increment transaction ID for the next transaction

print("Random daily orders with peak days have been generated.")
