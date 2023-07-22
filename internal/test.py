from datetime import datetime

from db import DatabaseConnection
from decouple import config
import logging

"""
Helper for creating test users
"""


def createTestUser(conn, username, first_name, last_name, email, uid=None):
    uid = conn.add_new_user(username, "TestPassword", first_name, last_name, "12345678", "123 University Ave",
                            "Waterloo",
                            "ON", "A1B 2C3", email, uid)
    return uid


'''
Test for creating users
'''


def testCreateUser(conn):
    print("********** TEST_CREATE_USER STARTS **********")
    try:
        uid = conn.add_new_user("JM_Test", "123456", "Jerry", "Meng", 123456789, "1234 University Ave", "Waterloo",
                                "Ontario", "A1B 2C3", "jerrymeng20@gmail.com")
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
        # Create a party with all the fields
        party_name = "JM_Test_Party"
        date_time = "'2023-10-01 08:00:00'"
        max_capacity = 50
        description = "This is a test party"
        thumbnail = b"\x01\x02\x03\x04\x05"  # Example byte array for thumbnail
        # Example list of byte arrays for photos
        photos = [b"\x10\x20\x30", b"\x40\x50\x60"]
        entry_fee = 10

        conn.add_new_party(party_name, date_time, max_capacity,
                           description, thumbnail, photos, entry_fee)

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
        uid1 = createTestUser(conn, "JM_Test_User_1",
                              "Jerry", "Meng", "jerry1@gmail.com")
        uid2 = createTestUser(conn, "JM_Test_User_2",
                              "Jerry", "Meng", "jerry2@gmail.com")
        uid3 = createTestUser(conn, "JM_Test_User_3",
                              "Jerry", "Meng", "jerry3@gmail.com")
        pid = conn.add_new_party("Test Party", "'2023-09-05 12:30:00'", 10, "Test Party Description",
                                 b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 10)
        conn.host_party(uid1, pid)
        conn.add_new_transaction(uid2, uid1, pid, 5.00, "NULL")
        conn.add_new_transaction(uid3, uid1, pid, 4.50, "NULL")
        rows = conn.query_transaction()
        print(rows)

    except Exception as e:
        logging.fatal("ERROR: TEST_CREATE_TRANS FAILED")
        logging.fatal(e)
    conn.clear_table("Users")
    conn.clear_table("Parties")
    print("********** TEST_CREATE_TRANS ENDS **********")


"""
Test for querying users
"""


