from db import DatabaseConnection
import logging

'''
Test for creating users
'''
def testCreateUser(conn):
    print("********** TEST_CREATE_USER STARTS **********")
    try:
        uid = conn.add_new_user("JM_Test", "123456", "Jerry", "Meng", 123456789, "1234 University Ave", "Waterloo", "Ontario", "A1B 2C3", "jerrymeng20@gmail.com")
        rows = conn.exec_DML("SELECT * FROM Users")
        print(uid)
        print(rows)
    except Exception as e:
        logging.fatal("ERROR: TEST_CREATE_USER FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    print("********** TEST_CREATE_USER ENDS **********")


"""
Test for creating parties
"""
def testCreateParty(conn):
    print("********** TEST_CREATE_PARTY STARTS **********")
    try:
        pid = conn.add_new_party("JM_Test_Party", "2023-10-01 08:00:00")
        rows = conn.query_party()
        print(pid)
        print(rows)
    except Exception as e:
        logging.fatal("ERROR: TEST_CREATE_PARTY FAILED")
        logging.fatal(e)
    conn.clear_table("Parties")
    print("********** TEST_CREATE_PARTY ENDS **********")


"""
Test for querying parties
"""
def testQueryParty(conn):
    print("********** TEST_QUERY_PARTY STARTS **********")
    try:
        pid1 = conn.add_new_party("JM_Poker_Party", "2023-10-01 08:00:00")
        conn.add_new_party("JM_Chess_Party", "2023-10-04 09:00:00")
        conn.add_new_party("JM_Music_Party", "2023-12-01 16:30:00")
        rows = conn.query_party()
        print(rows)
        rows = conn.query_party(party_id=pid1)
        print(rows)
        rows = conn.query_party(party_name="Chess")
        print(rows)
        rows = conn.query_party(start_date="2023-10-02", end_date="2024-01-01")
        print(rows)
        rows = conn.query_party(start_date="2023-10-05 08:00:00")
        print(rows)
    except Exception as e:
        logging.fatal("ERROR: TEST_QUERY_PARTY FAILED")
        logging.fatal(e)
    conn.clear_table("Parties")
    print("********** TEST_QUERY_PARTY ENDS **********")


"""
Helper for creating test users
"""
def createTestUser(conn, username, first_name, last_name, email, uid=None):
    uid = conn.add_new_user(username, "TestPassword", first_name, last_name, "12345678", "123 University Ave", "Waterloo",
                            "ON", "A1B 2C3", email, uid)
    return uid


"""
Test for querying users
"""
def testQueryUser(conn):
    print("********** TEST_QUERY_USER STARTS **********")
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        createTestUser(conn, "JM_Test_User_2", "YC", "Meng", "jerry2@gmail.com")
        createTestUser(conn, "JM_Test_User_3", "Jerry", "Meng", "jerrymeng3@gmail.com")
        createTestUser(conn, "YC_Test_User_4", "YC", "Meng", "ym2023@gmail.com")
        rows = conn.query_user()
        print(rows)
        rows = conn.query_user(user_id=uid1)
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
        logging.fatal("ERROR: TEST_QUERY_USER FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    print("********** TEST_QUERY_USER ENDS **********")


"""
Test for adding user as a guest
"""
def testAddGuest(conn):
    print("********** TEST_ADD_GUEST STARTS **********")
    try:
        uid = createTestUser(conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        pid = conn.add_new_party("JM_Poker_Party", "2023-10-01 08:00:00")
        conn.attend_party(uid, pid)
        rows = conn.exec_DML("SELECT u.username, p.party_name FROM Guests g "
                             "JOIN Users u ON g.guest_id = u.user_id "
                             "JOIN Parties p ON g.party_id = p.party_id")
        print(rows)
    except Exception as e:
        logging.fatal("ERROR: TEST_ADD_GUEST FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    conn.clear_table("Parties")
    conn.clear_table("Guests")
    print("********** TEST_ADD_GUEST ENDS **********")


"""
Test for adding user as a host
"""
def testAddHost(conn):
    print("********** TEST_ADD_HOST STARTS **********")
    try:
        uid = createTestUser(conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        pid = conn.add_new_party("JM_Poker_Party", "2023-10-01 08:00:00")
        conn.host_party(uid, pid)
        rows = conn.exec_DML("SELECT u.username, p.party_name FROM Hosts h "
                             "JOIN Users u ON h.host_id = u.user_id "
                             "JOIN Parties p ON h.party_id = p.party_id")
        print(rows)
    except Exception as e:
        logging.fatal("ERROR: TEST_ADD_HOST FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    conn.clear_table("Parties")
    conn.clear_table("Hosts")
    print("********** TEST_ADD_HOST ENDS **********")



# Hardcoded URL, for POC only
db_url = "postgresql://yanchen:9gAOcPaBx4tJJ3OAsD7G6A@vibees-db-11486.7tt.cockroachlabs.cloud:26257/VIBEES?sslmode=verify-full"
connection = DatabaseConnection(db_url)
# connection.drop_table("Accounts")
# connection.create_tables()
# testCreateUser(connection)
# testCreateParty(connection)
# testQueryParty(connection)
# testQueryUser(connection)
# testAddGuest(connection)
testAddHost(connection)

