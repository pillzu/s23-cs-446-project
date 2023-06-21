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
    except Exception as e:
        logging.fatal("Test_Create_User Failed")
        logging.fatal(e)
    conn.clear_table("Users")


"""
Test for creating parties
"""
def testCreateParty(conn):
    try:
        conn.add_new_party("JM_Test_Party", "'2023-10-01 08:00:00'")
        rows = conn.query_party()
        print(rows)
    except Exception as e:
        logging.fatal("Test_Create_Party Failed")
        logging.fatal(e)
    conn.clear_table("Parties")


"""
Test for querying parties
"""
def testQueryParty(conn):
    try:
        conn.add_new_party("JM_Poker_Party", "'2023-10-01 08:00:00'")
        conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'")
        conn.add_new_party("JM_Music_Party", "'2023-12-01 16:30:00'")
        rows = conn.query_party()
        print(rows)
        rows = conn.query_party(party_name="Chess")
        print(rows)
        rows = conn.query_party(start_date="2023-10-02", end_date="2024-01-01")
        print(rows)
        rows = conn.query_party(start_date="2023-10-05 08:00:00")
        print(rows)
    except Exception as e:
        logging.fatal("Test_Query_Party Failed")
        logging.fatal(e)
    conn.clear_table("Parties")


"""
Test for querying users
"""
def createTestUser(conn, username, first_name, last_name, email):
    conn.add_new_user(username, "TestPassword", first_name, last_name, "12345678", "123 University Ave", "Waterloo",
                      "ON", "A1B 2C3", email)

def testQueryUser(conn):
    try:
        createTestUser(conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        createTestUser(conn, "JM_Test_User_2", "YC", "Meng", "jerry2@gmail.com")
        createTestUser(conn, "JM_Test_User_3", "Jerry", "Meng", "jerrymeng3@gmail.com")
        createTestUser(conn, "YC_Test_User_4", "YC", "Meng", "ym2023@gmail.com")
        rows = conn.query_user()
        print(rows)
        rows = conn.query_user(username="JM")
        print(rows)
        rows = conn.query_user(username="JM", first_name="YC")
        print(rows)
        rows = conn.query_user(email="jerrymeng")
        print(rows)
        rows = conn.query_user(limit=1)
        print(rows)
    except Exception as e:
        logging.fatal("Test_Query_User Failed")
        logging.fatal(e)
    conn.clear_table("Parties")



# Hardcoded URL, for POC only
db_url = "postgresql://yanchen:9gAOcPaBx4tJJ3OAsD7G6A@vibees-db-11486.7tt.cockroachlabs.cloud:26257/VIBEES?sslmode=verify-full"
connection = DatabaseConnection(db_url)
# connection.drop_table("Parties")
# connection.create_tables()
# testCreateUser(connection)
# testCreateParty(connection)
# testQueryParty(connection)
testQueryUser(connection)

