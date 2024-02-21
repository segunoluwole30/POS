import random
import datetime
from datetime import timedelta

# Configuration
num_transactions = 365  # Assuming one transaction per day for simplicity
employee_ids = [1, 2, 3, 4, 5]  # Example EmployeeID values
total_range = (10.00, 1000.00)  # Min and max for Total
start_date = datetime.date(2023, 1, 1)  # Start of the year

# Function to generate a single transaction's SQL statement


def generate_transaction_sql(transaction_id, employee_id, total, date):
    return f"INSERT INTO Transactions (TransactionID, EmployeeID, Total, Date) VALUES ({transaction_id}, {employee_id}, {total}, '{date}');"


# Generate and print SQL statements
for i in range(num_transactions):
    transaction_id = i + 1  # Simple incrementing ID, adjust as needed
    employee_id = random.choice(employee_ids)  # Randomly select an EmployeeID
    # Random Total within the specified range
    total = round(random.uniform(*total_range), 2)
    # Increment date by one day for each transaction
    date = start_date + timedelta(days=i)

    # Generate the SQL statement
    sql_statement = generate_transaction_sql(
        transaction_id, employee_id, total, date)

    # Print or write the SQL statement to a file
    print(sql_statement)
