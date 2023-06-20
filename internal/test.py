from db import DatabaseConnection
import logging

'''
Test for creating users
'''
def testCreateUser(conn):
    try:
        conn.add_new_user("JM_Test", "123456", "Jerry", "Meng", 123456789, "1234 University Ave", "Waterloo", "Ontario", "A1B 2C3", "jerrymeng20@gmail.com")
        rows = conn.exec_DML("SELECT * FROM Users")
        print(rows)
    except Exception:
        logging.fatal("Test_Create_User Failed")
    conn.clear_table("Users")


"""
Test for creating parties
"""
def testCreateParty(conn):
    try:
        conn.add_new_party("100001", "JM_Test_Party", "'2023-10-01 08:00:00'")
        rows = conn.query_party()
        print(rows)
    except Exception:
        logging.fatal("Test_Create_Party Failed")
    conn.clear_table("Parties")


"""
Test for querying parties
"""
def testQueryParty(conn):
    try:
        conn.add_new_party("100001", "JM_Poker_Party", "'2023-10-01 08:00:00'")
        conn.add_new_party("100002", "JM_Chess_Party", "'2023-10-04 09:00:00'")
        conn.add_new_party("100003", "JM_Music_Party", "'2023-12-01 16:30:00'")
        rows = conn.query_party()
        print(rows)
        rows = conn.query_party(party_name="Chess")
        print(rows)
        rows = conn.query_party(start_date="2023-10-02", end_date="2024-01-01")
        print(rows)
        rows = conn.query_party(start_date="2023-10-05 08:00:00")
        print(rows)
    except Exception:
        logging.fatal("Test_Query_Party Failed")
    conn.clear_table("Parties")


# Hardcoded URL, for POC only
db_url = "postgresql://yanchen:9gAOcPaBx4tJJ3OAsD7G6A@vibees-db-11486.7tt.cockroachlabs.cloud:26257/VIBEES?sslmode=verify-full"
connection = DatabaseConnection(db_url)
# connection.drop_table("Users")
# connection.create_tables()
# testCreateUser(connection)
# testCreateParty(connection)
testQueryParty(connection)

