"""Add CockroachDB related functions"""
import psycopg2 as pscg
import logging
from datetime import datetime
import pytz


class DatabaseConnection:
    conn = None

    def __init__(self, db_url):
        try:
            self.conn = pscg.connect(db_url,
                                     application_name="$ docs_simplecrud_psycopg2")
        except Exception as e:
            logging.fatal("Database Connection Failed")
            logging.fatal(e)

    """
    create_query_statement(main_query, sub_queries): Create a SQL query statement by concatenating sub_queries 
    then joining main_query
        Parameters:
            - main_query: the main query body
            - sub_queries: sub-queries that should be concatenated using AND
        Returns:
            - row: The query result
            - None: If the query present no result
    """
    def __create_query_statement(self, main_query, sub_queries):
        if len(sub_queries) == 0:
            return main_query

        connector = " WHERE"
        stmt = main_query
        for q in sub_queries:
            stmt += connector + q
            connector = " AND"

        return stmt
    
    """
    exec_DDL(stmt): Executes the given DDL statement.
        Parameters:
            - stmt: the executed DDL statement
        Returns:
            - True: if stmt is valid and queried successfully
            - False: otherwise
        Throws Exception if:
            - the statement has format error
    """
    def exec_DDL(self, stmt) -> bool:
        try:
            with self.conn.cursor() as cur:
                cur.execute(stmt)
                self.conn.commit()
            return True
        except Exception as e:
            logging.fatal("Query Execution Failed")
            logging.fatal(f"Last Executed Query: {stmt}")
            logging.fatal(e)
            return False


    """
    exec_DML(stmt, limit): Executes the given DML statement.
        Parameters: 
            - stmt: the executed DML statement
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result, if stmt is queried successfully
            - None: otherwise
        Throws Exception if:
            - the statement has format error
    """
    def exec_DML(self, stmt, limit=50):
        try:
            with self.conn.cursor() as cur:
                cur.execute(stmt)
                self.conn.commit()
                return cur.fetchmany(limit)
        except Exception as e:
            logging.fatal("Query Execution Failed")
            logging.fatal(f"Last Executed Query: {stmt}")
            logging.fatal(e)
            return None


    """
    add_new_user(username, password, first_name, last_name, phone_no, address, email): Insert a new user with the
    given information into Users table. The user will be created with a default party point of 0.
        Parameters: 
            - username: the user's account username (String with len <= 20)
            - password: the user's account password (String with len <= 20)
            - first_name: the user's first name (String with len <= 20)
            - last_name: the user's last name (String with len <= 20)
            - phone_no: the user's phone number (Long)
            - address_*: the user's home address (street, city, prov, postal code)
            - email: the user's email address (String with len <= 50)
            - uid: the user's uuid. If left blank then will use gen_random_uuid() (Integer)
        Returns:
            - True: if the user is inserted successfully
            - False: otherwise
        Throws Exception if:
            - the user's information may have invalid fields
    """
    def add_new_user(self, username, password, first_name, last_name, phone_no, address_street, address_city, address_prov,
                     address_postal, email, uid=None) -> bool:
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            if uid is None:
                statement = f"INSERT INTO Users VALUES (gen_random_uuid(), '{username}', '{password}', '{first_name}', " \
                            f"'{last_name}', {phone_no}, '{address_street}', '{address_city}', '{address_prov}', " \
                            f"'{address_postal}', '{email}', 0)"
            else:
                statement = f"INSERT INTO Users VALUES ('{uid}', '{username}', '{password}', '{first_name}', " \
                            f"'{last_name}', {phone_no}, '{address_street}', '{address_city}', '{address_prov}', " \
                            f"'{address_postal}', '{email}', 0)"

            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Adding new user to database failed")
            logging.fatal(e)
            return False


    """
    add_new_party(party_id, party_name, date_time, max_capacity, description, thumbnail, photos, entry_fee): 
    Insert a new party entry with the given information into the Parties table. 
    The party's creation time will be filled as the current system time.
    
    Parameters: 
        - party_id: The party's id number (integer)
        - party_name: The party's name (string)
        - date_time: The scheduled date of the party (datetime)
        - max_capacity: The maximum capacity of the guests in the party (integer)
        - description: The description of the party, entered by the host (string)
        - thumbnail: The thumbnail of the party, submitted by the host (bytes)
        - photos: A list of photos displayed in the info page of the party (list of bytes)
        - entry_fee: The entry fee of the party (integer)
    
    Returns:
        - True: If the party is inserted successfully
        - False: Otherwise
        
    Throws Exception if:
        - The party's information may have invalid fields
    """
    def add_new_party(self, party_name, date_time, max_capacity, description, thumbnail, photos, entry_fee, party_id=None):
   
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            timez = pytz.timezone("Canada/Eastern")
   
            photos = str(photos)[1:-1]
            
            if party_id is None:
                statement = f"INSERT INTO Parties " \
                            f"VALUES (gen_random_uuid(), '{party_name}', {date_time}, '{datetime.now(timez)}', " \
                            f"{max_capacity}, '{description}', {thumbnail}, " \
                            f"ARRAY[{photos}], {entry_fee})"
            else:
                statement = f"INSERT INTO Parties " \
                            f"VALUES ('{party_id}', '{party_name}', {date_time}, '{datetime.now(timez)}', " \
                            f"{max_capacity}, '{description}', {thumbnail}, " \
                            f"ARRAY[{photos}], {entry_fee})"
            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Adding new party to database failed")
            logging.fatal(e)
            return False


    """
    set_tags(party_id, tag_list): Sets the tags for a given party in the Tags table.
        Parameters:
            - party_id: the ID of the party associated with the tag list
            - tag_list: the list of tags to be associated with the party
        Returns:
            - True: if the tag list is inserted successfully
            - False: otherwise
        Throws Exception if:
            - the tag's information may have invalid fields or references a non-existing party
    """
    def set_tags(self, party_id, tag_list):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            # Check if the party_id exists in the Parties table
            party = self.query_party(party_id=party_id)
            if party is None or len(party) == 0:
                raise Exception(f"Party with ID '{party_id}' does not exist")

            tag_list = str(tag_list)[1:-1]
            statement = f"INSERT INTO Tags VALUES ('{party_id}', ARRAY[{tag_list}]) " \
                        f"ON CONFLICT (party_id) DO UPDATE SET tag_list = ARRAY[{tag_list}]"

            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Setting tags failed")
            logging.fatal(e)
            return False

    """
    set_location(party_id, street, city, prov, postal_code): Set the location for a party by adding a new PartyLocation entry to the PartyLocations table.
        Parameters:
            - party_id: the ID of the party 
            - street: the street address of the party location
            - city: the city of the party location 
            - prov: the province/state of the party location 
            - postal_code: the postal code of the party location 
        Returns:
            - True: if the party location is set successfully
            - False: otherwise
        Throws Exception if:
            - the party location information may have invalid fields or references a non-existing party
    """
    def set_location(self, party_id, street, city, prov, postal_code):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            # Check if the party_id exists in the Parties table
            party = self.query_party(party_id=party_id)
            if party is None or len(party) == 0:
                raise Exception(f"Party with ID '{party_id}' does not exist")

            location = self.query_locations
            statement = f"INSERT INTO PartyLocations VALUES ('{party_id}', '{street}', '{city}', '{prov}', '{postal_code}') " \
                        f"ON CONFLICT (party_id) DO UPDATE " \
                        f"SET (street, city, prov, postal_code) = ('{street}', '{city}', '{prov}', '{postal_code}')"

            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Setting party location failed")
            logging.fatal(e)
            return False
    """
    set_suggestions(guest_id, party_id, suggested_tracks): Sets a MusicSuggestions entry in the MusicSuggestions table.
        Parameters:
            - guest_id: the ID of the guest making the music suggestions 
            - party_id: the ID of the party associated with the music suggestions 
            - suggested_tracks: a list of suggested tracks 
        Returns:
            - True: if the music suggestions are set successfully
            - False: otherwise
        Throws Exception if:
            - the music suggestion information may have invalid fields or references a non-existing guest or party
    """
    def set_suggestions(self, guest_id, party_id, suggested_tracks):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            # Check if the party_id exists in the Parties table
            # TODO: Add a check for guest_id once it becomes a UUID
            party = self.query_party(party_id=party_id)
            if party is None or len(party) == 0:
                raise Exception(f"Party with ID '{party_id}' does not exist")

            suggested_tracks = str(suggested_tracks)[1:-1]

            statement = f"INSERT INTO MusicSuggestions VALUES ('{guest_id}', '{party_id}', ARRAY[{suggested_tracks}]) " \
                        f"ON CONFLICT (guest_id, party_id) DO UPDATE SET suggested_tracks = ARRAY[{suggested_tracks}]"

            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Setting music suggestions failed")
            logging.fatal(e)
            return False



    """
    query_party(party_id, party_name, start_date, end_date, created_after, max_capacity, entry_fee):
    Query parties using some attributes. If an  attribute is left blank then its constraint is ignored.
    Return at most 50 results.
        Parameters:
            - party_id : Return parties where party_id is equal to the party's generated id
            - party_name: Return parties where party_name is a substring of the party's name.
            - start_date: Return parties with scheduled dates later than start_date.
            - end_date: Return parties with scheduled dates earlier than end_date.
            - created_after: Return parties created after the timestamp created_after.
            - max_capacity: Return parties with maximum capacity greater than or equal to max_capacity.
            - entry_fee: Return parties with entry fee less than or equal to entry_fee.
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result, if stmt is queried successfully
            - None: If the query present no result
    """  
    def query_party(self, party_id=None, party_name=None, start_date=None, end_date=None, created_after=None, max_capacity=None, entry_fee=None, limit=50):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")
    
            sub_queries = []
    
            if party_id is not None:
                sub_queries.append(f" p.party_id = '{party_id}'")
    
            if party_name is not None:
                sub_queries.append(f" p.party_name LIKE '%{party_name}%'")
    
            if start_date is not None:
                sub_queries.append(f" p.date_time >= '{start_date}'")
    
            if end_date is not None:
                sub_queries.append(f" p.date_time <= '{end_date}'")
    
            if created_after is not None:
                sub_queries.append(f" p.created_at >= '{created_after}'")
    
            if max_capacity is not None:
                sub_queries.append(f" p.max_capacity >= {max_capacity}")
    
            if entry_fee is not None:
                sub_queries.append(f" p.entry_fee <= {entry_fee}")
    
            statement = self.__create_query_statement("SELECT * FROM Parties p", sub_queries)
            print(statement)
    
            return self.exec_DML(statement, limit)
    
        except Exception as e:
            logging.fatal("Query parties failed")
            logging.fatal(e)
            return None


    """
    query_tags(party_id, tag_list): Query tags using some attributes. If an attribute is left blank then its constraint is ignored.
        Parameters: 
            - party_id: return the tag list associated with the party with party_id
            - tag_list: return all parties associated with the given tags
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result
            - None: If the query present no result
    """
    def query_tags(self, party_id=None, tag_subset=None, limit=50):
        try:
            sub_queries = []

            if party_id is not None:
                sub_queries.append(f" t.party_id = '{party_id}'")

            
            if tag_subset is not None:
                tag_subset = str(tag_subset)[1:-1]
                sub_queries.append(f" t.tag_list @> ARRAY[{tag_subset}]")

            stmt = self.__create_query_statement("SELECT * FROM Tags t", sub_queries)
            print(stmt)

            return self.exec_DML(stmt, limit)

        except Exception as e:
            logging.fatal("Query tags failed")
            logging.fatal(e)
            return None

    """
    query_locations(party_id, street, city, prov, postal_code): Query locations using some attributes.
    If an attribute is left blank then its constraint is ignored.
        Parameters: 
            - party_id: return the location associated with the party with party_id
            - street: return all parties taking place on the given street
            - city: return all parties taking place in the given city
            - prov: return all parties taking place in the given province
            - postal_code: return all parties taking place in the location with the given postal_code
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result
            - None: If the query present no result
    """
    def query_locations(self, party_id=None, street=None, city=None, prov=None, postal_code=None, limit=50):
        try:
            sub_queries = []

            if party_id is not None:
                sub_queries.append(f" l.party_id = '{party_id}'")

            if street is not None:
                sub_queries.append(f" l.street = '{street}'")

            if city is not None:
                sub_queries.append(f" l.city = '{city}'")

            if prov is not None:
                sub_queries.append(f" l.prov = '{prov}'")

            if postal_code is not None:
                sub_queries.append(f" l.postal_code = '{postal_code}'")

            stmt = self.__create_query_statement("SELECT * FROM PartyLocations l", sub_queries)
            print(stmt)

            return self.exec_DML(stmt, limit)

        except Exception as e:
            logging.fatal("Query locations failed")
            logging.fatal(e)
            return None

    """
    query_suggestions(guest_id, party_id, track_subset): Query music suggestions using some attributes.
    If an attribute is left blank then its constraint is ignored.
        Parameters: 
            - guest_id: return all suggestions made by the given guest_id for the specified parties
            - party_id: return all suggestions made for the given party_id by all its guests
            - track_subset: return all suggestion lists that contain the given tracks in track_subset
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result
            - None: If the query present no result
    """
    def query_suggestions(self, guest_id=None, party_id=None, track_subset=None, limit=50):
        try:
            sub_queries = []

            if guest_id is not None:
                sub_queries.append(f" m.guest_id = '{guest_id}'")

            if party_id is not None:
                sub_queries.append(f" m.party_id = '{party_id}'")

            
            if track_subset is not None:
                track_subset = str(track_subset)[1:-1]
                sub_queries.append(f" m.suggested_tracks @> ARRAY[{track_subset}]")

            stmt = self.__create_query_statement("SELECT * FROM MusicSuggestions m", sub_queries)
            print(stmt)

            return self.exec_DML(stmt, limit)

        except Exception as e:
            logging.fatal("Query locations failed")
            logging.fatal(e)
            return None

    """
    clear_table(table): Delete all rows from a table
        Parameters: 
            - table: name of the cleared table
        Returns:
            - True: if the table is cleared successfully
            - False: otherwise
    """
    def clear_table(self, table):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            self.exec_DDL(f"DELETE FROM {table}")

            return True

        except Exception as e:
            logging.fatal("Clearing Tables Failed")
            logging.fatal(e)
            return False
    """
    delete_party(party_id): Delete a specified party from the Parties table
        Parameters: 
            - party_id: the ID of the party to be deleted
        Returns:
            - True: if the party is successfully deleted
            - False: otherwise
    """
    def delete_party(self, party_id):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            self.exec_DDL(f"DELETE FROM Parties WHERE party_id = '{party_id}'")

            return True

        except Exception as e:
            logging.fatal("Deleting Party Failed")
            logging.fatal(e)
            return False

    """
    drop_table(table): Drop the corresponding table in the database.
        Parameters: 
            - table: name of dropped table
        Returns:
            - True: if the table is dropped successfully
            - False: otherwise
    """
    def drop_table(self, table):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            self.exec_DDL(f"DROP TABLE IF EXISTS {table}")

            return True

        except Exception as e:
            logging.fatal("Dropping Tables Failed")
            logging.fatal(e)
            return False


    """
    create_tables(conn): Create all tables in the database.
        Parameters: 
            - conn: the database connection
        Returns:
            - True: if conn is valid and all tables are created
            - False: otherwise
        Throws Exception if:
            - the given database connection is None
    """
    def create_tables(self):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            # Users
            statement = "CREATE TABLE IF NOT EXISTS Users (" \
                        "user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), " \
                        "username VARCHAR(20) NOT NULL, " \
                        "password VARCHAR(20) NOT NULL, " \
                        "first_name VARCHAR(20) NOT NULL, " \
                        "last_name VARCHAR(20) NOT NULL, " \
                        "phone_no BIGINT NOT NULL, " \
                        "address_street VARCHAR(50) NOT NULL, " \
                        "address_city VARCHAR(10) NOT NULL, " \
                        "address_prov VARCHAR(10) NOT NULL, " \
                        "address_postal VARCHAR(10) NOT NULL, " \
                        "email VARCHAR(50) NOT NULL, " \
                        "UNIQUE(username), " \
                        "UNIQUE(phone_no), " \
                        "UNIQUE(email), " \
                        "party_points INTEGER)"
            self.exec_DDL(statement)

            # Parties
            statement = "CREATE TABLE IF NOT EXISTS Parties (" \
                        "party_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), " \
                        "party_name VARCHAR(50) NOT NULL, " \
                        "date_time TIMESTAMP NOT NULL, " \
                        "created_at TIMESTAMP NOT NULL, " \
                        "max_capacity INTEGER NOT NULL, " \
                        "description VARCHAR(250) NOT NULL, " \
                        "thumbnail BYTEA, " \
                        "photos BYTEA[], " \
                        "UNIQUE(party_name), " \
                        "entry_fee INTEGER)"
            self.exec_DDL(statement)

            

            # Tags
            statement = "CREATE TABLE IF NOT EXISTS Tags (" \
                        "party_id UUID, " \
                        "UNIQUE(party_id), " \
                        "tag_list VARCHAR[] NOT NULL, " \
                        "CONSTRAINT party_id " \
                        "FOREIGN KEY (party_id) REFERENCES Parties(party_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)

            # PartyLocations
            statement = "CREATE TABLE IF NOT EXISTS PartyLocations (" \
                        "party_id UUID, " \
                        "UNIQUE(party_id), " \
                        "street VARCHAR(100) NOT NULL, " \
                        "city VARCHAR(50) NOT NULL, " \
                        "prov VARCHAR(50) NOT NULL, " \
                        "postal_code VARCHAR(20) NOT NULL, " \
                        "UNIQUE(street, city, prov, postal_code), " \
                        "CONSTRAINT party_id " \
                        "FOREIGN KEY (party_id) REFERENCES Parties(party_id) ON DELETE CASCADE)" \

            self.exec_DDL(statement)
            # MusicSuggestions
            # TODO: Make the Guests table so that we can have
            # "guest_id UUID, " \
            # "CONSTRAINT guest_id " \
            # "FOREIGN KEY (guest_id) REFERENCES Guests(guest_id) ON DELETE CASCADE, " \
            statement = "CREATE TABLE IF NOT EXISTS MusicSuggestions (" \
                        "guest_id VARCHAR(100) NOT NULL, " \
                        "party_id UUID, " \
                        "UNIQUE(guest_id, party_id), " \
                        "suggested_tracks VARCHAR[] NOT NULL, " \
                        "CONSTRAINT party_id " \
                        "FOREIGN KEY (party_id) REFERENCES Parties(party_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)

            # TODO: Add DDLs for Hosts, Guests, Transactions,
            # Accounts, and CreditCards
            return True

        except Exception as e:
            logging.fatal("Creating Tables Failed")
            logging.fatal(e)
            return False
