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
        # Create a party with all the fields
        party_name = "JM_Test_Party"
        date_time = "'2023-10-01 08:00:00'"
        max_capacity = 50
        description = "This is a test party"
        thumbnail = b"\x01\x02\x03\x04\x05"  # Example byte array for thumbnail
        photos = [b"\x10\x20\x30", b"\x40\x50\x60"]  # Example list of byte arrays for photos
        entry_fee = 10

        conn.add_new_party(party_name, date_time, max_capacity, description, thumbnail, photos, entry_fee)

        rows = conn.query_party()
        print(rows)
    except Exception:
        logging.fatal("Test_Create_Party Failed")
    conn.clear_table("Parties")


"""
Test for setting tags
"""
def testSetTags(conn):
    try:
        conn.add_new_party("JM_Bowling_Party", "'2023-07-02 14:30:00'", 60, "Bowling Night", b"\x01\x01\x01\x01\x01", [b"\x11\x22\x33", b"\x44\x55\x66"], 45)
        party_id = conn.query_party(party_name="JM_Bowling_Party")[0][0]

        conn.set_tags(party_id, ["Bowling", "Pizza"])
        rows = conn.exec_DML("SELECT * FROM Tags")
        print(rows)

        conn.set_tags(party_id, ["Bowling", "Pizza", "Root Beer"])
        rows = conn.exec_DML("SELECT * FROM Tags")
        print(rows)

        
    except Exception:
        logging.fatal("Test_Set_Tags Failed")
    conn.clear_table("Tags")
    conn.clear_table("Parties")

"""
Test for setting party locations
"""  
def testSetLocation(conn):
    try:
        conn.add_new_party("Test Party", "'2023-09-05 12:30:00'", 10, "Test Party Description", b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 10)
        
        party_id = conn.query_party(party_name="Test Party")[0][0]

        conn.set_location(party_id, "123 Main St", "City", "Province", "12345")
        rows = conn.exec_DML("SELECT * FROM PartyLocations")
        print(rows)

        conn.set_location(party_id, "321 Main St", "City", "Province", "12345")
        rows = conn.exec_DML("SELECT * FROM PartyLocations")
        print(rows)
        
    except Exception:
        logging.fatal("Test_Set_Location Failed")
    conn.clear_table("PartyLocations")
    conn.clear_table("Parties")

"""
Test for setting music suggestions
"""
# TODO: Edit the test once the Guests table is complete so that guest_id can be validated as well
def testSetSuggestions(conn):
    try:
        conn.add_new_party("Test Party", "'2023-09-05 12:30:00'", 10, "Test Party Description", b'Thumbnail Data', [b'Photo 1', b'Photo 2'], 10)
        party_id = conn.query_party(party_name="Test Party")[0][0]

        conn.set_suggestions("1", party_id, ["Track 1", "Track 2", "Track 3"])
        rows = conn.exec_DML("SELECT * FROM MusicSuggestions")
        print(rows)

        conn.set_suggestions("2", party_id, ["Track 4", "Track 5", "Track 6", "Track 7"])
        rows = conn.exec_DML("SELECT * FROM MusicSuggestions")
        print(rows)

        conn.set_suggestions("1", party_id, ["Track 4", "Track 5", "Track 6", "Track 7"])
        rows = conn.exec_DML("SELECT * FROM MusicSuggestions")
        print(rows)
        
    except Exception:
        logging.fatal("Test_Set_Suggestions Failed")
    conn.clear_table("MusicSuggestions")
    conn.clear_table("Parties")
"""
Test for querying parties
"""
def testQueryParty(conn):
    try:
        # Create parties with various attributes
        conn.add_new_party("JM_Poker_Party", "'2023-10-01 08:00:00'", 30, "Poker Night", b"\x01\x02\x03\x04\x05", [b"\x10\x20\x30"], 20)
        conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'", 20, "Chess Tournament", b"\x05\x04\x03\x02\x01", [b"\x30\x20\x10"], 15)
        conn.add_new_party("JM_Music_Party", "'2023-12-01 16:30:00'", 40, "Music Festival", b"\x01\x01\x01\x01\x01", [b"\x11\x22\x33", b"\x44\x55\x66"], 30)
        # Query parties with different constraints
        print("Test 1")
        rows = conn.query_party()
        print(rows)

        print("Test 2")
        rows = conn.query_party(party_name="Chess")
        print(rows)

        print("Test 3")
        rows = conn.query_party(start_date="2023-10-02", end_date="2024-01-01", max_capacity=25)
        print(rows)

        print("Test 4")
        rows = conn.query_party(start_date="2023-09-05 08:00:00", entry_fee=20)
        print(rows)
    except Exception:
        logging.fatal("Test_Query_Party Failed")
    conn.clear_table("Parties")