def testQueryUser(conn):
    print("********** TEST_QUERY_USER STARTS **********")
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1",
                              "Jerry", "Meng", "jerry1@gmail.com")
        createTestUser(conn, "JM_Test_User_2", "YC",
                       "Meng", "jerry2@gmail.com")
        createTestUser(conn, "JM_Test_User_3", "Jerry",
                       "Meng", "jerrymeng3@gmail.com")
        createTestUser(conn, "YC_Test_User_4", "YC",
                       "Meng", "ym2023@gmail.com")
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
Test for setting tags
"""


def testSetTags(conn):
    try:
        party_id = conn.add_new_party("JM_Bowling_Party", "'2023-07-02 14:30:00'", 60, "Bowling Night",
                                      b"\x01\x01\x01\x01\x01", [b"\x11\x22\x33", b"\x44\x55\x66"], 45)

        conn.set_tags(party_id, ["Bowling", "Pizza"])
        rows = conn.exec_DML("SELECT * FROM Tags")
        print(rows)

        conn.set_tags(party_id, ["Bowling", "Pizza", "Root Beer"])
        rows = conn.exec_DML("SELECT * FROM Tags")
        print(rows)

    except Exception as e:
        logging.fatal("Test_Set_Tags Failed")
        logging.fatal(e)
    conn.clear_table("Tags")
    conn.clear_table("Parties")


"""
Test for setting party locations
"""


def testSetLocation(conn):
    try:
        party_id = conn.add_new_party("Test Party", "'2023-09-05 12:30:00'", 10, "Test Party Description",
                                      b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 10)

        conn.set_location(party_id, "123 Main St", "City", "Province", "12345")
        rows = conn.exec_DML("SELECT * FROM PartyLocations")
        print(rows)

        conn.set_location(party_id, "321 Main St", "City", "Province", "12345")
        rows = conn.exec_DML("SELECT * FROM PartyLocations")
        print(rows)

    except Exception as e:
        logging.fatal("Test_Set_Location Failed")
        logging.fatal(e)
    conn.clear_table("PartyLocations")
    conn.clear_table("Parties")


"""
Test for setting music suggestions
"""


def testSetSuggestions(conn):
    try:
        user_id = createTestUser(
            conn, "JM_Test_User_1", "Jerry", "Meng", "jerry1@gmail.com")
        party_id = conn.add_new_party("Test Party", "'2023-09-05 12:30:00'", 10, "Test Party Description",
                                      b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 10)
        conn.attend_party(user_id, party_id)

        conn.set_suggestions(user_id, party_id, [
                             "Track 1", "Track 2", "Track 3"])
        rows = conn.exec_DML("SELECT * FROM MusicSuggestions")
        print(rows)

        conn.set_suggestions(user_id, party_id, [
                             "Track 4", "Track 5", "Track 6", "Track 7"])
        rows = conn.exec_DML("SELECT * FROM MusicSuggestions")
        print(rows)

        conn.set_suggestions(user_id, party_id, [
                             "Track 4", "Track 5", "Track 6", "Track 7"])
        rows = conn.exec_DML("SELECT * FROM MusicSuggestions")
        print(rows)

    except Exception as e:
        logging.fatal("Test_Set_Suggestions Failed")
        logging.fatal(e)
    conn.clear_table("MusicSuggestions")
    conn.clear_table("Parties")


"""
Test for querying parties
"""


def testQueryParty(conn):
    try:
        # Create parties with various attributes
        conn.add_new_party("JM_Poker_Party", "'2023-10-01 08:00:00'", 30, "Poker Night", b"\x01\x02\x03\x04\x05",
                           [b"\x10\x20\x30"], 20)
        conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'", 20, "Chess Tournament", b"\x05\x04\x03\x02\x01",
                           [b"\x30\x20\x10"], 15)
        conn.add_new_party("JM_Music_Party", "'2023-12-01 16:30:00'", 40, "Music Festival", b"\x01\x01\x01\x01\x01",
                           [b"\x11\x22\x33", b"\x44\x55\x66"], 30)
        # Query parties with different constraints
        print("Test 1")
        rows = conn.query_party()
        print(rows)

        print("Test 2")
        rows = conn.query_party(party_name="Chess")
        print(rows)

        print("Test 3")
        rows = conn.query_party(start_date="2023-10-02",
                                end_date="2024-01-01", max_capacity=25)
        print(rows)

        print("Test 4")
        rows = conn.query_party(start_date="2023-09-05 08:00:00", entry_fee=20)
        print(rows)
    except Exception as e:
        logging.fatal("Test_Query_Party Failed")
        logging.fatal(e)
    conn.clear_table("Parties")


"""
Test for querying tags
"""


def testQueryTags(conn):
    try:
        conn.add_new_party("JM_Poker_Party", "'2023-10-01 20:00:00'", 30, "Poker Night", b"\x01\x02\x03\x04\x05",
                           [b"\x10\x20\x30"], 20)
        conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'", 20, "Chess Tournament", b"\x05\x04\x03\x02\x01",
                           [b"\x30\x20\x10"], 15)
        conn.add_new_party("JM_Music_Party", "'2023-12-01 21:30:00'", 40, "Music Festival", b"\x01\x01\x01\x01\x01",
                           [b"\x11\x22\x33", b"\x44\x55\x66"], 30)

        rows = conn.query_party()
        party_id = conn.query_party(party_name="JM_Poker_Party")[0][0]
        conn.set_tags(party_id, ["Poker", "Game", "Night"])

        party_id = conn.query_party(party_name="JM_Chess_Party")[0][0]
        conn.set_tags(party_id, ["Chess", "Game", "Morning", "Tournament"])

        party_id = conn.query_party(party_name="JM_Music_Party")[0][0]
        conn.set_tags(party_id, ["Music", "DJ", "Night", "Festival"])

        print("Test 1")
        rows = conn.query_tags()
        print(rows)

        print("Test 2")
        rows = conn.query_tags(tag_subset=["Game"])
        print(rows)

        print("Test 3")
        rows = conn.query_tags(tag_subset=["Night"])
        print(rows)

        print("Test 4")
        rows = conn.query_tags(tag_subset=["Game", "Tournament"])
        print(rows)

    except Exception as e:
        logging.fatal("Test_Query_Tags Failed")
        logging.fatal(e)
    conn.clear_table("Tags")
    conn.clear_table("Parties")


"""
Test for querying locations
"""


def testQueryLocations(conn):
    try:
        conn.add_new_party("JM_Poker_Party", "'2023-10-01 20:00:00'", 30, "Poker Night", b"\x01\x02\x03\x04\x05",
                           [b"\x10\x20\x30"], 20)
        conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'", 20, "Chess Tournament", b"\x05\x04\x03\x02\x01",
                           [b"\x30\x20\x10"], 15)
        conn.add_new_party("JM_Music_Party", "'2023-12-01 21:30:00'", 40, "Music Festival", b"\x01\x01\x01\x01\x01",
                           [b"\x11\x22\x33", b"\x44\x55\x66"], 30)

        rows = conn.query_party()
        party_id = conn.query_party(party_name="JM_Poker_Party")[0][0]
        conn.set_location(party_id, "Street1", "City1", "Prov1", "PostC1")

        party_id = conn.query_party(party_name="JM_Chess_Party")[0][0]
        conn.set_location(party_id, "Street2", "City2", "Prov2", "PostC2")

        party_id = conn.query_party(party_name="JM_Music_Party")[0][0]
        conn.set_location(party_id, "Street1", "City2", "Prov1", "PostC2")

        print("Test 1")
        rows = conn.query_locations()
        print(rows)

        print("Test 2")
        rows = conn.query_locations(street="Street1")
        print(rows)

        print("Test 3")
        rows = conn.query_locations(city="City2")
        print(rows)

        print("Test 4")
        rows = conn.query_locations(prov="Prov1", postal_code="PostC2")
        print(rows)

    except Exception as e:
        logging.fatal("Test_Query_Locations Failed")
        logging.fatal(e)
    conn.clear_table("PartyLocations")
    conn.clear_table("Parties")


"""
Test for querying suggestions
"""


def testQuerySuggestions(conn):
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1",
                              "Jerry", "Meng", "jerry1@gmail.com")
        uid2 = createTestUser(conn, "JM_Test_User_2",
                              "Jerry", "Meng", "jerry2@gmail.com")
        pid1 = conn.add_new_party("JM_Poker_Party", "'2023-10-01 20:00:00'", 30, "Poker Night", b"\x01\x02\x03\x04\x05",
                                  [b"\x10\x20\x30"], 20)
        pid2 = conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'", 20, "Chess Tournament", b"\x05\x04\x03\x02\x01",
                                  [b"\x30\x20\x10"], 15)
        pid3 = conn.add_new_party("JM_Music_Party", "'2023-12-01 21:30:00'", 40, "Music Festival", b"\x01\x01\x01\x01\x01",
                                  [b"\x11\x22\x33", b"\x44\x55\x66"], 30)

        conn.attend_party(uid1, pid1)
        conn.attend_party(uid2, pid2)
        conn.attend_party(uid2, pid3)

        conn.set_suggestions(uid1, pid1, ["Music1", "Music2"])

        conn.set_suggestions(uid2, pid2, ["Music3", "Music4"])

        conn.set_suggestions(uid2, pid3, ["Music1", "Music3"])

        print("Test 1")
        rows = conn.query_suggestions()
        print(rows)

        print("Test 2")
        rows = conn.query_suggestions(guest_id=uid1)
        print(rows)

        print("Test 3")
        rows = conn.query_suggestions(track_subset=["Music1"])
        print(rows)

        print("Test 4")
        rows = conn.query_suggestions(guest_id=uid2, track_subset=["Music4"])
        print(rows)

    except Exception as e:
        logging.fatal("Test_Query_Suggestions Failed")
        logging.fatal(e)
    conn.clear_table("MusicSuggestions")
    conn.clear_table("Parties")


"""
Test for querying transactions
"""


def testQueryTransaction(conn):
    print("********** TEST_QUERY_TRANS STARTS **********")
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1",
                              "Jerry", "Meng", "jerry1@gmail.com")
        uid2 = createTestUser(conn, "JM_Test_User_2",
                              "Jerry", "Meng", "jerry2@gmail.com")
        uid3 = createTestUser(conn, "JM_Test_User_3",
                              "Jerry", "Meng", "jerry3@gmail.com")
        pid = conn.add_new_party("Test Party", "'2023-09-05 12:30:00'", 10, "Test Party Description",
                                 b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 10)
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
    conn.clear_table("Parties")
    print("********** TEST_QUERY_TRANS ENDS **********")


"""
Test for attending parties
"""


def testAttendParties(conn):
    print("********** TEST_ATTEND_PARTIES STARTS **********")
    try:
        uid1 = createTestUser(conn, "JM_Test_User_1",
                              "Jerry", "Meng", "jerry1@gmail.com")
        uid2 = createTestUser(conn, "JM_Test_User_2",
                              "Jerry", "Meng", "jerry2@gmail.com")
        pid = conn.add_new_party("Test Party", "'2023-09-05 12:30:00'", 10, "Test Party Description",
                                 b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 10)
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
        uid1 = createTestUser(conn, "JM_Test_User_1",
                              "Jerry", "Meng", "jerry1@gmail.com")
        pid1 = conn.add_new_party("Test Party 1", "'2023-09-05 12:30:00'", 10, "Test Party 1 Description",
                                  b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 10)
        pid2 = conn.add_new_party("Test Party 2", "'2023-09-06 12:30:00'", 20, "Test Party 2 Description",
                                  b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 20)
        conn.host_party(uid1, pid1)
        rows = conn.show_hosted_parties(uid1, show_detail=True)
        print(rows)
        rows = conn.show_host(pid1, show_detail=True)
        print(rows)
        uid2 = createTestUser(conn, "JM_Test_User_2",
                              "Jerry", "Meng", "jerry2@gmail.com")
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
db_url = config("CDB_URL")
connection = DatabaseConnection(db_url)
# connection.create_tables()
# breakpoint()
# connection.drop_table("Users")
# connection.drop_table("Parties")
# connection.drop_table("Transactions")
# connection.drop_table("MusicSuggestions")
# connection.drop_table("Guests")
# connection.create_tables()
# breakpoint()

# print(connection.query_user(user_id="9c261503-02d2-4fd4-8399-3e905de39588"))
# print(connection.query_party(hosted_by="5bdfc21f-ea15-43b3-9654-093f15d63ba7", show_detail=False)[0])
# print(connection.show_hosted_parties("5bdfc21f-ea15-43b3-9654-093f15d63ba7", show_detail=True))
# print(connection.show_attendees(party_id="04afd1a3-9be3-469a-9f10-bc5937d68de4", show_detail=True))
# print(connection.add_new_user("NULL", "JM_Test_User_1", "JM_Test_User_1", 5198884567, "200 University Ave",
#                               "Waterloo", "ON", "A1B 2C3", "jerry_test_1@gmail.com"))
# print(connection.exec_attend_party("9c261503-02d2-4fd4-8399-3e905de39588", "04afd1a3-9be3-469a-9f10-bc5937d68de4", 5.0))
# print(connection.show_attended_parties("9c261503-02d2-4fd4-8399-3e905de39588", show_detail=True))
# print(connection.show_attended_parties("6515c9f8-57f1-406f-8707-20033dcd764e", show_detail=True))
# print(connection.exec_host_party("JM_Test_Party", "NULL", datetime.now(), "9c261503-02d2-4fd4-8399-3e905de39588",
#                                  50, "A test party", 15.0, "123 Lester St", "Waterloo", "ON", "N2L 3H8", ["Tag"]))
# print(connection.query_party(tag_subset=["EDM"]))
