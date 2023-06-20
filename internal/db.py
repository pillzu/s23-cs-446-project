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
    exec_DML(stmt): Executes the given DML statement.
        Parameters: 
            - stmt: the executed DML statement
        Returns:
            - row: The query result, if stmt is queried successfully
            - None: otherwise
        Throws Exception if:
            - the statement has format error
    """
    def exec_DML(self, stmt):
        try:
            with self.conn.cursor() as cur:
                cur.execute(stmt)
                row = cur.fetchone()
                self.conn.commit()
                return row
        except Exception as e:
            logging.fatal("Query Execution Failed")
            logging.fatal(f"Last Executed Query: {stmt}")
            logging.fatal(e)
            return None


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
    add_new_user(username, password, first_name, last_name, phone_no, address, email): Insert a new user with the
    given information into Users table. The user's uid will be generated randomly using gen_random_uuid(). The user
    will also be created with a default party point of 0.
        Parameters: 
            - username: the user's account username (String with len <= 20)
            - password: the user's account password (String with len <= 20)
            - first_name: the user's first name (String with len <= 20)
            - last_name: the user's last name (String with len <= 20)
            - phone_no: the user's phone number (Long)
            - address_*: the user's home address (street, city, prov, postal code)
            - email: the user's email address (String with len <= 50)
        Returns:
            - True: if the user is inserted successfully
            - False: otherwise
        Throws Exception if:
            - the user's information may have invalid fields
    """
    def add_new_user(self, username, password, first_name, last_name, phone_no, address_street, address_city, address_prov,
                     address_postal, email) -> bool:
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            statement = f"INSERT INTO Users VALUES (gen_random_uuid(), '{username}', '{password}', '{first_name}', " \
                        f"'{last_name}', {phone_no}, '{address_street}', '{address_city}', '{address_prov}', " \
                        f"'{address_postal}', '{email}', 0)"

            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Adding new user to database failed")
            logging.fatal(e)
            return False


    """
    add_new_party(party_id, party_name, date_time): Insert a new party entry with given information into Parties 
    table. The party's creation time will be filled as the current system time
        Parameters: 
            - party_id: the party's id number
            - party_name: the party's name
            - date_time: The scheduled date of the party
        Returns:
            - True: if the party is inserted successfully
            - False: otherwise
        Throws Exception if:
            - the party's information may have invalid fields
    """
    def add_new_party(self, party_id, party_name, date_time):
        try:
            if self.conn is None:
                raise Exception("Database Connection Not Initialized")

            timez = pytz.timezone("Canada/Eastern")
            statement = f"INSERT INTO Parties VALUES ({party_id}, '{party_name}', {date_time}, '{datetime.now(timez)}')"

            self.exec_DDL(statement)
            return True

        except Exception as e:
            logging.fatal("Adding new user to database failed")
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
                        "party_points INTEGER)"
            self.exec_DDL(statement)

            # Parties
            statement = "CREATE TABLE IF NOT EXISTS Parties (" \
                        "party_id Integer PRIMARY KEY, " \
                        "party_name VARCHAR(50) NOT NULL, " \
                        "date_time TIMESTAMP NOT NULL, " \
                        "created_at TIMESTAMP NOT NULL)"
            self.exec_DDL(statement)

            # TODO: Add all DDLs for CREATE TABLE
            return True

        except Exception as e:
            logging.fatal("Creating Tables Failed")
            logging.fatal(e)
            return False
