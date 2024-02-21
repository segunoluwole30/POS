import random
import datetime

# Configuration
start_date = datetime.datetime(2023, 1, 1)
num_days = 365  # One year
employee_ids = [5747, 6930, 2033, 4074, 2057, 9067, 4829, 3867, 6045, 7834]
menu_item_ids = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  # Example MenuItemID values
min_transactions_per_day = 1
max_transactions_per_day = 10  # Maximum number of transactions per day
min_items_per_transaction = 1
max_items_per_transaction = 5  # Maximum number of items per transaction
min_transaction_total = 10.00
max_transaction_total = 100.00  # Max total for a transaction


def generate_transaction_sql(transaction_id, employee_id, total, date_time):
    return f"INSERT INTO Transactions (TransactionID, EmployeeID, Total, Date) VALUES ({transaction_id}, {employee_id}, {total}, '{date_time}');"


def generate_transaction_entry_sql(transaction_id, menu_item_id):
    return f"INSERT INTO TransactionEntry (TransactionID, MenuItemID) VALUES ({transaction_id}, {menu_item_id});"


# Open a file to write the SQL statements
with open('transactions.sql', 'w') as file:
    transaction_id = 1  # Initialize transaction ID counter
    for day in range(num_days):
        num_transactions = random.randint(
            min_transactions_per_day, max_transactions_per_day)
        current_date = start_date + datetime.timedelta(days=day)

        for _ in range(num_transactions):
            employee_id = random.choice(employee_ids)
            total = round(random.uniform(
                min_transaction_total, max_transaction_total), 2)
            transaction_time = current_date + \
                datetime.timedelta(hours=random.randint(
                    8, 20), minutes=random.randint(0, 59))

            # Write the transaction SQL statement with timestamp and transaction ID
            transaction_sql = generate_transaction_sql(
                transaction_id, employee_id, total, transaction_time)
            file.write(transaction_sql + '\n')

            # Randomize the number of items in the transaction
            num_items = random.randint(
                min_items_per_transaction, max_items_per_transaction)

            # Write SQL statements for each transaction entry
            for _ in range(num_items):
                menu_item_id = random.choice(menu_item_ids)
                entry_sql = generate_transaction_entry_sql(
                    transaction_id, menu_item_id)
                file.write(entry_sql + '\n')

            transaction_id += 1  # Increment transaction ID for the next transaction

print("Random daily orders have been generated.")