"""
Test for querying tags
"""
def testQueryTags(conn):
    try:
        conn.add_new_party("JM_Poker_Party", "'2023-10-01 20:00:00'", 30, "Poker Night", b"\x01\x02\x03\x04\x05", [b"\x10\x20\x30"], 20)
        conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'", 20, "Chess Tournament", b"\x05\x04\x03\x02\x01", [b"\x30\x20\x10"], 15)
        conn.add_new_party("JM_Music_Party", "'2023-12-01 21:30:00'", 40, "Music Festival", b"\x01\x01\x01\x01\x01", [b"\x11\x22\x33", b"\x44\x55\x66"], 30)

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

    except Exception:
        logging.fatal("Test_Query_Tags Failed")
    conn.clear_table("Tags")
    conn.clear_table("Parties")

"""
Test for querying locations
"""
def testQueryLocations(conn):
    try:
        conn.add_new_party("JM_Poker_Party", "'2023-10-01 20:00:00'", 30, "Poker Night", b"\x01\x02\x03\x04\x05", [b"\x10\x20\x30"], 20)
        conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'", 20, "Chess Tournament", b"\x05\x04\x03\x02\x01", [b"\x30\x20\x10"], 15)
        conn.add_new_party("JM_Music_Party", "'2023-12-01 21:30:00'", 40, "Music Festival", b"\x01\x01\x01\x01\x01", [b"\x11\x22\x33", b"\x44\x55\x66"], 30)

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

    except Exception:
        logging.fatal("Test_Query_Locations Failed")
    conn.clear_table("PartyLocations")
    conn.clear_table("Parties")

"""
Test for querying suggestions
"""
#TODO: Update with actual guest_ids
def testQuerySuggestions(conn):
    try:
        conn.add_new_party("JM_Poker_Party", "'2023-10-01 20:00:00'", 30, "Poker Night", b"\x01\x02\x03\x04\x05", [b"\x10\x20\x30"], 20)
        conn.add_new_party("JM_Chess_Party", "'2023-10-04 09:00:00'", 20, "Chess Tournament", b"\x05\x04\x03\x02\x01", [b"\x30\x20\x10"], 15)
        conn.add_new_party("JM_Music_Party", "'2023-12-01 21:30:00'", 40, "Music Festival", b"\x01\x01\x01\x01\x01", [b"\x11\x22\x33", b"\x44\x55\x66"], 30)

        rows = conn.query_party()
        party_id = conn.query_party(party_name="JM_Poker_Party")[0][0]
        conn.set_suggestions('1', party_id, ["Music1", "Music2"])

        party_id = conn.query_party(party_name="JM_Chess_Party")[0][0]
        conn.set_suggestions('2', party_id, ["Music3", "Music4"])

        party_id = conn.query_party(party_name="JM_Music_Party")[0][0]
        conn.set_suggestions('2', party_id, ["Music1", "Music3"])

        print("Test 1")
        rows = conn.query_suggestions()
        print(rows)

        print("Test 2")
        rows = conn.query_suggestions(guest_id="1")
        print(rows)

        print("Test 3")
        rows = conn.query_suggestions(track_subset=["Music1"])
        print(rows)
        
        print("Test 4")
        rows = conn.query_suggestions(guest_id="2", track_subset=["Music4"])
        print(rows)

    except Exception:
        logging.fatal("Test_Query_Suggestions Failed")
    conn.clear_table("MusicSuggestions")
    conn.clear_table("Parties")    
# Hardcoded URL, for POC only
db_url = "postgresql://yanchen:9gAOcPaBx4tJJ3OAsD7G6A@vibees-db-11486.7tt.cockroachlabs.cloud:26257/VIBEES?sslmode=allow"
#connection = DatabaseConnection(db_url)
#connection.drop_table("hosts")
#connection.drop_table("guests")
#connection.drop_table("transactions")
#connection.drop_table("tags")
#connection.drop_table("partylocations")
#connection.drop_table("musicsuggestions")
#connection.drop_table("Parties")
#breakpoint()
#connection.create_tables()
#breakpoint()
#testCreateUser(connection)
#breakpoint()
#testCreateParty(connection)
#breakpoint()
#testSetTags(connection)
#breakpoint()
#testSetLocation(connection)
#breakpoint()
#testSetSuggestions(connection)
#breakpoint()
#testQueryParty(connection)
#breakpoint()
#testQueryTags(connection)
#breakpoint()
#testQueryLocations(connection)
#breakpoint()
#testQuerySuggestions(connection)
#breakpoint()
