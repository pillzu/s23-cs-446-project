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
    add_new_user(profile_url, first_name, last_name, phone_no, address, email): Insert a new user with the
    given information into Users table. The user will be created with a default party point of 0. The account
    associated with the user will also be created with a default balance of 0.
        Parameters: 
            - uid: the user's uuid. If left blank then will use gen_random_uuid() (string)
            - profile_url: the user's profile url (String with len <= 100)
            - first_name: the user's first name (String with len <= 20)
            - last_name: the user's last name (String with len <= 20)
            - phone_no: the user's phone number (Long)
            - address_*: the user's home address (street, city, prov, postal code)
            - email: the user's email address (String with len <= 50)
        Returns:
            - uuid: if exec_stmt=True and the user is inserted successfully, return the user's uuid
            - statement, uuid: if exec_stmt=False, return the generated uid and the SQL statement to be executed
            - None: otherwise
        Throws Exception if:
            - the user's information may have invalid fields
    """

    def add_new_user(self, profile_url, first_name, last_name, phone_no, address_street, address_city, address_prov,
                     address_postal, email, uid=None, exec_stmt=True):
        try:
            if uid is None:
                with self.conn.cursor() as cur:
                    cur.execute("SELECT gen_random_uuid()")
                    uid = cur.fetchone()[0]
            statement = f"INSERT INTO Users VALUES ('{uid}', '{profile_url}', '{first_name}', " \
                        f"'{last_name}', {phone_no}, '{address_street}', '{address_city}', '{address_prov}', " \
                        f"'{address_postal}', '{email}', 0);\n"
            if exec_stmt:
                self.exec_DDL(statement)
                return uid
            else:
                return statement, uid

        except Exception as e:
            logging.fatal("Adding new user to database failed")
            logging.fatal(e)
            return None

    """
    add_new_party(party_avatar_url, party_name, date_time, host_id, max_capacity, description, entry_fee): 
    Insert a new party entry with the given information into the Parties table. 
    The party's creation time will be filled as the current system time.
        Parameters: 
            - uid: the party's uuid. If left blank then will use gen_random_uuid() (string)
            - party_avatar_url: The party's avatar url (string)
            - party_name: The party's name (string)
            - date_time: The scheduled date of the party (datetime)
            - host_id: The user id of the host
            - max_capacity: The maximum capacity of the guests in the party (integer)
            - description: The description of the party, entered by the host (string)
            - entry_fee: The entry fee of the party (integer)
        Returns:
            - uuid: if exec_stmt=True and the party is inserted successfully, return the party's id
            - statement, uuid: if exec_stmt=False, return the SQL statement to be executed and the generated party id 
            - None: otherwise
        Throws Exception if:
            - the party's information may have invalid fields
    """

    def add_new_party(self, party_avatar_url, party_name, date_time, host_id, max_capacity, description, entry_fee, party_id=None,
                      exec_stmt=True):
        try:
            timez = pytz.timezone("Canada/Eastern")

            if party_id is None:
                with self.conn.cursor() as cur:
                    cur.execute("SELECT gen_random_uuid()")
                    party_id = cur.fetchone()[0]

            # TODO: Remove parties and thumbnail
            statement = f"INSERT INTO Parties " \
                        f"VALUES ('{party_id}', '{party_avatar_url}', '{party_name}', '{date_time}', '{host_id}', '{datetime.now(timez)}', " \
                        f"{max_capacity}, '{description}', " \
                        f"{entry_fee});\n"

            if exec_stmt:
                self.exec_DDL(statement)
                return party_id
            else:
                return statement, party_id

        except Exception as e:
            logging.fatal("Adding new party to database failed")
            logging.fatal(e)
            return None

    """
    add_new_transaction(guest_id, party_id, amount): Insert a new transaction entry with given information
    into the Transactions table. The transaction's creation time will be filled as the current system time
        Parameters: 
            - guest_id: the guest's id number
            - party_id: the party's id number
            - amount: the payment amount
        Returns:
            - uuid: if exec_stmt=True and the transaction entry is inserted successfully, return the transaction's id
            - statement, uuid: if exec_stmt=False, return the SQL statement to be executed and the generated id
            - None: otherwise
        Throws Exception if:
            - the transaction's information may have invalid fields
    """

    def add_new_transaction(self, guest_id, party_id, amount, trans_id=None, exec_stmt=True):
        try:
            timez = pytz.timezone("Canada/Eastern")

            if trans_id is None:
                with self.conn.cursor() as cur:
                    cur.execute("SELECT gen_random_uuid()")
                    trans_id = cur.fetchone()[0]
            statement = f"INSERT INTO Transactions VALUES ('{trans_id}', '{datetime.now(timez)}', '{guest_id}', " \
                        f"'{party_id}', {amount});\n"

            if exec_stmt:
                self.exec_DDL(statement)
                return trans_id
            else:
                return statement, trans_id

        except Exception as e:
            logging.fatal("Adding new transaction to database failed")
            logging.fatal(e)
            return None

    """
    set_tags(party_id, tag_list): Sets the tags for a given party in the Tags table.
        Parameters:
            - party_id: the ID of the party associated with the tag list
            - tag_list: the list of tags to be associated with the party
        Returns:
            - True: if exec_stmt=True and the tag list is inserted successfully
            - statement: if exec_stmt=False, return the SQL statement to be executed
            - False: otherwise
        Throws Exception if:
            - the tag's information may have invalid fields or references a non-existing party
    """

    def set_tags(self, party_id, tag_list, exec_stmt=True):
        try:
            # Check if the party_id exists in the Parties table
            party = self.query_party(party_id=party_id)
            if party is None or len(party) == 0:
                raise Exception(f"Party with ID '{party_id}' does not exist")

            tag_list = str(tag_list)[1:-1]
            statement = f"INSERT INTO Tags VALUES ('{party_id}', ARRAY[{tag_list}]) " \
                        f"ON CONFLICT (party_id) DO UPDATE SET tag_list = ARRAY[{tag_list}];\n"

            if exec_stmt:
                self.exec_DDL(statement)
                return True
            else:
                return statement

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
            - True: if exec_stmt=True the party location is set successfully
            - statement: if exec_stmt=False, return the SQL statement to be executed
            - False: otherwise
        Throws Exception if:
            - the party location information may have invalid fields or references a non-existing party
    """

    def set_location(self, party_id, street, city, prov, postal_code, exec_stmt=True):
        try:
            # Check if the party_id exists in the Parties table
            party = self.query_party(party_id=party_id)
            if party is None or len(party) == 0:
                raise Exception(f"Party with ID '{party_id}' does not exist")

            location = self.query_locations
            statement = f"INSERT INTO PartyLocations VALUES ('{party_id}', '{street}', '{city}', '{prov}', '{postal_code}') " \
                        f"ON CONFLICT (party_id) DO UPDATE " \
                        f"SET (street, city, prov, postal_code) = ('{street}', '{city}', '{prov}', '{postal_code}');\n"

            if exec_stmt:
                self.exec_DDL(statement)
                return True
            else:
                return statement

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
            - True: if exec_stmt=True and the music suggestions are set successfully
            - statement: if exec_stmt=False, return the SQL statement to be executed
            - False: otherwise
        Throws Exception if:
            - the music suggestion information may have invalid fields or references a non-existing guest or party
    """

    def set_suggestions(self, guest_id, party_id, suggested_tracks, exec_stmt=True):
        try:
            # TODO: Do we need to verify this? We may add constraints to avoid these verifications
            # Check if the party_id exists in the Parties table
            party = self.query_party(party_id=party_id)
            if party is None or len(party) == 0:
                raise Exception(f"Party with ID '{party_id}' does not exist")

            # check if guest attends the party
            attend = self.check_attends(party_id=party_id, guest_id=guest_id)
            if attend is False:
                raise Exception(
                    f"Guest ('{guest_id}') does not attend party ('{party_id}')")

            suggested_tracks = str(suggested_tracks)[1:-1]

            statement = f"INSERT INTO MusicSuggestions VALUES ('{guest_id}', '{party_id}', ARRAY[{suggested_tracks}]) " \
                        f"ON CONFLICT (guest_id, party_id) DO UPDATE SET suggested_tracks = ARRAY[{suggested_tracks}];\n"

            if exec_stmt:
                self.exec_DDL(statement)
                return True
            else:
                return statement

        except Exception as e:
            logging.fatal("Setting music suggestions failed")
            logging.fatal(e)
            return False

    """
    leave_party(user_id, party_id): Remove the user with user_id from the party with party_id as a guest
        Parameters: 
            - user_id: the user's id number
            - party_id: the party's id number
        Returns:
            - True: if the user is removed from the party successfully
            - False: otherwise
    """

    def leave_party(self, user_id, party_id):
        try:
            statement = f"DELETE FROM Transactions t WHERE t.guest_id = '{user_id}' AND t.party_id = '{party_id}'"
            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Failed to leave party")
            logging.fatal(e)
            return False

    """
    cancel_party(party_id): Cancels the party with party_id.
        Parameters: 
            - party_id: the party's id number
        Returns:
            - True: if the party is cancelled successfully
            - False: otherwise
    """

    def cancel_party(self, party_id):
        try:
            statement = f"DELETE FROM Parties p WHERE p.party_id = '{party_id}';\n " \
                        f"DELETE FROM Transactions t WHERE t.party_id = '{party_id}';"
            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Failed to cancel party")
            logging.fatal(e)
            return False

    """
    show_attended_parties(user_id): Returns all parties that the user with user_id attends
        Parameters: 
            - user_id: the user's id number
            - show_detail: if set to true, then return the details of the attended parties. Otherwise only return the ids.
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - Parties: the id of the parties that the user attends
            - None: otherwise
    """

    def show_attended_parties(self, user_id, show_detail=False, limit=50):
        try:
            if show_detail:
                statement = f"SELECT p.*, pl.* FROM Transactions t " \
                            f"JOIN Parties p ON t.party_id = p.party_id " \
                            f"JOIN PartyLocations pl ON t.party_id = pl.party_id " \
                            f"WHERE t.guest_id = '{user_id}'"
            else:
                statement = f"SELECT t.party_id FROM Transactions t WHERE t.guest_id = '{user_id}'"
            return self.exec_DML(statement, limit)

        except Exception as e:
            logging.fatal("Displaying attended parties failed")
            logging.fatal(e)
            return None

    """
    show_hosted_parties(user_id): Returns all parties that the user with user_id hosts
        Parameters: 
            - user_id: the user's id number
            - show_detail: if set to true, then return the details of the hosted parties. Otherwise only return the ids.
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - Parties: the id of the parties that the user hosts
            - None: otherwise
    """

    def show_hosted_parties(self, user_id, show_detail=False, limit=50):
        try:
            if show_detail:
                statement = f"SELECT * FROM Parties p " \
                            f"JOIN PartyLocations pl ON p.party_id = pl.party_id " \
                            f"WHERE p.host_id = '{user_id}'"
            else:
                statement = f"SELECT p.party_id FROM Parties p WHERE p.host_id = '{user_id}'"
            return self.exec_DML(statement, limit)

        except Exception as e:
            logging.fatal("Displaying hosted parties failed")
            logging.fatal(e)
            return None

    """
    show_attendees(party_id): Returns all users that attends the current party
        Parameters: 
            - party_id: the party's id number
            - show_detail: if set to true, then return the details of the attended users. Otherwise only return the ids.
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - Users: the id of the users that attends the party
            - None: otherwise
    """

    def show_attendees(self, party_id, show_detail=False, limit=50):
        try:
            if show_detail:
                statement = f"SELECT u.* FROM Transactions t " \
                            f"JOIN Users u ON t.guest_id = u.user_id " \
                            f"WHERE t.party_id = '{party_id}'"
            else:
                statement = f"SELECT t.guest_id FROM Transactions t WHERE t.party_id = '{party_id}'"
            return self.exec_DML(statement, limit)

        except Exception as e:
            logging.fatal("Displaying attendees failed")
            logging.fatal(e)
            return None

    """
    check_attends(party_id, guest_id): Checks if the user with guest_id attends party with party_id
        Parameters: 
            - party_id: the party's id number
            - guest_id: the guest's user id
        Returns:
            - true: if the guest attends the party
            - false: otherwise
    """

    def check_attends(self, party_id, guest_id):
        try:
            statement = f"SELECT * FROM Transactions t WHERE t.party_id = '{party_id}' AND t.guest_id = '{guest_id}'"
            return len(self.exec_DML(statement)) != 0

        except Exception as e:
            logging.fatal("Checking attendance failed")
            logging.fatal(e)
            return False

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
    query_user(user_id, profile_url, first_name, last_name, email, limit): Query users using some attributes. If an 
    attribute is left blank then its constraint is ignored.
        Parameters: 
            - user_id: return the user with the matching user_id
            - profile_url: return the user with matching profile_url
            - first_name: return all users with matching first_name
            - last_name: return all users with matching last_name
            - email: return the user with matching email address
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result
            - None: If the query present no result
    """

    def query_user(self, user_id=None, profile_url=None, first_name=None, last_name=None, email=None, limit=50):
        try:
            sub_queries = []

            if user_id is not None:
                sub_queries.append(f" u.user_id = '{user_id}'")

            if profile_url is not None:
                sub_queries.append(f" u.profile_url = '{profile_url}'")

            if first_name is not None:
                sub_queries.append(f" u.first_name LIKE '%{first_name}%'")

            if last_name is not None:
                sub_queries.append(f" u.last_name LIKE '%{last_name}%'")

            if email is not None:
                sub_queries.append(f" u.email LIKE '%{email}%'")

            stmt = self.__create_query_statement(
                "SELECT * FROM Users u", sub_queries)
            print(stmt)

            return self.exec_DML(stmt, limit)

        except Exception as e:
            logging.fatal("Query users failed")
            logging.fatal(e)
            return None

    """
    query_transaction(trans_id, guest_id, party_id, min_amount, max_amount, start_date, end_date): Query 
    transactions using some attributes. If an attribute is left blank then its constraint is ignored.
        Parameters: 
            - trans_id: return the transaction with the matching transaction_id
            - guest_id: return all transactions with paid by user with guest_id
            - party_id: return all transactions associated with the party with party_id
            - min_amount: return all transactions with payment amount >= min_amount
            - max_amount: return all transactions with payment amount <= max_amount
            - start_date: return all transactions happening after this time
            - end_date: return all transactions happening before this time
            - show_party_detail: if set to true then show the party's info
            - show_guest_detail: if set to true then show the guest's info
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result
            - None: If the query present no result
    """

    def query_transaction(self, trans_id=None, guest_id=None, party_id=None, min_amount=None,
                          max_amount=None, start_date=None, end_date=None,
                          show_party_detail=False, show_guest_detail=False, limit=50):
        try:
            sub_queries = []

            if trans_id is not None:
                sub_queries.append(f" t.transaction_id = '{trans_id}'")

            if guest_id is not None:
                sub_queries.append(f" t.guest_id = '{guest_id}'")

            if party_id is not None:
                sub_queries.append(f" t.party_id = '{party_id}'")

            if min_amount is not None:
                sub_queries.append(f" t.payment_amount >= {min_amount}")

            if max_amount is not None:
                sub_queries.append(f" t.payment_amount <= {max_amount}")

            if start_date is not None:
                sub_queries.append(f" t.time >= '{start_date}'")

            if end_date is not None:
                sub_queries.append(f" t.time <= '{end_date}'")

            prefix = "SELECT * FROM Transactions t"
            if show_party_detail:
                prefix += " JOIN Parties p ON t.party_id = p.party_id" \
                          " JOIN PartyLocations pl ON t.party_id = pl.party_id"
            if show_guest_detail:
                prefix += " JOIN Users u ON t.guest_id = u.user_id"

            stmt = self.__create_query_statement(prefix, sub_queries)
            print(stmt)

            return self.exec_DML(stmt, limit)

        except Exception as e:
            logging.fatal("Query transactions failed")
            logging.fatal(e)
            return None

    """
    query_party(party_id, party_name, start_date, end_date, created_after, max_capacity, entry_fee):
    Query parties using some attributes. If an  attribute is left blank then its constraint is ignored.
    Return at most 50 results.
        Parameters:
            - party_id : Return parties where party_id is equal to the party's generated id
            - party_avatar_url: Return parties with the matching party_avatar_url.
            - party_name: Return parties where party_name is a substring of the party's name.
            - start_date: Return parties with scheduled dates later than start_date.
            - end_date: Return parties with scheduled dates earlier than end_date.
            - hosted_by: Return parties that are hosted by this uid
            - created_after: Return parties created after the timestamp created_after.
            - max_capacity: Return parties with maximum capacity greater than or equal to max_capacity.
            - entry_fee: Return parties with entry fee less than or equal to entry_fee.
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result, if stmt is queried successfully
            - None: If the query present no result
    """

    def query_party(self, party_id=None, party_avatar_url=None, party_name=None, start_date=None, end_date=None, hosted_by=None,
                    created_after=None, max_capacity=None, entry_fee=None, limit=50):
        try:
            sub_queries = []

            if party_id is not None:
                sub_queries.append(f" p.party_id = '{party_id}'")

            if party_avatar_url is not None:
                sub_queries.append(f" p.party_avatar_url = '{party_avatar_url}'")

            if party_name is not None:
                sub_queries.append(f" p.party_name LIKE '%{party_name}%'")

            if start_date is not None:
                sub_queries.append(f" p.date_time >= '{start_date}'")

            if end_date is not None:
                sub_queries.append(f" p.date_time <= '{end_date}'")

            if hosted_by is not None:
                sub_queries.append(f" p.host_id = '{hosted_by}'")

            if created_after is not None:
                sub_queries.append(f" p.created_at >= '{created_after}'")

            if max_capacity is not None:
                sub_queries.append(f" p.max_capacity >= {max_capacity}")

            if entry_fee is not None:
                sub_queries.append(f" p.entry_fee <= {entry_fee}")

            statement = self.__create_query_statement(
                "SELECT * FROM Parties p", sub_queries)
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
            - show_detail: if set to true, then return the details of the queried parties. Otherwise only return the ids.
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result
            - None: If the query present no result
    """

    def query_tags(self, party_id=None, tag_subset=None, show_detail=False, limit=50):
        try:
            sub_queries = []

            if party_id is not None:
                sub_queries.append(f" t.party_id = '{party_id}'")

            if tag_subset is not None:
                tag_subset = str(tag_subset)[1:-1]
                sub_queries.append(f" t.tag_list @> ARRAY[{tag_subset}]")

            prefix = "SELECT * FROM Tags t"
            if show_detail:
                prefix += " JOIN Parties p ON t.party_id = p.party_id" \
                          " JOIN PartyLocations pl ON t.party_id = pl.party_id"

            stmt = self.__create_query_statement(prefix, sub_queries)
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

            stmt = self.__create_query_statement(
                "SELECT * FROM PartyLocations l", sub_queries)
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
                sub_queries.append(
                    f" m.suggested_tracks @> ARRAY[{track_subset}]")

            stmt = self.__create_query_statement(
                "SELECT * FROM MusicSuggestions m", sub_queries)
            print(stmt)

            return self.exec_DML(stmt, limit)

        except Exception as e:
            logging.fatal("Query locations failed")
            logging.fatal(e)
            return None


    """
    exec_attend_party(user_id, party_id): Registers the user with user_id into the party with party_id as a guest
        Parameters: 
            - user_id: the user's id number
            - party_id: the party's id number
        Returns:
            - True: if the user is registered as a guest successfully
            - False: otherwise
    """

    def exec_attend_party(self, user_id, party_id, entry_fee):
        try:
            self.add_new_transaction(user_id, party_id, entry_fee)
            return True

        except Exception as e:
            logging.fatal("Attending party failed")
            logging.fatal(e)
            return False


    """
    exec_host_party(party_name, party_avatar_url, date_time, host_id, max_capacity, description, entry_fee, street, city, prov, 
    postal_code, tag_list): Adds the party into the database, sets its location and tags
        Parameters:
            - party_name, date_time, host_id, max_capacity, description, entry_fee: identifies a party
            - street, city, prov, postal_code: identifies a party location
            - tag_list: a list of party tags
        Returns:
            - True: if the party is hosted successfully
            - False: otherwise
    """

    def exec_host_party(self, party_name, party_avatar_url, date_time, host_id, max_capacity, description, entry_fee, street, city,
                        prov, postal_code, tag_list):
        try:
            statements = ""
            add_party = self.add_new_party(party_name, party_avatar_url, date_time, host_id, max_capacity, description,
                                           entry_fee, exec_stmt=False)
            statements += add_party[0]
            party_id = add_party[1]
            statements += self.set_location(party_id, street, city, prov, postal_code, exec_stmt=False)
            statements += self.set_tags(party_id, tag_list, exec_stmt=False)
            self.exec_DDL(statements)
            return True

        except Exception as e:
            logging.fatal("Hosting party failed")
            logging.fatal(e)
            return False


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
            self.exec_DDL(f"DELETE FROM {table}")

            return True

        except Exception as e:
            logging.fatal("Clearing Tables Failed")
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
            # Users
            statement = "CREATE TABLE IF NOT EXISTS Users (" \
                        "user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), " \
                        "profile_url VARCHAR(100) NOT NULL, " \
                        "first_name VARCHAR(20) NOT NULL, " \
                        "last_name VARCHAR(20) NOT NULL, " \
                        "phone_no BIGINT NOT NULL, " \
                        "address_street VARCHAR(50) NOT NULL, " \
                        "address_city VARCHAR(10) NOT NULL, " \
                        "address_prov VARCHAR(10) NOT NULL, " \
                        "address_postal VARCHAR(10) NOT NULL, " \
                        "email VARCHAR(50) NOT NULL, " \
                        "party_points INTEGER)"
            self.exec_DDL(statement)

            # Parties
            statement = "CREATE TABLE IF NOT EXISTS Parties (" \
                        "party_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), " \
                        "party_avatar_url VARCHAR(100) NOT NULL, " \
                        "party_name VARCHAR(50) NOT NULL, " \
                        "date_time TIMESTAMP NOT NULL, " \
                        "host_id UUID NOT NULL" \
                        "created_at TIMESTAMP NOT NULL, " \
                        "max_capacity INTEGER NOT NULL, " \
                        "description VARCHAR(250) NOT NULL, " \
                        "UNIQUE(party_name), " \
                        "entry_fee INTEGER, " \
                        "CONSTRAINT host_user_id " \
                        "FOREIGN KEY (host_id) REFERENCES Users(user_id) ON DELETE SET NULL)"
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
                        "FOREIGN KEY (party_id) REFERENCES Parties(party_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)

            # MusicSuggestions
            statement = "CREATE TABLE IF NOT EXISTS MusicSuggestions (" \
                        "guest_id UUID, " \
                        "party_id UUID, " \
                        "UNIQUE(guest_id, party_id), " \
                        "suggested_tracks VARCHAR[] NOT NULL, " \
                        "CONSTRAINT party_id " \
                        "FOREIGN KEY (party_id) REFERENCES Parties(party_id) ON DELETE CASCADE, " \
                        "CONSTRAINT guest_id " \
                        "FOREIGN KEY (guest_id) REFERENCES Users(user_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)

            # Transactions
            statement = "CREATE TABLE IF NOT EXISTS Transactions (" \
                        "transaction_id UUID PRIMARY KEY, " \
                        "time TIMESTAMP, " \
                        "guest_id UUID, " \
                        "party_id UUID, " \
                        "payment_amount DECIMAL(10, 2), " \
                        "CONSTRAINT guest_user_id " \
                        "FOREIGN KEY (from_id) REFERENCES Users(user_id) ON DELETE CASCADE, " \
                        "CONSTRAINT host_user_id " \
                        "FOREIGN KEY (to_id) REFERENCES Users(user_id) ON DELETE CASCADE, " \
                        "CONSTRAINT party_id " \
                        "FOREIGN KEY (party_id) REFERENCES Parties(party_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Creating Tables Failed")
            logging.fatal(e)
            return False
