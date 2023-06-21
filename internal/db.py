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
    add_new_user(username, password, first_name, last_name, phone_no, address, email): Insert a new user with the
    given information into Users table. The user will be created with a default party point of 0. The account
    associated with the user will also be created with a default balance of 0.
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
            - uuid: if the user is inserted successfully, return the user's uuid
            - None: otherwise
        Throws Exception if:
            - the user's information may have invalid fields
    """
    def add_new_user(self, username, password, first_name, last_name, phone_no, address_street, address_city, address_prov,
                     address_postal, email, uid=None):
        try:
            if uid is None:
                with self.conn.cursor() as cur:
                    cur.execute("SELECT gen_random_uuid()")
                    uid = cur.fetchone()[0]
                statement = f"INSERT INTO Users VALUES ('{uid}', '{username}', '{password}', '{first_name}', " \
                            f"'{last_name}', {phone_no}, '{address_street}', '{address_city}', '{address_prov}', " \
                            f"'{address_postal}', '{email}', 0)"
            else:
                statement = f"INSERT INTO Users VALUES ('{uid}', '{username}', '{password}', '{first_name}', " \
                            f"'{last_name}', {phone_no}, '{address_street}', '{address_city}', '{address_prov}', " \
                            f"'{address_postal}', '{email}', 0)"
            self.exec_DDL(statement)

            self.exec_DDL(f"INSERT INTO Accounts VALUES ('{uid}', 0)")

            return uid

        except Exception as e:
            logging.fatal("Adding new user to database failed")
            logging.fatal(e)
            return None


    """
    add_new_party(party_id, party_name, date_time): Insert a new party entry with given information into Parties 
    table. The party's creation time will be filled as the current system time
        Parameters: 
            - party_id: the party's id number
            - party_name: the party's name
            - date_time: The scheduled date of the party
        Returns:
            - uuid: if the party is inserted successfully, return the party's id
            - None: otherwise
        Throws Exception if:
            - the party's information may have invalid fields
    """
    def add_new_party(self, party_name, date_time, party_id=None):
        try:
            timez = pytz.timezone("Canada/Eastern")

            if party_id is None:
                with self.conn.cursor() as cur:
                    cur.execute("SELECT gen_random_uuid()")
                    party_id = cur.fetchone()[0]
                statement = f"INSERT INTO Parties VALUES ('{party_id}', '{party_name}', '{date_time}', '{datetime.now(timez)}')"
            else:
                statement = f"INSERT INTO Parties VALUES ('{party_id}', '{party_name}', '{date_time}', '{datetime.now(timez)}')"

            self.exec_DDL(statement)
            return party_id

        except Exception as e:
            logging.fatal("Adding new party to database failed")
            logging.fatal(e)
            return None


    """
    attend_party(user_id, party_id): Registers the user with user_id into the party with party_id as a guest
        Parameters: 
            - user_id: the user's id number
            - party_id: the party's id number
        Returns:
            - True: if the user is registered as a guest successfully
            - False: otherwise
    """
    def attend_party(self, user_id, party_id):
        try:
            statement = f"INSERT INTO Guests VALUES ('{user_id}', '{party_id}')"
            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Registering guest to party failed")
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
            statement = f"DELETE FROM Guests g WHERE g.guest_id = '{user_id}' AND g.party_id = '{party_id}'"
            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Failed to leave party")
            logging.fatal(e)
            return False


    """
    host_party(user_id, party_id): Registers the user with user_id into the party with party_id as a host
        Parameters: 
            - user_id: the user's id number
            - party_id: the party's id number
        Returns:
            - True: if the user is registered as a host successfully
            - False: otherwise
    """
    def host_party(self, user_id, party_id):
        try:
            statement = f"INSERT INTO Hosts VALUES ('{user_id}', '{party_id}')"
            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Registering host to party failed")
            logging.fatal(e)
            return False

    """
    cancel_party(user_id, party_id): Cancels the party with party_id.
        Parameters: 
            - user_id: the user's id number
            - party_id: the party's id number
            - force_leave: if set to true, then all attenders will also be forced to leave the party
        Returns:
            - True: if the party is cancelled successfully
            - False: otherwise
    """

    def cancel_party(self, party_id, force_leave=True):
        try:
            statement = f"DELETE FROM Hosts h WHERE h.party_id = '{party_id}'"
            self.exec_DDL(statement)
            if force_leave:
                statement = f"DELETE FROM guests g WHERE g.party_id = '{party_id}'"
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
                statement = f"SELECT p.* FROM Guests g " \
                            f"JOIN Parties p ON g.party_id = p.party_id " \
                            f"WHERE g.guest_id = '{user_id}'"
            else:
                statement = f"SELECT g.party_id FROM Guests g WHERE g.guest_id = '{user_id}'"
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
                statement = f"SELECT p.* FROM Hosts h " \
                            f"JOIN Parties p ON h.party_id = p.party_id " \
                            f"WHERE h.host_id = '{user_id}'"
            else:
                statement = f"SELECT h.party_id FROM Hosts h WHERE h.host_id = '{user_id}'"
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
                statement = f"SELECT u.* FROM Guests g " \
                            f"JOIN Users u ON g.guest_id = u.user_id " \
                            f"WHERE g.party_id = '{party_id}'"
            else:
                statement = f"SELECT g.guest_id FROM Guests g WHERE g.party_id = '{party_id}'"
            return self.exec_DML(statement, limit)

        except Exception as e:
            logging.fatal("Displaying attendees failed")
            logging.fatal(e)
            return None


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
    query_party(party_name, start_date, end_date, created_after, limit): Query parties using some attributes. If an 
    attribute is left blank then its constraint is ignored.
        Parameters: 
            - party_name: return all parties where party_name is a substring of the party's name
            - start_date: return all parties with scheduled dates later than start_date
            - end_date: return all parties with scheduled dates earlier than end_date
            - created_after: return all parties created after the timestamp created_after
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result
            - None: If the query present no result
    """
    def query_party(self, party_id=None, party_name=None, start_date=None, end_date=None, created_after=None, limit=50):
        try:
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

            stmt = self.__create_query_statement("SELECT * FROM Parties p", sub_queries)
            print(stmt)

            return self.exec_DML(stmt, limit)

        except Exception as e:
            logging.fatal("Query parties failed")
            logging.fatal(e)
            return None


    """
    query_user(user_id, username, first_name, last_name, email, limit): Query users using some attributes. If an 
    attribute is left blank then its constraint is ignored.
        Parameters: 
            - user_id: return the user with the matching user_id
            - username: return the user with matching username
            - first_name: return all users with matching first_name
            - last_name: return all users with matching last_name
            - email: return the user with matching email address
            - limit: if specified return at most this amount of rows. Defaulted to 50.
        Returns:
            - row: The query result
            - None: If the query present no result
    """
    def query_user(self, user_id=None, username=None, first_name=None, last_name=None, email=None, limit=50):
        try:
            sub_queries = []

            if user_id is not None:
                sub_queries.append(f" u.user_id = '{user_id}'")

            if username is not None:
                sub_queries.append(f" u.username LIKE '%{username}%'")

            if first_name is not None:
                sub_queries.append(f" u.first_name LIKE '%{first_name}%'")

            if last_name is not None:
                sub_queries.append(f" u.last_name LIKE '%{last_name}%'")

            if email is not None:
                sub_queries.append(f" u.email LIKE '%{email}%'")

            stmt = self.__create_query_statement("SELECT * FROM Users u", sub_queries)
            print(stmt)

            return self.exec_DML(stmt, limit)

        except Exception as e:
            logging.fatal("Query parties failed")
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
                        "party_points INTEGER)"
            self.exec_DDL(statement)

            # Parties
            statement = "CREATE TABLE IF NOT EXISTS Parties (" \
                        "party_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), " \
                        "party_name VARCHAR(50) NOT NULL, " \
                        "date_time TIMESTAMP NOT NULL, " \
                        "created_at TIMESTAMP NOT NULL)"
            self.exec_DDL(statement)

            # Accounts
            statement = "CREATE TABLE IF NOT EXISTS Accounts (" \
                        "account_id UUID PRIMARY KEY, " \
                        "balance DECIMAL(10, 2) NOT NULL, " \
                        "CONSTRAINT user_account " \
                        "FOREIGN KEY (account_id) REFERENCES Users(user_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)

            # CreditCards
            statement = "CREATE TABLE IF NOT EXISTS CreditCards (" \
                        "account_id UUID, " \
                        "card_number BIGINT NOT NULL, " \
                        "cvv INTEGER NOT NULL, " \
                        "CONSTRAINT account_credit_card " \
                        "FOREIGN KEY (account_id) REFERENCES Accounts(account_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)

            # Hosts
            statement = "CREATE TABLE IF NOT EXISTS Hosts (" \
                        "host_id UUID, " \
                        "party_id UUID, " \
                        "CONSTRAINT host_user_id " \
                        "FOREIGN KEY (host_id) REFERENCES Users(user_id) ON DELETE SET NULL, " \
                        "CONSTRAINT host_party_id " \
                        "FOREIGN KEY (party_id) REFERENCES Parties(party_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)

            # Guests
            statement = "CREATE TABLE IF NOT EXISTS Guests (" \
                        "guest_id UUID, " \
                        "party_id UUID, " \
                        "CONSTRAINT guest_user_id " \
                        "FOREIGN KEY (guest_id) REFERENCES Users(user_id) ON DELETE SET NULL, " \
                        "CONSTRAINT guest_party_id " \
                        "FOREIGN KEY (party_id) REFERENCES Parties(party_id) ON DELETE CASCADE)"
            self.exec_DDL(statement)

            # TODO: Add all DDLs for CREATE TABLE
            return True

        except Exception as e:
            logging.fatal("Creating Tables Failed")
            logging.fatal(e)
            return False
