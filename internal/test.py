from db import DatabaseConnection

'''
Test for creating users
'''
def testCreateUser(conn):
    conn.add_new_user("JM_Test", "123456", "Jerry", "Meng", 123456789, "1234 University Ave", "Waterloo", "Ontario", "A1B 2C3", "jerrymeng20@gmail.com")
    rows = conn.exec_DML("SELECT * FROM Users")
    print(rows)
    conn.exec_DDL("DELETE FROM Users")


"""
Test for creating parties
"""
def testCreateParty(conn):
    conn.add_new_party("100001", "JM_Test_Party", "'2023-10-01 08:00:00'")
    rows = conn.exec_DML("SELECT * FROM Parties")
    print(rows)
    conn.exec_DDL("DELETE FROM Parties")


# Hardcoded URL, for POC only
db_url = "postgresql://yanchen:9gAOcPaBx4tJJ3OAsD7G6A@vibees-db-11486.7tt.cockroachlabs.cloud:26257/VIBEES?sslmode=verify-full"
connection = DatabaseConnection(db_url)
# connection.drop_table("Users")
# connection.create_tables()
testCreateUser(connection)
testCreateParty(connection)

