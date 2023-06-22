from db import DatabaseConnection
import logging

"""
Helper for creating test users
"""
def createTestUser(conn, username, first_name, last_name, email, uid=None):
    uid = conn.add_new_user(username, "TestPassword", first_name, last_name, "12345678", "123 University Ave", "Waterloo",
                            "ON", "A1B 2C3", email, uid)
    return uid


'''
Test for creating users
'''
def testCreateUser(conn):
    print("********** TEST_CREATE_USER STARTS **********")
    try:
        uid = conn.add_new_user("JM_Test", "123456", "Jerry", "Meng", 123456789, "1234 University Ave", "Waterloo", "Ontario", "A1B 2C3", "jerrymeng20@gmail.com")
        print(uid)
        rows = conn.query_user()
        print(rows)
        rows = conn.exec_DML("SELECT * FROM Accounts")
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
        print(pid)
        rows = conn.query_party()
        print(rows)
    except Exception as e:
        logging.fatal("ERROR: TEST_CREATE_PARTY FAILED")
        logging.fatal(e)
    conn.clear_table("Parties")
    print("********** TEST_CREATE_PARTY ENDS **********")


"""
Test for creating transactions
"""
def testCreateTransaction(conn):
    print("********** TEST_CREATE_TRANS STARTS **********")
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        uid2 = createTestUser(conn, "JM_Test_User_2", "Jerry", "Meng", "jerry2@gmail.com")
        uid3 = createTestUser(conn, "JM_Test_User_3", "Jerry", "Meng", "jerry3@gmail.com")
        pid = conn.add_new_party("JM_Poker_Party", "2023-10-01 08:00:00")
        conn.host_party(uid1, pid)
        conn.add_new_transaction(uid2, uid1, pid, 5.00, "NULL")
        conn.add_new_transaction(uid3, uid1, pid, 4.50, "NULL")
        rows = conn.query_transaction()
        print(rows)

    except Exception as e:
        logging.fatal("ERROR: TEST_CREATE_TRANS FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    print("********** TEST_CREATE_TRANS ENDS **********")


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
Test for querying transactions
"""
def testQueryTransaction(conn):
    print("********** TEST_QUERY_TRANS STARTS **********")
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        uid2 = createTestUser(conn, "JM_Test_User_2", "Jerry", "Meng", "jerry2@gmail.com")
        uid3 = createTestUser(conn, "JM_Test_User_3", "Jerry", "Meng", "jerry3@gmail.com")
        pid = conn.add_new_party("JM_Poker_Party", "2023-10-01 08:00:00")
        conn.host_party(uid1, pid)
        trans1 = conn.add_new_transaction(uid2, uid1, pid, 5.00, "NULL")
        trans2 = conn.add_new_transaction(uid3, uid1, pid, 4.50, "NULL")
        rows = conn.query_transaction()
        print(rows)
        rows = conn.query_transaction(trans_id=trans1)
        print(rows)
        rows = conn.query_transaction(from_id=uid2, to_id=uid1)
        print(rows)
        rows = conn.query_transaction(party_id=pid)
        print(rows)
        rows = conn.query_transaction(min_amount=5.00)
        print(rows)
        rows = conn.query_transaction(max_amount=4.50)
        print(rows)

    except Exception as e:
        logging.fatal("ERROR: TEST_QUERY_TRANS FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    print("********** TEST_QUERY_TRANS ENDS **********")


"""
Test for attending parties
"""
def testAttendParties(conn):
    print("********** TEST_ATTEND_PARTIES STARTS **********")
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        uid2 = createTestUser(conn, "JM_Test_User_2", "Jerry", "Meng", "jerry2@gmail.com")
        pid = conn.add_new_party("JM_Poker_Party", "2023-10-01 08:00:00")
        conn.attend_party(uid1, pid)
        conn.attend_party(uid2, pid)
        rows = conn.show_attendees(pid, show_detail=True)
        print(rows)
        conn.leave_party(uid1, pid)
        rows = conn.show_attendees(pid, show_detail=True)
        print(rows)
    except Exception as e:
        logging.fatal("ERROR: TEST_ATTEND_PARTIES FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    conn.clear_table("Parties")
    conn.clear_table("Guests")
    print("********** TEST_ATTEND_PARTIES ENDS **********")


"""
Test for hosting parties
"""
def testHostParties(conn):
    print("********** TEST_HOST_PARTIES STARTS **********")
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        pid1 = conn.add_new_party("JM_Poker_Party", "2023-10-01 08:00:00")
        pid2 = conn.add_new_party("JM_Chess_Party", "2023-10-04 09:00:00")
        conn.host_party(uid1, pid1)
        rows = conn.show_hosted_parties(uid1, show_detail=True)
        print(rows)
        uid2 = createTestUser(conn, "JM_Test_User_2", "Jerry", "Meng", "jerry2@gmail.com")
        conn.attend_party(uid2, pid1)
        conn.attend_party(uid2, pid2)
        rows = conn.show_attended_parties(uid2, show_detail=True)
        print(rows)
        conn.cancel_party(pid1)
        rows = conn.show_attended_parties(uid2, show_detail=True)
        print(rows)
        rows = conn.show_hosted_parties(uid1, show_detail=True)
        print(rows)
    except Exception as e:
        logging.fatal("ERROR: TEST_HOST_PARTIES FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    conn.clear_table("Parties")
    conn.clear_table("Guests")
    print("********** TEST_HOST_PARTIES ENDS **********")


# Hardcoded URL, for POC only
db_url = "postgresql://yanchen:9gAOcPaBx4tJJ3OAsD7G6A@vibees-db-11486.7tt.cockroachlabs.cloud:26257/VIBEES?sslmode=verify-full"
connection = DatabaseConnection(db_url)
# connection.drop_table("Accounts")
# connection.create_tables()
# testCreateUser(connection)
# testCreateParty(connection)
# testCreateTransaction(connection)
# testQueryParty(connection)
# testQueryUser(connection)
# testQueryTransaction(connection)
# testAttendParties(connection)
# testHostParties(connection)

